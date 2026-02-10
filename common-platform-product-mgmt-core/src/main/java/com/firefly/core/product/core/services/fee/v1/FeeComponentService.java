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
import com.firefly.core.product.interfaces.dtos.fee.v1.FeeComponentDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface FeeComponentService {

    /**
     * Retrieve a paginated list of fee components associated with a specific fee structure.
     */
    Mono<PaginationResponse<FeeComponentDTO>> getByFeeStructureId(UUID feeStructureId, PaginationRequest paginationRequest);

    /**
     * Create a new fee component under the specified fee structure.
     */
    Mono<FeeComponentDTO> createFeeComponent(UUID feeStructureId, FeeComponentDTO feeComponentDTO);

    /**
     * Retrieve a specific fee component by its unique identifier under the given fee structure.
     */
    Mono<FeeComponentDTO> getFeeComponent(UUID feeStructureId, UUID componentId);

    /**
     * Update an existing fee component by its unique identifier under the given fee structure.
     */
    Mono<FeeComponentDTO> updateFeeComponent(UUID feeStructureId, UUID componentId, FeeComponentDTO feeComponentDTO);

    /**
     * Delete an existing fee component by its unique identifier under the specified fee structure.
     */
    Mono<Void> deleteFeeComponent(UUID feeStructureId, UUID componentId);
}
