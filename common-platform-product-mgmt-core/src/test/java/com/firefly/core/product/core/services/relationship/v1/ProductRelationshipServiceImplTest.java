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


package com.firefly.core.product.core.services.relationship.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import com.firefly.core.product.core.mappers.relationship.v1.ProductRelationshipMapper;
import com.firefly.core.product.interfaces.dtos.relationship.v1.ProductRelationshipDTO;
import com.firefly.core.product.interfaces.enums.relationship.v1.RelationshipTypeEnum;
import com.firefly.core.product.models.entities.relationship.v1.ProductRelationship;
import com.firefly.core.product.models.repositories.relationship.v1.ProductRelationshipRepository;
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
class ProductRelationshipServiceImplTest {

    @Mock
    private ProductRelationshipRepository repository;

    @Mock
    private ProductRelationshipMapper mapper;

    @InjectMocks
    private ProductRelationshipServiceImpl service;

    private ProductRelationship relationship;
    private ProductRelationshipDTO relationshipDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID RELATIONSHIP_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private final UUID RELATED_PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440003");

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        relationship = new ProductRelationship();
        relationship.setProductRelationshipId(RELATIONSHIP_ID);
        relationship.setProductId(PRODUCT_ID);
        relationship.setRelatedProductId(RELATED_PRODUCT_ID);
        relationship.setRelationshipType(RelationshipTypeEnum.COMPLIMENTARY);
        relationship.setDescription("Complimentary product relationship");
        relationship.setDateCreated(now);
        relationship.setDateUpdated(now);

