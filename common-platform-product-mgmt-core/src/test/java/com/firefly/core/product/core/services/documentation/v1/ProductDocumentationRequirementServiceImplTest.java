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
import com.firefly.core.product.core.mappers.documentation.v1.ProductDocumentationRequirementMapper;
import com.firefly.core.product.interfaces.dtos.documentation.v1.ProductDocumentationRequirementDTO;
import com.firefly.core.product.interfaces.enums.documentation.v1.ContractingDocTypeEnum;
import com.firefly.core.product.models.entities.documentation.v1.ProductDocumentationRequirement;
import com.firefly.core.product.models.repositories.documentation.v1.ProductDocumentationRequirementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ProductDocumentationRequirementServiceImplTest {

    @Mock
    private ProductDocumentationRequirementRepository repository;

    @Mock
    private ProductDocumentationRequirementMapper mapper;

    @InjectMocks
    private ProductDocumentationRequirementServiceImpl service;

    private ProductDocumentationRequirement entity;
    private ProductDocumentationRequirementDTO dto;
    private final UUID productId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID requirementId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        // Setup test data
        entity = new ProductDocumentationRequirement();
        entity.setProductDocRequirementId(requirementId);
        entity.setProductId(productId);
        entity.setDocType(ContractingDocTypeEnum.IDENTIFICATION);
        entity.setIsMandatory(true);
        entity.setDescription("Identification document");

        dto = new ProductDocumentationRequirementDTO();
        dto.setProductDocRequirementId(requirementId);
        dto.setProductId(productId);
        dto.setDocType(ContractingDocTypeEnum.IDENTIFICATION);
        dto.setIsMandatory(true);
        dto.setDescription("Identification document");
    }

    @Test
    void getAllDocumentationRequirements_ShouldReturnPaginatedList() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        when(repository.findByProductId(eq(productId), eq(pageable)))
                .thenReturn(Flux.just(entity));
        when(repository.countByProductId(productId))
                .thenReturn(Mono.just(1L));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act & Assert
        StepVerifier.create(service.getAllDocumentationRequirements(productId, paginationRequest))
                .expectNextMatches(response -> 
                    response.getContent().size() == 1 &&
                    response.getContent().get(0).getProductDocRequirementId().equals(requirementId) &&
                    response.getTotalElements() == 1)
                .verifyComplete();
    }

    @Test
    void createDocumentationRequirement_ShouldCreateAndReturnDTO() {
        // Arrange
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act & Assert
        StepVerifier.create(service.createDocumentationRequirement(productId, dto))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void getDocumentationRequirement_ShouldReturnDTO() {
        // Arrange
        when(repository.findById(requirementId)).thenReturn(Mono.just(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act & Assert
        StepVerifier.create(service.getDocumentationRequirement(productId, requirementId))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void getDocumentationRequirementByType_ShouldReturnDTO() {
        // Arrange
        ContractingDocTypeEnum docType = ContractingDocTypeEnum.IDENTIFICATION;
        when(repository.findByProductIdAndDocType(productId, docType)).thenReturn(Mono.just(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act & Assert
        StepVerifier.create(service.getDocumentationRequirementByType(productId, docType))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void updateDocumentationRequirement_ShouldUpdateAndReturnDTO() {
        // Arrange
        when(repository.findById(requirementId)).thenReturn(Mono.just(entity));
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act & Assert
        StepVerifier.create(service.updateDocumentationRequirement(productId, requirementId, dto))
                .expectNext(dto)
                .verifyComplete();
    }

    @Test
    void deleteDocumentationRequirement_ShouldDeleteAndReturnVoid() {
        // Arrange
        when(repository.findById(requirementId)).thenReturn(Mono.just(entity));
        when(repository.delete(entity)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteDocumentationRequirement(productId, requirementId))
                .verifyComplete();
    }

    @Test
    void getMandatoryDocumentationRequirements_ShouldReturnMandatoryRequirements() {
        // Arrange
        when(repository.findByProductIdAndIsMandatory(productId, true))
                .thenReturn(Flux.just(entity));
        when(mapper.toDto(entity)).thenReturn(dto);

        // Act & Assert
        StepVerifier.create(service.getMandatoryDocumentationRequirements(productId))
                .expectNext(dto)
                .verifyComplete();
    }
}
