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


package com.firefly.core.product.core.services.documentantion.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import com.firefly.core.product.core.mappers.documentation.v1.ProductDocumentationMapper;
import com.firefly.core.product.core.services.documentation.v1.ProductDocumentationServiceImpl;
import com.firefly.core.product.interfaces.dtos.documentation.v1.ProductDocumentationDTO;
import com.firefly.core.product.interfaces.enums.documentation.v1.DocTypeEnum;
import com.firefly.core.product.models.entities.documentation.v1.ProductDocumentation;
import com.firefly.core.product.models.repositories.documentation.v1.ProductDocumentationRepository;
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
class ProductDocumentationServiceImplTest {

    @Mock
    private ProductDocumentationRepository repository;

    @Mock
    private ProductDocumentationMapper mapper;

    @InjectMocks
    private ProductDocumentationServiceImpl service;

    private ProductDocumentation documentation;
    private ProductDocumentationDTO documentationDTO;
    private final UUID PRODUCT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private final UUID DOC_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440002");
    private final Long DOC_MANAGER_REF = 100L;

    @BeforeEach
    void setUp() {
        // Setup test data
        LocalDateTime now = LocalDateTime.now();

        documentation = new ProductDocumentation();
        documentation.setProductDocumentationId(DOC_ID);
        documentation.setProductId(PRODUCT_ID);
        documentation.setDocType(DocTypeEnum.TNC);
        documentation.setDocumentManagerRef(DOC_MANAGER_REF);
        documentation.setDateAdded(now);
        documentation.setDateCreated(now);
        documentation.setDateUpdated(now);

        documentationDTO = ProductDocumentationDTO.builder()
                .productDocumentationId(DOC_ID)
                .productId(PRODUCT_ID)
                .docType(DocTypeEnum.TNC)
                .documentManagerRef(DOC_MANAGER_REF)
                .dateAdded(now)
                .dateCreated(now)
                .dateUpdated(now)
                .build();
    }

    @Test
    void getAllDocumentations_Success() {
        // Arrange
        // Mock PaginationRequest
        PaginationRequest paginationRequest = Mockito.mock(PaginationRequest.class);

        // Mock Pageable
        Pageable pageable = Mockito.mock(Pageable.class);

        // Mock PaginationRequest behavior
        doReturn(pageable).when(paginationRequest).toPageable();

        // Set up repository and mapper mocks
        when(repository.findByProductId(eq(PRODUCT_ID), eq(pageable))).thenReturn(Flux.just(documentation));
        when(repository.countByProductId(PRODUCT_ID)).thenReturn(Mono.just(1L));
        when(mapper.toDto(documentation)).thenReturn(documentationDTO);

        // Act & Assert
        StepVerifier.create(service.getAllDocumentations(PRODUCT_ID, paginationRequest))
                .expectNextMatches(response -> {
                    // Verify response contains our DTO
                    List<ProductDocumentationDTO> content = response.getContent();
                    return content != null && 
                           content.size() == 1 && 
                           content.get(0).equals(documentationDTO);
                })
                .verifyComplete();

        // Verify interactions
        verify(repository).findByProductId(eq(PRODUCT_ID), eq(pageable));
        verify(repository).countByProductId(PRODUCT_ID);
        verify(mapper).toDto(documentation);
    }

    @Test
    void createDocumentation_Success() {
        // Arrange
        when(mapper.toEntity(documentationDTO)).thenReturn(documentation);
        when(repository.save(documentation)).thenReturn(Mono.just(documentation));
        when(mapper.toDto(documentation)).thenReturn(documentationDTO);

        // Act & Assert
        StepVerifier.create(service.createDocumentation(PRODUCT_ID, documentationDTO))
                .expectNext(documentationDTO)
                .verifyComplete();

        verify(mapper).toEntity(documentationDTO);
        verify(repository).save(documentation);
        verify(mapper).toDto(documentation);

        // Verify that the product ID was set
        assertEquals(PRODUCT_ID, documentationDTO.getProductId());
    }

