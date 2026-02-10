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


package com.firefly.core.product.core.services.pricing.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.pricing.v1.ProductPricingDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductPricingService {

    /**
     * Retrieve a paginated list of all pricing records associated with the specified product.
     */
    Mono<PaginationResponse<ProductPricingDTO>> getAllPricings(UUID productId, PaginationRequest paginationRequest);

    /**
     * Create a new pricing record and associate it with a product.
     */
    Mono<ProductPricingDTO> createPricing(UUID productId, ProductPricingDTO productPricingDTO);

    /**
     * Retrieve a specific product pricing record by its unique identifier,
     * ensuring it matches the specified product.
     */
    Mono<ProductPricingDTO> getPricing(UUID productId, UUID pricingId);

    /**
     * Update an existing pricing record associated with the specified product.
     */
    Mono<ProductPricingDTO> updatePricing(UUID productId, UUID pricingId, ProductPricingDTO productPricingDTO);

    /**
     * Remove an existing pricing record from a product by its unique identifier.
     */
    Mono<Void> deletePricing(UUID productId, UUID pricingId);
}