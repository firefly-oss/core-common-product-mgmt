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


package com.firefly.core.product.core.services.fee.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.fee.v1.ProductFeeStructureDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductFeeStructureService {

    /**
     * Retrieve a paginated list of fee structures associated with a given product.
     */
    Mono<PaginationResponse<ProductFeeStructureDTO>> getAllFeeStructuresByProduct(
            UUID productId,
            PaginationRequest paginationRequest
    );

    /**
     * Create a new fee structure and associate it with a specific product.
     */
    Mono<ProductFeeStructureDTO> createFeeStructure(UUID productId, ProductFeeStructureDTO request);

    /**
     * Retrieve a specific fee structure by its unique identifier, checking it belongs to the specified product.
     */
    Mono<ProductFeeStructureDTO> getFeeStructureById(UUID productId, UUID feeStructureId);

    /**
     * Update an existing fee structure for a specific product.
     */
    Mono<ProductFeeStructureDTO> updateFeeStructure(UUID productId, UUID feeStructureId, ProductFeeStructureDTO request);

    /**
     * Delete an existing fee structure from a specific product.
     */
    Mono<Void> deleteFeeStructure(UUID productId, UUID feeStructureId);
}