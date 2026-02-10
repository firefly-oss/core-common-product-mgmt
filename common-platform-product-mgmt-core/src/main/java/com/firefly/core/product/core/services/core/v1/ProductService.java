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


package com.firefly.core.product.core.services.core.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.core.v1.ProductDTO;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface ProductService {

    /**
     * Retrieve a paginated list of products.
     */
    Mono<PaginationResponse<ProductDTO>> getAllProducts(PaginationRequest paginationRequest);

    /**
     * Create a new product.
     */
    Mono<ProductDTO> createProduct(ProductDTO productDTO);

    /**
     * Retrieve a specific product by its unique identifier.
     */
    Mono<ProductDTO> getProduct(UUID productId);

    /**
     * Update an existing product by its unique identifier.
     */
    Mono<ProductDTO> updateProduct(UUID productId, ProductDTO productDTO);

    /**
     * Delete an existing product by its unique identifier.
     */
    Mono<Void> deleteProduct(UUID productId);
}
