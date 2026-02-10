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


package com.firefly.core.product.core.services.category.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.mappers.category.v1.ProductSubtypeMapper;
import com.firefly.core.product.interfaces.dtos.category.v1.ProductCategorySubtypeDTO;
import com.firefly.core.product.models.entities.category.v1.ProductSubtype;
import com.firefly.core.product.models.repositories.category.v1.ProductSubtypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ProductCategorySubtypeServiceImplTest {

    @Mock
    private ProductSubtypeRepository repository;

    @Mock
    private ProductSubtypeMapper mapper;

    @InjectMocks
    private ProductCategorySubtypeServiceImpl service;

    private ProductSubtype productSubtype;
    private ProductCategorySubtypeDTO productSubtypeDTO;
    private final UUID CATEGORY_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID SUBTYPE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        productSubtype = new ProductSubtype();
        productSubtype.setProductSubtypeId(SUBTYPE_ID);
        productSubtype.setProductCategoryId(CATEGORY_ID);
        productSubtype.setSubtypeName("Test Subtype");
        productSubtype.setSubtypeDescription("Test Description");
        productSubtype.setDateCreated(now);
        productSubtype.setDateUpdated(now);

        productSubtypeDTO = ProductCategorySubtypeDTO.builder()
                .productSubtypeId(SUBTYPE_ID)
                .productCategoryId(CATEGORY_ID)
                .subtypeName("Test Subtype")
                .subtypeDescription("Test Description")
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getAllByCategoryId_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductCategoryId(eq(CATEGORY_ID), eq(pageable))).thenReturn(Flux.just(productSubtype));
        when(repository.countByProductCategoryId(CATEGORY_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(productSubtype)).thenReturn(productSubtypeDTO);

        // Act & Assert
        StepVerifier.create(service.getAllByCategoryId(CATEGORY_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductCategorySubtypeDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(productSubtypeDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductCategoryId(eq(CATEGORY_ID), eq(pageable));
        verify(repository).countByProductCategoryId(CATEGORY_ID);
        verify(mapper).toDto(productSubtype);
    }

    @Test
    void create_Success() {
        // Arrange
        when(repository.findBySubtypeName(productSubtypeDTO.getSubtypeName())).thenReturn(Mono.empty());
        when(mapper.toEntity(productSubtypeDTO)).thenReturn(productSubtype);
        when(repository.save(productSubtype)).thenReturn(Mono.just(productSubtype));
        when(mapper.toDto(productSubtype)).thenReturn(productSubtypeDTO);

        // Act & Assert
        StepVerifier.create(service.create(CATEGORY_ID, productSubtypeDTO))
                .expectNext(productSubtypeDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findBySubtypeName(productSubtypeDTO.getSubtypeName());
        verify(mapper).toEntity(productSubtypeDTO);
        verify(repository).save(productSubtype);
        verify(mapper).toDto(productSubtype);

        // Verify that the category ID was set
        assertEquals(CATEGORY_ID, productSubtypeDTO.getProductCategoryId());
    }

    @Test
    void create_SubtypeNameExists() {
        // Arrange
        when(repository.findBySubtypeName(productSubtypeDTO.getSubtypeName())).thenReturn(Mono.just(productSubtype));

        // Act & Assert
        StepVerifier.create(service.create(CATEGORY_ID, productSubtypeDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Subtype creation process failed"))
                .verify();

        // Verify interactions
        verify(repository).findBySubtypeName(productSubtypeDTO.getSubtypeName());
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getById_Success() {
        // Arrange
        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.just(productSubtype));
        when(mapper.toDto(productSubtype)).thenReturn(productSubtypeDTO);

        // Act & Assert
        StepVerifier.create(service.getById(CATEGORY_ID, SUBTYPE_ID))
                .expectNext(productSubtypeDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(mapper).toDto(productSubtype);
    }

    @Test
    void getById_NotFound() {
        // Arrange
        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getById(CATEGORY_ID, SUBTYPE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Failed to retrieve the subtype"))
                .verify();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getById_WrongCategory() {
        // Arrange
        ProductSubtype subtypeFromDifferentCategory = new ProductSubtype();
        subtypeFromDifferentCategory.setProductSubtypeId(SUBTYPE_ID);
        subtypeFromDifferentCategory.setProductCategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different category ID

        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.just(subtypeFromDifferentCategory));

        // Act & Assert
        StepVerifier.create(service.getById(CATEGORY_ID, SUBTYPE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Failed to retrieve the subtype"))
                .verify();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void update_Success() {
        // Arrange
        ProductSubtype existingSubtype = spy(new ProductSubtype());
        existingSubtype.setProductSubtypeId(SUBTYPE_ID);
        existingSubtype.setProductCategoryId(CATEGORY_ID);
        existingSubtype.setSubtypeName("Old Name");
        existingSubtype.setSubtypeDescription("Old Description");

        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.just(existingSubtype));
        when(repository.save(existingSubtype)).thenReturn(Mono.just(existingSubtype));
        when(mapper.toDto(existingSubtype)).thenReturn(productSubtypeDTO);

        // Act & Assert
        StepVerifier.create(service.update(CATEGORY_ID, SUBTYPE_ID, productSubtypeDTO))
                .expectNext(productSubtypeDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(repository).save(existingSubtype);
        verify(mapper).toDto(existingSubtype);

        // Verify that the fields were updated
        verify(existingSubtype).setSubtypeName(productSubtypeDTO.getSubtypeName());
        verify(existingSubtype).setSubtypeDescription(productSubtypeDTO.getSubtypeDescription());
    }

    @Test
    void update_NotFound() {
        // Arrange
        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.update(CATEGORY_ID, SUBTYPE_ID, productSubtypeDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Subtype update process failed"))
                .verify();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void update_WrongCategory() {
        // Arrange
        ProductSubtype subtypeFromDifferentCategory = new ProductSubtype();
        subtypeFromDifferentCategory.setProductSubtypeId(SUBTYPE_ID);
        subtypeFromDifferentCategory.setProductCategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different category ID

        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.just(subtypeFromDifferentCategory));

        // Act & Assert
        StepVerifier.create(service.update(CATEGORY_ID, SUBTYPE_ID, productSubtypeDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Subtype update process failed"))
                .verify();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void delete_Success() {
        // Arrange
        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.just(productSubtype));
        when(repository.delete(productSubtype)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.delete(CATEGORY_ID, SUBTYPE_ID))
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(repository).delete(productSubtype);
    }

    @Test
    void delete_NotFound() {
        // Arrange
        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.delete(CATEGORY_ID, SUBTYPE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Subtype deletion process failed"))
                .verify();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void delete_WrongCategory() {
        // Arrange
        ProductSubtype subtypeFromDifferentCategory = new ProductSubtype();
        subtypeFromDifferentCategory.setProductSubtypeId(SUBTYPE_ID);
        subtypeFromDifferentCategory.setProductCategoryId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different category ID

        when(repository.findById(SUBTYPE_ID)).thenReturn(Mono.just(subtypeFromDifferentCategory));

        // Act & Assert
        StepVerifier.create(service.delete(CATEGORY_ID, SUBTYPE_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Subtype deletion process failed"))
                .verify();

        // Verify interactions
        verify(repository).findById(SUBTYPE_ID);
        verify(repository, never()).delete(any());
    }
}
