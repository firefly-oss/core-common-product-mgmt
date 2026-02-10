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


package com.firefly.core.product.core.services.relationship.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.relationship.v1.ProductRelationshipDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductRelationshipService {

    /**
     * Retrieve a paginated list of product relationships associated with the specified product.
     */
    Mono<PaginationResponse<ProductRelationshipDTO>> getAllRelationships(UUID productId, PaginationRequest paginationRequest);

    /**
     * Create a new relationship record for a specific product.
     */
    Mono<ProductRelationshipDTO> createRelationship(UUID productId, ProductRelationshipDTO dto);

    /**
     * Retrieve a specific relationship record by its unique identifier, ensuring it matches the product.
     */
    Mono<ProductRelationshipDTO> getRelationship(UUID productId, UUID relationshipId);

    /**
     * Update an existing product relationship record associated with the specified product.
     */
    Mono<ProductRelationshipDTO> updateRelationship(UUID productId, UUID relationshipId, ProductRelationshipDTO dto);

    /**
     * Remove an existing product relationship record by its unique identifier.
     */
    Mono<Void> deleteRelationship(UUID productId, UUID relationshipId);
}