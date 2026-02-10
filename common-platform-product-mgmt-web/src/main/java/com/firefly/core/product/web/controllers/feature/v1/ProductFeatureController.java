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


package com.firefly.core.product.web.controllers.feature.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.feature.v1.ProductFeatureServiceImpl;
import com.firefly.core.product.interfaces.dtos.feature.v1.ProductFeatureDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Tag(name = "Product Feature", description = "APIs for managing features associated with a specific product")
@RestController
@RequestMapping("/api/v1/products/{productId}/features")
public class ProductFeatureController {

    @Autowired
    private ProductFeatureServiceImpl service;

    @Operation(
            summary = "List Product Features",
            description = "Retrieve a paginated list of all features associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of product features",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No features found for the specified product",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductFeatureDTO>>> getAllFeatures(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getAllFeatures(productId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Product Feature",
            description = "Create a new feature linked to the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product feature created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductFeatureDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid feature data provided",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductFeatureDTO>> createFeature(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Data for the new product feature", required = true,
                    schema = @Schema(implementation = ProductFeatureDTO.class))
            @RequestBody ProductFeatureDTO featureDTO
    ) {
        return service.createFeature(productId, featureDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Get Product Feature by ID",
            description = "Retrieve a specific product feature using its unique identifier, ensuring it matches the product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product feature",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductFeatureDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product feature not found",
                    content = @Content)
    })
    @GetMapping(value = "/{featureId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductFeatureDTO>> getFeature(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the feature", required = true)
            @PathVariable UUID featureId
    ) {
        return service.getFeature(productId, featureId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Product Feature",
            description = "Update an existing feature associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product feature updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductFeatureDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product feature not found",
                    content = @Content)
    })
    @PutMapping(value = "/{featureId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductFeatureDTO>> updateFeature(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product feature to update", required = true)
            @PathVariable UUID featureId,

            @Parameter(description = "Updated product feature data", required = true,
                    schema = @Schema(implementation = ProductFeatureDTO.class))
            @RequestBody ProductFeatureDTO featureDTO
    ) {
        return service.updateFeature(productId, featureId, featureDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Product Feature",
            description = "Remove an existing feature from the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product feature deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product feature not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{featureId}")
    public Mono<ResponseEntity<Void>> deleteFeature(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product feature to delete", required = true)
            @PathVariable UUID featureId
    ) {
        return service.deleteFeature(productId, featureId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}