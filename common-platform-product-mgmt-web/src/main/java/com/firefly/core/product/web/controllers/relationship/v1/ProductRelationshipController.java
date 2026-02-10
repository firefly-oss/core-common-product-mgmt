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


package com.firefly.core.product.web.controllers.relationship.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.relationship.v1.ProductRelationshipServiceImpl;
import com.firefly.core.product.interfaces.dtos.relationship.v1.ProductRelationshipDTO;
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

@Tag(name = "Product Relationship", description = "APIs for managing relationships between products")
@RestController
@RequestMapping("/api/v1/products/{productId}/relationships")
public class ProductRelationshipController {

    @Autowired
    private ProductRelationshipServiceImpl service;

    @Operation(
            summary = "List Product Relationships",
            description = "Retrieve a paginated list of all product relationships associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product relationships",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No relationships found for the specified product",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductRelationshipDTO>>> getAllRelationships(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getAllRelationships(productId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Product Relationship",
            description = "Create a new relationship record for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product relationship created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductRelationshipDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product relationship data provided",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductRelationshipDTO>> createRelationship(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Data for the new product relationship record", required = true,
                    schema = @Schema(implementation = ProductRelationshipDTO.class))
            @RequestBody ProductRelationshipDTO productRelationshipDTO
    ) {
        return service.createRelationship(productId, productRelationshipDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Get Product Relationship by ID",
            description = "Retrieve a specific relationship record by its unique identifier, ensuring it matches the product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product relationship record",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductRelationshipDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product relationship not found",
                    content = @Content)
    })
    @GetMapping(value = "/{relationshipId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductRelationshipDTO>> getRelationship(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product relationship record", required = true)
            @PathVariable UUID relationshipId
    ) {
        return service.getRelationship(productId, relationshipId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Product Relationship",
            description = "Update an existing product relationship record associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product relationship updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductRelationshipDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product relationship not found",
                    content = @Content)
    })
    @PutMapping(value = "/{relationshipId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductRelationshipDTO>> updateRelationship(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product relationship record to update", required = true)
            @PathVariable UUID relationshipId,

            @Parameter(description = "Updated product relationship data", required = true,
                    schema = @Schema(implementation = ProductRelationshipDTO.class))
            @RequestBody ProductRelationshipDTO productRelationshipDTO
    ) {
        return service.updateRelationship(productId, relationshipId, productRelationshipDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Product Relationship",
            description = "Remove an existing product relationship record by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product relationship deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product relationship not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{relationshipId}")
    public Mono<ResponseEntity<Void>> deleteRelationship(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product relationship record to delete", required = true)
            @PathVariable UUID relationshipId
    ) {
        return service.deleteRelationship(productId, relationshipId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}