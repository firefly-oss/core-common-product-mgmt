package io.fireflysoftware.core.product.core.services.impl;

import io.fireflysoftware.core.product.core.mappers.ProductRelationshipMapper;
import io.fireflysoftware.core.product.interfaces.dtos.ProductRelationshipDTO;
import io.fireflysoftware.core.product.models.repositories.ProductRelationshipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductRelationshipServiceImplTest {

    @Mock
    private ProductRelationshipRepository repository;

    @Mock
    private ProductRelationshipMapper mapper;

    @InjectMocks
    private ProductRelationshipServiceImpl service;

    @Test
    void createRelationship_rejectsAProductRelatedToItself() {
        UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174040");
        ProductRelationshipDTO request = new ProductRelationshipDTO();
        request.setRelatedProductId(productId);

        StepVerifier.create(service.createRelationship(productId, request))
                .expectErrorMatches(error -> error.getMessage().contains("cannot be related to itself"))
                .verify();

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
