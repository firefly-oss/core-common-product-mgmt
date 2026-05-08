/*
 * Copyright 2025 Firefly Software Solutions Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.firefly.core.product.core.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.core.product.core.services.ProductPricingAggregatorService;
import com.firefly.core.product.interfaces.dtos.FeeDefinitionDTO;
import com.firefly.core.product.interfaces.dtos.InterestRateBracketDTO;
import com.firefly.core.product.interfaces.dtos.ProductPricingDTO;
import com.firefly.core.product.interfaces.enums.ProductConfigTypeEnum;
import com.firefly.core.product.interfaces.enums.ProductStatusEnum;
import com.firefly.core.product.models.entities.Product;
import com.firefly.core.product.models.entities.ProductConfiguration;
import com.firefly.core.product.models.repositories.ProductConfigurationRepository;
import com.firefly.core.product.models.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fireflyframework.web.error.exceptions.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Default {@link ProductPricingAggregatorService} implementation. Reads the
 * generic {@code product_configuration} key-value rows that back PRICING and
 * LIMITS for a product, parses the JSON payloads with Jackson and returns a
 * calculator-friendly {@link ProductPricingDTO}.
 *
 * <p>Three configuration rows are required per product:
 * <ul>
 *   <li>{@code (LIMITS, amount_term)} — JSON object with {@code currency},
 *       {@code minAmount}, {@code maxAmount}, {@code minTerm},
 *       {@code maxTerm}.</li>
 *   <li>{@code (PRICING, interest_rate_brackets)} — JSON array of
 *       {@code {minAmount,maxAmount,tin}} objects.</li>
 *   <li>{@code (PRICING, fees)} — JSON array of
 *       {@code {type,percentage,fixed}} objects.</li>
 * </ul>
 *
 * <p>Missing rows surface as a "configuration not found" error; malformed JSON
 * is propagated as a "configuration parse error" so the controller layer can
 * map it to a 502 Bad Gateway.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductPricingAggregatorServiceImpl implements ProductPricingAggregatorService {

    /** Configuration key holding the LIMITS JSON object. */
    private static final String CONFIG_KEY_AMOUNT_TERM = "amount_term";

    /** Configuration key holding the PRICING interest-rate brackets array. */
    private static final String CONFIG_KEY_INTEREST_RATE_BRACKETS = "interest_rate_brackets";

    /** Configuration key holding the PRICING fees array. */
    private static final String CONFIG_KEY_FEES = "fees";

    private final ProductRepository productRepository;
    private final ProductConfigurationRepository productConfigurationRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<ProductPricingDTO> getProductPricing(UUID productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "PRODUCT_NOT_FOUND",
                        "Product not found with ID: " + productId)))
                .flatMap(product -> productConfigurationRepository.findByProductId(productId)
                        .collectList()
                        .flatMap(configs -> buildPricing(product, configs)))
                .doOnSuccess(dto -> log.debug(
                        "Aggregated pricing for product {} (code={})",
                        productId,
                        dto != null ? dto.getProductCode() : "<null>"))
                .doOnError(err -> log.warn(
                        "Failed to aggregate pricing for product {}: {}",
                        productId, err.getMessage()));
    }

    @Override
    public Flux<ProductPricingDTO> listProductsWithPricing(String productType) {
        String filter = productType == null ? null : productType.trim();
        // Regulatory hardening: only ACTIVE products are eligible for pricing
        // aggregation. Returning RETIRED / DRAFT / PROPOSED products would let
        // a deprecated rate leak into the calculator and surface to a borrower
        // a number that the scoring engine would later contradict. The
        // {@code product.product_status} column is declared NOT NULL by V2, so
        // a strict status filter is safe — there are no legacy NULL rows.
        // The {@code productType} parameter is the calculator-friendly label
        // derived from {@code product_code} (e.g. "PERSONAL_LOAN", "LEASING")
        // and not the persisted {@code ProductTypeEnum}, so it cannot be pushed
        // into SQL and remains an in-memory filter.
        return productRepository.findByProductStatus(ProductStatusEnum.ACTIVE)
                .filter(product -> filter == null || filter.isEmpty()
                        || filter.equalsIgnoreCase(deriveProductType(product)))
                .flatMap(product -> getProductPricing(product.getProductId())
                        .onErrorResume(err -> {
                            log.warn(
                                    "Skipping product {} in pricing listing due to error: {}",
                                    product.getProductId(), err.getMessage());
                            return Mono.empty();
                        }));
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private Mono<ProductPricingDTO> buildPricing(Product product, List<ProductConfiguration> configs) {
        Optional<ProductConfiguration> limitsCfg = findConfig(
                configs, ProductConfigTypeEnum.LIMITS, CONFIG_KEY_AMOUNT_TERM);
        if (limitsCfg.isEmpty()) {
            return Mono.error(new BusinessException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "PRODUCT_CONFIG_MISSING",
                    "Configuration not found: LIMITS/amount_term for product " + product.getProductId()));
        }

        Optional<ProductConfiguration> ratesCfg = findConfig(
                configs, ProductConfigTypeEnum.PRICING, CONFIG_KEY_INTEREST_RATE_BRACKETS);
        if (ratesCfg.isEmpty()) {
            return Mono.error(new BusinessException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "PRODUCT_CONFIG_MISSING",
                    "Configuration not found: PRICING/interest_rate_brackets for product " + product.getProductId()));
        }

        Optional<ProductConfiguration> feesCfg = findConfig(
                configs, ProductConfigTypeEnum.PRICING, CONFIG_KEY_FEES);
        if (feesCfg.isEmpty()) {
            return Mono.error(new BusinessException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "PRODUCT_CONFIG_MISSING",
                    "Configuration not found: PRICING/fees for product " + product.getProductId()));
        }

        try {
            Map<String, Object> limits = objectMapper.readValue(
                    nullToEmptyObject(limitsCfg.get().getConfigValue()),
                    new TypeReference<Map<String, Object>>() {});

            List<InterestRateBracketDTO> rates = objectMapper.readValue(
                    nullToEmptyArray(ratesCfg.get().getConfigValue()),
                    new TypeReference<List<InterestRateBracketDTO>>() {});

            List<FeeDefinitionDTO> fees = objectMapper.readValue(
                    nullToEmptyArray(feesCfg.get().getConfigValue()),
                    new TypeReference<List<FeeDefinitionDTO>>() {});

            ProductPricingDTO dto = ProductPricingDTO.builder()
                    .productId(product.getProductId())
                    .productCode(product.getProductCode())
                    .productType(deriveProductType(product))
                    .currency(asString(limits.get("currency")))
                    .minAmount(asBigDecimal(limits.get("minAmount")))
                    .maxAmount(asBigDecimal(limits.get("maxAmount")))
                    .minTerm(asInteger(limits.get("minTerm")))
                    .maxTerm(asInteger(limits.get("maxTerm")))
                    .interestRates(rates != null ? rates : Collections.emptyList())
                    .fees(fees != null ? fees : Collections.emptyList())
                    .build();
            return Mono.just(dto);
        } catch (JsonProcessingException ex) {
            return Mono.error(new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "PRODUCT_CONFIG_PARSE_ERROR",
                    "Configuration parse error for product " + product.getProductId() + ": " + ex.getOriginalMessage(),
                    ex));
        }
    }

    private static Optional<ProductConfiguration> findConfig(
            List<ProductConfiguration> configs,
            ProductConfigTypeEnum type,
            String key) {
        return configs.stream()
                .filter(c -> type == c.getConfigType())
                .filter(c -> key.equals(c.getConfigKey()))
                .findFirst();
    }

    /**
     * Derives the calculator-friendly product type label from the product's
     * business code. Convention: {@code "<TYPE>_DEMO"} -> {@code "<TYPE>"};
     * any non-demo code returns the code itself stripped of trailing
     * {@code _SUFFIX} segments. Falls back to the entity-level product type
     * (FINANCIAL / NON_FINANCIAL) when no code is present.
     */
    private static String deriveProductType(Product product) {
        String code = product.getProductCode();
        if (code != null && !code.isBlank()) {
            String upper = code.toUpperCase();
            if (upper.endsWith("_DEMO")) {
                return upper.substring(0, upper.length() - "_DEMO".length());
            }
            return upper;
        }
        return product.getProductType() != null ? product.getProductType().name() : null;
    }

    private static String nullToEmptyObject(String json) {
        return json == null || json.isBlank() ? "{}" : json;
    }

    private static String nullToEmptyArray(String json) {
        return json == null || json.isBlank() ? "[]" : json;
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private static BigDecimal asBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        return new BigDecimal(value.toString());
    }

    private static Integer asInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer i) {
            return i;
        }
        if (value instanceof Number n) {
            return n.intValue();
        }
        return Integer.valueOf(value.toString());
    }
}
