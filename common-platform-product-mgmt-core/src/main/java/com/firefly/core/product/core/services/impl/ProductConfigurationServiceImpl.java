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

import com.firefly.common.core.filters.FilterRequest;
import com.firefly.common.core.filters.FilterUtils;
import com.firefly.common.core.queries.PaginationResponse;
import com.firefly.core.product.core.mappers.ProductConfigurationMapper;
import com.firefly.core.product.core.services.ProductConfigurationService;
import com.firefly.core.product.interfaces.dtos.ProductConfigurationDTO;
import com.firefly.core.product.interfaces.enums.ProductConfigTypeEnum;
import com.firefly.core.product.models.entities.ProductConfiguration;
import com.firefly.core.product.models.repositories.ProductConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional
public class ProductConfigurationServiceImpl implements ProductConfigurationService {

    @Autowired
    private ProductConfigurationRepository repository;

    @Autowired
    private ProductConfigurationMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductConfigurationDTO>> filterConfigurations(
            UUID productId, FilterRequest<ProductConfigurationDTO> filterRequest) {
        return FilterUtils
                .createFilter(
                        ProductConfiguration.class,
                        mapper::toDto
                )
                .filter(filterRequest);
    }

    @Override
    public Mono<ProductConfigurationDTO> createConfiguration(UUID productId, ProductConfigurationDTO configDTO) {
        return Mono.just(configDTO)
                .doOnNext(dto -> dto.setProductId(productId))
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductConfigurationDTO> getConfigurationById(UUID productId, UUID configId) {
        return repository.findById(configId)
                .switchIfEmpty(Mono.error(new RuntimeException("Configuration not found with ID: " + configId)))
                .flatMap(config -> {
                    if (!productId.equals(config.getProductId())) {
                        return Mono.error(new RuntimeException("Configuration with ID " + configId + " does not belong to product " + productId));
                    }
                    return Mono.just(mapper.toDto(config));
                });
    }

    @Override
    public Mono<ProductConfigurationDTO> getConfigurationByKey(UUID productId, String configKey) {
        return repository.findByProductIdAndConfigKey(productId, configKey)
                .switchIfEmpty(Mono.error(new RuntimeException("Configuration not found for product " + productId + " with key " + configKey)))
                .map(mapper::toDto);
    }

    @Override
    public Flux<ProductConfigurationDTO> getConfigurationsByType(UUID productId, ProductConfigTypeEnum configType) {
        return repository.findByProductIdAndConfigType(productId, configType)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductConfigurationDTO> updateConfiguration(
            UUID productId, UUID configId, ProductConfigurationDTO configDTO) {
        return repository.findById(configId)
                .switchIfEmpty(Mono.error(new RuntimeException("Configuration not found with ID: " + configId)))
                .flatMap(existingConfig -> {
                    if (!productId.equals(existingConfig.getProductId())) {
                        return Mono.error(new RuntimeException("Configuration with ID " + configId + " does not belong to product " + productId));
                    }
                    mapper.updateEntityFromDto(configDTO, existingConfig);
                    return repository.save(existingConfig);
                })
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteConfiguration(UUID productId, UUID configId) {
        return repository.findById(configId)
                .switchIfEmpty(Mono.error(new RuntimeException("Configuration not found with ID: " + configId)))
                .flatMap(config -> {
                    if (!productId.equals(config.getProductId())) {
                        return Mono.error(new RuntimeException("Configuration with ID " + configId + " does not belong to product " + productId));
                    }
                    return repository.deleteById(configId);
                });
    }
}

