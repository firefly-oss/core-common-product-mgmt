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


package com.firefly.core.product.web.controllers.lifecycle.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.lifecycle.v1.ProductLifecycleServiceImpl;
import com.firefly.core.product.interfaces.dtos.lifecycle.v1.ProductLifecycleDTO;
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

@Tag(name = "Product Lifecycle", description = "APIs for managing lifecycle stages or events associated with a specific product")
@RestController
@RequestMapping("/api/v1/products/{productId}/lifecycle")
public class ProductLifecycleController {

    @Autowired
    private ProductLifecycleServiceImpl service;

    @Operation(
            summary = "List Product Lifecycles",
            description = "Retrieve a paginated list of all lifecycle entries associated with a given product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved lifecycles",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No lifecycle entries found for the specified product",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductLifecycleDTO>>> getProductLifecycles(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getProductLifecycles(productId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Product Lifecycle",
            description = "Create a new lifecycle entry for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product lifecycle created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductLifecycleDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid lifecycle data provided",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductLifecycleDTO>> createProductLifecycle(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Data for the new lifecycle entry", required = true,
                    schema = @Schema(implementation = ProductLifecycleDTO.class))
            @RequestBody ProductLifecycleDTO request
    ) {
        return service.createProductLifecycle(productId, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Get Product Lifecycle by ID",
            description = "Retrieve a specific lifecycle entry using its unique identifier, ensuring it belongs to the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product lifecycle entry",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductLifecycleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Lifecycle entry not found",
                    content = @Content)
    })
    @GetMapping(value = "/{lifecycleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductLifecycleDTO>> getProductLifecycle(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the lifecycle entry", required = true)
            @PathVariable UUID lifecycleId
    ) {
        return service.getProductLifecycle(productId, lifecycleId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Product Lifecycle",
            description = "Update an existing lifecycle entry associated with the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product lifecycle entry updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductLifecycleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Lifecycle entry not found",
                    content = @Content)
    })
    @PutMapping(value = "/{lifecycleId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductLifecycleDTO>> updateProductLifecycle(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the lifecycle entry to update", required = true)
            @PathVariable UUID lifecycleId,

            @Parameter(description = "Updated product lifecycle data", required = true,
                    schema = @Schema(implementation = ProductLifecycleDTO.class))
            @RequestBody ProductLifecycleDTO request
    ) {
        return service.updateProductLifecycle(productId, lifecycleId, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Product Lifecycle",
            description = "Remove an existing lifecycle entry from a product by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Lifecycle entry deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lifecycle entry not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{lifecycleId}")
    public Mono<ResponseEntity<Void>> deleteProductLifecycle(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the lifecycle entry to delete", required = true)
            @PathVariable UUID lifecycleId
    ) {
        return service.deleteProductLifecycle(productId, lifecycleId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}