        relationshipDTO = ProductRelationshipDTO.builder()
                .productRelationshipId(RELATIONSHIP_ID)
                .productId(PRODUCT_ID)
                .relatedProductId(RELATED_PRODUCT_ID)
                .relationshipType(RelationshipTypeEnum.COMPLIMENTARY)
                .description("Complimentary product relationship")
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getAllRelationships_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(relationship));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(relationship)).thenReturn(relationshipDTO);

        // Act & Assert
        StepVerifier.create(service.getAllRelationships(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductRelationshipDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(relationshipDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(relationship);
    }

    @Test
    void createRelationship_Success() {
        // Arrange
        ProductRelationshipDTO requestDTO = ProductRelationshipDTO.builder()
                .relatedProductId(RELATED_PRODUCT_ID)
                .relationshipType(RelationshipTypeEnum.COMPLIMENTARY)
                .description("Complimentary product relationship")
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(relationship);
        when(repository.save(relationship)).thenReturn(Mono.just(relationship));
        when(mapper.toDto(relationship)).thenReturn(relationshipDTO);

        // Act & Assert
        StepVerifier.create(service.createRelationship(PRODUCT_ID, requestDTO))
                .expectNext(relationshipDTO)
                .verifyComplete();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(relationship);
        verify(mapper).toDto(relationship);
    }

    @Test
    void createRelationship_Error() {
        // Arrange
        ProductRelationshipDTO requestDTO = ProductRelationshipDTO.builder()
                .relatedProductId(RELATED_PRODUCT_ID)
                .relationshipType(RelationshipTypeEnum.COMPLIMENTARY)
                .description("Complimentary product relationship")
                .build();

        when(mapper.toEntity(requestDTO)).thenReturn(relationship);
        when(repository.save(relationship)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createRelationship(PRODUCT_ID, requestDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while creating the relationship"))
                .verify();

        // Verify interactions
        verify(mapper).toEntity(requestDTO);
        verify(repository).save(relationship);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getRelationship_Success() {
        // Arrange
        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.just(relationship));
        when(mapper.toDto(relationship)).thenReturn(relationshipDTO);

        // Act & Assert
        StepVerifier.create(service.getRelationship(PRODUCT_ID, RELATIONSHIP_ID))
                .expectNext(relationshipDTO)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(mapper).toDto(relationship);
    }

    @Test
    void getRelationship_NotFound() {
        // Arrange
        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getRelationship(PRODUCT_ID, RELATIONSHIP_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while retrieving the relationship"))
                .verify();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getRelationship_WrongProduct() {
        // Arrange
        ProductRelationship relationshipFromDifferentProduct = new ProductRelationship();
        relationshipFromDifferentProduct.setProductRelationshipId(RELATIONSHIP_ID);
        relationshipFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.just(relationshipFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.getRelationship(PRODUCT_ID, RELATIONSHIP_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while retrieving the relationship"))
                .verify();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateRelationship_Success() {
        // Arrange
        ProductRelationshipDTO updateRequest = ProductRelationshipDTO.builder()
                .relatedProductId(RELATED_PRODUCT_ID)
                .relationshipType(RelationshipTypeEnum.UPGRADE)
                .description("Upgrade product relationship")
                .build();

        ProductRelationship updatedEntity = new ProductRelationship();
        updatedEntity.setProductRelationshipId(RELATIONSHIP_ID);
        updatedEntity.setProductId(PRODUCT_ID);
        updatedEntity.setRelatedProductId(RELATED_PRODUCT_ID);
        updatedEntity.setRelationshipType(RelationshipTypeEnum.UPGRADE);
        updatedEntity.setDescription("Upgrade product relationship");

        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.just(relationship));
        when(mapper.toEntity(updateRequest)).thenReturn(updatedEntity);
        when(repository.save(any(ProductRelationship.class))).thenReturn(Mono.just(updatedEntity));
        when(mapper.toDto(updatedEntity)).thenReturn(updateRequest);

        // Act & Assert
        StepVerifier.create(service.updateRelationship(PRODUCT_ID, RELATIONSHIP_ID, updateRequest))
                .expectNext(updateRequest)
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(mapper).toEntity(updateRequest);
        verify(repository).save(any(ProductRelationship.class));
        verify(mapper).toDto(any(ProductRelationship.class));
    }

    @Test
    void updateRelationship_NotFound() {
        // Arrange
        ProductRelationshipDTO updateRequest = ProductRelationshipDTO.builder()
                .relatedProductId(RELATED_PRODUCT_ID)
                .relationshipType(RelationshipTypeEnum.UPGRADE)
                .description("Upgrade product relationship")
                .build();

        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updateRelationship(PRODUCT_ID, RELATIONSHIP_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while mapping the updated relationship"))
                .verify();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateRelationship_WrongProduct() {
        // Arrange
        ProductRelationshipDTO updateRequest = ProductRelationshipDTO.builder()
                .relatedProductId(RELATED_PRODUCT_ID)
                .relationshipType(RelationshipTypeEnum.UPGRADE)
                .description("Upgrade product relationship")
                .build();

        ProductRelationship relationshipFromDifferentProduct = new ProductRelationship();
        relationshipFromDifferentProduct.setProductRelationshipId(RELATIONSHIP_ID);
        relationshipFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.just(relationshipFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.updateRelationship(PRODUCT_ID, RELATIONSHIP_ID, updateRequest))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while mapping the updated relationship"))
                .verify();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deleteRelationship_Success() {
        // Arrange
        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.just(relationship));
        when(repository.delete(relationship)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteRelationship(PRODUCT_ID, RELATIONSHIP_ID))
                .verifyComplete();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(repository).delete(relationship);
    }

    @Test
    void deleteRelationship_NotFound() {
        // Arrange
        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteRelationship(PRODUCT_ID, RELATIONSHIP_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while deleting the relationship"))
                .verify();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteRelationship_WrongProduct() {
        // Arrange
        ProductRelationship relationshipFromDifferentProduct = new ProductRelationship();
        relationshipFromDifferentProduct.setProductRelationshipId(RELATIONSHIP_ID);
        relationshipFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(RELATIONSHIP_ID)).thenReturn(Mono.just(relationshipFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.deleteRelationship(PRODUCT_ID, RELATIONSHIP_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Error occurred while deleting the relationship"))
                .verify();

        // Verify interactions
        verify(repository).findById(RELATIONSHIP_ID);
        verify(repository, never()).delete(any());
    }
}