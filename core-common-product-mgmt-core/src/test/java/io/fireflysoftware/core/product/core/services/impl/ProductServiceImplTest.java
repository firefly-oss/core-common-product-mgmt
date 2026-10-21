/*
 * Copyright 2025 Firefly Software Foundation
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


package io.fireflysoftware.core.product.core.services.impl;

import io.fireflysoftware.core.product.core.mappers.ProductMapper;
import io.fireflysoftware.core.product.interfaces.enums.ProductStatusEnum;
import io.fireflysoftware.core.product.models.entities.Product;
import io.fireflysoftware.core.product.models.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductServiceImpl service;

    private final UUID productId = UUID.fromString("123e4567-e89b-12d3-a456-426614174030");

    @Test
    void deleteProduct_keepsAnOfferedDefinition() {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductStatus(ProductStatusEnum.ACTIVE);
        when(repository.findById(productId)).thenReturn(Mono.just(product));

        StepVerifier.create(service.deleteProduct(productId))
                .expectErrorMatches(error -> error.getMessage().contains("cannot be deleted"))
                .verify();

        verify(repository, never()).deleteById(productId);
    }

    @Test
    void deleteProduct_keepsASuspendedDefinition() {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductStatus(ProductStatusEnum.SUSPENDED);
        when(repository.findById(productId)).thenReturn(Mono.just(product));

        StepVerifier.create(service.deleteProduct(productId))
                .expectErrorMatches(error -> error.getMessage().contains("SUSPENDED"))
                .verify();
    }

    @Test
    void deleteProduct_removesADraftThatWasNeverOffered() {
        Product product = new Product();
        product.setProductId(productId);
        product.setProductStatus(ProductStatusEnum.DRAFT);
        when(repository.findById(productId)).thenReturn(Mono.just(product));
        when(repository.deleteById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(service.deleteProduct(productId))
                .verifyComplete();
    }
}
