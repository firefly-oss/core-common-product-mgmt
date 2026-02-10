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
import com.firefly.core.product.core.services.fee.v1.FeeComponentServiceImpl;
import com.firefly.core.product.interfaces.dtos.fee.v1.FeeComponentDTO;
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

@Tag(name = "Product Fee Component", description = "APIs for managing components under a specific fee structure")
@RestController
@RequestMapping("/api/v1/fee-structures/{feeStructureId}/components")
public class FeeComponentController {

    @Autowired
    private FeeComponentServiceImpl service;

    @Operation(
            summary = "List Fee Components by Fee Structure",
            description = "Retrieve a paginated list of fee components associated with a specific fee structure."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved fee components",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No fee components found for the specified structure",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<FeeComponentDTO>>> getByFeeStructureId(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service.getByFeeStructureId(feeStructureId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Fee Component",
            description = "Create a new fee component, which can be attached to a fee structure."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fee component created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FeeComponentDTO.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<FeeComponentDTO>> createFeeComponent(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Data for the new fee component", required = true,
                    schema = @Schema(implementation = FeeComponentDTO.class))
            @RequestBody FeeComponentDTO feeComponentDTO
    ) {
        return service.createFeeComponent(feeStructureId, feeComponentDTO)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Get Fee Component by ID",
            description = "Retrieve a specific fee component by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the fee component",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FeeComponentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Fee component not found",
                    content = @Content)
    })
    @GetMapping(value = "/{componentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<FeeComponentDTO>> getFeeComponent(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Unique identifier of the fee component", required = true)
            @PathVariable UUID componentId
    ) {
        return service.getFeeComponent(feeStructureId, componentId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Fee Component",
            description = "Update an existing fee component by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fee component updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FeeComponentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Fee component not found",
                    content = @Content)
    })
    @PutMapping(value = "/{componentId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<FeeComponentDTO>> updateFeeComponent(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Unique identifier of the fee component to update", required = true)
            @PathVariable UUID componentId,

            @Parameter(description = "Updated data for the fee component", required = true,
                    schema = @Schema(implementation = FeeComponentDTO.class))
            @RequestBody FeeComponentDTO feeComponentDTO
    ) {
        return service.updateFeeComponent(feeStructureId, componentId, feeComponentDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Fee Component",
            description = "Remove an existing fee component by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fee component deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Fee component not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{componentId}")
    public Mono<ResponseEntity<Void>> deleteFeeComponent(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Unique identifier of the fee component to delete", required = true)
            @PathVariable UUID componentId
    ) {
        return service.deleteFeeComponent(feeStructureId, componentId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}