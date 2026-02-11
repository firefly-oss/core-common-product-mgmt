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

import com.firefly.common.core.filters.FilterRequest;
import com.firefly.common.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.ProductRelationshipService;
import com.firefly.core.product.interfaces.dtos.ProductRelationshipDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/relationships")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Relationship", description = "APIs for managing relationships between products")
public class ProductRelationshipController {

    private final ProductRelationshipService service;

    @PostMapping("/filter")
    @Operation(
            summary = "Filter product relationships",
            description = "Retrieve a paginated list of product relationships based on filtering criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered relationships",
                    content = @Content(schema = @Schema(implementation = PaginationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter request",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<PaginationResponse<ProductRelationshipDTO>>> filterRelationships(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Filter criteria for relationships", required = true)
            @Valid @RequestBody FilterRequest<ProductRelationshipDTO> filterRequest) {
        return service.filterRelationships(productId, filterRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(
            summary = "Create product relationship",
            description = "Create a new relationship record for a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product relationship successfully created",
                    content = @Content(schema = @Schema(implementation = ProductRelationshipDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid relationship data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductRelationshipDTO>> createRelationship(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Relationship data to create", required = true)
            @Valid @RequestBody ProductRelationshipDTO productRelationshipDTO) {
        return service.createRelationship(productId, productRelationshipDTO)
                .map(rel -> ResponseEntity.status(HttpStatus.CREATED).body(rel));
    }

    @GetMapping("/{relationshipId}")
    @Operation(
            summary = "Get product relationship by ID",
            description = "Retrieve a specific relationship record by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product relationship",
                    content = @Content(schema = @Schema(implementation = ProductRelationshipDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product relationship not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductRelationshipDTO>> getRelationshipById(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the relationship record", required = true)
            @PathVariable UUID relationshipId) {
        return service.getRelationshipById(productId, relationshipId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{relationshipId}")
    @Operation(
            summary = "Update product relationship",
            description = "Update an existing product relationship record"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product relationship successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductRelationshipDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid relationship data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product relationship not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductRelationshipDTO>> updateRelationship(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the relationship record", required = true)
            @PathVariable UUID relationshipId,
            @Parameter(description = "Updated relationship data", required = true)
            @Valid @RequestBody ProductRelationshipDTO productRelationshipDTO) {
        return service.updateRelationship(productId, relationshipId, productRelationshipDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{relationshipId}")
    @Operation(
            summary = "Delete product relationship",
            description = "Remove an existing product relationship record by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product relationship successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product relationship not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Void>> deleteRelationship(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the relationship record", required = true)
            @PathVariable UUID relationshipId) {
        return service.deleteRelationship(productId, relationshipId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}