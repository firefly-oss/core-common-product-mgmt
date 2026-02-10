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
import com.firefly.core.product.core.mappers.fee.v1.FeeComponentMapper;
import com.firefly.core.product.interfaces.dtos.fee.v1.FeeComponentDTO;
import com.firefly.core.product.models.entities.fee.v1.FeeComponent;
import com.firefly.core.product.models.repositories.fee.v1.FeeComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class FeeComponentServiceImpl implements FeeComponentService {

    @Autowired
    private FeeComponentRepository repository;

    @Autowired
    private FeeComponentMapper mapper;

    @Override
    public Mono<PaginationResponse<FeeComponentDTO>> getByFeeStructureId(UUID feeStructureId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findByFeeStructureId(feeStructureId, pageable),
                () -> repository.countByFeeStructureId(feeStructureId)
        ).onErrorMap(e -> new RuntimeException("Failed to retrieve fee components for fee structure ID: " + feeStructureId, e));
    }

    @Override
    public Mono<FeeComponentDTO> createFeeComponent(UUID feeStructureId, FeeComponentDTO feeComponentDTO) {
        FeeComponent entity = mapper.toEntity(feeComponentDTO);
        entity.setFeeStructureId(feeStructureId);
        return repository.save(entity)
                .map(mapper::toDto)
                .onErrorMap(e -> new RuntimeException("Failed to create fee component under fee structure ID: " + feeStructureId, e));
    }

    @Override
    public Mono<FeeComponentDTO> getFeeComponent(UUID feeStructureId, UUID componentId) {
        return repository.findById(componentId)
                .filter(feeComponent -> feeStructureId.equals(feeComponent.getFeeStructureId()))
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Fee component not found for component ID: " + componentId + " under fee structure ID: " + feeStructureId)))
                .onErrorMap(e -> new RuntimeException("Failed to retrieve fee component with component ID: " + componentId, e));
    }

    @Override
    public Mono<FeeComponentDTO> updateFeeComponent(UUID feeStructureId, UUID componentId, FeeComponentDTO feeComponentDTO) {
        return repository.findById(componentId)
                .filter(feeComponent -> feeStructureId.equals(feeComponent.getFeeStructureId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Fee component not found for component ID: " + componentId + " under fee structure ID: " + feeStructureId)))
                .flatMap(existingComponent -> {
                    FeeComponent updatedComponent = mapper.toEntity(feeComponentDTO);
                    updatedComponent.setFeeStructureId(feeStructureId);
                    updatedComponent.setFeeComponentId(componentId);
                    return repository.save(updatedComponent);
                })
                .map(mapper::toDto)
                .onErrorMap(e -> new RuntimeException("Failed to update fee component with component ID: " + componentId, e));
    }

    @Override
    public Mono<Void> deleteFeeComponent(UUID feeStructureId, UUID componentId) {
        return repository.findById(componentId)
                .filter(feeComponent -> feeStructureId.equals(feeComponent.getFeeStructureId()))
                .switchIfEmpty(Mono.error(new RuntimeException("Fee component not found for component ID: " + componentId + " under fee structure ID: " + feeStructureId)))
                .flatMap(repository::delete)
                .onErrorMap(e -> new RuntimeException("Failed to delete fee component with component ID: " + componentId, e));
    }
}
