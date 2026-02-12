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
import com.firefly.core.product.core.services.ProductService;
import com.firefly.core.product.interfaces.dtos.ProductDTO;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product", description = "APIs for managing core product entities in the product management platform")
public class ProductController {

    private final ProductService service;

    @PostMapping("/filter")
    @Operation(
            summary = "Filter products",
            description = "Retrieve a paginated list of products based on filtering criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered products",
                    content = @Content(schema = @Schema(implementation = PaginationResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid filter request",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<PaginationResponse<ProductDTO>>> filterProducts(
            @Parameter(description = "Filter criteria for products", required = true)
            @Valid @RequestBody FilterRequest<ProductDTO> filterRequest) {
        return service.filterProducts(filterRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(
            summary = "Create product",
            description = "Create a new product with its associated details"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product successfully created",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDTO>> createProduct(
            @Parameter(description = "Product data to create", required = true)
            @Valid @RequestBody ProductDTO productDTO) {
        return service.createProduct(productDTO)
                .map(product -> ResponseEntity.status(HttpStatus.CREATED).body(product));
    }

    @GetMapping("/{productId}")
    @Operation(
            summary = "Get product by ID",
            description = "Retrieve a specific product by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDTO>> getProductById(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId) {
        return service.getProductById(productId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{productId}")
    @Operation(
            summary = "Update product",
            description = "Update the information of an existing product by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid product data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDTO>> updateProduct(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Updated product data", required = true)
            @Valid @RequestBody ProductDTO productDTO) {
        return service.updateProduct(productId, productDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{productId}")
    @Operation(
            summary = "Delete product",
            description = "Remove an existing product by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Void>> deleteProduct(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId) {
        return service.deleteProduct(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}