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


package com.firefly.core.product.core.services;

import com.firefly.common.core.filters.FilterRequest;
import com.firefly.common.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for managing products.
 * All product operations are tenant-scoped to ensure proper multi-tenancy isolation.
 */
public interface ProductService {

    /**
     * Filters the products based on the given criteria.
     *
     * @param filterRequest the request object containing filtering criteria for ProductDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of products
     */
    Mono<PaginationResponse<ProductDTO>> filterProducts(FilterRequest<ProductDTO> filterRequest);

    /**
     * Creates a new product based on the provided information.
     * The tenantId must be provided in the productDTO.
     *
     * @param productDTO the DTO object containing details of the product to be created
     * @return a Mono that emits the created ProductDTO object
     * @throws RuntimeException if tenantId is not provided
     */
    Mono<ProductDTO> createProduct(ProductDTO productDTO);

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param productId the unique identifier of the product to retrieve
     * @return a Mono emitting the {@link ProductDTO} representing the product if found,
     *         or an error if the product does not exist
     */
    Mono<ProductDTO> getProductById(UUID productId);

    /**
     * Retrieves all products belonging to a specific tenant.
     *
     * @param tenantId the unique identifier of the tenant
     * @return a Flux emitting all products for the specified tenant
     */
    Flux<ProductDTO> getProductsByTenantId(UUID tenantId);

    /**
     * Updates an existing product with updated information.
     *
     * @param productId the unique identifier of the product to be updated
     * @param productDTO the data transfer object containing the updated details of the product
     * @return a reactive Mono containing the updated ProductDTO
     */
    Mono<ProductDTO> updateProduct(UUID productId, ProductDTO productDTO);

    /**
     * Deletes a product identified by its unique ID.
     *
     * @param productId the unique identifier of the product to be deleted
     * @return a Mono that completes when the product is successfully deleted or errors if the deletion fails
     */
    Mono<Void> deleteProduct(UUID productId);
}
