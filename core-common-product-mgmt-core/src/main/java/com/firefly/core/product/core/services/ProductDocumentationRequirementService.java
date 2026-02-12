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
import com.firefly.core.product.interfaces.dtos.ProductDocumentationRequirementDTO;
import com.firefly.core.product.interfaces.enums.ContractingDocTypeEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for managing product documentation requirements.
 */
public interface ProductDocumentationRequirementService {

    /**
     * Filters the documentation requirements based on the given criteria for a specific product.
     *
     * @param productId the unique identifier of the product owning the requirements
     * @param filterRequest the request object containing filtering criteria for ProductDocumentationRequirementDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of requirements
     */
    Mono<PaginationResponse<ProductDocumentationRequirementDTO>> filterDocumentationRequirements(
            UUID productId, FilterRequest<ProductDocumentationRequirementDTO> filterRequest);

    /**
     * Creates a new documentation requirement based on the provided information for a specific product.
     *
     * @param productId the unique identifier of the product that will own the requirement
     * @param requirementDTO the DTO object containing details of the requirement to be created
     * @return a Mono that emits the created ProductDocumentationRequirementDTO object
     */
    Mono<ProductDocumentationRequirementDTO> createDocumentationRequirement(
            UUID productId, ProductDocumentationRequirementDTO requirementDTO);

    /**
     * Retrieves a documentation requirement by its unique identifier, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the requirement
     * @param requirementId the unique identifier of the requirement to retrieve
     * @return a Mono emitting the {@link ProductDocumentationRequirementDTO} representing the requirement if found,
     *         or an error if the requirement does not exist or doesn't belong to the product
     */
    Mono<ProductDocumentationRequirementDTO> getDocumentationRequirementById(
            UUID productId, UUID requirementId);

    /**
     * Retrieves a documentation requirement by its document type for a specific product.
     *
     * @param productId the unique identifier of the product
     * @param docType the type of document
     * @return a Mono emitting the {@link ProductDocumentationRequirementDTO} representing the requirement if found
     */
    Mono<ProductDocumentationRequirementDTO> getDocumentationRequirementByType(
            UUID productId, ContractingDocTypeEnum docType);

    /**
     * Updates an existing documentation requirement with updated information, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the requirement
     * @param requirementId the unique identifier of the requirement to be updated
     * @param requirementDTO the data transfer object containing the updated details of the requirement
     * @return a reactive Mono containing the updated ProductDocumentationRequirementDTO
     */
    Mono<ProductDocumentationRequirementDTO> updateDocumentationRequirement(
            UUID productId, UUID requirementId, ProductDocumentationRequirementDTO requirementDTO);

    /**
     * Deletes a documentation requirement identified by its unique ID, validating product ownership.
     *
     * @param productId the unique identifier of the product that owns the requirement
     * @param requirementId the unique identifier of the requirement to be deleted
     * @return a Mono that completes when the requirement is successfully deleted or errors if the deletion fails
     */
    Mono<Void> deleteDocumentationRequirement(UUID productId, UUID requirementId);

    /**
     * Retrieves all mandatory documentation requirements for a specific product.
     *
     * @param productId the unique identifier of the product
     * @return a Flux emitting all mandatory documentation requirements for the product
     */
    Flux<ProductDocumentationRequirementDTO> filterMandatoryDocumentationRequirements(UUID productId);
}