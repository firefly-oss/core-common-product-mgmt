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
import com.firefly.core.product.interfaces.dtos.documentation.v1.ProductDocumentationDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductDocumentationService {

    /**
     * Retrieve a paginated list of all documentation items linked to a product.
     */
    Mono<PaginationResponse<ProductDocumentationDTO>> getAllDocumentations(UUID productId, PaginationRequest paginationRequest);

    /**
     * Create a new documentation item for a specific product.
     */
    Mono<ProductDocumentationDTO> createDocumentation(UUID productId, ProductDocumentationDTO documentationDTO);

    /**
     * Retrieve a specific documentation item by its unique identifier within a product.
     */
    Mono<ProductDocumentationDTO> getDocumentation(UUID productId, UUID docId);

    /**
     * Update an existing documentation item for a specific product.
     */
    Mono<ProductDocumentationDTO> updateDocumentation(UUID productId, UUID docId, ProductDocumentationDTO documentationDTO);

    /**
     * Delete an existing documentation item by its unique identifier, within the context of a product.
     */
    Mono<Void> deleteDocumentation(UUID productId, UUID docId);
}

