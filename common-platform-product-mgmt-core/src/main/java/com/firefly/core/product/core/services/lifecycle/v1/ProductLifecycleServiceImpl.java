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
import com.firefly.core.product.core.mappers.lifecycle.v1.ProductLifecycleMapper;
import com.firefly.core.product.interfaces.dtos.lifecycle.v1.ProductLifecycleDTO;
import com.firefly.core.product.models.entities.lifecycle.v1.ProductLifecycle;
import com.firefly.core.product.models.repositories.lifecycle.v1.ProductLifecycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class ProductLifecycleServiceImpl implements ProductLifecycleService {

    @Autowired
    private ProductLifecycleRepository repository;

    @Autowired
    private ProductLifecycleMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductLifecycleDTO>> getProductLifecycles(UUID productId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findByProductId(productId, pageable),
                () -> repository.countByProductId(productId)
        ).onErrorMap(e -> new RuntimeException("Error retrieving product lifecycles", e));
    }

    @Override
    public Mono<ProductLifecycleDTO> createProductLifecycle(UUID productId, ProductLifecycleDTO request) {
        request.setProductId(productId);
        ProductLifecycle entity = mapper.toEntity(request);
        return repository.save(entity)
                .map(mapper::toDto)
                .onErrorMap(e -> new RuntimeException("Error creating product lifecycle", e));
    }

    @Override
    public Mono<ProductLifecycleDTO> getProductLifecycle(UUID productId, UUID lifecycleId) {
        return repository.findById(lifecycleId)
                .filter(entity -> entity.getProductId().equals(productId))
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Product lifecycle not found")))
                .onErrorMap(e -> new RuntimeException("Error retrieving product lifecycle", e));
    }

    @Override
    public Mono<ProductLifecycleDTO> updateProductLifecycle(UUID productId, UUID lifecycleId, ProductLifecycleDTO request) {
        return repository.findById(lifecycleId)
                .filter(entity -> entity.getProductId().equals(productId))
                .flatMap(existingEntity -> {
                    try {
                        ProductLifecycle updatedEntity = mapper.toEntity(request);
                        updatedEntity.setProductLifecycleId(lifecycleId);
                        updatedEntity.setProductId(productId);
                        return repository.save(updatedEntity).map(mapper::toDto);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Invalid update request data", e));
                    }
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Product lifecycle not found or not associated with the given product")))
                .onErrorMap(e -> new RuntimeException("Error updating product lifecycle", e));
    }

    @Override
    public Mono<Void> deleteProductLifecycle(UUID productId, UUID lifecycleId) {
        return repository.findById(lifecycleId)
                .filter(entity -> entity.getProductId().equals(productId))
                .switchIfEmpty(Mono.error(new RuntimeException("Product lifecycle not found or not associated with the given product")))
                .flatMap(repository::delete)
                .onErrorMap(e -> new RuntimeException("Error deleting product lifecycle", e));
    }
}