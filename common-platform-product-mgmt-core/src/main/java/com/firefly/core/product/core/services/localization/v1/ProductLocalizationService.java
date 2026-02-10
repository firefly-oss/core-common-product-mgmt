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


package com.firefly.core.product.core.services.localization.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.localization.v1.ProductLocalizationDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductLocalizationService {

    /**
     * Retrieve a paginated list of localizations for a specific product.
     */
    Mono<PaginationResponse<ProductLocalizationDTO>> getAllLocalizations(
            UUID productId,
            PaginationRequest paginationRequest
    );

    /**
     * Create a new localization for a specific product.
     */
    Mono<ProductLocalizationDTO> createLocalization(UUID productId, ProductLocalizationDTO localizationDTO);

    /**
     * Retrieve a specific localization by its unique ID, ensuring it belongs to the specified product.
     */
    Mono<ProductLocalizationDTO> getLocalizationById(UUID productId, UUID localizationId);

    /**
     * Update an existing localization for a specific product.
     */
    Mono<ProductLocalizationDTO> updateLocalization(UUID productId, UUID localizationId, ProductLocalizationDTO localizationDTO);

    /**
     * Delete an existing product localization by its unique ID.
     */
    Mono<Void> deleteLocalization(UUID productId, UUID localizationId);
}
