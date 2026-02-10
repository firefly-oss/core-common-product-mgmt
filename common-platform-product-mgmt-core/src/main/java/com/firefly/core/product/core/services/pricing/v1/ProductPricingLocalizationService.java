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
import com.firefly.core.product.interfaces.dtos.pricing.v1.ProductPricingLocalizationDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductPricingLocalizationService {

    /**
     * Retrieve a paginated list of all localization records associated with a given product pricing.
     */
    Mono<PaginationResponse<ProductPricingLocalizationDTO>> getAllLocalizations(
            UUID pricingId,
            PaginationRequest paginationRequest
    );

    /**
     * Create a new localization record and associate it with the specified product pricing.
     */
    Mono<ProductPricingLocalizationDTO> createLocalization(
            UUID pricingId,
            ProductPricingLocalizationDTO request
    );

    /**
     * Retrieve a specific localization record by its unique identifier.
     * (Optionally validate that it belongs to the correct pricing.)
     */
    Mono<ProductPricingLocalizationDTO> getLocalization(
            UUID pricingId,
            UUID localizationId
    );

    /**
     * Update an existing localization record for a product pricing.
     */
    Mono<ProductPricingLocalizationDTO> updateLocalization(
            UUID pricingId,
            UUID localizationId,
            ProductPricingLocalizationDTO request
    );

    /**
     * Remove an existing localization record by its unique identifier.
     */
    Mono<Void> deleteLocalization(UUID pricingId, UUID localizationId);
}