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


package com.firefly.core.product.web.controllers.core.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.documentation.v1.ProductDocumentationServiceImpl;
import com.firefly.core.product.interfaces.dtos.documentation.v1.ProductDocumentationDTO;
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

@Tag(name = "Product Documentation", description = "APIs for managing documentation (manuals, guides, etc.) associated with a specific product")
@RestController
@RequestMapping("/api/v1/products/{productId}/documentation")
public class ProductDocumentationController {

    @Autowired
    private ProductDocumentationServiceImpl service;

    @Operation(
            summary = "List Product Documentation",
            description = "Retrieve a paginated list of all documentation items linked to a given product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product documentation list",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No documentation found for the specified product",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductDocumentationDTO>>> getAllDocumentation(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getAllDocumentations(productId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Product Documentation",
            description = "Create a new documentation item for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product documentation created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDocumentationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product documentation data provided",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductDocumentationDTO>> createDocumentation(
            @Parameter(description = "Unique identifier of the product to associate this documentation with", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Data for the new product documentation", required = true,
                    schema = @Schema(implementation = ProductDocumentationDTO.class))
            @RequestBody ProductDocumentationDTO documentationDTO
    ) {
        return service.createDocumentation(productId, documentationDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Get Product Documentation by ID",
            description = "Retrieve a specific documentation item by its unique identifier, ensuring it belongs to the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product documentation",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDocumentationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product documentation not found",
                    content = @Content)
    })
    @GetMapping(value = "/{docId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductDocumentationDTO>> getDocumentation(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the documentation item", required = true)
            @PathVariable UUID docId
    ) {
        return service.getDocumentation(productId, docId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Product Documentation",
            description = "Update an existing documentation item for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product documentation updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDocumentationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product documentation not found",
                    content = @Content)
    })
    @PutMapping(value = "/{docId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductDocumentationDTO>> updateDocumentation(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the documentation to be updated", required = true)
            @PathVariable UUID docId,

            @Parameter(description = "Updated product documentation data", required = true,
                    schema = @Schema(implementation = ProductDocumentationDTO.class))
            @RequestBody ProductDocumentationDTO documentationDTO
    ) {
        return service.updateDocumentation(productId, docId, documentationDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Product Documentation",
            description = "Remove an existing product documentation item by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product documentation deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product documentation not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{docId}")
    public Mono<ResponseEntity<Void>> deleteDocumentation(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the documentation to be deleted", required = true)
            @PathVariable UUID docId
    ) {
        return service.deleteDocumentation(productId, docId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}