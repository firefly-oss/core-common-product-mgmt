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


package com.firefly.core.product.core.services.bundle.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.bundle.v1.ProductBundleMapper;
import com.firefly.core.product.interfaces.dtos.bundle.v1.ProductBundleDTO;
import com.firefly.core.product.models.entities.bundle.v1.ProductBundle;
import com.firefly.core.product.models.repositories.bundle.v1.ProductBundleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class ProductBundleServiceImpl implements ProductBundleService {

    @Autowired
    private ProductBundleRepository repository;

    @Autowired
    private ProductBundleMapper mapper;

    @Override
    public Mono<ProductBundleDTO> getById(UUID bundleId) {
        return repository.findById(bundleId)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product bundle not found")))
                .onErrorMap(e -> new RuntimeException("An error occurred while retrieving the product bundle", e));
    }

    @Override
    public Mono<PaginationResponse<ProductBundleDTO>> getAll(PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findAllBy(pageable),
                () -> repository.count()
        ).onErrorMap(e -> new RuntimeException("An error occurred while retrieving all product bundles", e));
    }

    @Override
    public Mono<ProductBundleDTO> create(ProductBundleDTO productBundleDTO) {
        ProductBundle entity = mapper.toEntity(productBundleDTO);
        return repository.save(entity)
                .map(mapper::toDto)
                .onErrorMap(e -> new RuntimeException("An error occurred while creating the product bundle", e));
    }

    @Override
    public Mono<ProductBundleDTO> update(UUID bundleId, ProductBundleDTO productBundleDTO) {
        return repository.findById(bundleId)
                .flatMap(existing -> {
                    existing.setBundleName(productBundleDTO.getBundleName());
                    existing.setBundleDescription(productBundleDTO.getBundleDescription());
                    existing.setBundleStatus(productBundleDTO.getBundleStatus());
                    return repository.save(existing);
                })
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product bundle not found")))
                .onErrorMap(e -> new RuntimeException("An error occurred while updating the product bundle", e));
    }

    @Override
    public Mono<Void> delete(UUID bundleId) {
        return repository.findById(bundleId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product bundle not found")))
                .flatMap(repository::delete)
                .onErrorMap(e -> new RuntimeException("An error occurred while deleting the product bundle", e));
    }
}
