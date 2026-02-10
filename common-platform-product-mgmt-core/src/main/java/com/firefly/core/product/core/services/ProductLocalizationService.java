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
import com.firefly.core.product.interfaces.dtos.ProductLocalizationDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for managing product localizations.
 */
public interface ProductLocalizationService {

    /**
     * Filters the product localizations based on the given criteria for a specific product.
     *
     * @param productId the unique identifier of the product owning the localizations
     * @param filterRequest the request object containing filtering criteria for ProductLocalizationDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of localizations
     */
    Mono<PaginationResponse<ProductLocalizationDTO>> filterLocalizations(UUID productId, FilterRequest<ProductLocalizationDTO> filterRequest);

    /**
     * Creates a new localization based on the provided information for a specific product.
     *
     * @param productId the unique identifier of the product that will own the localization
     * @param localizationDTO the DTO object containing details of the localization to be created
     * @return a Mono that emits the created ProductLocalizationDTO object
     */
    Mono<ProductLocalizationDTO> createLocalization(UUID productId, ProductLocalizationDTO localizationDTO);

    /**
     * Retrieves a localization by its unique identifier, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the localization
     * @param localizationId the unique identifier of the localization to retrieve
     * @return a Mono emitting the {@link ProductLocalizationDTO} representing the localization if found,
     *         or an error if the localization does not exist or doesn't belong to the product
     */
    Mono<ProductLocalizationDTO> getLocalizationById(UUID productId, UUID localizationId);

    /**
     * Updates an existing localization with updated information, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the localization
     * @param localizationId the unique identifier of the localization to be updated
     * @param localizationDTO the data transfer object containing the updated details of the localization
     * @return a reactive Mono containing the updated ProductLocalizationDTO
     */
    Mono<ProductLocalizationDTO> updateLocalization(UUID productId, UUID localizationId, ProductLocalizationDTO localizationDTO);

    /**
     * Deletes a localization identified by its unique ID, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the localization
     * @param localizationId the unique identifier of the localization to be deleted
     * @return a Mono that completes when the localization is successfully deleted or errors if the deletion fails
     */
    Mono<Void> deleteLocalization(UUID productId, UUID localizationId);
}
