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
import com.firefly.core.product.core.services.ProductDocumentationService;
import com.firefly.core.product.interfaces.dtos.ProductDocumentationDTO;
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
@RequestMapping("/api/v1/products/{productId}/documentation")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Documentation", description = "APIs for managing documentation (manuals, guides, etc.) associated with a specific product")
public class ProductDocumentationController {

    private final ProductDocumentationService service;

    @PostMapping("/filter")
    @Operation(
            summary = "Filter product documentation",
            description = "Retrieve a paginated list of documentation items for a product based on filtering criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered documentation",
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
    public Mono<ResponseEntity<PaginationResponse<ProductDocumentationDTO>>> filterDocumentation(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Filter criteria for documentation", required = true)
            @Valid @RequestBody FilterRequest<ProductDocumentationDTO> filterRequest) {
        return service.filterDocumentations(productId, filterRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(
            summary = "Create product documentation",
            description = "Create a new documentation item for a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product documentation successfully created",
                    content = @Content(schema = @Schema(implementation = ProductDocumentationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid documentation data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDocumentationDTO>> createDocumentation(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Documentation data to create", required = true)
            @Valid @RequestBody ProductDocumentationDTO documentationDTO) {
        return service.createDocumentation(productId, documentationDTO)
                .map(doc -> ResponseEntity.status(HttpStatus.CREATED).body(doc));
    }

    @GetMapping("/{docId}")
    @Operation(
            summary = "Get product documentation by ID",
            description = "Retrieve a specific documentation item by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product documentation",
                    content = @Content(schema = @Schema(implementation = ProductDocumentationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product documentation not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDocumentationDTO>> getDocumentationById(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the documentation item", required = true)
            @PathVariable UUID docId) {
        return service.getDocumentationById(productId, docId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{docId}")
    @Operation(
            summary = "Update product documentation",
            description = "Update an existing documentation item for a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product documentation successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductDocumentationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid documentation data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product documentation not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDocumentationDTO>> updateDocumentation(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the documentation item", required = true)
            @PathVariable UUID docId,
            @Parameter(description = "Updated documentation data", required = true)
            @Valid @RequestBody ProductDocumentationDTO documentationDTO) {
        return service.updateDocumentation(productId, docId, documentationDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{docId}")
    @Operation(
            summary = "Delete product documentation",
            description = "Remove an existing product documentation item by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product documentation successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product documentation not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Void>> deleteDocumentation(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the documentation item", required = true)
            @PathVariable UUID docId) {
        return service.deleteDocumentation(productId, docId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}