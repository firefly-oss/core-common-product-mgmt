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
import com.firefly.core.product.core.mappers.pricing.v1.ProductPricingMapper;
import com.firefly.core.product.interfaces.dtos.pricing.v1.ProductPricingDTO;
import com.firefly.core.product.interfaces.enums.pricing.v1.PricingTypeEnum;
import com.firefly.core.product.models.entities.pricing.v1.ProductPricing;
import com.firefly.core.product.models.repositories.pricing.v1.ProductPricingRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductPricingServiceImplTest {

    @Mock
    private ProductPricingRepository repository;

    @Mock
    private ProductPricingMapper mapper;

    @InjectMocks
    private ProductPricingServiceImpl service;

    private ProductPricing pricing;
    private ProductPricingDTO pricingDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID PRICING_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDate nextYear = today.plusYears(1);

        pricing = new ProductPricing();
        pricing.setProductPricingId(PRICING_ID);
        pricing.setProductId(PRODUCT_ID);
        pricing.setPricingType(PricingTypeEnum.INTEREST_RATE);
        pricing.setAmountValue(new BigDecimal("5.75"));
        pricing.setAmountUnit("PERCENT");
        pricing.setPricingCondition("Standard rate for premium customers");
        pricing.setEffectiveDate(today);
        pricing.setExpiryDate(nextYear);
        pricing.setDateCreated(now);
        pricing.setDateUpdated(now);

        pricingDTO = ProductPricingDTO.builder()
                .productPricingId(PRICING_ID)
                .productId(PRODUCT_ID)
                .pricingType(PricingTypeEnum.INTEREST_RATE)
                .amountValue(new BigDecimal("5.75"))
                .amountUnit("PERCENT")
                .pricingCondition("Standard rate for premium customers")
                .effectiveDate(today)
                .expiryDate(nextYear)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getAllPricings_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(pricing));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(pricing)).thenReturn(pricingDTO);

        // Act & Assert
        StepVerifier.create(service.getAllPricings(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductPricingDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(pricingDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(pricing);
    }

    @Test
    void createPricing_Success() {
        // Arrange
        ProductPricingDTO requestDTO = ProductPricingDTO.builder()
                .pricingType(PricingTypeEnum.INTEREST_RATE)
                .amountValue(new BigDecimal("5.75"))
                .amountUnit("PERCENT")
                .pricingCondition("Standard rate for premium customers")
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(1))
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(pricing);
        when(repository.save(pricing)).thenReturn(Mono.just(pricing));
        when(mapper.toDto(pricing)).thenReturn(pricingDTO);

        // Act & Assert
        StepVerifier.create(service.createPricing(PRODUCT_ID, requestDTO))
                .expectNext(pricingDTO)
                .verifyComplete();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(pricing);
        verify(mapper).toDto(pricing);
    }

    @Test
    void createPricing_Error() {
        // Arrange
        ProductPricingDTO requestDTO = ProductPricingDTO.builder()
                .pricingType(PricingTypeEnum.INTEREST_RATE)
                .amountValue(new BigDecimal("5.75"))
                .amountUnit("PERCENT")
                .pricingCondition("Standard rate for premium customers")
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(1))
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(pricing);
        when(repository.save(pricing)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createPricing(PRODUCT_ID, requestDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to create pricing"))
                .verify();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(pricing);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getPricing_Success() {
        // Arrange
        when(repository.findById(PRICING_ID)).thenReturn(Mono.just(pricing));
        when(mapper.toDto(pricing)).thenReturn(pricingDTO);

        // Act & Assert
        StepVerifier.create(service.getPricing(PRODUCT_ID, PRICING_ID))
                .expectNext(pricingDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(mapper).toDto(pricing);
    }

    @Test
    void getPricing_NotFound() {
        // Arrange
        when(repository.findById(PRICING_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getPricing(PRODUCT_ID, PRICING_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to retrieve pricing"))
                .verify();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getPricing_WrongProduct() {
        // Arrange
        ProductPricing pricingFromDifferentProduct = new ProductPricing();
        pricingFromDifferentProduct.setProductPricingId(PRICING_ID);
        pricingFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(PRICING_ID)).thenReturn(Mono.just(pricingFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.getPricing(PRODUCT_ID, PRICING_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to retrieve pricing"))
                .verify();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updatePricing_Success() {
        // Arrange
        ProductPricingDTO updateRequest = ProductPricingDTO.builder()
                .pricingType(PricingTypeEnum.FEE)
                .amountValue(new BigDecimal("25.00"))
                .amountUnit("USD")
                .pricingCondition("Monthly maintenance fee")
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(6))
                .build();

        ProductPricing updatedEntity = new ProductPricing();
        updatedEntity.setProductPricingId(PRICING_ID);
        updatedEntity.setProductId(PRODUCT_ID);
        updatedEntity.setPricingType(PricingTypeEnum.FEE);
        updatedEntity.setAmountValue(new BigDecimal("25.00"));
        updatedEntity.setAmountUnit("USD");
        updatedEntity.setPricingCondition("Monthly maintenance fee");
        updatedEntity.setEffectiveDate(updateRequest.getEffectiveDate());
        updatedEntity.setExpiryDate(updateRequest.getExpiryDate());

        when(repository.findById(PRICING_ID)).thenReturn(Mono.just(pricing));
        doNothing().when(mapper).updateEntityFromDto(updateRequest, pricing);
        when(repository.save(pricing)).thenReturn(Mono.just(pricing));
        when(mapper.toDto(pricing)).thenReturn(updateRequest);

        // Act & Assert
        StepVerifier.create(service.updatePricing(PRODUCT_ID, PRICING_ID, updateRequest))
                .expectNext(updateRequest)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(mapper).updateEntityFromDto(updateRequest, pricing);
        verify(repository).save(pricing);
        verify(mapper).toDto(pricing);
    }

    @Test
    void updatePricing_NotFound() {
        // Arrange
        ProductPricingDTO updateRequest = ProductPricingDTO.builder()
                .pricingType(PricingTypeEnum.FEE)
                .amountValue(new BigDecimal("25.00"))
                .amountUnit("USD")
                .pricingCondition("Monthly maintenance fee")
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(6))
                .build();

        when(repository.findById(PRICING_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updatePricing(PRODUCT_ID, PRICING_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to update pricing"))
                .verify();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updatePricing_WrongProduct() {
        // Arrange
        ProductPricingDTO updateRequest = ProductPricingDTO.builder()
                .pricingType(PricingTypeEnum.FEE)
                .amountValue(new BigDecimal("25.00"))
                .amountUnit("USD")
                .pricingCondition("Monthly maintenance fee")
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(6))
                .build();

        ProductPricing pricingFromDifferentProduct = new ProductPricing();
        pricingFromDifferentProduct.setProductPricingId(PRICING_ID);
        pricingFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(PRICING_ID)).thenReturn(Mono.just(pricingFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.updatePricing(PRODUCT_ID, PRICING_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to update pricing"))
                .verify();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deletePricing_Success() {
        // Arrange
        when(repository.findById(PRICING_ID)).thenReturn(Mono.just(pricing));
        when(repository.delete(pricing)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deletePricing(PRODUCT_ID, PRICING_ID))
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(repository).delete(pricing);
    }

    @Test
    void deletePricing_NotFound() {
        // Arrange
        when(repository.findById(PRICING_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deletePricing(PRODUCT_ID, PRICING_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to delete pricing"))
                .verify();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deletePricing_WrongProduct() {
        // Arrange
        ProductPricing pricingFromDifferentProduct = new ProductPricing();
        pricingFromDifferentProduct.setProductPricingId(PRICING_ID);
        pricingFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(PRICING_ID)).thenReturn(Mono.just(pricingFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.deletePricing(PRODUCT_ID, PRICING_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to delete pricing"))
                .verify();

        // Verify interactions
        verify(repository).findById(PRICING_ID);
        verify(repository, never()).delete(any());
    }
}
