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


package com.firefly.core.product.web.controllers.categories.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.category.v1.ProductCategorySubtypeService;
import com.firefly.core.product.core.services.category.v1.ProductCategorySubtypeServiceImpl;
import com.firefly.core.product.interfaces.dtos.category.v1.ProductCategorySubtypeDTO;
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

@Tag(name = "Product Subtype", description = "APIs for managing product categories in the product management platform")
@RestController
@RequestMapping("/api/v1/categories/{categoryId}/subtypes")
public class ProductCategorySubtypeController {

    @Autowired
    private ProductCategorySubtypeServiceImpl service;

    @Operation(
            summary = "List Category Subtypes",
            description = "Retrieve a paginated list of product subtypes belonging to a specific category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product subtypes",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No subtypes found for the specified category",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductCategorySubtypeDTO>>> getAllSubtypes(
            @Parameter(description = "Unique identifier of the product category", required = true)
            @PathVariable UUID categoryId,
            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getAllByCategoryId(categoryId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Category Subtype",
            description = "Create a new product subtype under a specific product category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product subtype created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductCategorySubtypeDTO.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductCategorySubtypeDTO>> createSubtype(
            @Parameter(description = "Unique identifier of the product category", required = true)
            @PathVariable UUID categoryId,
            @Parameter(description = "Data for the new product subtype", required = true,
                    schema = @Schema(implementation = ProductCategorySubtypeDTO.class))
            @RequestBody ProductCategorySubtypeDTO request
    ) {
        return service.create(categoryId, request)
                .map(savedSubtype -> ResponseEntity.status(201).body(savedSubtype));
    }

    @Operation(
            summary = "Get Category Subtype by ID",
            description = "Retrieve a specific product subtype using its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product subtype",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductCategorySubtypeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product subtype not found",
                    content = @Content)
    })
    @GetMapping(value = "/{subtypeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductCategorySubtypeDTO>> getSubtype(
            @Parameter(description = "Unique identifier of the product category", required = true)
            @PathVariable UUID categoryId,
            @Parameter(description = "Unique identifier of the product subtype", required = true)
            @PathVariable UUID subtypeId
    ) {
        return service.getById(categoryId, subtypeId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Category Subtype",
            description = "Update an existing product subtype by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product subtype updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductCategorySubtypeDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product subtype not found",
                    content = @Content)
    })
    @PutMapping(value = "/{subtypeId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductCategorySubtypeDTO>> updateSubtype(
            @Parameter(description = "Unique identifier of the product category", required = true)
            @PathVariable UUID categoryId,
            @Parameter(description = "Unique identifier of the product subtype", required = true)
            @PathVariable UUID subtypeId,
            @Parameter(description = "Updated data for the product subtype", required = true,
                    schema = @Schema(implementation = ProductCategorySubtypeDTO.class))
            @RequestBody ProductCategorySubtypeDTO request
    ) {
        return service.update(categoryId, subtypeId, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Category Subtype",
            description = "Remove an existing product subtype by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product subtype deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product subtype not found", content = @Content)
    })
    @DeleteMapping(value = "/{subtypeId}")
    public Mono<ResponseEntity<Void>> deleteSubtype(
            @Parameter(description = "Unique identifier of the product category", required = true)
            @PathVariable UUID categoryId,
            @Parameter(description = "Unique identifier of the product subtype", required = true)
            @PathVariable UUID subtypeId
    ) {
        return service.delete(categoryId, subtypeId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}