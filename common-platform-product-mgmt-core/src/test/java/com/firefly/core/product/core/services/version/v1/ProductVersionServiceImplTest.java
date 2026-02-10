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


package com.firefly.core.product.core.services.version.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.mappers.version.v1.ProductVersionMapper;
import com.firefly.core.product.interfaces.dtos.version.v1.ProductVersionDTO;
import com.firefly.core.product.models.entities.version.v1.ProductVersion;
import com.firefly.core.product.models.repositories.version.v1.ProductVersionRepository;
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
class ProductVersionServiceImplTest {

    @Mock
    private ProductVersionRepository repository;

    @Mock
    private ProductVersionMapper mapper;

    @InjectMocks
    private ProductVersionServiceImpl service;

    private ProductVersion version;
    private ProductVersionDTO versionDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID VERSION_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private final Long VERSION_NUMBER = 1L;

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        version = new ProductVersion();
        version.setProductVersionId(VERSION_ID);
        version.setProductId(PRODUCT_ID);
        version.setVersionNumber(VERSION_NUMBER);
        version.setVersionDescription("Initial version");
        version.setEffectiveDate(now);
        version.setDateCreated(now);
        version.setDateUpdated(now);

        versionDTO = ProductVersionDTO.builder()
                .productVersionId(VERSION_ID)
                .productId(PRODUCT_ID)
                .versionNumber(VERSION_NUMBER)
                .versionDescription("Initial version")
                .effectiveDate(now)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getAllProductVersions_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(version));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(version)).thenReturn(versionDTO);

        // Act & Assert
        StepVerifier.create(service.getAllProductVersions(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductVersionDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(versionDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(version);
    }

    @Test
    void createProductVersion_Success() {
        // Arrange
        ProductVersionDTO requestDTO = ProductVersionDTO.builder()
                .versionNumber(VERSION_NUMBER)
                .versionDescription("Initial version")
                .effectiveDate(LocalDateTime.now())
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(version);
        when(repository.save(version)).thenReturn(Mono.just(version));
        when(mapper.toDto(version)).thenReturn(versionDTO);

        // Act & Assert
        StepVerifier.create(service.createProductVersion(PRODUCT_ID, requestDTO))
                .expectNext(versionDTO)
                .verifyComplete();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(version);
        verify(mapper).toDto(version);
    }

    @Test
    void createProductVersion_Error() {
        // Arrange
        ProductVersionDTO requestDTO = ProductVersionDTO.builder()
                .versionNumber(VERSION_NUMBER)
                .versionDescription("Initial version")
                .effectiveDate(LocalDateTime.now())
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(version);
        when(repository.save(version)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createProductVersion(PRODUCT_ID, requestDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to create product version"))
                .verify();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(version);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getProductVersion_Success() {
        // Arrange
        when(repository.findById(VERSION_ID)).thenReturn(Mono.just(version));
        when(mapper.toDto(version)).thenReturn(versionDTO);

        // Act & Assert
        StepVerifier.create(service.getProductVersion(PRODUCT_ID, VERSION_ID))
                .expectNext(versionDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(mapper).toDto(version);
    }

    @Test
    void getProductVersion_NotFound() {
        // Arrange
        when(repository.findById(VERSION_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getProductVersion(PRODUCT_ID, VERSION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Product version not found or does not belong to the product"))
                .verify();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getProductVersion_WrongProduct() {
        // Arrange
        ProductVersion versionFromDifferentProduct = new ProductVersion();
        versionFromDifferentProduct.setProductVersionId(VERSION_ID);
        versionFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(VERSION_ID)).thenReturn(Mono.just(versionFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.getProductVersion(PRODUCT_ID, VERSION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Product version not found or does not belong to the product"))
                .verify();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateProductVersion_Success() {
        // Arrange
        ProductVersionDTO updateRequest = ProductVersionDTO.builder()
                .versionNumber(2L)
                .versionDescription("Updated version")
                .effectiveDate(LocalDateTime.now())
                .build();

        ProductVersion updatedEntity = new ProductVersion();
        updatedEntity.setProductVersionId(VERSION_ID);
        updatedEntity.setProductId(PRODUCT_ID);
        updatedEntity.setVersionNumber(2L);
        updatedEntity.setVersionDescription("Updated version");
        updatedEntity.setEffectiveDate(updateRequest.getEffectiveDate());

        when(repository.findById(VERSION_ID)).thenReturn(Mono.just(version));
        when(mapper.toEntity(updateRequest)).thenReturn(updatedEntity);
        when(repository.save(any(ProductVersion.class))).thenReturn(Mono.just(updatedEntity));
        when(mapper.toDto(updatedEntity)).thenReturn(updateRequest);

        // Act & Assert
        StepVerifier.create(service.updateProductVersion(PRODUCT_ID, VERSION_ID, updateRequest))
                .expectNext(updateRequest)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(mapper).toEntity(updateRequest);
        verify(repository).save(any(ProductVersion.class));
        verify(mapper).toDto(any(ProductVersion.class));
    }

    @Test
    void updateProductVersion_NotFound() {
        // Arrange
        ProductVersionDTO updateRequest = ProductVersionDTO.builder()
                .versionNumber(2L)
                .versionDescription("Updated version")
                .effectiveDate(LocalDateTime.now())
                .build();

        when(repository.findById(VERSION_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updateProductVersion(PRODUCT_ID, VERSION_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Product version not found or does not belong to the product"))
                .verify();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateProductVersion_WrongProduct() {
        // Arrange
        ProductVersionDTO updateRequest = ProductVersionDTO.builder()
                .versionNumber(2L)
                .versionDescription("Updated version")
                .effectiveDate(LocalDateTime.now())
                .build();

        ProductVersion versionFromDifferentProduct = new ProductVersion();
        versionFromDifferentProduct.setProductVersionId(VERSION_ID);
        versionFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(VERSION_ID)).thenReturn(Mono.just(versionFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.updateProductVersion(PRODUCT_ID, VERSION_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Product version not found or does not belong to the product"))
                .verify();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deleteProductVersion_Success() {
        // Arrange
        when(repository.findById(VERSION_ID)).thenReturn(Mono.just(version));
        when(repository.delete(version)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteProductVersion(PRODUCT_ID, VERSION_ID))
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(repository).delete(version);
    }

    @Test
    void deleteProductVersion_NotFound() {
        // Arrange
        when(repository.findById(VERSION_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteProductVersion(PRODUCT_ID, VERSION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Product version not found or does not belong to the product"))
                .verify();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteProductVersion_WrongProduct() {
        // Arrange
        ProductVersion versionFromDifferentProduct = new ProductVersion();
        versionFromDifferentProduct.setProductVersionId(VERSION_ID);
        versionFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(VERSION_ID)).thenReturn(Mono.just(versionFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.deleteProductVersion(PRODUCT_ID, VERSION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Product version not found or does not belong to the product"))
                .verify();

        // Verify interactions
        verify(repository).findById(VERSION_ID);
        verify(repository, never()).delete(any());
    }
}
