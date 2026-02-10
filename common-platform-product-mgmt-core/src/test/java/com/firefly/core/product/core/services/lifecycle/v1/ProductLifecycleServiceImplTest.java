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


package com.firefly.core.product.core.services.lifecycle.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.mappers.lifecycle.v1.ProductLifecycleMapper;
import com.firefly.core.product.interfaces.dtos.lifecycle.v1.ProductLifecycleDTO;
import com.firefly.core.product.interfaces.enums.lifecycle.v1.LifecycleStatusEnum;
import com.firefly.core.product.models.entities.lifecycle.v1.ProductLifecycle;
import com.firefly.core.product.models.repositories.lifecycle.v1.ProductLifecycleRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductLifecycleServiceImplTest {

    @Mock
    private ProductLifecycleRepository repository;

    @Mock
    private ProductLifecycleMapper mapper;

    @InjectMocks
    private ProductLifecycleServiceImpl service;

    private ProductLifecycle lifecycle;
    private ProductLifecycleDTO lifecycleDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID LIFECYCLE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        lifecycle = new ProductLifecycle();
        lifecycle.setProductLifecycleId(LIFECYCLE_ID);
        lifecycle.setProductId(PRODUCT_ID);
        lifecycle.setLifecycleStatus(LifecycleStatusEnum.ACTIVE);
        lifecycle.setStatusStartDate(now);
        lifecycle.setStatusEndDate(now.plusMonths(6));
        lifecycle.setReason("Initial activation");
        lifecycle.setDateCreated(now);
        lifecycle.setDateUpdated(now);

        lifecycleDTO = ProductLifecycleDTO.builder()
                .productLifecycleId(LIFECYCLE_ID)
                .productId(PRODUCT_ID)
                .lifecycleStatus(LifecycleStatusEnum.ACTIVE)
                .statusStartDate(now)
                .statusEndDate(now.plusMonths(6))
                .reason("Initial activation")
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getProductLifecycles_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(lifecycle));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(lifecycle)).thenReturn(lifecycleDTO);

        // Act & Assert
        StepVerifier.create(service.getProductLifecycles(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductLifecycleDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(lifecycleDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(lifecycle);
    }

    @Test
    void createProductLifecycle_Success() {
        // Arrange
        ProductLifecycleDTO requestDTO = ProductLifecycleDTO.builder()
                .lifecycleStatus(LifecycleStatusEnum.ACTIVE)
                .statusStartDate(LocalDateTime.now())
                .reason("Initial activation")
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(lifecycle);
        when(repository.save(lifecycle)).thenReturn(Mono.just(lifecycle));
        when(mapper.toDto(lifecycle)).thenReturn(lifecycleDTO);

        // Act & Assert
        StepVerifier.create(service.createProductLifecycle(PRODUCT_ID, requestDTO))
                .expectNext(lifecycleDTO)
                .verifyComplete();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(lifecycle);
        verify(mapper).toDto(lifecycle);
    }

    @Test
    void createProductLifecycle_Error() {
        // Arrange
        ProductLifecycleDTO requestDTO = ProductLifecycleDTO.builder()
                .lifecycleStatus(LifecycleStatusEnum.ACTIVE)
                .statusStartDate(LocalDateTime.now())
                .reason("Initial activation")
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(lifecycle);
        when(repository.save(lifecycle)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createProductLifecycle(PRODUCT_ID, requestDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error creating product lifecycle"))
                .verify();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(lifecycle);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getProductLifecycle_Success() {
        // Arrange
        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.just(lifecycle));
        when(mapper.toDto(lifecycle)).thenReturn(lifecycleDTO);

        // Act & Assert
        StepVerifier.create(service.getProductLifecycle(PRODUCT_ID, LIFECYCLE_ID))
                .expectNext(lifecycleDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(mapper).toDto(lifecycle);
    }

    @Test
    void getProductLifecycle_NotFound() {
        // Arrange
        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getProductLifecycle(PRODUCT_ID, LIFECYCLE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error retrieving product lifecycle"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getProductLifecycle_WrongProduct() {
        // Arrange
        ProductLifecycle lifecycleFromDifferentProduct = new ProductLifecycle();
        lifecycleFromDifferentProduct.setProductLifecycleId(LIFECYCLE_ID);
        lifecycleFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.just(lifecycleFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.getProductLifecycle(PRODUCT_ID, LIFECYCLE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error retrieving product lifecycle"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateProductLifecycle_Success() {
        // Arrange
        ProductLifecycleDTO updateRequest = ProductLifecycleDTO.builder()
                .lifecycleStatus(LifecycleStatusEnum.SUSPENDED)
                .statusStartDate(LocalDateTime.now())
                .statusEndDate(LocalDateTime.now().plusMonths(1))
                .reason("Temporary suspension")
                .build();

        ProductLifecycle updatedEntity = new ProductLifecycle();
        updatedEntity.setProductLifecycleId(LIFECYCLE_ID);
        updatedEntity.setProductId(PRODUCT_ID);
        updatedEntity.setLifecycleStatus(LifecycleStatusEnum.SUSPENDED);
        updatedEntity.setStatusStartDate(updateRequest.getStatusStartDate());
        updatedEntity.setStatusEndDate(updateRequest.getStatusEndDate());
        updatedEntity.setReason("Temporary suspension");

        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.just(lifecycle));
        when(mapper.toEntity(updateRequest)).thenReturn(updatedEntity);
        when(repository.save(any(ProductLifecycle.class))).thenReturn(Mono.just(updatedEntity));
        when(mapper.toDto(updatedEntity)).thenReturn(updateRequest);

        // Act & Assert
        StepVerifier.create(service.updateProductLifecycle(PRODUCT_ID, LIFECYCLE_ID, updateRequest))
                .expectNext(updateRequest)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(mapper).toEntity(updateRequest);
        verify(repository).save(any(ProductLifecycle.class));
        verify(mapper).toDto(any(ProductLifecycle.class));
    }

    @Test
    void updateProductLifecycle_NotFound() {
        // Arrange
        ProductLifecycleDTO updateRequest = ProductLifecycleDTO.builder()
                .lifecycleStatus(LifecycleStatusEnum.SUSPENDED)
                .statusStartDate(LocalDateTime.now())
                .reason("Temporary suspension")
                .build();

        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updateProductLifecycle(PRODUCT_ID, LIFECYCLE_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error updating product lifecycle"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateProductLifecycle_WrongProduct() {
        // Arrange
        ProductLifecycleDTO updateRequest = ProductLifecycleDTO.builder()
                .lifecycleStatus(LifecycleStatusEnum.SUSPENDED)
                .statusStartDate(LocalDateTime.now())
                .reason("Temporary suspension")
                .build();

        ProductLifecycle lifecycleFromDifferentProduct = new ProductLifecycle();
        lifecycleFromDifferentProduct.setProductLifecycleId(LIFECYCLE_ID);
        lifecycleFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.just(lifecycleFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.updateProductLifecycle(PRODUCT_ID, LIFECYCLE_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error updating product lifecycle"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateProductLifecycle_InvalidData() {
        // Arrange
        ProductLifecycleDTO updateRequest = ProductLifecycleDTO.builder()
                .lifecycleStatus(LifecycleStatusEnum.SUSPENDED)
                .statusStartDate(LocalDateTime.now())
                .reason("Temporary suspension")
                .build();

        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.just(lifecycle));
        when(mapper.toEntity(updateRequest)).thenThrow(new RuntimeException("Invalid data"));

        // Act & Assert
        StepVerifier.create(service.updateProductLifecycle(PRODUCT_ID, LIFECYCLE_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error updating product lifecycle"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(mapper).toEntity(updateRequest);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deleteProductLifecycle_Success() {
        // Arrange
        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.just(lifecycle));
        when(repository.delete(lifecycle)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteProductLifecycle(PRODUCT_ID, LIFECYCLE_ID))
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(repository).delete(lifecycle);
    }

    @Test
    void deleteProductLifecycle_NotFound() {
        // Arrange
        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteProductLifecycle(PRODUCT_ID, LIFECYCLE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error deleting product lifecycle"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteProductLifecycle_WrongProduct() {
        // Arrange
        ProductLifecycle lifecycleFromDifferentProduct = new ProductLifecycle();
        lifecycleFromDifferentProduct.setProductLifecycleId(LIFECYCLE_ID);
        lifecycleFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LIFECYCLE_ID)).thenReturn(Mono.just(lifecycleFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.deleteProductLifecycle(PRODUCT_ID, LIFECYCLE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error deleting product lifecycle"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIFECYCLE_ID);
        verify(repository, never()).delete(any());
    }
}
