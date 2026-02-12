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
import com.firefly.core.product.core.mappers.ProductVersionMapper;
import com.firefly.core.product.core.services.ProductVersionService;
import com.firefly.core.product.interfaces.dtos.ProductVersionDTO;
import com.firefly.core.product.models.entities.ProductVersion;
import com.firefly.core.product.models.repositories.ProductVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional
public class ProductVersionServiceImpl implements ProductVersionService {

    @Autowired
    private ProductVersionRepository repository;

    @Autowired
    private ProductVersionMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductVersionDTO>> filterProductVersions(UUID productId, FilterRequest<ProductVersionDTO> filterRequest) {
        return FilterUtils
                .createFilter(
                        ProductVersion.class,
                        mapper::toDto
                )
                .filter(filterRequest);
    }

    @Override
    public Mono<ProductVersionDTO> createProductVersion(UUID productId, ProductVersionDTO productVersionDTO) {
        return Mono.just(productVersionDTO)
                .doOnNext(dto -> dto.setProductId(productId))
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductVersionDTO> getProductVersionById(UUID productId, UUID versionId) {
        return repository.findById(versionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Product version not found with ID: " + versionId)))
                .flatMap(version -> {
                    if (!productId.equals(version.getProductId())) {
                        return Mono.error(new RuntimeException("Product version with ID " + versionId + " does not belong to product " + productId));
                    }
                    return Mono.just(mapper.toDto(version));
                });
    }

    @Override
    public Mono<ProductVersionDTO> updateProductVersion(UUID productId, UUID versionId, ProductVersionDTO productVersionDTO) {
        return repository.findById(versionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Product version not found with ID: " + versionId)))
                .flatMap(existingVersion -> {
                    if (!productId.equals(existingVersion.getProductId())) {
                        return Mono.error(new RuntimeException("Product version with ID " + versionId + " does not belong to product " + productId));
                    }
                    mapper.updateEntityFromDto(productVersionDTO, existingVersion);
                    return repository.save(existingVersion);
                })
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteProductVersion(UUID productId, UUID versionId) {
        return repository.findById(versionId)
                .switchIfEmpty(Mono.error(new RuntimeException("Product version not found with ID: " + versionId)))
                .flatMap(version -> {
                    if (!productId.equals(version.getProductId())) {
                        return Mono.error(new RuntimeException("Product version with ID " + versionId + " does not belong to product " + productId));
                    }
                    return repository.deleteById(versionId);
                });
    }
}
