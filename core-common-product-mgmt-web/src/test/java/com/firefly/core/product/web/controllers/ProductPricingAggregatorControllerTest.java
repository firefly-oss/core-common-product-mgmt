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


package com.firefly.core.product.web.controllers;

import com.firefly.core.product.core.services.ProductPricingAggregatorService;
import com.firefly.core.product.interfaces.dtos.FeeDefinitionDTO;
import com.firefly.core.product.interfaces.dtos.InterestRateBracketDTO;
import com.firefly.core.product.interfaces.dtos.ProductPricingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductPricingAggregatorControllerTest {

    private static final UUID PERSONAL_LOAN_ID =
            UUID.fromString("00000000-0000-0000-0000-00000000000a");

    @Mock
    private ProductPricingAggregatorService service;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ProductPricingAggregatorController controller =
                new ProductPricingAggregatorController(service);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void getProductPricing_returns200_andExpectedShape() {
        ProductPricingDTO dto = ProductPricingDTO.builder()
                .productId(PERSONAL_LOAN_ID)
                .productCode("PERSONAL_LOAN_DEMO")
                .productType("PERSONAL_LOAN")
                .currency("EUR")
                .minAmount(new BigDecimal("1000"))
                .maxAmount(new BigDecimal("60000"))
                .minTerm(12)
                .maxTerm(96)
                .interestRates(List.of(InterestRateBracketDTO.builder()
                        .minAmount(new BigDecimal("1000"))
                        .maxAmount(new BigDecimal("60000"))
                        .tin(new BigDecimal("7.99"))
                        .build()))
                .fees(List.of(FeeDefinitionDTO.builder()
                        .type("OPENING_FEE")
                        .percentage(BigDecimal.ZERO)
                        .fixed(BigDecimal.ZERO)
                        .build()))
                .build();

        when(service.getProductPricing(PERSONAL_LOAN_ID))
                .thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/api/v1/products/{productId}/pricing", PERSONAL_LOAN_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.productId").isEqualTo(PERSONAL_LOAN_ID.toString())
                .jsonPath("$.productCode").isEqualTo("PERSONAL_LOAN_DEMO")
                .jsonPath("$.productType").isEqualTo("PERSONAL_LOAN")
                .jsonPath("$.currency").isEqualTo("EUR")
                .jsonPath("$.minTerm").isEqualTo(12)
                .jsonPath("$.maxTerm").isEqualTo(96)
                .jsonPath("$.interestRates.length()").isEqualTo(1)
                .jsonPath("$.interestRates[0].tin").isEqualTo(7.99)
                .jsonPath("$.fees.length()").isEqualTo(1)
                .jsonPath("$.fees[0].type").isEqualTo("OPENING_FEE");
    }

    @Test
    void getProductPricing_unknownProduct_returns500FromRuntimeException() {
        UUID unknown = UUID.fromString("00000000-0000-0000-0000-0000000000ff");
        when(service.getProductPricing(unknown))
                .thenReturn(Mono.error(new RuntimeException("Product not found with ID: " + unknown)));

        // Without a custom @ControllerAdvice in this slice the error surfaces
        // as a 5xx; the contract for callers in production maps it to 404 via
        // the global exception handler. The point of this test is to assert
        // the controller propagates the error (does NOT swallow it as 200).
        webTestClient.get()
                .uri("/api/v1/products/{productId}/pricing", unknown)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void listProductsWithPricing_returns200_andStreamsResults() {
        ProductPricingDTO dto = ProductPricingDTO.builder()
                .productId(PERSONAL_LOAN_ID)
                .productCode("PERSONAL_LOAN_DEMO")
                .productType("PERSONAL_LOAN")
                .currency("EUR")
                .minAmount(new BigDecimal("1000"))
                .maxAmount(new BigDecimal("60000"))
                .minTerm(12)
                .maxTerm(96)
                .interestRates(List.of())
                .fees(List.of())
                .build();

        when(service.listProductsWithPricing(any()))
                .thenReturn(reactor.core.publisher.Flux.just(dto));

        webTestClient.get()
                .uri(builder -> builder.path("/api/v1/products/with-pricing")
                        .queryParam("productType", "PERSONAL_LOAN")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].productCode").isEqualTo("PERSONAL_LOAN_DEMO");
    }
}
