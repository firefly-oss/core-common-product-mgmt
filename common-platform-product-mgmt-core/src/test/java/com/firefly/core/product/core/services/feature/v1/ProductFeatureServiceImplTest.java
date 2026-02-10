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


package com.firefly.core.product.core.services.feature.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.mappers.feature.v1.ProductFeatureMapper;
import com.firefly.core.product.interfaces.dtos.feature.v1.ProductFeatureDTO;
import com.firefly.core.product.interfaces.enums.feature.v1.FeatureTypeEnum;
import com.firefly.core.product.models.entities.feature.v1.ProductFeature;
import com.firefly.core.product.models.repositories.feature.v1.ProductFeatureRepository;
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
class ProductFeatureServiceImplTest {

    @Mock
    private ProductFeatureRepository repository;

    @Mock
    private ProductFeatureMapper mapper;

    @InjectMocks
    private ProductFeatureServiceImpl service;

    private ProductFeature feature;
    private ProductFeatureDTO featureDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID FEATURE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        feature = new ProductFeature();
        feature.setProductFeatureId(FEATURE_ID);
        feature.setProductId(PRODUCT_ID);
        feature.setFeatureName("Test Feature");
        feature.setFeatureDescription("Test Description");
        feature.setFeatureType(FeatureTypeEnum.STANDARD);
        feature.setIsMandatory(true);
        feature.setDateCreated(now);
        feature.setDateUpdated(now);

