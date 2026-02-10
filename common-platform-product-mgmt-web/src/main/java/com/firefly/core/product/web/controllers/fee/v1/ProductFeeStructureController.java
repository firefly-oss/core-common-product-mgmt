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


package com.firefly.core.product.web.controllers.fee.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.fee.v1.ProductFeeStructureServiceImpl;
import com.firefly.core.product.interfaces.dtos.fee.v1.ProductFeeStructureDTO;
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

@Tag(name = "Product Fee Structure", description = "APIs for managing fee structures associated with a specific product")
@RestController
@RequestMapping("/api/v1/products/{productId}/fee-structures")
public class ProductFeeStructureController {

    @Autowired
    private ProductFeeStructureServiceImpl service;


    @Operation(
            summary = "List Fee Structures by Product",
            description = "Retrieve a paginated list of fee structures associated with a given product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved fee structures",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No fee structures found for the specified product",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductFeeStructureDTO>>> getAllFeeStructures(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getAllFeeStructuresByProduct(productId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Fee Structure",
            description = "Create a new fee structure and associate it with a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product fee structure created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductFeeStructureDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid fee structure data provided",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductFeeStructureDTO>> createFeeStructure(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Data for the new product fee structure", required = true,
                    schema = @Schema(implementation = ProductFeeStructureDTO.class))
            @RequestBody ProductFeeStructureDTO request
    ) {
        return service.createFeeStructure(productId, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Get Fee Structure by ID",
            description = "Retrieve a specific fee structure by its unique identifier, confirming it belongs to the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product fee structure",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductFeeStructureDTO.class))),
            @ApiResponse(responseCode = "404", description = "Fee structure not found", content = @Content)
    })
    @GetMapping(value = "/{productFeeStructId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductFeeStructureDTO>> getFeeStructure(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product fee structure", required = true)
            @PathVariable UUID productFeeStructId
    ) {
        return service.getFeeStructureById(productId, productFeeStructId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Fee Structure",
            description = "Update an existing fee structure for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fee structure updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductFeeStructureDTO.class))),
            @ApiResponse(responseCode = "404", description = "Fee structure not found",
                    content = @Content)
    })
    @PutMapping(value = "/{productFeeStructId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductFeeStructureDTO>> updateFeeStructure(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the product fee structure to update", required = true)
            @PathVariable UUID productFeeStructId,

            @Parameter(description = "Updated product fee structure data", required = true,
                    schema = @Schema(implementation = ProductFeeStructureDTO.class))
            @RequestBody ProductFeeStructureDTO request
    ) {
        return service.updateFeeStructure(productId, productFeeStructId, request)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Fee Structure",
            description = "Remove an existing fee structure from a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fee structure deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Fee structure not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{productFeeStructId}")
    public Mono<ResponseEntity<Void>> deleteProductFeeStructure(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the fee structure to delete", required = true)
            @PathVariable UUID productFeeStructId
    ) {
        return service.deleteFeeStructure(productId, productFeeStructId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}