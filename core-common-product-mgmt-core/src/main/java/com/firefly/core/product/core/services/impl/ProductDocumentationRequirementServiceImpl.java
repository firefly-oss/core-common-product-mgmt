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
import com.firefly.core.product.core.mappers.ProductDocumentationRequirementMapper;
import com.firefly.core.product.core.services.ProductDocumentationRequirementService;
import com.firefly.core.product.interfaces.dtos.ProductDocumentationRequirementDTO;
import com.firefly.core.product.interfaces.enums.ContractingDocTypeEnum;
import com.firefly.core.product.models.entities.ProductDocumentationRequirement;
import com.firefly.core.product.models.repositories.ProductDocumentationRequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional
public class ProductDocumentationRequirementServiceImpl implements ProductDocumentationRequirementService {

    @Autowired
    private ProductDocumentationRequirementRepository repository;

    @Autowired
    private ProductDocumentationRequirementMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductDocumentationRequirementDTO>> filterDocumentationRequirements(
            UUID productId, FilterRequest<ProductDocumentationRequirementDTO> filterRequest) {
        return FilterUtils
                .createFilter(
                        ProductDocumentationRequirement.class,
                        mapper::toDto
                )
                .filter(filterRequest);
    }

    @Override
    public Mono<ProductDocumentationRequirementDTO> createDocumentationRequirement(
            UUID productId, ProductDocumentationRequirementDTO requirementDTO) {
        return Mono.just(requirementDTO)
                .doOnNext(dto -> dto.setProductId(productId))
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductDocumentationRequirementDTO> getDocumentationRequirementById(
            UUID productId, UUID requirementId) {
        return repository.findById(requirementId)
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation requirement not found with ID: " + requirementId)))
                .flatMap(requirement -> {
                    if (!productId.equals(requirement.getProductId())) {
                        return Mono.error(new RuntimeException("Documentation requirement with ID " + requirementId + " does not belong to product " + productId));
                    }
                    return Mono.just(mapper.toDto(requirement));
                });
    }

    @Override
    public Mono<ProductDocumentationRequirementDTO> getDocumentationRequirementByType(
            UUID productId, ContractingDocTypeEnum docType) {
        return repository.findByProductIdAndDocType(productId, docType)
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation requirement not found for product " + productId + " with type " + docType)))
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductDocumentationRequirementDTO> updateDocumentationRequirement(
            UUID productId, UUID requirementId, ProductDocumentationRequirementDTO requirementDTO) {
        return repository.findById(requirementId)
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation requirement not found with ID: " + requirementId)))
                .flatMap(existingRequirement -> {
                    if (!productId.equals(existingRequirement.getProductId())) {
                        return Mono.error(new RuntimeException("Documentation requirement with ID " + requirementId + " does not belong to product " + productId));
                    }
                    mapper.updateEntityFromDto(requirementDTO, existingRequirement);
                    return repository.save(existingRequirement);
                })
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteDocumentationRequirement(UUID productId, UUID requirementId) {
        return repository.findById(requirementId)
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation requirement not found with ID: " + requirementId)))
                .flatMap(requirement -> {
                    if (!productId.equals(requirement.getProductId())) {
                        return Mono.error(new RuntimeException("Documentation requirement with ID " + requirementId + " does not belong to product " + productId));
                    }
                    return repository.deleteById(requirementId);
                });
    }

    @Override
    public Flux<ProductDocumentationRequirementDTO> filterMandatoryDocumentationRequirements(UUID productId) {
        return repository.findByProductIdAndIsMandatory(productId, true)
                .map(mapper::toDto);
    }
}