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
import com.firefly.core.product.interfaces.dtos.fee.v1.FeeApplicationRuleDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface FeeApplicationRuleService {

    /**
     * Retrieve a paginated list of fee application rules for a specific fee structure component.
     */
    Mono<PaginationResponse<FeeApplicationRuleDTO>> getRulesByComponentId(
            UUID feeStructureId,
            UUID componentId,
            PaginationRequest paginationRequest
    );

    /**
     * Create a new fee application rule under the specified fee structure component.
     */
    Mono<FeeApplicationRuleDTO> createRule(UUID feeStructureId, UUID componentId, FeeApplicationRuleDTO ruleDTO);

    /**
     * Retrieve a specific fee application rule by its unique identifier.
     */
    Mono<FeeApplicationRuleDTO> getRule(UUID feeStructureId, UUID componentId, UUID ruleId);

    /**
     * Update an existing fee application rule by its unique identifier.
     */
    Mono<FeeApplicationRuleDTO> updateRule(UUID feeStructureId, UUID componentId, UUID ruleId, FeeApplicationRuleDTO ruleDTO);

    /**
     * Delete an existing fee application rule by its unique identifier.
     */
    Mono<Void> deleteRule(UUID feeStructureId, UUID componentId, UUID ruleId);
}