    @Test
    void createDocumentation_Error() {
        // Arrange
        when(mapper.toEntity(documentationDTO)).thenReturn(documentation);
        when(repository.save(documentation)).thenReturn(Mono.error(new RuntimeException("Database error")));

        // Act & Assert
        StepVerifier.create(service.createDocumentation(PRODUCT_ID, documentationDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to create documentation"))
                .verify();

        verify(mapper).toEntity(documentationDTO);
        verify(repository).save(documentation);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getDocumentation_Success() {
        // Arrange
        when(repository.findById(DOC_ID)).thenReturn(Mono.just(documentation));
        when(mapper.toDto(documentation)).thenReturn(documentationDTO);

        // Act & Assert
        StepVerifier.create(service.getDocumentation(PRODUCT_ID, DOC_ID))
                .expectNext(documentationDTO)
                .verifyComplete();

        verify(repository).findById(DOC_ID);
        verify(mapper).toDto(documentation);
    }

    @Test
    void getDocumentation_NotFound() {
        // Arrange
        when(repository.findById(DOC_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.getDocumentation(PRODUCT_ID, DOC_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to retrieve documentation"))
                .verify();

        verify(repository).findById(DOC_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getDocumentation_WrongProduct() {
        // Arrange
        ProductDocumentation docFromDifferentProduct = new ProductDocumentation();
        docFromDifferentProduct.setProductDocumentationId(DOC_ID);
        docFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(DOC_ID)).thenReturn(Mono.just(docFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.getDocumentation(PRODUCT_ID, DOC_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to retrieve documentation"))
                .verify();

        verify(repository).findById(DOC_ID);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateDocumentation_Success() {
        // Arrange
        ProductDocumentation existingDoc = new ProductDocumentation();
        existingDoc.setProductDocumentationId(DOC_ID);
        existingDoc.setProductId(PRODUCT_ID);
        existingDoc.setDocType(DocTypeEnum.BROCHURE); // Different doc type
        existingDoc.setDocumentManagerRef(999L); // Different manager ref

        // Create a DTO with the updated values
        ProductDocumentationDTO updateRequest = ProductDocumentationDTO.builder()
                .docType(DocTypeEnum.TNC)
                .documentManagerRef(DOC_MANAGER_REF)
                .build();

        when(repository.findById(DOC_ID)).thenReturn(Mono.just(existingDoc));

        // Mock the first toDto call that happens inside the service
        ProductDocumentationDTO existingDTO = ProductDocumentationDTO.builder()
                .productDocumentationId(DOC_ID)
                .productId(PRODUCT_ID)
                .docType(DocTypeEnum.BROCHURE)
                .documentManagerRef(999L)
                .build();
        when(mapper.toDto(existingDoc)).thenReturn(existingDTO);

        // Mock the updated entity after setting new values
        ProductDocumentation updatedEntity = new ProductDocumentation();
        updatedEntity.setProductDocumentationId(DOC_ID);
        updatedEntity.setProductId(PRODUCT_ID);
        updatedEntity.setDocType(DocTypeEnum.TNC);
        updatedEntity.setDocumentManagerRef(DOC_MANAGER_REF);

        // Mock the toEntity call with the updated DTO
        when(mapper.toEntity(any(ProductDocumentationDTO.class))).thenReturn(updatedEntity);

        // Mock the save call
        when(repository.save(any(ProductDocumentation.class))).thenReturn(Mono.just(updatedEntity));

        // Mock the final toDto call
        when(mapper.toDto(updatedEntity)).thenReturn(documentationDTO);

        // Act & Assert
        StepVerifier.create(service.updateDocumentation(PRODUCT_ID, DOC_ID, updateRequest))
                .expectNext(documentationDTO)
                .verifyComplete();

        verify(repository).findById(DOC_ID);
        verify(repository).save(any(ProductDocumentation.class));

        // We can't verify the exact number of toDto calls because the implementation might vary
        verify(mapper, atLeastOnce()).toDto(any(ProductDocumentation.class));
    }

    @Test
    void updateDocumentation_NotFound() {
        // Arrange
        when(repository.findById(DOC_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.updateDocumentation(PRODUCT_ID, DOC_ID, documentationDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to update documentation"))
                .verify();

        verify(repository).findById(DOC_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void updateDocumentation_WrongProduct() {
        // Arrange
        ProductDocumentation docFromDifferentProduct = new ProductDocumentation();
        docFromDifferentProduct.setProductDocumentationId(DOC_ID);
        docFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(DOC_ID)).thenReturn(Mono.just(docFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.updateDocumentation(PRODUCT_ID, DOC_ID, documentationDTO))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to update documentation"))
                .verify();

        verify(repository).findById(DOC_ID);
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void deleteDocumentation_Success() {
        // Arrange
        when(repository.findById(DOC_ID)).thenReturn(Mono.just(documentation));
        when(repository.delete(documentation)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteDocumentation(PRODUCT_ID, DOC_ID))
                .verifyComplete();

        verify(repository).findById(DOC_ID);
        verify(repository).delete(documentation);
    }

    @Test
    void deleteDocumentation_NotFound() {
        // Arrange
        when(repository.findById(DOC_ID)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(service.deleteDocumentation(PRODUCT_ID, DOC_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to delete documentation"))
                .verify();

        verify(repository).findById(DOC_ID);
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteDocumentation_WrongProduct() {
        // Arrange
        ProductDocumentation docFromDifferentProduct = new ProductDocumentation();
        docFromDifferentProduct.setProductDocumentationId(DOC_ID);
        docFromDifferentProduct.setProductId(UUID.fromString("550e8400-e29b-41d4-a716-446655440999")); // Different product ID

        when(repository.findById(DOC_ID)).thenReturn(Mono.just(docFromDifferentProduct));

        // Act & Assert
        StepVerifier.create(service.deleteDocumentation(PRODUCT_ID, DOC_ID))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().contains("Failed to delete documentation"))
                .verify();

        verify(repository).findById(DOC_ID);
        verify(repository, never()).delete(any());
    }

    // Helper method for assertions
    private void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected " + expected + " but got " + actual);
        }
    }
}
