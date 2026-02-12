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

import org.fireflyframework.core.filters.FilterRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.interfaces.dtos.ProductCategoryDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Service interface for managing product categories.
 */
public interface ProductCategoryService {

    /**
     * Filters the root categories (categories without a parent) based on the given criteria.
     *
     * @param filterRequest the request object containing filtering criteria for ProductCategoryDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of root categories
     */
    Mono<PaginationResponse<ProductCategoryDTO>> filterRootCategories(FilterRequest<ProductCategoryDTO> filterRequest);

    /**
     * Filters the child categories for a given parent category based on the given criteria.
     *
     * @param parentCategoryId the unique identifier of the parent category
     * @param filterRequest the request object containing filtering criteria for ProductCategoryDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of child categories
     */
    Mono<PaginationResponse<ProductCategoryDTO>> filterChildCategories(UUID parentCategoryId, FilterRequest<ProductCategoryDTO> filterRequest);

    /**
     * Filters categories by name pattern based on the given criteria.
     *
     * @param namePattern the name pattern to search for
     * @param filterRequest the request object containing filtering criteria for ProductCategoryDTO
     * @return a reactive {@code Mono} emitting a {@code PaginationResponse} containing the filtered list of matching categories
     */
    Mono<PaginationResponse<ProductCategoryDTO>> filterCategoriesByName(String namePattern, FilterRequest<ProductCategoryDTO> filterRequest);

    /**
     * Creates a new product category based on the provided information.
     *
     * @param categoryDTO the DTO object containing details of the category to be created
     * @return a Mono that emits the created ProductCategoryDTO object
     */
    Mono<ProductCategoryDTO> createCategory(ProductCategoryDTO categoryDTO);

    /**
     * Retrieves a product category by its unique identifier.
     *
     * @param categoryId the unique identifier of the category to retrieve
     * @return a Mono emitting the {@link ProductCategoryDTO} representing the category if found,
     *         or an error if the category does not exist
     */
    Mono<ProductCategoryDTO> getCategoryById(UUID categoryId);

    /**
     * Updates an existing product category with updated information.
     *
     * @param categoryId the unique identifier of the category to be updated
     * @param categoryDTO the data transfer object containing the updated details of the category
     * @return a reactive Mono containing the updated ProductCategoryDTO
     */
    Mono<ProductCategoryDTO> updateCategory(UUID categoryId, ProductCategoryDTO categoryDTO);

    /**
     * Deletes a product category identified by its unique ID.
     *
     * @param categoryId the unique identifier of the category to be deleted
     * @return a Mono that completes when the category is successfully deleted or errors if the deletion fails
     */
    Mono<Void> deleteCategory(UUID categoryId);
}
