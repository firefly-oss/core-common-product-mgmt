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
import com.firefly.core.product.core.services.ProductCategoryService;
import com.firefly.core.product.interfaces.dtos.ProductCategoryDTO;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Category", description = "APIs for managing product categories in the product management platform")
public class ProductCategoryController {

    private final ProductCategoryService service;

    @PostMapping("/filter")
    @Operation(
            summary = "Filter product categories",
            description = "Retrieve a paginated list of root product categories based on filtering criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered categories",
                    content = @Content(schema = @Schema(implementation = PaginationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter request",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<PaginationResponse<ProductCategoryDTO>>> filterCategories(
            @Parameter(description = "Filter criteria for categories", required = true)
            @Valid @RequestBody FilterRequest<ProductCategoryDTO> filterRequest) {
        return service.filterRootCategories(filterRequest)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{categoryId}")
    @Operation(
            summary = "Get product category by ID",
            description = "Retrieve a specific product category by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product category",
                    content = @Content(schema = @Schema(implementation = ProductCategoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product category not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductCategoryDTO>> getCategoryById(
            @Parameter(description = "Unique identifier of the product category", required = true)
            @PathVariable UUID categoryId) {
        return service.getCategoryById(categoryId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(
            summary = "Create product category",
            description = "Create a new product category with its associated attributes"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product category successfully created",
                    content = @Content(schema = @Schema(implementation = ProductCategoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category data",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductCategoryDTO>> createCategory(
            @Parameter(description = "Category data to create", required = true)
            @Valid @RequestBody ProductCategoryDTO categoryDTO) {
        return service.createCategory(categoryDTO)
                .map(category -> ResponseEntity.status(HttpStatus.CREATED).body(category));
    }

    @PutMapping("/{categoryId}")
    @Operation(
            summary = "Update product category",
            description = "Update the information of an existing product category by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product category successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductCategoryDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product category not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductCategoryDTO>> updateCategory(
            @Parameter(description = "Unique identifier of the product category", required = true)
            @PathVariable UUID categoryId,
            @Parameter(description = "Updated category data", required = true)
            @Valid @RequestBody ProductCategoryDTO categoryDTO) {
        return service.updateCategory(categoryId, categoryDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{categoryId}")
    @Operation(
            summary = "Delete product category",
            description = "Remove an existing product category by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product category successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product category not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Void>> deleteCategory(
            @Parameter(description = "Unique identifier of the product category", required = true)
            @PathVariable UUID categoryId) {
        return service.deleteCategory(categoryId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}