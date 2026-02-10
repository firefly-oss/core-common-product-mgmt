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


package com.firefly.core.product.core.services.version.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.version.v1.ProductVersionMapper;
import com.firefly.core.product.interfaces.dtos.version.v1.ProductVersionDTO;
import com.firefly.core.product.models.entities.version.v1.ProductVersion;
import com.firefly.core.product.models.repositories.version.v1.ProductVersionRepository;
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
    public Mono<PaginationResponse<ProductVersionDTO>> getAllProductVersions(UUID productId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findByProductId(productId, pageable),
                () -> repository.countByProductId(productId)
        ).onErrorResume(e -> Mono.error(new RuntimeException("Failed to fetch product versions", e)));
    }

    @Override
    public Mono<ProductVersionDTO> createProductVersion(UUID productId, ProductVersionDTO productVersionDTO) {
        productVersionDTO.setProductId(productId);
        ProductVersion entity = mapper.toEntity(productVersionDTO);
        return repository.save(entity)
                .map(mapper::toDto)
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to create product version", e)));
    }

    @Override
    public Mono<ProductVersionDTO> getProductVersion(UUID productId, UUID versionId) {
        return repository.findById(versionId)
                .filter(productVersion -> productVersion.getProductId().equals(productId))
                .flatMap(productVersion -> Mono.just(mapper.toDto(productVersion)))
                .switchIfEmpty(Mono.error(new RuntimeException("Product version not found or does not belong to the product")));
    }

    @Override
    public Mono<ProductVersionDTO> updateProductVersion(UUID productId, UUID versionId, ProductVersionDTO productVersionDTO) {
        return repository.findById(versionId)
                .filter(productVersion -> productVersion.getProductId().equals(productId))
                .flatMap(existingVersion -> {
                    ProductVersion updatedEntity = mapper.toEntity(productVersionDTO);
                    updatedEntity.setProductVersionId(existingVersion.getProductVersionId());
                    updatedEntity.setProductId(productId);
                    return repository.save(updatedEntity);
                })
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Product version not found or does not belong to the product")));
    }

    @Override
    public Mono<Void> deleteProductVersion(UUID productId, UUID versionId) {
        return repository.findById(versionId)
                .filter(productVersion -> productVersion.getProductId().equals(productId))
                .switchIfEmpty(Mono.error(new RuntimeException("Product version not found or does not belong to the product")))
                .flatMap(repository::delete);
    }
}
