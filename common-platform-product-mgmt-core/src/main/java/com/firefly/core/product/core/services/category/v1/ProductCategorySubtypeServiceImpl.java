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
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.category.v1.ProductSubtypeMapper;
import com.firefly.core.product.interfaces.dtos.category.v1.ProductCategorySubtypeDTO;
import com.firefly.core.product.models.repositories.category.v1.ProductSubtypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class ProductCategorySubtypeServiceImpl implements ProductCategorySubtypeService {

    @Autowired
    private ProductSubtypeRepository repository;

    @Autowired
    private ProductSubtypeMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductCategorySubtypeDTO>> getAllByCategoryId(UUID categoryId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findByProductCategoryId(categoryId, pageable)
                        .onErrorResume(e -> Mono.error(new RuntimeException("Failed to retrieve subtypes", e))),
                () -> repository.countByProductCategoryId(categoryId)
                        .onErrorResume(e -> Mono.error(new RuntimeException("Failed to count subtypes", e)))
        ).onErrorResume(e -> Mono.error(new RuntimeException("Pagination failed", e)));
    }

    @Override
    public Mono<ProductCategorySubtypeDTO> create(UUID categoryId, ProductCategorySubtypeDTO subtypeRequest) {
        subtypeRequest.setProductCategoryId(categoryId);
        return repository.findBySubtypeName(subtypeRequest.getSubtypeName())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Subtype name already exists"));
                    }
                    return repository.save(mapper.toEntity(subtypeRequest))
                            .map(mapper::toDto)
                            .onErrorResume(e -> Mono.error(new RuntimeException("Failed to create subtype", e)));
                })
                .onErrorResume(e -> Mono.error(new RuntimeException("Subtype creation process failed", e)));
    }

    @Override
    public Mono<ProductCategorySubtypeDTO> getById(UUID categoryId, UUID subtypeId) {
        return repository.findById(subtypeId)
                .filter(subtype -> categoryId.equals(subtype.getProductCategoryId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Subtype not found for the given category ID")))
                .map(mapper::toDto)
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to retrieve the subtype", e)));
    }

    @Override
    public Mono<ProductCategorySubtypeDTO> update(UUID categoryId, UUID subtypeId, ProductCategorySubtypeDTO subtypeRequest) {
        return repository.findById(subtypeId)
                .filter(existingSubtype -> categoryId.equals(existingSubtype.getProductCategoryId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Subtype not found for the given category ID")))
                .flatMap(existingSubtype -> {
                    existingSubtype.setSubtypeName(subtypeRequest.getSubtypeName());
                    existingSubtype.setSubtypeDescription(subtypeRequest.getSubtypeDescription());
                    return repository.save(existingSubtype)
                            .map(mapper::toDto)
                            .onErrorResume(e -> Mono.error(new RuntimeException("Failed to update subtype", e)));
                })
                .onErrorResume(e -> Mono.error(new RuntimeException("Subtype update process failed", e)));
    }

    @Override
    public Mono<Void> delete(UUID categoryId, UUID subtypeId) {
        return repository.findById(subtypeId)
                .filter(existingSubtype -> categoryId.equals(existingSubtype.getProductCategoryId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Subtype not found for the given category ID")))
                .flatMap(existingSubtype -> repository.delete(existingSubtype)
                        .onErrorResume(e -> Mono.error(new RuntimeException("Failed to delete subtype", e))))
                .onErrorResume(e -> Mono.error(new RuntimeException("Subtype deletion process failed", e)));
    }
}
