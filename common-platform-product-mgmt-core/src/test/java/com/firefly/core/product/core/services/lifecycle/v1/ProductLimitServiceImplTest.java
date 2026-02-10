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
import com.firefly.core.product.core.mappers.lifecycle.v1.ProductLimitMapper;
import com.firefly.core.product.interfaces.dtos.lifecycle.v1.ProductLimitDTO;
import com.firefly.core.product.interfaces.enums.lifecycle.v1.LimitTypeEnum;
import com.firefly.core.product.interfaces.enums.lifecycle.v1.TimePeriodEnum;
import com.firefly.core.product.models.entities.lifecycle.v1.ProductLimit;
import com.firefly.core.product.models.repositories.lifecycle.v1.ProductLimitRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductLimitServiceImplTest {

    @Mock
    private ProductLimitRepository repository;

    @Mock
    private ProductLimitMapper mapper;

    @InjectMocks
    private ProductLimitServiceImpl service;

    private ProductLimit limit;
    private ProductLimitDTO limitDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID LIMIT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDate nextYear = today.plusYears(1);

        limit = new ProductLimit();
        limit.setProductLimitId(LIMIT_ID);
        limit.setProductId(PRODUCT_ID);
        limit.setLimitType(LimitTypeEnum.CREDIT_LIMIT);
        limit.setLimitValue(new BigDecimal("10000.00"));
        limit.setLimitUnit("USD");
        limit.setTimePeriod(TimePeriodEnum.MONTHLY);
        limit.setEffectiveDate(today);
        limit.setExpiryDate(nextYear);
        limit.setDateCreated(now);
        limit.setDateUpdated(now);

        limitDTO = ProductLimitDTO.builder()
                .productLimitId(LIMIT_ID)
                .productId(PRODUCT_ID)
                .limitType(LimitTypeEnum.CREDIT_LIMIT)
                .limitValue(new BigDecimal("10000.00"))
                .limitUnit("USD")
                .timePeriod(TimePeriodEnum.MONTHLY)
                .effectiveDate(today)
                .expiryDate(nextYear)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getAllProductLimits_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(limit));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(limit)).thenReturn(limitDTO);

        // Act & Assert
        StepVerifier.create(service.getAllProductLimits(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductLimitDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(limitDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(limit);
    }

    @Test
    void createProductLimit_Success() {
        // Arrange
        ProductLimitDTO requestDTO = ProductLimitDTO.builder()
                .limitType(LimitTypeEnum.CREDIT_LIMIT)
                .limitValue(new BigDecimal("10000.00"))
                .limitUnit("USD")
                .timePeriod(TimePeriodEnum.MONTHLY)
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(1))
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(limit);
        when(repository.save(limit)).thenReturn(Mono.just(limit));
        when(mapper.toDto(limit)).thenReturn(limitDTO);

        // Act & Assert
        StepVerifier.create(service.createProductLimit(PRODUCT_ID, requestDTO))
                .expectNext(limitDTO)
                .verifyComplete();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(limit);
        verify(mapper).toDto(limit);
    }

    @Test
    void createProductLimit_Error() {
        // Arrange
        ProductLimitDTO requestDTO = ProductLimitDTO.builder()
                .limitType(LimitTypeEnum.CREDIT_LIMIT)
                .limitValue(new BigDecimal("10000.00"))
                .limitUnit("USD")
                .timePeriod(TimePeriodEnum.MONTHLY)
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusYears(1))
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(limit);
        when(repository.save(limit)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createProductLimit(PRODUCT_ID, requestDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error creating product limit"))
                .verify();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(limit);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getProductLimit_Success() {
        // Arrange
        when(repository.findById(LIMIT_ID)).thenReturn(Mono.just(limit));
        when(mapper.toDto(limit)).thenReturn(limitDTO);

        // Act & Assert
        StepVerifier.create(service.getProductLimit(PRODUCT_ID, LIMIT_ID))
                .expectNext(limitDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(mapper).toDto(limit);
    }

    @Test
    void getProductLimit_NotFound() {
        // Arrange
        when(repository.findById(LIMIT_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getProductLimit(PRODUCT_ID, LIMIT_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error fetching product limit"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getProductLimit_WrongProduct() {
        // Arrange
        ProductLimit limitFromDifferentProduct = new ProductLimit();
        limitFromDifferentProduct.setProductLimitId(LIMIT_ID);
        limitFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LIMIT_ID)).thenReturn(Mono.just(limitFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.getProductLimit(PRODUCT_ID, LIMIT_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error fetching product limit"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateProductLimit_Success() {
        // Arrange
        ProductLimitDTO updateRequest = ProductLimitDTO.builder()
                .limitType(LimitTypeEnum.WITHDRAWAL_LIMIT)
                .limitValue(new BigDecimal("5000.00"))
                .limitUnit("USD")
                .timePeriod(TimePeriodEnum.DAILY)
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(6))
                .build();

        ProductLimit updatedEntity = new ProductLimit();
        updatedEntity.setProductLimitId(LIMIT_ID);
        updatedEntity.setProductId(PRODUCT_ID);
        updatedEntity.setLimitType(LimitTypeEnum.WITHDRAWAL_LIMIT);
        updatedEntity.setLimitValue(new BigDecimal("5000.00"));
        updatedEntity.setLimitUnit("USD");
        updatedEntity.setTimePeriod(TimePeriodEnum.DAILY);
        updatedEntity.setEffectiveDate(updateRequest.getEffectiveDate());
        updatedEntity.setExpiryDate(updateRequest.getExpiryDate());

        when(repository.findById(LIMIT_ID)).thenReturn(Mono.just(limit));
        when(mapper.toEntity(updateRequest)).thenReturn(updatedEntity);
        when(repository.save(any(ProductLimit.class))).thenReturn(Mono.just(updatedEntity));
        when(mapper.toDto(updatedEntity)).thenReturn(updateRequest);

        // Act & Assert
        StepVerifier.create(service.updateProductLimit(PRODUCT_ID, LIMIT_ID, updateRequest))
                .expectNext(updateRequest)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(mapper).toEntity(updateRequest);
        verify(repository).save(any(ProductLimit.class));
        verify(mapper).toDto(any(ProductLimit.class));
    }

    @Test
    void updateProductLimit_NotFound() {
        // Arrange
        ProductLimitDTO updateRequest = ProductLimitDTO.builder()
                .limitType(LimitTypeEnum.WITHDRAWAL_LIMIT)
                .limitValue(new BigDecimal("5000.00"))
                .limitUnit("USD")
                .timePeriod(TimePeriodEnum.DAILY)
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(6))
                .build();

        when(repository.findById(LIMIT_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updateProductLimit(PRODUCT_ID, LIMIT_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error updating product limit"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateProductLimit_WrongProduct() {
        // Arrange
        ProductLimitDTO updateRequest = ProductLimitDTO.builder()
                .limitType(LimitTypeEnum.WITHDRAWAL_LIMIT)
                .limitValue(new BigDecimal("5000.00"))
                .limitUnit("USD")
                .timePeriod(TimePeriodEnum.DAILY)
                .effectiveDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusMonths(6))
                .build();

        ProductLimit limitFromDifferentProduct = new ProductLimit();
        limitFromDifferentProduct.setProductLimitId(LIMIT_ID);
        limitFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LIMIT_ID)).thenReturn(Mono.just(limitFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.updateProductLimit(PRODUCT_ID, LIMIT_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error updating product limit"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deleteProductLimit_Success() {
        // Arrange
        when(repository.findById(LIMIT_ID)).thenReturn(Mono.just(limit));
        when(repository.delete(limit)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteProductLimit(PRODUCT_ID, LIMIT_ID))
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(repository).delete(limit);
    }

    @Test
    void deleteProductLimit_NotFound() {
        // Arrange
        when(repository.findById(LIMIT_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteProductLimit(PRODUCT_ID, LIMIT_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error deleting product limit"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteProductLimit_WrongProduct() {
        // Arrange
        ProductLimit limitFromDifferentProduct = new ProductLimit();
        limitFromDifferentProduct.setProductLimitId(LIMIT_ID);
        limitFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(LIMIT_ID)).thenReturn(Mono.just(limitFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.deleteProductLimit(PRODUCT_ID, LIMIT_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error deleting product limit"))
                .verify();

        // Verify interactions
        verify(repository).findById(LIMIT_ID);
        verify(repository, never()).delete(any());
    }
}