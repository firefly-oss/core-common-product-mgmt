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


package com.firefly.core.product.core.services.documentation.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.documentation.v1.ProductDocumentationMapper;
import com.firefly.core.product.interfaces.dtos.documentation.v1.ProductDocumentationDTO;
import com.firefly.core.product.models.repositories.documentation.v1.ProductDocumentationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class ProductDocumentationServiceImpl implements ProductDocumentationService {

    @Autowired
    private ProductDocumentationRepository repository;

    @Autowired
    private ProductDocumentationMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductDocumentationDTO>> getAllDocumentations(UUID productId, PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findByProductId(productId, pageable),
                () -> repository.countByProductId(productId)
        ).onErrorResume(e -> Mono.error(new RuntimeException("Failed to retrieve documentations", e)));
    }

    @Override
    public Mono<ProductDocumentationDTO> createDocumentation(UUID productId, ProductDocumentationDTO documentationDTO) {
        documentationDTO.setProductId(productId);
        return repository.save(mapper.toEntity(documentationDTO))
                .map(mapper::toDto)
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to create documentation", e)));
    }

    @Override
    public Mono<ProductDocumentationDTO> getDocumentation(UUID productId, UUID docId) {
        return repository.findById(docId)
                .filter(entity -> entity.getProductId().equals(productId))
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation not found")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to retrieve documentation", e)));
    }

    @Override
    public Mono<ProductDocumentationDTO> updateDocumentation(UUID productId, UUID docId, ProductDocumentationDTO documentationDTO) {
        return repository.findById(docId)
                .filter(entity -> entity.getProductId().equals(productId))
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation not found for update")))
                .flatMap(existingEntity -> {
                    ProductDocumentationDTO updatedDTO = mapper.toDto(existingEntity);
                    updatedDTO.setDocType(documentationDTO.getDocType());
                    updatedDTO.setDocumentManagerRef(documentationDTO.getDocumentManagerRef());
                    return repository.save(mapper.toEntity(updatedDTO));
                })
                .map(mapper::toDto)
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to update documentation", e)));
    }

    @Override
    public Mono<Void> deleteDocumentation(UUID productId, UUID docId) {
        return repository.findById(docId)
                .filter(entity -> entity.getProductId().equals(productId))
                .switchIfEmpty(Mono.error(new RuntimeException("Documentation not found for deletion")))
                .flatMap(repository::delete)
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to delete documentation", e)));
    }
}