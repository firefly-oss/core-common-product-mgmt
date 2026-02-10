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
import com.firefly.core.product.core.mappers.fee.v1.ProductFeeStructureMapper;
import com.firefly.core.product.interfaces.dtos.fee.v1.ProductFeeStructureDTO;
import com.firefly.core.product.models.entities.fee.v1.ProductFeeStructure;
import com.firefly.core.product.models.repositories.fee.v1.ProductFeeStructureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductFeeStructureServiceImplTest {

    @Mock
    private ProductFeeStructureRepository repository;

    @Mock
    private ProductFeeStructureMapper mapper;

    @InjectMocks
    private ProductFeeStructureServiceImpl service;

    private ProductFeeStructure feeStructure;
    private ProductFeeStructureDTO feeStructureDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID FEE_STRUCTURE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private final Integer PRIORITY = 1;

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        feeStructure = new ProductFeeStructure();
        feeStructure.setProductFeeStructureId(FEE_STRUCTURE_ID);
        feeStructure.setProductId(PRODUCT_ID);
        feeStructure.setFeeStructureId(FEE_STRUCTURE_ID);
        feeStructure.setPriority(PRIORITY);
        feeStructure.setDateCreated(now);
        feeStructure.setDateUpdated(now);

        feeStructureDTO = ProductFeeStructureDTO.builder()
                .productFeeStructureId(FEE_STRUCTURE_ID)
                .productId(PRODUCT_ID)
                .feeStructureId(FEE_STRUCTURE_ID)
                .priority(PRIORITY)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getAllFeeStructuresByProduct_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(feeStructure));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(feeStructure)).thenReturn(feeStructureDTO);

        // Act & Assert
        StepVerifier.create(service.getAllFeeStructuresByProduct(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductFeeStructureDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(feeStructureDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(feeStructure);
    }

    @Test
    void createFeeStructure_Success() {
        // Arrange
        ProductFeeStructureDTO requestDTO = ProductFeeStructureDTO.builder()
                .feeStructureId(FEE_STRUCTURE_ID)
                .priority(PRIORITY)
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(feeStructure);
        when(repository.save(feeStructure)).thenReturn(Mono.just(feeStructure));
        when(mapper.toDto(feeStructure)).thenReturn(feeStructureDTO);

        // Act & Assert
        StepVerifier.create(service.createFeeStructure(PRODUCT_ID, requestDTO))
                .expectNext(feeStructureDTO)
                .verifyComplete();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(feeStructure);
        verify(mapper).toDto(feeStructure);
    }

    @Test
    void createFeeStructure_Error() {
        // Arrange
        ProductFeeStructureDTO requestDTO = ProductFeeStructureDTO.builder()
                .feeStructureId(FEE_STRUCTURE_ID)
                .priority(PRIORITY)
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(feeStructure);
        when(repository.save(feeStructure)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createFeeStructure(PRODUCT_ID, requestDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error creating fee structure"))
                .verify();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(feeStructure);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getFeeStructureById_Success() {
        // Arrange
        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.just(feeStructure));
        when(mapper.toDto(feeStructure)).thenReturn(feeStructureDTO);

        // Act & Assert
        StepVerifier.create(service.getFeeStructureById(PRODUCT_ID, FEE_STRUCTURE_ID))
                .expectNext(feeStructureDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(mapper).toDto(feeStructure);
    }

    @Test
    void getFeeStructureById_NotFound() {
        // Arrange
        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.getFeeStructureById(PRODUCT_ID, FEE_STRUCTURE_ID))
                .verifyComplete(); // The service returns an empty Mono when no fee structure is found

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getFeeStructureById_WrongFeeStructureId() {
        // Arrange
        ProductFeeStructure differentFeeStructure = new ProductFeeStructure();
        differentFeeStructure.setProductFeeStructureId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different fee structure ID
        differentFeeStructure.setProductId(PRODUCT_ID);

        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.just(differentFeeStructure));

        // Act & Assert
        StepVerifier.create(service.getFeeStructureById(PRODUCT_ID, FEE_STRUCTURE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error retrieving fee structure by ID"))
                .verify();

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateFeeStructure_Success() {
        // Arrange
        ProductFeeStructureDTO updateRequest = ProductFeeStructureDTO.builder()
                .feeStructureId(FEE_STRUCTURE_ID)
                .priority(2) // Updated priority
                .build();

        // Create a spy of the feeStructure
        ProductFeeStructure spyFeeStructure = spy(new ProductFeeStructure());
        spyFeeStructure.setProductFeeStructureId(FEE_STRUCTURE_ID);
        spyFeeStructure.setProductId(PRODUCT_ID);
        spyFeeStructure.setFeeStructureId(FEE_STRUCTURE_ID);
        spyFeeStructure.setPriority(PRIORITY);

        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.just(spyFeeStructure));
        when(repository.save(spyFeeStructure)).thenReturn(Mono.just(spyFeeStructure));
        when(mapper.toDto(spyFeeStructure)).thenReturn(updateRequest);

        // Act & Assert
        StepVerifier.create(service.updateFeeStructure(PRODUCT_ID, FEE_STRUCTURE_ID, updateRequest))
                .expectNext(updateRequest)
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(repository).save(spyFeeStructure);
        verify(mapper).toDto(spyFeeStructure);

        // Verify that the fields were updated
        verify(spyFeeStructure).setPriority(updateRequest.getPriority());
        verify(spyFeeStructure, atLeastOnce()).setFeeStructureId(updateRequest.getFeeStructureId());
    }

    @Test
    void updateFeeStructure_NotFound() {
        // Arrange
        ProductFeeStructureDTO updateRequest = ProductFeeStructureDTO.builder()
                .feeStructureId(FEE_STRUCTURE_ID)
                .priority(2) // Updated priority
                .build();

        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.updateFeeStructure(PRODUCT_ID, FEE_STRUCTURE_ID, updateRequest))
                .verifyComplete(); // The service returns an empty Mono when no fee structure is found

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateFeeStructure_WrongFeeStructureId() {
        // Arrange
        ProductFeeStructureDTO updateRequest = ProductFeeStructureDTO.builder()
                .feeStructureId(FEE_STRUCTURE_ID)
                .priority(2) // Updated priority
                .build();

        ProductFeeStructure differentFeeStructure = new ProductFeeStructure();
        differentFeeStructure.setProductFeeStructureId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different fee structure ID
        differentFeeStructure.setProductId(PRODUCT_ID);

        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.just(differentFeeStructure));

        // Act & Assert
        StepVerifier.create(service.updateFeeStructure(PRODUCT_ID, FEE_STRUCTURE_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error updating fee structure"))
                .verify();

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deleteFeeStructure_Success() {
        // Arrange
        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.just(feeStructure));
        when(repository.delete(feeStructure)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteFeeStructure(PRODUCT_ID, FEE_STRUCTURE_ID))
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(repository).delete(feeStructure);
    }

    @Test
    void deleteFeeStructure_NotFound() {
        // Arrange
        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.empty());

        // Act & Assert
        StepVerifier.create(service.deleteFeeStructure(PRODUCT_ID, FEE_STRUCTURE_ID))
                .verifyComplete(); // The service returns an empty Mono when no fee structure is found

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteFeeStructure_WrongFeeStructureId() {
        // Arrange
        ProductFeeStructure differentFeeStructure = new ProductFeeStructure();
        differentFeeStructure.setProductFeeStructureId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different fee structure ID
        differentFeeStructure.setProductId(PRODUCT_ID);

        when(repository.findByProductId(PRODUCT_ID)).thenReturn(Flux.just(differentFeeStructure));

        // Act & Assert
        StepVerifier.create(service.deleteFeeStructure(PRODUCT_ID, FEE_STRUCTURE_ID))
                .verifyComplete(); // The service returns an empty Mono when no matching fee structure is found

        // Verify interactions
        verify(repository).findByProductId(PRODUCT_ID);
        verify(repository, never()).delete(any());
    }
}
