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


package com.firefly.core.product.core.services.pricing.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.pricing.v1.ProductPricingMapper;
import com.firefly.core.product.interfaces.dtos.pricing.v1.ProductPricingDTO;
import com.firefly.core.product.models.entities.pricing.v1.ProductPricing;
import com.firefly.core.product.models.repositories.pricing.v1.ProductPricingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class ProductPricingServiceImpl implements ProductPricingService {

    @Autowired
    private ProductPricingRepository repository;

    @Autowired
    private ProductPricingMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductPricingDTO>> getAllPricings(UUID productId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findByProductId(productId, pageable),
                () -> repository.countByProductId(productId)
        ).onErrorResume(e -> Mono.error(new RuntimeException("Failed to retrieve pricings", e)));
    }

    @Override
    public Mono<ProductPricingDTO> createPricing(UUID productId, ProductPricingDTO productPricingDTO) {
        try {
            productPricingDTO.setProductId(productId);
            ProductPricing entity = mapper.toEntity(productPricingDTO);
            return repository.save(entity)
                    .map(mapper::toDto)
                    .onErrorResume(e -> Mono.error(new RuntimeException("Failed to create pricing", e)));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Unexpected error occurred while creating pricing", e));
        }
    }

    @Override
    public Mono<ProductPricingDTO> getPricing(UUID productId, UUID pricingId) {
        try {
            return repository.findById(pricingId)
                    .filter(productPricing -> productPricing.getProductId().equals(productId))
                    .map(mapper::toDto)
                    .switchIfEmpty(Mono.error(new RuntimeException("Pricing not found for the provided productId and pricingId.")))
                    .onErrorResume(e -> Mono.error(new RuntimeException("Failed to retrieve pricing", e)));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Unexpected error occurred while retrieving pricing", e));
        }
    }

    @Override
    public Mono<ProductPricingDTO> updatePricing(UUID productId, UUID pricingId, ProductPricingDTO productPricingDTO) {
        try {
            return repository.findById(pricingId)
                    .filter(productPricing -> productPricing.getProductId().equals(productId))
                    .flatMap(existingPricing -> {
                        mapper.updateEntityFromDto(productPricingDTO, existingPricing);
                        return repository.save(existingPricing);
                    })
                    .map(mapper::toDto)
                    .switchIfEmpty(Mono.error(new RuntimeException("Pricing not found for the provided productId and pricingId.")))
                    .onErrorResume(e -> Mono.error(new RuntimeException("Failed to update pricing", e)));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Unexpected error occurred while updating pricing", e));
        }
    }

    @Override
    public Mono<Void> deletePricing(UUID productId, UUID pricingId) {
        try {
            return repository.findById(pricingId)
                    .filter(productPricing -> productPricing.getProductId().equals(productId))
                    .switchIfEmpty(Mono.error(new RuntimeException("Pricing not found for the provided productId and pricingId.")))
                    .flatMap(repository::delete)
                    .onErrorResume(e -> Mono.error(new RuntimeException("Failed to delete pricing", e)));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Unexpected error occurred while deleting pricing", e));
        }
    }
}