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

import org.fireflyframework.core.filters.FilterRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.ProductVersionService;
import com.firefly.core.product.interfaces.dtos.ProductVersionDTO;
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
@RequestMapping("/api/v1/products/{productId}/versions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Version", description = "APIs for managing different versions of a product")
public class ProductVersionController {

    private final ProductVersionService service;

    @PostMapping("/filter")
    @Operation(
            summary = "Filter product versions",
            description = "Retrieve a paginated list of product versions based on filtering criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered versions",
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
    public Mono<ResponseEntity<PaginationResponse<ProductVersionDTO>>> filterProductVersions(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Filter criteria for versions", required = true)
            @Valid @RequestBody FilterRequest<ProductVersionDTO> filterRequest) {
        return service.filterProductVersions(productId, filterRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(
            summary = "Create product version",
            description = "Create a new product version for a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product version successfully created",
                    content = @Content(schema = @Schema(implementation = ProductVersionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid version data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductVersionDTO>> createProductVersion(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Version data to create", required = true)
            @Valid @RequestBody ProductVersionDTO productVersionDTO) {
        return service.createProductVersion(productId, productVersionDTO)
                .map(ver -> ResponseEntity.status(HttpStatus.CREATED).body(ver));
    }

    @GetMapping("/{versionId}")
    @Operation(
            summary = "Get product version by ID",
            description = "Retrieve a specific product version by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product version",
                    content = @Content(schema = @Schema(implementation = ProductVersionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product version not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductVersionDTO>> getProductVersionById(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the product version", required = true)
            @PathVariable UUID versionId) {
        return service.getProductVersionById(productId, versionId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{versionId}")
    @Operation(
            summary = "Update product version",
            description = "Update an existing product version record"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product version successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductVersionDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid version data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product version not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductVersionDTO>> updateProductVersion(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the product version", required = true)
            @PathVariable UUID versionId,
            @Parameter(description = "Updated version data", required = true)
            @Valid @RequestBody ProductVersionDTO productVersionDTO) {
        return service.updateProductVersion(productId, versionId, productVersionDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{versionId}")
    @Operation(
            summary = "Delete product version",
            description = "Remove an existing product version record by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product version successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product version not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Void>> deleteProductVersion(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the product version", required = true)
            @PathVariable UUID versionId) {
        return service.deleteProductVersion(productId, versionId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}