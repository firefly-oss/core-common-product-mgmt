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
import com.firefly.core.product.interfaces.dtos.ProductDocumentationDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for managing product documentation.
 */
public interface ProductDocumentationService {

    /**
     * Filters the product documentations based on the given criteria for a specific product.
     *
     * @param productId the unique identifier of the product owning the documentations
     * @param filterRequest the request object containing filtering criteria for ProductDocumentationDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of documentations
     */
    Mono<PaginationResponse<ProductDocumentationDTO>> filterDocumentations(UUID productId, FilterRequest<ProductDocumentationDTO> filterRequest);

    /**
     * Creates a new documentation based on the provided information for a specific product.
     *
     * @param productId the unique identifier of the product that will own the documentation
     * @param documentationDTO the DTO object containing details of the documentation to be created
     * @return a Mono that emits the created ProductDocumentationDTO object
     */
    Mono<ProductDocumentationDTO> createDocumentation(UUID productId, ProductDocumentationDTO documentationDTO);

    /**
     * Retrieves a documentation by its unique identifier, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the documentation
     * @param documentationId the unique identifier of the documentation to retrieve
     * @return a Mono emitting the {@link ProductDocumentationDTO} representing the documentation if found,
     *         or an error if the documentation does not exist or doesn't belong to the product
     */
    Mono<ProductDocumentationDTO> getDocumentationById(UUID productId, UUID documentationId);

    /**
     * Updates an existing documentation with updated information, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the documentation
     * @param documentationId the unique identifier of the documentation to be updated
     * @param documentationDTO the data transfer object containing the updated details of the documentation
     * @return a reactive Mono containing the updated ProductDocumentationDTO
     */
    Mono<ProductDocumentationDTO> updateDocumentation(UUID productId, UUID documentationId, ProductDocumentationDTO documentationDTO);

    /**
     * Deletes a documentation identified by its unique ID, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the documentation
     * @param documentationId the unique identifier of the documentation to be deleted
     * @return a Mono that completes when the documentation is successfully deleted or errors if the deletion fails
     */
    Mono<Void> deleteDocumentation(UUID productId, UUID documentationId);
}
