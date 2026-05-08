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
import com.firefly.core.product.interfaces.dtos.ProductPricingDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Read-only aggregator controller that exposes calculator-friendly pricing
 * parameters composed from the underlying {@code product_configuration}
 * key-value rows. All write operations (create / update / delete of pricing
 * configuration) continue to be served by
 * {@link ProductConfigurationController}.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Pricing", description =
        "Read-only aggregator that composes product configurations into pricing parameters for calculators")
public class ProductPricingAggregatorController {

    private final ProductPricingAggregatorService service;

    @GetMapping("/{productId}/pricing")
    @Operation(
            operationId = "getProductPricing",
            summary = "Get aggregated product pricing",
            description = "Returns the aggregated pricing parameters (limits, interest-rate brackets and fees) " +
                    "of a single product, composed from its underlying key-value configuration rows."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved aggregated pricing",
                    content = @Content(schema = @Schema(implementation = ProductPricingDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product or required configuration row not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Configuration payload could not be parsed (malformed JSON in product_configuration)",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductPricingDTO>> getProductPricing(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId) {
        return service.getProductPricing(productId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/with-pricing")
    @Operation(
            operationId = "listProductsWithPricing",
            summary = "List products with pricing",
            description = "Streams every product that has a complete pricing configuration. " +
                    "Optionally filters by the derived product-type label (e.g. PERSONAL_LOAN, LEASING)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the list of products with pricing",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductPricingDTO.class)))
            )
    })
    public Flux<ProductPricingDTO> listProductsWithPricing(
            @Parameter(description = "Optional derived product-type label (e.g. PERSONAL_LOAN, LEASING)", required = false)
            @RequestParam(value = "productType", required = false) String productType) {
        return service.listProductsWithPricing(productType);
    }
}
