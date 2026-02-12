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


package com.firefly.core.product.core.services.impl;

import org.fireflyframework.core.filters.FilterRequest;
import org.fireflyframework.core.filters.FilterUtils;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.mappers.ProductCategoryMapper;
import com.firefly.core.product.core.services.ProductCategoryService;
import com.firefly.core.product.interfaces.dtos.ProductCategoryDTO;
import com.firefly.core.product.models.entities.ProductCategory;
import com.firefly.core.product.models.repositories.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository repository;

    @Autowired
    private ProductCategoryMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductCategoryDTO>> filterRootCategories(FilterRequest<ProductCategoryDTO> filterRequest) {
        return FilterUtils
                .createFilter(
                        ProductCategory.class,
                        mapper::toDto
                )
                .filter(filterRequest);
    }

    @Override
    public Mono<PaginationResponse<ProductCategoryDTO>> filterChildCategories(UUID parentCategoryId, FilterRequest<ProductCategoryDTO> filterRequest) {
        return repository.findById(parentCategoryId)
                .switchIfEmpty(Mono.error(new RuntimeException("Parent category not found with ID: " + parentCategoryId)))
                .flatMap(parent -> FilterUtils
                        .createFilter(
                                ProductCategory.class,
                                mapper::toDto
                        )
                        .filter(filterRequest));
    }

    @Override
    public Mono<PaginationResponse<ProductCategoryDTO>> filterCategoriesByName(String namePattern, FilterRequest<ProductCategoryDTO> filterRequest) {
        return FilterUtils
                .createFilter(
                        ProductCategory.class,
                        mapper::toDto
                )
                .filter(filterRequest);
    }

    @Override
    public Mono<ProductCategoryDTO> createCategory(ProductCategoryDTO categoryDTO) {
        return Mono.just(categoryDTO)
                .flatMap(this::validateAndSetLevel)
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductCategoryDTO> getCategoryById(UUID categoryId) {
        return repository.findById(categoryId)
                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with ID: " + categoryId)))
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductCategoryDTO> updateCategory(UUID categoryId, ProductCategoryDTO categoryDTO) {
        return repository.findById(categoryId)
                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with ID: " + categoryId)))
                .flatMap(existingEntity -> {
                    // Check for circular reference - a category cannot be its own parent
                    if (categoryDTO.getParentCategoryId() != null && categoryDTO.getParentCategoryId().equals(categoryId)) {
                        return Mono.error(new RuntimeException("A category cannot be its own parent"));
                    }
                    return validateNoCircularReference(categoryId, categoryDTO.getParentCategoryId())
                            .then(calculateLevel(categoryDTO.getParentCategoryId()))
                            .flatMap(level -> {
                                mapper.updateEntityFromDto(categoryDTO, existingEntity);
                                existingEntity.setLevel(level);
                                return repository.save(existingEntity);
                            })
                            .map(mapper::toDto);
                });
    }

    @Override
    public Mono<Void> deleteCategory(UUID categoryId) {
        return repository.findById(categoryId)
                .switchIfEmpty(Mono.error(new RuntimeException("Category not found with ID: " + categoryId)))
                .flatMap(existingEntity ->
                    repository.countByParentCategoryId(categoryId)
                            .flatMap(count -> {
                                if (count > 0) {
                                    return Mono.error(new RuntimeException("Cannot delete category with ID " + categoryId + " because it has child categories"));
                                }
                                return repository.deleteById(categoryId);
                            })
                );
    }

    /**
     * Validates parent category exists (if specified) and sets the level.
     */
    private Mono<ProductCategoryDTO> validateAndSetLevel(ProductCategoryDTO categoryDTO) {
        return calculateLevel(categoryDTO.getParentCategoryId())
                .map(level -> {
                    categoryDTO.setLevel(level);
                    return categoryDTO;
                });
    }

    /**
     * Calculates the level based on parent category.
     * Root categories have level 0, children have parent's level + 1.
     */
    private Mono<Integer> calculateLevel(UUID parentCategoryId) {
        if (parentCategoryId == null) {
            return Mono.just(0);
        }
        return repository.findById(parentCategoryId)
                .switchIfEmpty(Mono.error(new RuntimeException("Parent category not found with ID: " + parentCategoryId)))
                .map(parent -> (parent.getLevel() != null ? parent.getLevel() : 0) + 1);
    }

    /**
     * Validates that setting a parent category would not create a circular reference.
     */
    private Mono<Void> validateNoCircularReference(UUID categoryId, UUID newParentId) {
        if (newParentId == null) {
            return Mono.empty();
        }
        return checkAncestors(categoryId, newParentId);
    }

    /**
     * Recursively checks if categoryId appears in the ancestor chain of potentialAncestorId.
     */
    private Mono<Void> checkAncestors(UUID categoryId, UUID potentialAncestorId) {
        if (potentialAncestorId == null) {
            return Mono.empty();
        }
        if (potentialAncestorId.equals(categoryId)) {
            return Mono.error(new RuntimeException("Circular reference detected: category cannot be an ancestor of itself"));
        }
        return repository.findById(potentialAncestorId)
                .flatMap(ancestor -> checkAncestors(categoryId, ancestor.getParentCategoryId()));
    }
}