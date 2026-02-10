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


package com.firefly.core.product.web.controllers.pricing.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.pricing.v1.ProductPricingServiceImpl;
import com.firefly.core.product.interfaces.dtos.pricing.v1.ProductPricingDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Tag(name = "Product Pricing", description = "APIs for managing pricing records associated with a specific product")
@RestController
@RequestMapping("/api/v1/products/{productId}/pricings")
public class ProductPricingController{

    @Autowired
    private ProductPricingServiceImpl service;

    @Operation(
            summary = "List Product Pricings",
            description = "Retrieve a paginated list of all pricing records associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product pricings",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No pricing records found for the specified product",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductPricingDTO>>> getAllPricings(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getAllPricings(productId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Product Pricing",
            description = "Create a new pricing record and associate it with a product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product pricing created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductPricingDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product pricing data provided",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductPricingDTO>> createPricing(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Data for the new product pricing", required = true,
                    schema = @Schema(implementation = ProductPricingDTO.class))
            @RequestBody ProductPricingDTO productPricingDTO
    ) {
        return service.createPricing(productId, productPricingDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Get Product Pricing by ID",
            description = "Retrieve a specific product pricing record using its unique identifier, ensuring it matches the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product pricing record",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductPricingDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product pricing not found",
                    content = @Content)
    })
    @GetMapping(value = "/{pricingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductPricingDTO>> getPricing(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product pricing record", required = true)
            @PathVariable UUID pricingId
    ) {
        return service.getPricing(productId, pricingId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Product Pricing",
            description = "Update an existing pricing record associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product pricing updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductPricingDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product pricing not found",
                    content = @Content)
    })
    @PutMapping(value = "/{pricingId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductPricingDTO>> updatePricing(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product pricing record to update", required = true)
            @PathVariable UUID pricingId,

            @Parameter(description = "Updated product pricing data", required = true,
                    schema = @Schema(implementation = ProductPricingDTO.class))
            @RequestBody ProductPricingDTO productPricingDTO
    ) {
        return service.updatePricing(productId, pricingId, productPricingDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Product Pricing",
            description = "Remove an existing pricing record from a product by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product pricing deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product pricing not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{pricingId}")
    public Mono<ResponseEntity<Void>> deletePricing(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product pricing record to delete", required = true)
            @PathVariable UUID pricingId
    ) {
        return service.deletePricing(productId, pricingId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}