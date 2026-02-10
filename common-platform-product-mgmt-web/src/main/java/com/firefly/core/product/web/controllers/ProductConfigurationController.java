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
import com.firefly.core.product.core.services.ProductConfigurationService;
import com.firefly.core.product.interfaces.dtos.ProductConfigurationDTO;
import com.firefly.core.product.interfaces.enums.ProductConfigTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@RequestMapping("/api/v1/products/{productId}/configurations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Configuration", description = "APIs for managing key-value configuration data for products")
public class ProductConfigurationController {

    private final ProductConfigurationService service;

    @PostMapping("/filter")
    @Operation(
            summary = "Filter product configurations",
            description = "Retrieve a paginated list of configurations for a product based on filtering criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered configurations",
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
    public Mono<ResponseEntity<PaginationResponse<ProductConfigurationDTO>>> filterConfigurations(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Filter criteria for configurations", required = true)
            @Valid @RequestBody FilterRequest<ProductConfigurationDTO> filterRequest) {
        return service.filterConfigurations(productId, filterRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(
            summary = "Create product configuration",
            description = "Create a new configuration for a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product configuration successfully created",
                    content = @Content(schema = @Schema(implementation = ProductConfigurationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid configuration data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductConfigurationDTO>> createConfiguration(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Configuration data to create", required = true)
            @Valid @RequestBody ProductConfigurationDTO configDTO) {
        return service.createConfiguration(productId, configDTO)
                .map(config -> ResponseEntity.status(HttpStatus.CREATED).body(config));
    }

    @GetMapping("/{configId}")
    @Operation(
            summary = "Get product configuration by ID",
            description = "Retrieve a specific product configuration using its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product configuration",
                    content = @Content(schema = @Schema(implementation = ProductConfigurationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product configuration not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductConfigurationDTO>> getConfigurationById(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the product configuration", required = true)
            @PathVariable UUID configId) {
        return service.getConfigurationById(productId, configId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-key/{configKey}")
    @Operation(
            summary = "Get product configuration by key",
            description = "Retrieve a specific product configuration using its key"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product configuration",
                    content = @Content(schema = @Schema(implementation = ProductConfigurationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product configuration not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductConfigurationDTO>> getConfigurationByKey(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Configuration key", required = true)
            @PathVariable String configKey) {
        return service.getConfigurationByKey(productId, configKey)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-type/{configType}")
    @Operation(
            summary = "Get product configurations by type",
            description = "Retrieve all configurations of a specific type for the specified product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product configurations",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductConfigurationDTO.class)))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Flux<ProductConfigurationDTO> getConfigurationsByType(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Configuration type", required = true)
            @PathVariable ProductConfigTypeEnum configType) {
        return service.getConfigurationsByType(productId, configType);
    }

    @PutMapping("/{configId}")
    @Operation(
            summary = "Update product configuration",
            description = "Update an existing configuration record associated with the specified product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product configuration successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductConfigurationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid configuration data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product configuration not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductConfigurationDTO>> updateConfiguration(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the product configuration", required = true)
            @PathVariable UUID configId,
            @Parameter(description = "Updated configuration data", required = true)
            @Valid @RequestBody ProductConfigurationDTO configDTO) {
        return service.updateConfiguration(productId, configId, configDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{configId}")
    @Operation(
            summary = "Delete product configuration",
            description = "Remove an existing product configuration record by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product configuration successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product configuration not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Void>> deleteConfiguration(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the product configuration", required = true)
            @PathVariable UUID configId) {
        return service.deleteConfiguration(productId, configId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}

