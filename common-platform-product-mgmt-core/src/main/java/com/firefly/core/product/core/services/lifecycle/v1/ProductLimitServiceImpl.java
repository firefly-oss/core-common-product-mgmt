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


package com.firefly.core.product.core.services.lifecycle.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.lifecycle.v1.ProductLimitMapper;
import com.firefly.core.product.interfaces.dtos.lifecycle.v1.ProductLimitDTO;
import com.firefly.core.product.models.entities.lifecycle.v1.ProductLimit;
import com.firefly.core.product.models.repositories.lifecycle.v1.ProductLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class ProductLimitServiceImpl implements ProductLimitService {

    @Autowired
    private ProductLimitRepository repository;

    @Autowired
    private ProductLimitMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductLimitDTO>> getAllProductLimits(UUID productId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findByProductId(productId, pageable),
                () -> repository.countByProductId(productId)
        ).onErrorResume(e -> Mono.error(new RuntimeException("Error fetching product limits", e)));
    }

    @Override
    public Mono<ProductLimitDTO> createProductLimit(UUID productId, ProductLimitDTO productLimitDTO) {
        ProductLimit entity = mapper.toEntity(productLimitDTO);
        entity.setProductId(productId);
        return repository.save(entity)
                .map(mapper::toDto)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error creating product limit", e)));
    }

    @Override
    public Mono<ProductLimitDTO> getProductLimit(UUID productId, UUID limitId) {
        return repository.findById(limitId)
                .filter(limit -> limit.getProductId().equals(productId))
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product limit not found or does not belong to the product")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Error fetching product limit", e)));
    }

    @Override
    public Mono<ProductLimitDTO> updateProductLimit(UUID productId, UUID limitId, ProductLimitDTO productLimitDTO) {
        return repository.findById(limitId)
                .filter(limit -> limit.getProductId().equals(productId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product limit not found or does not belong to the product")))
                .flatMap(existingLimit -> {
                    ProductLimit updatedEntity = mapper.toEntity(productLimitDTO);
                    updatedEntity.setProductLimitId(limitId);
                    updatedEntity.setProductId(productId);
                    return repository.save(updatedEntity);
                })
                .map(mapper::toDto)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error updating product limit", e)));
    }

    @Override
    public Mono<Void> deleteProductLimit(UUID productId, UUID limitId) {
        return repository.findById(limitId)
                .filter(limit -> limit.getProductId().equals(productId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product limit not found or does not belong to the product")))
                .flatMap(repository::delete)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error deleting product limit", e)));
    }
}
