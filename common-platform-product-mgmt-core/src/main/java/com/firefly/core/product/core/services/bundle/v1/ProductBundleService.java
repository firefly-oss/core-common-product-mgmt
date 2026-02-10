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


package com.firefly.core.product.core.services.bundle.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.bundle.v1.ProductBundleDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductBundleService {

    /**
     * Retrieve a specific product bundle by its unique identifier.
     */
    Mono<ProductBundleDTO> getById(UUID bundleId);

    /**
     * Retrieve a paginated list of all product bundles.
     */
    Mono<PaginationResponse<ProductBundleDTO>> getAll(PaginationRequest paginationRequest);

    /**
     * Create a new product bundle.
     */
    Mono<ProductBundleDTO> create(ProductBundleDTO productBundleDTO);

    /**
     * Update an existing product bundle by its unique identifier.
     */
    Mono<ProductBundleDTO> update(UUID bundleId, ProductBundleDTO productBundleDTO);

    /**
     * Delete a product bundle by its unique identifier.
     */
    Mono<Void> delete(UUID bundleId);
}