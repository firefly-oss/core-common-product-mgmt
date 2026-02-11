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
import com.firefly.core.product.interfaces.dtos.ProductRelationshipDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for managing product relationships.
 */
public interface ProductRelationshipService {

    /**
     * Filters the product relationships based on the given criteria for a specific product.
     *
     * @param productId the unique identifier of the product owning the relationships
     * @param filterRequest the request object containing filtering criteria for ProductRelationshipDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of relationships
     */
    Mono<PaginationResponse<ProductRelationshipDTO>> filterRelationships(UUID productId, FilterRequest<ProductRelationshipDTO> filterRequest);

    /**
     * Creates a new relationship based on the provided information for a specific product.
     *
     * @param productId the unique identifier of the product that will own the relationship
     * @param relationshipDTO the DTO object containing details of the relationship to be created
     * @return a Mono that emits the created ProductRelationshipDTO object
     */
    Mono<ProductRelationshipDTO> createRelationship(UUID productId, ProductRelationshipDTO relationshipDTO);

    /**
     * Retrieves a relationship by its unique identifier, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the relationship
     * @param relationshipId the unique identifier of the relationship to retrieve
     * @return a Mono emitting the {@link ProductRelationshipDTO} representing the relationship if found,
     *         or an error if the relationship does not exist or doesn't belong to the product
     */
    Mono<ProductRelationshipDTO> getRelationshipById(UUID productId, UUID relationshipId);

    /**
     * Updates an existing relationship with updated information, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the relationship
     * @param relationshipId the unique identifier of the relationship to be updated
     * @param relationshipDTO the data transfer object containing the updated details of the relationship
     * @return a reactive Mono containing the updated ProductRelationshipDTO
     */
    Mono<ProductRelationshipDTO> updateRelationship(UUID productId, UUID relationshipId, ProductRelationshipDTO relationshipDTO);

    /**
     * Deletes a relationship identified by its unique ID, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the relationship
     * @param relationshipId the unique identifier of the relationship to be deleted
     * @return a Mono that completes when the relationship is successfully deleted or errors if the deletion fails
     */
    Mono<Void> deleteRelationship(UUID productId, UUID relationshipId);
}