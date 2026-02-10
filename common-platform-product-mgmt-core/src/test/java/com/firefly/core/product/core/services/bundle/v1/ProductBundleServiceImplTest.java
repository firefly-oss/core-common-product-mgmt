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


package com.firefly.core.product.core.services.bundle.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.mappers.bundle.v1.ProductBundleMapper;
import com.firefly.core.product.interfaces.dtos.bundle.v1.ProductBundleDTO;
import com.firefly.core.product.interfaces.enums.bundle.v1.BundleStatusEnum;
import com.firefly.core.product.models.entities.bundle.v1.ProductBundle;
import com.firefly.core.product.models.repositories.bundle.v1.ProductBundleRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductBundleServiceImplTest {

    @Mock
    private ProductBundleRepository repository;

    @Mock
    private ProductBundleMapper mapper;

    // No need to mock PaginationUtils as it contains static methods

    @InjectMocks
    private ProductBundleServiceImpl service;

    private ProductBundle productBundle;
    private ProductBundleDTO productBundleDTO;
    private final UUID BUNDLE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        productBundle = new ProductBundle();
        productBundle.setProductBundleId(BUNDLE_ID);
        productBundle.setBundleName("Test Bundle");
        productBundle.setBundleDescription("Test Description");
        productBundle.setBundleStatus(BundleStatusEnum.ACTIVE);
        productBundle.setDateCreated(now);
        productBundle.setDateUpdated(now);

        productBundleDTO = ProductBundleDTO.builder()
                .productBundleId(BUNDLE_ID)
                .bundleName("Test Bundle")
                .bundleDescription("Test Description")
                .bundleStatus(BundleStatusEnum.ACTIVE)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getById_Success() {
        // Arrange
        when(repository.findById(BUNDLE_ID)).thenReturn(Mono.just(productBundle));
        when(mapper.toDto(productBundle)).thenReturn(productBundleDTO);

        // Act & Assert
        StepVerifier.create(service.getById(BUNDLE_ID))
                .expectNext(productBundleDTO)
                .verifyComplete();

        verify(repository).findById(BUNDLE_ID);
        verify(mapper).toDto(productBundle);
    }

    @Test
    void getById_NotFound() {
        // Arrange
        when(repository.findById(BUNDLE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getById(BUNDLE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("An error occurred while retrieving the product bundle"))
                .verify();

        verify(repository).findById(BUNDLE_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getAll_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationUtils behavior
        // We need to use doReturn().when() syntax to avoid issues with null arguments
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findAllBy(pageable)).thenReturn(Flux.just(productBundle));
        when(repository.count()).thenReturn(Mono.just(1L));
        when(mapper.toDto(productBundle)).thenReturn(productBundleDTO);

        // Act & Assert
        StepVerifier.create(service.getAll(paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductBundleDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(productBundleDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findAllBy(pageable);
        verify(repository).count();
        verify(mapper).toDto(productBundle);
    }

    @Test
    void create_Success() {
        // Arrange
        when(mapper.toEntity(productBundleDTO)).thenReturn(productBundle);
        when(repository.save(productBundle)).thenReturn(Mono.just(productBundle));
        when(mapper.toDto(productBundle)).thenReturn(productBundleDTO);

        // Act & Assert
        StepVerifier.create(service.create(productBundleDTO))
                .expectNext(productBundleDTO)
                .verifyComplete();

        verify(mapper).toEntity(productBundleDTO);
        verify(repository).save(productBundle);
        verify(mapper).toDto(productBundle);
    }

    @Test
    void update_Success() {
        // Arrange
        ProductBundle existingBundle = spy(new ProductBundle());
        existingBundle.setProductBundleId(BUNDLE_ID);
        existingBundle.setBundleName("Old Name");
        existingBundle.setBundleDescription("Old Description");
        existingBundle.setBundleStatus(BundleStatusEnum.RETIRED);

        when(repository.findById(BUNDLE_ID)).thenReturn(Mono.just(existingBundle));
        when(repository.save(existingBundle)).thenReturn(Mono.just(existingBundle));
        when(mapper.toDto(existingBundle)).thenReturn(productBundleDTO);

        // Act & Assert
        StepVerifier.create(service.update(BUNDLE_ID, productBundleDTO))
                .expectNext(productBundleDTO)
                .verifyComplete();

        verify(repository).findById(BUNDLE_ID);
        verify(repository).save(existingBundle);
        verify(mapper).toDto(existingBundle);

        // Verify that the fields were updated
        verify(existingBundle).setBundleName(productBundleDTO.getBundleName());
        verify(existingBundle).setBundleDescription(productBundleDTO.getBundleDescription());
        verify(existingBundle).setBundleStatus(productBundleDTO.getBundleStatus());
    }

    @Test
    void update_NotFound() {
        // Arrange
        when(repository.findById(BUNDLE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.update(BUNDLE_ID, productBundleDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("An error occurred while updating the product bundle"))
                .verify();

        verify(repository).findById(BUNDLE_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(repository.findById(BUNDLE_ID)).thenReturn(Mono.just(productBundle));
        when(repository.delete(productBundle)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.delete(BUNDLE_ID))
                .verifyComplete();

        verify(repository).findById(BUNDLE_ID);
        verify(repository).delete(productBundle);
    }

    @Test
    void delete_NotFound() {
        // Arrange
        when(repository.findById(BUNDLE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.delete(BUNDLE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("An error occurred while deleting the product bundle"))
                .verify();

        verify(repository).findById(BUNDLE_ID);
        verify(repository, never()).delete(any());
    }
}
