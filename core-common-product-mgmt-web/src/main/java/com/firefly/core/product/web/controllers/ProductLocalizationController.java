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
import com.firefly.core.product.core.services.ProductLocalizationService;
import com.firefly.core.product.interfaces.dtos.ProductLocalizationDTO;
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
@RequestMapping("/api/v1/products/{productId}/localizations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Product Localization", description = "APIs for managing localized data (translations, region-specific info) for a product")
public class ProductLocalizationController {

    private final ProductLocalizationService service;

    @PostMapping("/filter")
    @Operation(
            summary = "Filter product localizations",
            description = "Retrieve a paginated list of localizations for a product based on filtering criteria"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved filtered localizations",
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
    public Mono<ResponseEntity<PaginationResponse<ProductLocalizationDTO>>> filterLocalizations(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Filter criteria for localizations", required = true)
            @Valid @RequestBody FilterRequest<ProductLocalizationDTO> filterRequest) {
        return service.filterLocalizations(productId, filterRequest)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    @Operation(
            summary = "Create product localization",
            description = "Create a new localization record associated with a specific product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product localization successfully created",
                    content = @Content(schema = @Schema(implementation = ProductLocalizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid localization data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductLocalizationDTO>> createLocalization(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Localization data to create", required = true)
            @Valid @RequestBody ProductLocalizationDTO localizationDTO) {
        return service.createLocalization(productId, localizationDTO)
                .map(loc -> ResponseEntity.status(HttpStatus.CREATED).body(loc));
    }

    @GetMapping("/{localizationId}")
    @Operation(
            summary = "Get product localization by ID",
            description = "Retrieve a specific localization record by its unique identifier"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product localization",
                    content = @Content(schema = @Schema(implementation = ProductLocalizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product localization not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductLocalizationDTO>> getLocalizationById(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the localization record", required = true)
            @PathVariable UUID localizationId) {
        return service.getLocalizationById(productId, localizationId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{localizationId}")
    @Operation(
            summary = "Update product localization",
            description = "Update an existing product localization record"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product localization successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductLocalizationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid localization data",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product localization not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<ProductLocalizationDTO>> updateLocalization(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the localization record", required = true)
            @PathVariable UUID localizationId,
            @Parameter(description = "Updated localization data", required = true)
            @Valid @RequestBody ProductLocalizationDTO localizationDTO) {
        return service.updateLocalization(productId, localizationId, localizationDTO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{localizationId}")
    @Operation(
            summary = "Delete product localization",
            description = "Remove an existing localization record from the product"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Product localization successfully deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product localization not found",
                    content = @Content
            )
    })
    public Mono<ResponseEntity<Void>> deleteLocalization(
            @Parameter(description = "Unique identifier of the product", required = true)
            @PathVariable UUID productId,
            @Parameter(description = "Unique identifier of the localization record", required = true)
            @PathVariable UUID localizationId) {
        return service.deleteLocalization(productId, localizationId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}