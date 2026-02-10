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


package com.firefly.core.product.web.controllers.documentation.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.services.documentation.v1.ProductDocumentationRequirementService;
import com.firefly.core.product.interfaces.dtos.documentation.v1.ProductDocumentationRequirementDTO;
import com.firefly.core.product.interfaces.enums.documentation.v1.ContractingDocTypeEnum;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Controller for managing product documentation requirements during the contracting/opening phase.
 */
@Tag(name = "Product Documentation Requirements", description = "APIs for managing documentation requirements for products during the contracting/opening phase")
@RestController
@RequestMapping("/api/v1/products/{productId}/documentation-requirements")
public class ProductDocumentationRequirementController {

    @Autowired
    private ProductDocumentationRequirementService service;

    @Operation(
            summary = "List Product Documentation Requirements",
            description = "Retrieve a paginated list of all documentation requirements for a given product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved product documentation requirements list",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No documentation requirements found for the specified product",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<ProductDocumentationRequirementDTO>>> getAllDocumentationRequirements(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getAllDocumentationRequirements(productId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Product Documentation Requirement",
            description = "Create a new documentation requirement for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product documentation requirement created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid product documentation requirement data provided",
                    content = @Content)
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductDocumentationRequirementDTO>> createDocumentationRequirement(
            @Parameter(description = "Unique identifier of the product to associate this documentation requirement with", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Data for the new product documentation requirement", required = true,
                    schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))
            @RequestBody ProductDocumentationRequirementDTO requirementDTO
    ) {
        return service.createDocumentationRequirement(productId, requirementDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(
            summary = "Get Product Documentation Requirement by ID",
            description = "Retrieve a specific documentation requirement by its unique identifier, ensuring it belongs to the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product documentation requirement",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product documentation requirement not found",
                    content = @Content)
    })
    @GetMapping(value = "/{requirementId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductDocumentationRequirementDTO>> getDocumentationRequirement(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the documentation requirement", required = true)
            @PathVariable UUID requirementId
    ) {
        return service.getDocumentationRequirement(productId, requirementId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get Product Documentation Requirement by Type",
            description = "Retrieve a specific documentation requirement by its document type, ensuring it belongs to the specified product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the product documentation requirement",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product documentation requirement not found for the specified type",
                    content = @Content)
    })
    @GetMapping(value = "/by-type/{docType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductDocumentationRequirementDTO>> getDocumentationRequirementByType(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Type of document", required = true)
            @PathVariable ContractingDocTypeEnum docType
    ) {
        return service.getDocumentationRequirementByType(productId, docType)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get Mandatory Product Documentation Requirements",
            description = "Retrieve all mandatory documentation requirements for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the mandatory product documentation requirements",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))),
            @ApiResponse(responseCode = "404", description = "No mandatory documentation requirements found for the specified product",
                    content = @Content)
    })
    @GetMapping(value = "/mandatory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Flux<ProductDocumentationRequirementDTO>>> getMandatoryDocumentationRequirements(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId
    ) {
        Flux<ProductDocumentationRequirementDTO> requirements = service.getMandatoryDocumentationRequirements(productId);
        return Mono.just(ResponseEntity.ok(requirements));
    }

    @Operation(
            summary = "Update Product Documentation Requirement",
            description = "Update an existing documentation requirement for a specific product."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product documentation requirement updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product documentation requirement not found",
                    content = @Content)
    })
    @PutMapping(value = "/{requirementId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ProductDocumentationRequirementDTO>> updateDocumentationRequirement(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the documentation requirement to be updated", required = true)
            @PathVariable UUID requirementId,

            @Parameter(description = "Updated product documentation requirement data", required = true,
                    schema = @Schema(implementation = ProductDocumentationRequirementDTO.class))
            @RequestBody ProductDocumentationRequirementDTO requirementDTO
    ) {
        return service.updateDocumentationRequirement(productId, requirementId, requirementDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Product Documentation Requirement",
            description = "Remove an existing product documentation requirement by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product documentation requirement deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Product documentation requirement not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{requirementId}")
    public Mono<ResponseEntity<Void>> deleteDocumentationRequirement(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,

            @Parameter(description = "Unique identifier of the documentation requirement to be deleted", required = true)
            @PathVariable UUID requirementId
    ) {
        return service.deleteDocumentationRequirement(productId, requirementId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}