        featureDTO = ProductFeatureDTO.builder()
                .productFeatureId(FEATURE_ID)
                .productId(PRODUCT_ID)
                .featureName("Test Feature")
                .featureDescription("Test Description")
                .featureType(FeatureTypeEnum.STANDARD)
                .isMandatory(true)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getAllFeatures_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(feature));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(feature)).thenReturn(featureDTO);

        // Act & Assert
        StepVerifier.create(service.getAllFeatures(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductFeatureDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(featureDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(feature);
    }

    @Test
    void createFeature_Success() {
        // Arrange
        ProductFeature spyFeature = spy(new ProductFeature());
        when(mapper.toEntity(featureDTO)).thenReturn(spyFeature);
        when(repository.save(spyFeature)).thenReturn(Mono.just(spyFeature));
        when(mapper.toDto(spyFeature)).thenReturn(featureDTO);

        // Act & Assert
        StepVerifier.create(service.createFeature(PRODUCT_ID, featureDTO))
                .expectNext(featureDTO)
                .verifyComplete();

        verify(mapper).toEntity(featureDTO);
        verify(repository).save(spyFeature);
        verify(mapper).toDto(spyFeature);

        // Verify that the product ID was set
        verify(spyFeature).setProductId(PRODUCT_ID);
    }

    @Test
    void createFeature_Error() {
        // Arrange
        when(mapper.toEntity(featureDTO)).thenReturn(feature);
        when(repository.save(feature)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createFeature(PRODUCT_ID, featureDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while creating feature"))
                .verify();

        verify(mapper).toEntity(featureDTO);
        verify(repository).save(feature);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getFeature_Success() {
        // Arrange
        when(repository.findById(FEATURE_ID)).thenReturn(Mono.just(feature));
        when(mapper.toDto(feature)).thenReturn(featureDTO);

        // Act & Assert
        StepVerifier.create(service.getFeature(PRODUCT_ID, FEATURE_ID))
                .expectNext(featureDTO)
                .verifyComplete();

        verify(repository).findById(FEATURE_ID);
        verify(mapper).toDto(feature);
    }

    @Test
    void getFeature_NotFound() {
        // Arrange
        when(repository.findById(FEATURE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getFeature(PRODUCT_ID, FEATURE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while retrieving feature"))
                .verify();

        verify(repository).findById(FEATURE_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getFeature_WrongProduct() {
        // Arrange
        ProductFeature featureFromDifferentProduct = new ProductFeature();
        featureFromDifferentProduct.setProductFeatureId(FEATURE_ID);
        featureFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(FEATURE_ID)).thenReturn(Mono.just(featureFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.getFeature(PRODUCT_ID, FEATURE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while retrieving feature"))
                .verify();

        verify(repository).findById(FEATURE_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateFeature_Success() {
        // Arrange
        ProductFeature existingFeature = new ProductFeature();
        existingFeature.setProductFeatureId(FEATURE_ID);
        existingFeature.setProductId(PRODUCT_ID);
        existingFeature.setFeatureName("Old Name");
        existingFeature.setFeatureDescription("Old Description");
        existingFeature.setFeatureType(FeatureTypeEnum.OPTIONAL);
        existingFeature.setIsMandatory(false);

        // Create a DTO with the updated values
        ProductFeatureDTO updateRequest = ProductFeatureDTO.builder()
                .featureName("Test Feature")
                .featureDescription("Test Description")
                .featureType(FeatureTypeEnum.STANDARD)
                .isMandatory(true)
                .build();

        when(repository.findById(FEATURE_ID)).thenReturn(Mono.just(existingFeature));

        // Mock the toEntity call with the updated DTO
        ProductFeature updatedEntity = new ProductFeature();
        updatedEntity.setProductFeatureId(FEATURE_ID);
        updatedEntity.setProductId(PRODUCT_ID);
        updatedEntity.setFeatureName("Test Feature");
        updatedEntity.setFeatureDescription("Test Description");
        updatedEntity.setFeatureType(FeatureTypeEnum.STANDARD);
        updatedEntity.setIsMandatory(true);

        when(mapper.toEntity(updateRequest)).thenReturn(updatedEntity);

        // Mock the save call
        when(repository.save(any(ProductFeature.class))).thenReturn(Mono.just(updatedEntity));

        // Mock the final toDto call
        when(mapper.toDto(updatedEntity)).thenReturn(featureDTO);

        // Act & Assert
        StepVerifier.create(service.updateFeature(PRODUCT_ID, FEATURE_ID, updateRequest))
                .expectNext(featureDTO)
                .verifyComplete();

        verify(repository).findById(FEATURE_ID);
        verify(mapper).toEntity(updateRequest);
        verify(repository).save(any(ProductFeature.class));
        verify(mapper).toDto(any(ProductFeature.class));
    }

    @Test
    void updateFeature_NotFound() {
        // Arrange
        when(repository.findById(FEATURE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updateFeature(PRODUCT_ID, FEATURE_ID, featureDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while updating feature"))
                .verify();

        verify(repository).findById(FEATURE_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateFeature_WrongProduct() {
        // Arrange
        ProductFeature featureFromDifferentProduct = new ProductFeature();
        featureFromDifferentProduct.setProductFeatureId(FEATURE_ID);
        featureFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(FEATURE_ID)).thenReturn(Mono.just(featureFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.updateFeature(PRODUCT_ID, FEATURE_ID, featureDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while updating feature"))
                .verify();

        verify(repository).findById(FEATURE_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deleteFeature_Success() {
        // Arrange
        when(repository.findById(FEATURE_ID)).thenReturn(Mono.just(feature));
        when(repository.delete(feature)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteFeature(PRODUCT_ID, FEATURE_ID))
                .verifyComplete();

        verify(repository).findById(FEATURE_ID);
        verify(repository).delete(feature);
    }

    @Test
    void deleteFeature_NotFound() {
        // Arrange
        when(repository.findById(FEATURE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteFeature(PRODUCT_ID, FEATURE_ID))
                .verifyComplete(); // Note: The service doesn't throw an error if the feature doesn't exist

        verify(repository).findById(FEATURE_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteFeature_WrongProduct() {
        // Arrange
        ProductFeature featureFromDifferentProduct = new ProductFeature();
        featureFromDifferentProduct.setProductFeatureId(FEATURE_ID);
        featureFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(FEATURE_ID)).thenReturn(Mono.just(featureFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.deleteFeature(PRODUCT_ID, FEATURE_ID))
                .verifyComplete(); // Note: The service doesn't throw an error if the feature is not in the product

        verify(repository).findById(FEATURE_ID);
        verify(repository, never()).delete(any());
    }
}
