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


package com.firefly.core.product.core.services.documentation.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.documentation.v1.ProductDocumentationRequirementDTO;
import com.firefly.core.product.interfaces.enums.documentation.v1.ContractingDocTypeEnum;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

/**
 * Service for managing product documentation requirements during the contracting/opening phase.
 */
public interface ProductDocumentationRequirementService {

    /**
     * Retrieve a paginated list of all documentation requirements for a product.
     *
     * @param productId The ID of the product
     * @param paginationRequest Pagination parameters
     * @return A paginated response of documentation requirements
     */
    Mono<PaginationResponse<ProductDocumentationRequirementDTO>> getAllDocumentationRequirements(
            UUID productId, PaginationRequest paginationRequest);

    /**
     * Create a new documentation requirement for a specific product.
     *
     * @param productId The ID of the product
     * @param requirementDTO The documentation requirement to create
     * @return The created documentation requirement
     */
    Mono<ProductDocumentationRequirementDTO> createDocumentationRequirement(
            UUID productId, ProductDocumentationRequirementDTO requirementDTO);

    /**
     * Retrieve a specific documentation requirement by its unique identifier within a product.
     *
     * @param productId The ID of the product
     * @param requirementId The ID of the documentation requirement
     * @return The documentation requirement
     */
    Mono<ProductDocumentationRequirementDTO> getDocumentationRequirement(
            UUID productId, UUID requirementId);

    /**
     * Retrieve a specific documentation requirement by its document type within a product.
     *
     * @param productId The ID of the product
     * @param docType The type of document
     * @return The documentation requirement
     */
    Mono<ProductDocumentationRequirementDTO> getDocumentationRequirementByType(
            UUID productId, ContractingDocTypeEnum docType);

    /**
     * Update an existing documentation requirement for a specific product.
     *
     * @param productId The ID of the product
     * @param requirementId The ID of the documentation requirement
     * @param requirementDTO The updated documentation requirement
     * @return The updated documentation requirement
     */
    Mono<ProductDocumentationRequirementDTO> updateDocumentationRequirement(
            UUID productId, UUID requirementId, ProductDocumentationRequirementDTO requirementDTO);

    /**
     * Delete an existing documentation requirement by its unique identifier, within the context of a product.
     *
     * @param productId The ID of the product
     * @param requirementId The ID of the documentation requirement
     * @return A Mono that completes when the deletion is done
     */
    Mono<Void> deleteDocumentationRequirement(UUID productId, UUID requirementId);

    /**
     * Get all mandatory documentation requirements for a product.
     *
     * @param productId The ID of the product
     * @return A flux of mandatory documentation requirements
     */
    Flux<ProductDocumentationRequirementDTO> getMandatoryDocumentationRequirements(UUID productId);
}