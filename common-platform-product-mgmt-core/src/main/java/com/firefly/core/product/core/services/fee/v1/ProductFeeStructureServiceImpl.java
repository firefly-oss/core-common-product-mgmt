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


package com.firefly.core.product.core.services.fee.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.fee.v1.ProductFeeStructureMapper;
import com.firefly.core.product.interfaces.dtos.fee.v1.ProductFeeStructureDTO;
import com.firefly.core.product.models.entities.fee.v1.ProductFeeStructure;
import com.firefly.core.product.models.repositories.fee.v1.ProductFeeStructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class ProductFeeStructureServiceImpl implements ProductFeeStructureService {

    @Autowired
    private ProductFeeStructureRepository repository;

    @Autowired
    private ProductFeeStructureMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductFeeStructureDTO>> getAllFeeStructuresByProduct(UUID productId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findByProductId(productId, pageable),
                () -> repository.countByProductId(productId)
        ).onErrorMap(e -> new RuntimeException("Error retrieving fee structures", e));
    }

    @Override
    public Mono<ProductFeeStructureDTO> createFeeStructure(UUID productId, ProductFeeStructureDTO request) {
        request.setProductId(productId);
        ProductFeeStructure entity = mapper.toEntity(request);
        return repository.save(entity)
                .map(mapper::toDto)
                .onErrorMap(e -> new RuntimeException("Error creating fee structure", e));
    }

    @Override
    public Mono<ProductFeeStructureDTO> getFeeStructureById(UUID productId, UUID feeStructureId) {
        return repository.findByProductId(productId)
                .filter(entity -> entity.getFeeStructureId().equals(feeStructureId))
                .singleOrEmpty()
                .map(mapper::toDto)
                .onErrorMap(e -> new RuntimeException("Error retrieving fee structure by ID", e));
    }

    @Override
    public Mono<ProductFeeStructureDTO> updateFeeStructure(UUID productId, UUID feeStructureId, ProductFeeStructureDTO request) {
        return repository.findByProductId(productId)
                .filter(entity -> entity.getFeeStructureId().equals(feeStructureId))
                .singleOrEmpty()
                .flatMap(entity -> {
                    entity.setPriority(request.getPriority());
                    entity.setFeeStructureId(request.getFeeStructureId());
                    return repository.save(entity);
                })
                .map(mapper::toDto)
                .onErrorMap(e -> new RuntimeException("Error updating fee structure", e));
    }

    @Override
    public Mono<Void> deleteFeeStructure(UUID productId, UUID productFeeStructureId) {
        return repository.findByProductId(productId)
                .filter(entity -> entity.getProductFeeStructureId().equals(productFeeStructureId))
                .singleOrEmpty()
                .flatMap(repository::delete)
                .onErrorMap(e -> new RuntimeException("Error deleting fee structure", e));
    }
}
