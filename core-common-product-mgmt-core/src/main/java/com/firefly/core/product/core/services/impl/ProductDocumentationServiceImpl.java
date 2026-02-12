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
import com.firefly.core.product.core.mappers.ProductDocumentationMapper;
import com.firefly.core.product.core.services.ProductDocumentationService;
import com.firefly.core.product.interfaces.dtos.ProductDocumentationDTO;
import com.firefly.core.product.models.entities.ProductDocumentation;
import com.firefly.core.product.models.repositories.ProductDocumentationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional
public class ProductDocumentationServiceImpl implements ProductDocumentationService {

    @Autowired
    private ProductDocumentationRepository repository;

    @Autowired
    private ProductDocumentationMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductDocumentationDTO>> filterDocumentations(UUID productId, FilterRequest<ProductDocumentationDTO> filterRequest) {
        return FilterUtils
                .createFilter(
                        ProductDocumentation.class,
                        mapper::toDto
                )
                .filter(filterRequest);
    }

    @Override
    public Mono<ProductDocumentationDTO> createDocumentation(UUID productId, ProductDocumentationDTO documentationDTO) {
        return Mono.just(documentationDTO)
                .doOnNext(dto -> dto.setProductId(productId))
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductDocumentationDTO> getDocumentationById(UUID productId, UUID documentationId) {
        return repository.findById(documentationId)
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation not found with ID: " + documentationId)))
                .flatMap(documentation -> {
                    if (!productId.equals(documentation.getProductId())) {
                        return Mono.error(new RuntimeException("Documentation with ID " + documentationId + " does not belong to product " + productId));
                    }
                    return Mono.just(mapper.toDto(documentation));
                });
    }

    @Override
    public Mono<ProductDocumentationDTO> updateDocumentation(UUID productId, UUID documentationId, ProductDocumentationDTO documentationDTO) {
        return repository.findById(documentationId)
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation not found with ID: " + documentationId)))
                .flatMap(existingDocumentation -> {
                    if (!productId.equals(existingDocumentation.getProductId())) {
                        return Mono.error(new RuntimeException("Documentation with ID " + documentationId + " does not belong to product " + productId));
                    }
                    mapper.updateEntityFromDto(documentationDTO, existingDocumentation);
                    return repository.save(existingDocumentation);
                })
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteDocumentation(UUID productId, UUID documentationId) {
        return repository.findById(documentationId)
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation not found with ID: " + documentationId)))
                .flatMap(documentation -> {
                    if (!productId.equals(documentation.getProductId())) {
                        return Mono.error(new RuntimeException("Documentation with ID " + documentationId + " does not belong to product " + productId));
                    }
                    return repository.deleteById(documentationId);
                });
    }
}