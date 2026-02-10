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


package com.firefly.core.product.core.services.localization.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.localization.v1.ProductLocalizationMapper;
import com.firefly.core.product.interfaces.dtos.localization.v1.ProductLocalizationDTO;
import com.firefly.core.product.models.entities.localization.v1.ProductLocalization;
import com.firefly.core.product.models.repositories.localization.v1.ProductLocalizationRepository;
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
    public Mono<PaginationResponse<ProductLocalizationDTO>> getAllLocalizations(UUID productId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findAllByProductId(productId, pageable)
                        .onErrorMap(error -> new RuntimeException("Failed to fetch localizations", error)),
                () -> repository.countByProductId(productId)
                        .onErrorMap(error -> new RuntimeException("Failed to count localizations", error))
        );
    }

    @Override
    public Mono<ProductLocalizationDTO> createLocalization(UUID productId, ProductLocalizationDTO localizationDTO) {
        ProductLocalization entity = mapper.toEntity(localizationDTO);
        entity.setProductId(productId);
        return repository.save(entity)
                .map(mapper::toDto)
                .onErrorMap(error -> new RuntimeException("Failed to create localization", error));
    }

    @Override
    public Mono<ProductLocalizationDTO> getLocalizationById(UUID productId, UUID localizationId) {
        return repository.findById(localizationId)
                .filter(entity -> entity.getProductId().equals(productId))
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Localization not found")))
                .onErrorMap(error -> new RuntimeException("Failed to fetch localization by ID", error));
    }

    @Override
    public Mono<ProductLocalizationDTO> updateLocalization(UUID productId, UUID localizationId, ProductLocalizationDTO localizationDTO) {
        return repository.findById(localizationId)
                .filter(entity -> entity.getProductId().equals(productId))
                .switchIfEmpty(Mono.error(new RuntimeException("Localization not found for update")))
                .flatMap(existingEntity -> {
                    existingEntity.setLanguageCode(localizationDTO.getLanguageCode());
                    existingEntity.setLocalizedName(localizationDTO.getLocalizedName());
                    existingEntity.setLocalizedDescription(localizationDTO.getLocalizedDescription());
                    return repository.save(existingEntity)
                            .onErrorMap(error -> new RuntimeException("Failed to update localization", error));
                })
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteLocalization(UUID productId, UUID localizationId) {
        return repository.findById(localizationId)
                .filter(entity -> entity.getProductId().equals(productId))
                .switchIfEmpty(Mono.error(new RuntimeException("Localization not found for deletion")))
                .flatMap(repository::delete)
                .onErrorMap(error -> new RuntimeException("Failed to delete localization", error));
    }
}