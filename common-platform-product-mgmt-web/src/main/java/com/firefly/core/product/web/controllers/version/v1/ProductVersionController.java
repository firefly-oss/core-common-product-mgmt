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


package com.firefly.core.product.web.controllers.version.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.version.v1.ProductVersionServiceImpl;
import com.firefly.core.product.interfaces.dtos.version.v1.ProductVersionDTO;
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

@Tag(name = "Product Version", description = "APIs for managing different versions of a product")
@RestController
@RequestMapping("/api/v1/products/{productId}/versions")
public class ProductVersionController {

    @Autowired
    private ProductVersionServiceImpl service;

    @Operation(
            summary = "List Product Versions",
            description = "Retrieve a paginated list of all versions associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product versions",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No product versions found for the specified product",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductVersionDTO>>> getAllProductVersions(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getAllProductVersions(productId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Product Version",
            description = "Create a new product version for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product version created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductVersionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product version data provided",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductVersionDTO>> createProductVersion(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Data for the new product version", required = true,
                    schema = @Schema(implementation = ProductVersionDTO.class))
            @RequestBody ProductVersionDTO productVersionDTO
    ) {
        return service.createProductVersion(productId, productVersionDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Get Product Version by ID",
            description = "Retrieve a specific product version using its unique identifier, ensuring it belongs to the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product version record",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductVersionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product version not found",
                    content = @Content)
    })
    @GetMapping(value = "/{versionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductVersionDTO>> getProductVersion(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product version", required = true)
            @PathVariable UUID versionId
    ) {
        return service.getProductVersion(productId, versionId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Product Version",
            description = "Update an existing version record associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product version updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductVersionDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product version not found",
                    content = @Content)
    })
    @PutMapping(value = "/{versionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductVersionDTO>> updateProductVersion(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product version record to update", required = true)
            @PathVariable UUID versionId,

            @Parameter(description = "Updated product version data", required = true,
                    schema = @Schema(implementation = ProductVersionDTO.class))
            @RequestBody ProductVersionDTO productVersionDTO
    ) {
        return service.updateProductVersion(productId, versionId, productVersionDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Product Version",
            description = "Remove an existing product version record by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product version deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product version not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{versionId}")
    public Mono<ResponseEntity<Void>> deleteProductVersion(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product version to delete", required = true)
            @PathVariable UUID versionId
    ) {
        return service.deleteProductVersion(productId, versionId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}