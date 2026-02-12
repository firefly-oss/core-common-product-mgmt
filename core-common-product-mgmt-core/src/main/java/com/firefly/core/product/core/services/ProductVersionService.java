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


package com.firefly.core.product.core.services;

import org.fireflyframework.core.filters.FilterRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.ProductVersionDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for managing product versions.
 */
public interface ProductVersionService {

    /**
     * Filters the product versions based on the given criteria for a specific product.
     *
     * @param productId the unique identifier of the product owning the versions
     * @param filterRequest the request object containing filtering criteria for ProductVersionDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of product versions
     */
    Mono<PaginationResponse<ProductVersionDTO>> filterProductVersions(UUID productId, FilterRequest<ProductVersionDTO> filterRequest);

    /**
     * Creates a new product version based on the provided information for a specific product.
     *
     * @param productId the unique identifier of the product that will own the version
     * @param productVersionDTO the DTO object containing details of the product version to be created
     * @return a Mono that emits the created ProductVersionDTO object
     */
    Mono<ProductVersionDTO> createProductVersion(UUID productId, ProductVersionDTO productVersionDTO);

    /**
     * Retrieves a product version by its unique identifier, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the version
     * @param versionId the unique identifier of the product version to retrieve
     * @return a Mono emitting the {@link ProductVersionDTO} representing the product version if found,
     *         or an error if the version does not exist or doesn't belong to the product
     */
    Mono<ProductVersionDTO> getProductVersionById(UUID productId, UUID versionId);

    /**
     * Updates an existing product version with updated information, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the version
     * @param versionId the unique identifier of the product version to be updated
     * @param productVersionDTO the data transfer object containing the updated details of the product version
     * @return a reactive Mono containing the updated ProductVersionDTO
     */
    Mono<ProductVersionDTO> updateProductVersion(UUID productId, UUID versionId, ProductVersionDTO productVersionDTO);

    /**
     * Deletes a product version identified by its unique ID, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the version
     * @param versionId the unique identifier of the product version to be deleted
     * @return a Mono that completes when the product version is successfully deleted or errors if the deletion fails
     */
    Mono<Void> deleteProductVersion(UUID productId, UUID versionId);
}