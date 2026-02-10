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
import com.firefly.core.product.core.mappers.pricing.v1.ProductPricingLocalizationMapper;
import com.firefly.core.product.interfaces.dtos.pricing.v1.ProductPricingLocalizationDTO;
import com.firefly.core.product.models.entities.pricing.v1.ProductPricingLocalization;
import com.firefly.core.product.models.repositories.pricing.v1.ProductPricingLocalizationRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductPricingLocalizationServiceImplTest {

    @Mock
    private ProductPricingLocalizationRepository repository;

    @Mock
    private ProductPricingLocalizationMapper mapper;

    @InjectMocks
    private ProductPricingLocalizationServiceImpl service;

    private ProductPricingLocalization localization;
    private ProductPricingLocalizationDTO localizationDTO;
    private final UUID PRICING_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID LOCALIZATION_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        localization = new ProductPricingLocalization();
        localization.setProductPricingLocalizationId(LOCALIZATION_ID);
        localization.setProductPricingId(PRICING_ID);
        localization.setCurrencyCode("USD");
        localization.setLocalizedAmountValue(new BigDecimal("100.00"));
        localization.setDateCreated(now);
        localization.setDateUpdated(now);

        localizationDTO = ProductPricingLocalizationDTO.builder()
                .productPricingLocalizationId(LOCALIZATION_ID)
                .productPricingId(PRICING_ID)
                .currencyCode("USD")
                .localizedAmountValue(new BigDecimal("100.00"))
                .dateCreated(now)
                .dateUpdated(now)
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
        when(repository.findByProductPricingId(eq(PRICING_ID), eq(pageable))).thenReturn(Flux.just(localization));
        when(repository.countByProductPricingId(PRICING_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(localization)).thenReturn(localizationDTO);

        // Act & Assert
        StepVerifier.create(service.getAllLocalizations(PRICING_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductPricingLocalizationDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(localizationDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductPricingId(eq(PRICING_ID), eq(pageable));
        verify(repository).countByProductPricingId(PRICING_ID);
        verify(mapper).toDto(localization);
    }

    @Test
    void createLocalization_Success() {
        // Arrange
        ProductPricingLocalizationDTO requestDTO = ProductPricingLocalizationDTO.builder()
                .currencyCode("USD")
                .localizedAmountValue(new BigDecimal("100.00"))
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(localization);
        when(repository.save(localization)).thenReturn(Mono.just(localization));
        when(mapper.toDto(localization)).thenReturn(localizationDTO);

        // Act & Assert
        StepVerifier.create(service.createLocalization(PRICING_ID, requestDTO))
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
        ProductPricingLocalizationDTO requestDTO = ProductPricingLocalizationDTO.builder()
                .currencyCode("USD")
                .localizedAmountValue(new BigDecimal("100.00"))
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(localization);
        when(repository.save(localization)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createLocalization(PRICING_ID, requestDTO))
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
    void getLocalization_Success() {
        // Arrange
        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localization));
        when(mapper.toDto(localization)).thenReturn(localizationDTO);

        // Act & Assert
        StepVerifier.create(service.getLocalization(PRICING_ID, LOCALIZATION_ID))
                .expectNext(localizationDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(mapper).toDto(localization);
    }

    @Test
    void getLocalization_NotFound() {
        // Arrange
        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getLocalization(PRICING_ID, LOCALIZATION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to retrieve localization"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getLocalization_WrongPricing() {
        // Arrange
        ProductPricingLocalization localizationFromDifferentPricing = new ProductPricingLocalization();
        localizationFromDifferentPricing.setProductPricingLocalizationId(LOCALIZATION_ID);
        localizationFromDifferentPricing.setProductPricingId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different pricing ID

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localizationFromDifferentPricing));

        // Act & Assert
        StepVerifier.create(service.getLocalization(PRICING_ID, LOCALIZATION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to retrieve localization"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateLocalization_Success() {
        // Arrange
        ProductPricingLocalizationDTO updateRequest = ProductPricingLocalizationDTO.builder()
                .currencyCode("EUR")
                .localizedAmountValue(new BigDecimal("120.00"))
                .build();

        ProductPricingLocalization updatedEntity = new ProductPricingLocalization();
        updatedEntity.setProductPricingLocalizationId(LOCALIZATION_ID);
        updatedEntity.setProductPricingId(PRICING_ID);
        updatedEntity.setCurrencyCode("EUR");
        updatedEntity.setLocalizedAmountValue(new BigDecimal("120.00"));

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localization));
        when(mapper.toEntity(any(ProductPricingLocalizationDTO.class))).thenReturn(updatedEntity);
        when(repository.save(any(ProductPricingLocalization.class))).thenReturn(Mono.just(updatedEntity));
        when(mapper.toDto(updatedEntity)).thenReturn(updateRequest);

        // Act & Assert
        StepVerifier.create(service.updateLocalization(PRICING_ID, LOCALIZATION_ID, updateRequest))
                .expectNext(updateRequest)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository).save(any(ProductPricingLocalization.class));
        verify(mapper).toDto(any(ProductPricingLocalization.class));
    }

    @Test
    void updateLocalization_NotFound() {
        // Arrange
        ProductPricingLocalizationDTO updateRequest = ProductPricingLocalizationDTO.builder()
                .currencyCode("EUR")
                .localizedAmountValue(new BigDecimal("120.00"))
                .build();

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updateLocalization(PRICING_ID, LOCALIZATION_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to update localization"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateLocalization_WrongPricing() {
        // Arrange
        ProductPricingLocalizationDTO updateRequest = ProductPricingLocalizationDTO.builder()
                .currencyCode("EUR")
                .localizedAmountValue(new BigDecimal("120.00"))
                .build();

        ProductPricingLocalization localizationFromDifferentPricing = new ProductPricingLocalization();
        localizationFromDifferentPricing.setProductPricingLocalizationId(LOCALIZATION_ID);
        localizationFromDifferentPricing.setProductPricingId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different pricing ID

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localizationFromDifferentPricing));

        // Act & Assert
        StepVerifier.create(service.updateLocalization(PRICING_ID, LOCALIZATION_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to update localization"))
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
        StepVerifier.create(service.deleteLocalization(PRICING_ID, LOCALIZATION_ID))
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
        StepVerifier.create(service.deleteLocalization(PRICING_ID, LOCALIZATION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to delete localization"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteLocalization_WrongPricing() {
        // Arrange
        ProductPricingLocalization localizationFromDifferentPricing = new ProductPricingLocalization();
        localizationFromDifferentPricing.setProductPricingLocalizationId(LOCALIZATION_ID);
        localizationFromDifferentPricing.setProductPricingId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different pricing ID

        when(repository.findById(LOCALIZATION_ID)).thenReturn(Mono.just(localizationFromDifferentPricing));

        // Act & Assert
        StepVerifier.create(service.deleteLocalization(PRICING_ID, LOCALIZATION_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to delete localization"))
                .verify();

        // Verify interactions
        verify(repository).findById(LOCALIZATION_ID);
        verify(repository, never()).delete(any());
    }
}
