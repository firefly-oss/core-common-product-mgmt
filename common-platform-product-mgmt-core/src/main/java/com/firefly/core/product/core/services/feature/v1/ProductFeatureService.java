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


package com.firefly.core.product.core.services.feature.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.feature.v1.ProductFeatureDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductFeatureService {

    /**
     * Retrieve a paginated list of all features associated with the specified product.
     */
    Mono<PaginationResponse<ProductFeatureDTO>> getAllFeatures(UUID productId, PaginationRequest paginationRequest);

    /**
     * Create a new feature linked to the specified product.
     */
    Mono<ProductFeatureDTO> createFeature(UUID productId, ProductFeatureDTO featureDTO);

    /**
     * Retrieve a specific product feature by its unique identifier, ensuring it matches the product.
     */
    Mono<ProductFeatureDTO> getFeature(UUID productId, UUID featureId);

    /**
     * Update an existing feature associated with the specified product.
     */
    Mono<ProductFeatureDTO> updateFeature(UUID productId, UUID featureId, ProductFeatureDTO featureDTO);

    /**
     * Remove an existing feature from the specified product.
     */
    Mono<Void> deleteFeature(UUID productId, UUID featureId);
}