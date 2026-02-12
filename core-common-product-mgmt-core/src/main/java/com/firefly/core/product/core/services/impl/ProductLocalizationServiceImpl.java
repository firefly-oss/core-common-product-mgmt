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
import com.firefly.core.product.core.mappers.ProductLocalizationMapper;
import com.firefly.core.product.core.services.ProductLocalizationService;
import com.firefly.core.product.interfaces.dtos.ProductLocalizationDTO;
import com.firefly.core.product.models.entities.ProductLocalization;
import com.firefly.core.product.models.repositories.ProductLocalizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Transactional
public class ProductLocalizationServiceImpl implements ProductLocalizationService {

    @Autowired
    private ProductLocalizationRepository repository;

    @Autowired
    private ProductLocalizationMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductLocalizationDTO>> filterLocalizations(UUID productId, FilterRequest<ProductLocalizationDTO> filterRequest) {
        return FilterUtils
                .createFilter(
                        ProductLocalization.class,
                        mapper::toDto
                )
                .filter(filterRequest);
    }

    @Override
    public Mono<ProductLocalizationDTO> createLocalization(UUID productId, ProductLocalizationDTO localizationDTO) {
        return Mono.just(localizationDTO)
                .doOnNext(dto -> dto.setProductId(productId))
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    @Override
    public Mono<ProductLocalizationDTO> getLocalizationById(UUID productId, UUID localizationId) {
        return repository.findById(localizationId)
                .switchIfEmpty(Mono.error(new RuntimeException("Localization not found with ID: " + localizationId)))
                .flatMap(localization -> {
                    if (!productId.equals(localization.getProductId())) {
                        return Mono.error(new RuntimeException("Localization with ID " + localizationId + " does not belong to product " + productId));
                    }
                    return Mono.just(mapper.toDto(localization));
                });
    }

    @Override
    public Mono<ProductLocalizationDTO> updateLocalization(UUID productId, UUID localizationId, ProductLocalizationDTO localizationDTO) {
        return repository.findById(localizationId)
                .switchIfEmpty(Mono.error(new RuntimeException("Localization not found with ID: " + localizationId)))
                .flatMap(existingLocalization -> {
                    if (!productId.equals(existingLocalization.getProductId())) {
                        return Mono.error(new RuntimeException("Localization with ID " + localizationId + " does not belong to product " + productId));
                    }
                    mapper.updateEntityFromDto(localizationDTO, existingLocalization);
                    return repository.save(existingLocalization);
                })
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteLocalization(UUID productId, UUID localizationId) {
        return repository.findById(localizationId)
                .switchIfEmpty(Mono.error(new RuntimeException("Localization not found with ID: " + localizationId)))
                .flatMap(localization -> {
                    if (!productId.equals(localization.getProductId())) {
                        return Mono.error(new RuntimeException("Localization with ID " + localizationId + " does not belong to product " + productId));
                    }
                    return repository.deleteById(localizationId);
                });
    }
}