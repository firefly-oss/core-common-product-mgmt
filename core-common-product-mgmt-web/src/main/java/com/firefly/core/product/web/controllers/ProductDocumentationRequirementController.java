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
import com.firefly.core.product.core.services.ProductDocumentationRequirementService;
import com.firefly.core.product.interfaces.dtos.ProductDocumentationRequirementDTO;
import com.firefly.core.product.interfaces.enums.ContractingDocTypeEnum;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products/{productId}/documentation-requirements")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Documentation Requirements", description = "APIs for managing documentation requirements for products during the contracting/opening phase")
public class ProductDocumentationRequirementController {

    private final ProductDocumentationRequirementService service;

    @PostMapping("/filter")
    @Operation(
            summary = "Filter product documentation requirements",
            description = "Retrieve a paginated list of documentation requirements for a product based on filtering criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered documentation requirements",
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
    public Mono<ResponseEntity<PaginationResponse<ProductDocumentationRequirementDTO>>> filterDocumentationRequirements(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Filter criteria for documentation requirements", required = true)
            @Valid @RequestBody FilterRequest<ProductDocumentationRequirementDTO> filterRequest) {
        return service.filterDocumentationRequirements(productId, filterRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(
            summary = "Create product documentation requirement",
            description = "Create a new documentation requirement for a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product documentation requirement successfully created",
                    content = @Content(schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid documentation requirement data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDocumentationRequirementDTO>> createDocumentationRequirement(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Documentation requirement data to create", required = true)
            @Valid @RequestBody ProductDocumentationRequirementDTO requirementDTO) {
        return service.createDocumentationRequirement(productId, requirementDTO)
                .map(req -> ResponseEntity.status(HttpStatus.CREATED).body(req));
    }

    @GetMapping("/{requirementId}")
    @Operation(
            summary = "Get product documentation requirement by ID",
            description = "Retrieve a specific documentation requirement by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product documentation requirement",
                    content = @Content(schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product documentation requirement not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDocumentationRequirementDTO>> getDocumentationRequirementById(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the documentation requirement", required = true)
            @PathVariable UUID requirementId) {
        return service.getDocumentationRequirementById(productId, requirementId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type/{docType}")
    @Operation(
            summary = "Get product documentation requirement by type",
            description = "Retrieve a specific documentation requirement by its document type"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product documentation requirement",
                    content = @Content(schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product documentation requirement not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDocumentationRequirementDTO>> getDocumentationRequirementByType(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Type of document", required = true)
            @PathVariable ContractingDocTypeEnum docType) {
        return service.getDocumentationRequirementByType(productId, docType)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/mandatory")
    @Operation(
            summary = "Get mandatory product documentation requirements",
            description = "Retrieve all mandatory documentation requirements for a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the mandatory documentation requirements",
                    content = @Content(schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Flux<ProductDocumentationRequirementDTO>>> getMandatoryDocumentationRequirements(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId) {
        Flux<ProductDocumentationRequirementDTO> requirements = service.filterMandatoryDocumentationRequirements(productId);
        return Mono.just(ResponseEntity.ok(requirements));
    }

    @PutMapping("/{requirementId}")
    @Operation(
            summary = "Update product documentation requirement",
            description = "Update an existing documentation requirement for a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product documentation requirement successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid documentation requirement data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product documentation requirement not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductDocumentationRequirementDTO>> updateDocumentationRequirement(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the documentation requirement", required = true)
            @PathVariable UUID requirementId,
            @Parameter(description = "Updated documentation requirement data", required = true)
            @Valid @RequestBody ProductDocumentationRequirementDTO requirementDTO) {
        return service.updateDocumentationRequirement(productId, requirementId, requirementDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{requirementId}")
    @Operation(
            summary = "Delete product documentation requirement",
            description = "Remove an existing product documentation requirement by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product documentation requirement successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product documentation requirement not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Void>> deleteDocumentationRequirement(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the documentation requirement", required = true)
            @PathVariable UUID requirementId) {
        return service.deleteDocumentationRequirement(productId, requirementId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}