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
import com.firefly.core.product.core.services.fee.v1.FeeApplicationRuleServiceImpl;
import com.firefly.core.product.interfaces.dtos.fee.v1.FeeApplicationRuleDTO;
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

@Tag(name = "Fee Application Rule", description = "APIs for managing fee application rules in a specific fee structure component")
@RestController
@RequestMapping("/api/v1/fee-structures/{feeStructureId}/components/{componentId}/rules")
public class FeeApplicationRuleController {

    @Autowired
    private FeeApplicationRuleServiceImpl service;


    @Operation(
            summary = "List Fee Application Rules",
            description = "Retrieve a paginated list of fee application rules associated with a specific fee component."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of fee application rules",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No rules found for the specified component",
                    content = @Content)
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaginationResponse<FeeApplicationRuleDTO>>> getRulesByComponentId(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Unique identifier of the fee component", required = true)
            @PathVariable UUID componentId,

            @ParameterObject
            @ModelAttribute PaginationRequest paginationRequest
    ) {
        return service
                .getRulesByComponentId(feeStructureId, componentId, paginationRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create Fee Application Rule",
            description = "Create a new fee application rule for the specified fee structure component."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fee application rule created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FeeApplicationRuleDTO.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<FeeApplicationRuleDTO>> createRule(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Unique identifier of the fee component", required = true)
            @PathVariable UUID componentId,

            @Parameter(description = "Data for the new fee application rule", required = true,
                    schema = @Schema(implementation = FeeApplicationRuleDTO.class))
            @RequestBody FeeApplicationRuleDTO feeApplicationRuleDTO
    ) {
        return service
                .createRule(feeStructureId, componentId, feeApplicationRuleDTO)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Get Fee Application Rule by ID",
            description = "Retrieve a specific fee application rule using its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the fee application rule",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FeeApplicationRuleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Fee application rule not found",
                    content = @Content)
    })
    @GetMapping(value = "/{ruleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<FeeApplicationRuleDTO>> getRule(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Unique identifier of the fee component", required = true)
            @PathVariable UUID componentId,

            @Parameter(description = "Unique identifier of the fee application rule", required = true)
            @PathVariable UUID ruleId
    ) {
        return service
                .getRule(feeStructureId, componentId, ruleId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update Fee Application Rule",
            description = "Update an existing fee application rule by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fee application rule updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FeeApplicationRuleDTO.class))),
            @ApiResponse(responseCode = "404", description = "Fee application rule not found",
                    content = @Content)
    })
    @PutMapping(value = "/{ruleId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<FeeApplicationRuleDTO>> updateRule(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Unique identifier of the fee component", required = true)
            @PathVariable UUID componentId,

            @Parameter(description = "Unique identifier of the fee application rule to update", required = true)
            @PathVariable UUID ruleId,

            @Parameter(description = "Updated data for the fee application rule", required = true,
                    schema = @Schema(implementation = FeeApplicationRuleDTO.class))
            @RequestBody FeeApplicationRuleDTO feeApplicationRuleDTO
    ) {
        return service
                .updateRule(feeStructureId, componentId, ruleId, feeApplicationRuleDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete Fee Application Rule",
            description = "Remove an existing fee application rule by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fee application rule deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Fee application rule not found",
                    content = @Content)
    })
    @DeleteMapping(value = "/{ruleId}")
    public Mono<ResponseEntity<Void>> deleteRule(
            @Parameter(description = "Unique identifier of the fee structure", required = true)
            @PathVariable UUID feeStructureId,

            @Parameter(description = "Unique identifier of the fee component", required = true)
            @PathVariable UUID componentId,

            @Parameter(description = "Unique identifier of the fee application rule to delete", required = true)
            @PathVariable UUID ruleId
    ) {
        return service
                .deleteRule(feeStructureId, componentId, ruleId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}