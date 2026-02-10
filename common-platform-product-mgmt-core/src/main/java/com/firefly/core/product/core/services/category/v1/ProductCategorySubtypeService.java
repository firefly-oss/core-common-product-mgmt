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


package com.firefly.core.product.core.services.category.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.category.v1.ProductCategorySubtypeDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductCategorySubtypeService {
    /**
     * Retrieve a paginated list of product subtypes associated with a category ID.
     */
    Mono<PaginationResponse<ProductCategorySubtypeDTO>> getAllByCategoryId(UUID categoryId, PaginationRequest paginationRequest);

    /**
     * Create a new product subtype under the specified category ID.
     */
    Mono<ProductCategorySubtypeDTO> create(UUID categoryId, ProductCategorySubtypeDTO subtypeRequest);

    /**
     * Retrieve a single product subtype by categoryId and subtypeId.
     */
    Mono<ProductCategorySubtypeDTO> getById(UUID categoryId, UUID subtypeId);

    /**
     * Update an existing product subtype by categoryId and subtypeId.
     */
    Mono<ProductCategorySubtypeDTO> update(UUID categoryId, UUID subtypeId, ProductCategorySubtypeDTO subtypeRequest);

    /**
     * Delete an existing product subtype by categoryId and subtypeId.
     */
    Mono<Void> delete(UUID categoryId, UUID subtypeId);
}
