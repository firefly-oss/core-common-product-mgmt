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
import com.firefly.core.product.core.mappers.ProductRelationshipMapper;
import com.firefly.core.product.core.services.ProductRelationshipService;
import com.firefly.core.product.interfaces.dtos.ProductRelationshipDTO;
import com.firefly.core.product.models.entities.ProductRelationship;
import com.firefly.core.product.models.repositories.ProductRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional
public class ProductRelationshipServiceImpl implements ProductRelationshipService {

    @Autowired
    private ProductRelationshipRepository repository;

    @Autowired
    private ProductRelationshipMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductRelationshipDTO>> filterRelationships(UUID productId, FilterRequest<ProductRelationshipDTO> filterRequest) {
        return FilterUtils
                .createFilter(
                        ProductRelationship.class,
                        mapper::toDto
                )
                .filter(filterRequest);
    }

    @Override
    public Mono<ProductRelationshipDTO> createRelationship(UUID productId, ProductRelationshipDTO relationshipDTO) {
        return Mono.just(relationshipDTO)
                .doOnNext(dto -> dto.setProductId(productId))
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductRelationshipDTO> getRelationshipById(UUID productId, UUID relationshipId) {
        return repository.findById(relationshipId)
                .switchIfEmpty(Mono.error(new RuntimeException("Relationship not found with ID: " + relationshipId)))
                .flatMap(relationship -> {
                    if (!productId.equals(relationship.getProductId())) {
                        return Mono.error(new RuntimeException("Relationship with ID " + relationshipId + " does not belong to product " + productId));
                    }
                    return Mono.just(mapper.toDto(relationship));
                });
    }

    @Override
    public Mono<ProductRelationshipDTO> updateRelationship(UUID productId, UUID relationshipId, ProductRelationshipDTO relationshipDTO) {
        return repository.findById(relationshipId)
                .switchIfEmpty(Mono.error(new RuntimeException("Relationship not found with ID: " + relationshipId)))
                .flatMap(existingRelationship -> {
                    if (!productId.equals(existingRelationship.getProductId())) {
                        return Mono.error(new RuntimeException("Relationship with ID " + relationshipId + " does not belong to product " + productId));
                    }
                    mapper.updateEntityFromDto(relationshipDTO, existingRelationship);
                    return repository.save(existingRelationship);
                })
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteRelationship(UUID productId, UUID relationshipId) {
        return repository.findById(relationshipId)
                .switchIfEmpty(Mono.error(new RuntimeException("Relationship not found with ID: " + relationshipId)))
                .flatMap(relationship -> {
                    if (!productId.equals(relationship.getProductId())) {
                        return Mono.error(new RuntimeException("Relationship with ID " + relationshipId + " does not belong to product " + productId));
                    }
                    return repository.deleteById(relationshipId);
                });
    }
}