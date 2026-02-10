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

import com.firefly.common.core.filters.FilterRequest;
import com.firefly.common.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.ProductConfigurationDTO;
import com.firefly.core.product.interfaces.enums.ProductConfigTypeEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for managing product configurations.
 */
public interface ProductConfigurationService {

    /**
     * Filters the configurations based on the given criteria for a specific product.
     *
     * @param productId the unique identifier of the product owning the configurations
     * @param filterRequest the request object containing filtering criteria for ProductConfigurationDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of configurations
     */
    Mono<PaginationResponse<ProductConfigurationDTO>> filterConfigurations(
            UUID productId, FilterRequest<ProductConfigurationDTO> filterRequest);

    /**
     * Creates a new configuration based on the provided information for a specific product.
     *
     * @param productId the unique identifier of the product that will own the configuration
     * @param configDTO the DTO object containing details of the configuration to be created
     * @return a Mono that emits the created ProductConfigurationDTO object
     */
    Mono<ProductConfigurationDTO> createConfiguration(UUID productId, ProductConfigurationDTO configDTO);

    /**
     * Retrieves a configuration by its unique identifier, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the configuration
     * @param configId the unique identifier of the configuration to retrieve
     * @return a Mono emitting the {@link ProductConfigurationDTO} representing the configuration if found,
     *         or an error if the configuration does not exist or doesn't belong to the product
     */
    Mono<ProductConfigurationDTO> getConfigurationById(UUID productId, UUID configId);

    /**
     * Retrieves a configuration by its key for a specific product.
     *
     * @param productId the unique identifier of the product
     * @param configKey the configuration key
     * @return a Mono emitting the {@link ProductConfigurationDTO} representing the configuration if found
     */
    Mono<ProductConfigurationDTO> getConfigurationByKey(UUID productId, String configKey);

    /**
     * Retrieves all configurations of a specific type for a product.
     *
     * @param productId the unique identifier of the product
     * @param configType the type of configuration
     * @return a Flux emitting all configurations of the specified type for the product
     */
    Flux<ProductConfigurationDTO> getConfigurationsByType(UUID productId, ProductConfigTypeEnum configType);

    /**
     * Updates an existing configuration with updated information, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the configuration
     * @param configId the unique identifier of the configuration to be updated
     * @param configDTO the data transfer object containing the updated details of the configuration
     * @return a reactive Mono containing the updated ProductConfigurationDTO
     */
    Mono<ProductConfigurationDTO> updateConfiguration(
            UUID productId, UUID configId, ProductConfigurationDTO configDTO);

    /**
     * Deletes a configuration identified by its unique ID, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the configuration
     * @param configId the unique identifier of the configuration to be deleted
     * @return a Mono that completes when the configuration is successfully deleted or errors if the deletion fails
     */
    Mono<Void> deleteConfiguration(UUID productId, UUID configId);
}

