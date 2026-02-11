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


package com.firefly.core.product.models.repositories;

import com.firefly.core.product.interfaces.enums.ContractingDocTypeEnum;
import com.firefly.core.product.models.entities.ProductDocumentationRequirement;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Repository for managing ProductDocumentationRequirement entities.
 */
@Repository
public interface ProductDocumentationRequirementRepository extends BaseRepository<ProductDocumentationRequirement, UUID> {
    
    /**
     * Find all documentation requirements for a specific product.
     *
     * @param productId The ID of the product
     * @return A Flux of ProductDocumentationRequirement entities
     */
    Flux<ProductDocumentationRequirement> findByProductId(UUID productId);
    
    /**
     * Find all documentation requirements for a specific product with pagination.
     *
     * @param productId The ID of the product
     * @param pageable Pagination information
     * @return A Flux of ProductDocumentationRequirement entities
     */
    Flux<ProductDocumentationRequirement> findByProductId(UUID productId, Pageable pageable);
    
    /**
     * Count the number of documentation requirements for a specific product.
     *
     * @param productId The ID of the product
     * @return A Mono with the count
     */
    Mono<Long> countByProductId(UUID productId);
    
    /**
     * Find all mandatory documentation requirements for a specific product.
     *
     * @param productId The ID of the product
     * @param isMandatory Whether the requirement is mandatory
     * @return A Flux of ProductDocumentationRequirement entities
     */
    Flux<ProductDocumentationRequirement> findByProductIdAndIsMandatory(UUID productId, Boolean isMandatory);
    
    /**
     * Find a specific documentation requirement for a product by document type.
     *
     * @param productId The ID of the product
     * @param docType The type of document
     * @return A Mono with the ProductDocumentationRequirement entity
     */
    Mono<ProductDocumentationRequirement> findByProductIdAndDocType(UUID productId, ContractingDocTypeEnum docType);
    
    /**
     * Delete all documentation requirements for a specific product.
     *
     * @param productId The ID of the product
     * @return A Mono with the number of deleted entities
     */
    Mono<UUID> deleteByProductId(Long productId);
}