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
import com.firefly.core.product.interfaces.dtos.lifecycle.v1.ProductLimitDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductLimitService {

    /**
     * Retrieve a paginated list of all limits associated with a specified product.
     */
    Mono<PaginationResponse<ProductLimitDTO>> getAllProductLimits(UUID productId, PaginationRequest paginationRequest);

    /**
     * Create a new limit and associate it with a specified product.
     */
    Mono<ProductLimitDTO> createProductLimit(UUID productId, ProductLimitDTO productLimitDTO);

    /**
     * Retrieve a specific product limit by its unique identifier (and possibly validate it belongs to the product).
     */
    Mono<ProductLimitDTO> getProductLimit(UUID productId, UUID limitId);

    /**
     * Update an existing product limit with new data.
     */
    Mono<ProductLimitDTO> updateProductLimit(UUID productId, UUID limitId, ProductLimitDTO productLimitDTO);

    /**
     * Remove an existing product limit by its unique identifier.
     */
    Mono<Void> deleteProductLimit(UUID productId, UUID limitId);
}