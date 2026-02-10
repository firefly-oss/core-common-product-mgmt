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


package com.firefly.core.product.core.services.lifecycle.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.lifecycle.v1.ProductLifecycleDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductLifecycleService {

    /**
     * Retrieve a paginated list of all lifecycle entries associated with a given product.
     */
    Mono<PaginationResponse<ProductLifecycleDTO>> getProductLifecycles(UUID productId, PaginationRequest paginationRequest);

    /**
     * Create a new lifecycle entry for a specific product.
     */
    Mono<ProductLifecycleDTO> createProductLifecycle(UUID productId, ProductLifecycleDTO request);

    /**
     * Retrieve a specific lifecycle entry by its unique identifier, ensuring it belongs to the specified product.
     */
    Mono<ProductLifecycleDTO> getProductLifecycle(UUID productId, UUID lifecycleId);

    /**
     * Update an existing lifecycle entry associated with the specified product.
     */
    Mono<ProductLifecycleDTO> updateProductLifecycle(UUID productId, UUID lifecycleId, ProductLifecycleDTO request);

    /**
     * Delete an existing lifecycle entry from a product by its unique identifier.
     */
    Mono<Void> deleteProductLifecycle(UUID productId, UUID lifecycleId);
}