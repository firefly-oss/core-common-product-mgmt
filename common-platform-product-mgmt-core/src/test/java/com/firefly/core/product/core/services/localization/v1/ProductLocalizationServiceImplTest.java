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
import com.firefly.core.product.core.mappers.localization.v1.ProductLocalizationMapper;
import com.firefly.core.product.interfaces.dtos.localization.v1.ProductLocalizationDTO;
import com.firefly.core.product.models.entities.localization.v1.ProductLocalization;
import com.firefly.core.product.models.repositories.localization.v1.ProductLocalizationRepository;
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
class ProductLocalizationServiceImplTest {

    @Mock
    private ProductLocalizationRepository repository;

    @Mock
    private ProductLocalizationMapper mapper;

    @InjectMocks
    private ProductLocalizationServiceImpl service;

    private ProductLocalization localization;
    private ProductLocalizationDTO localizationDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID LOCALIZATION_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        localization = new ProductLocalization();
        localization.setProductLocalizationId(LOCALIZATION_ID);
        localization.setProductId(PRODUCT_ID);
        localization.setLanguageCode("en-US");
        localization.setLocalizedName("Test Product Name");
        localization.setLocalizedDescription("Test Product Description");
        localization.setDateCreated(now);
        localization.setDateUpdated(now);

        localizationDTO = ProductLocalizationDTO.builder()
                .productLocalizationId(LOCALIZATION_ID)
                .productId(PRODUCT_ID)
                .languageCode("en-US")
                .localizedName("Test Product Name")
                .localizedDescription("Test Product Description")
                .build();
    }

    @Test
    void getAllLocalizations_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findAllByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(localization));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(localization)).thenReturn(localizationDTO);

        // Act & Assert
        StepVerifier.create(service.getAllLocalizations(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductLocalizationDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(localizationDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findAllByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(localization);
    }

    @Test
    void createLocalization_Success() {
        // Arrange
        ProductLocalizationDTO requestDTO = ProductLocalizationDTO.builder()
                .languageCode("en-US")
                .localizedName("Test Product Name")
                .localizedDescription("Test Product Description")
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(localization);
        when(repository.save(localization)).thenReturn(Mono.just(localization));
        when(mapper.toDto(localization)).thenReturn(localizationDTO);

        // Act & Assert
        StepVerifier.create(service.createLocalization(PRODUCT_ID, requestDTO))
                .expectNext(localizationDTO)
                .verifyComplete();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(localization);
        verify(mapper).toDto(localization);
    }

    @Test
    void createLocalization_Error() {
        // Arrange
        ProductLocalizationDTO requestDTO = ProductLocalizationDTO.builder()
                .languageCode("en-US")
                .localizedName("Test Product Name")
                .localizedDescription("Test Product Description")
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(localization);
        when(repository.save(localization)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createLocalization(PRODUCT_ID, requestDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to create localization"))
                .verify();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(localization);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getLocalizationById_Success() {
        // Arrange
        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localization));
        when(mapper.toDto(localization)).thenReturn(localizationDTO);

        // Act & Assert
        StepVerifier.create(service.getLocalizationById(PRODUCT_ID, LOCALIZATION_ID))
                .expectNext(localizationDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(mapper).toDto(localization);
    }

    @Test
    void getLocalizationById_NotFound() {
        // Arrange
        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getLocalizationById(PRODUCT_ID, LOCALIZATION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to fetch localization by ID"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getLocalizationById_WrongProduct() {
        // Arrange
        ProductLocalization localizationFromDifferentProduct = new ProductLocalization();
        localizationFromDifferentProduct.setProductLocalizationId(LOCALIZATION_ID);
        localizationFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localizationFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.getLocalizationById(PRODUCT_ID, LOCALIZATION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to fetch localization by ID"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateLocalization_Success() {
        // Arrange
        ProductLocalizationDTO updateRequest = ProductLocalizationDTO.builder()
                .languageCode("fr-FR")
                .localizedName("Nom du Produit Test")
                .localizedDescription("Description du Produit Test")
                .build();

        ProductLocalization existingLocalization = new ProductLocalization();
        existingLocalization.setProductLocalizationId(LOCALIZATION_ID);
        existingLocalization.setProductId(PRODUCT_ID);
        existingLocalization.setLanguageCode("en-US");
        existingLocalization.setLocalizedName("Old Name");
        existingLocalization.setLocalizedDescription("Old Description");

        ProductLocalization updatedLocalization = new ProductLocalization();
        updatedLocalization.setProductLocalizationId(LOCALIZATION_ID);
        updatedLocalization.setProductId(PRODUCT_ID);
        updatedLocalization.setLanguageCode("fr-FR");
        updatedLocalization.setLocalizedName("Nom du Produit Test");
        updatedLocalization.setLocalizedDescription("Description du Produit Test");

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(existingLocalization));
        when(repository.save(any(ProductLocalization.class))).thenReturn(Mono.just(updatedLocalization));
        when(mapper.toDto(updatedLocalization)).thenReturn(updateRequest);

        // Act & Assert
        StepVerifier.create(service.updateLocalization(PRODUCT_ID, LOCALIZATION_ID, updateRequest))
                .expectNext(updateRequest)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository).save(any(ProductLocalization.class));
        verify(mapper).toDto(any(ProductLocalization.class));
    }

    @Test
    void updateLocalization_NotFound() {
        // Arrange
        ProductLocalizationDTO updateRequest = ProductLocalizationDTO.builder()
                .languageCode("fr-FR")
                .localizedName("Nom du Produit Test")
                .localizedDescription("Description du Produit Test")
                .build();

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updateLocalization(PRODUCT_ID, LOCALIZATION_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Localization not found for update"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateLocalization_WrongProduct() {
        // Arrange
        ProductLocalizationDTO updateRequest = ProductLocalizationDTO.builder()
                .languageCode("fr-FR")
                .localizedName("Nom du Produit Test")
                .localizedDescription("Description du Produit Test")
                .build();

        ProductLocalization localizationFromDifferentProduct = new ProductLocalization();
        localizationFromDifferentProduct.setProductLocalizationId(LOCALIZATION_ID);
        localizationFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localizationFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.updateLocalization(PRODUCT_ID, LOCALIZATION_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Localization not found for update"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deleteLocalization_Success() {
        // Arrange
        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localization));
        when(repository.delete(localization)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteLocalization(PRODUCT_ID, LOCALIZATION_ID))
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository).delete(localization);
    }

    @Test
    void deleteLocalization_NotFound() {
        // Arrange
        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteLocalization(PRODUCT_ID, LOCALIZATION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to delete localization"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteLocalization_WrongProduct() {
        // Arrange
        ProductLocalization localizationFromDifferentProduct = new ProductLocalization();
        localizationFromDifferentProduct.setProductLocalizationId(LOCALIZATION_ID);
        localizationFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localizationFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.deleteLocalization(PRODUCT_ID, LOCALIZATION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to delete localization"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository, never()).delete(any());
    }
}
