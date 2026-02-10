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


package com.firefly.core.product.core.services.core.v1;

import org.fireflyframework.core.queries.PaginationRequest;
import org.fireflyframework.core.queries.PaginationResponse;
import org.fireflyframework.core.queries.PaginationUtils;
import com.firefly.core.product.core.mappers.core.v1.ProductMapper;
import com.firefly.core.product.interfaces.dtos.core.v1.ProductDTO;
import com.firefly.core.product.models.entities.core.v1.Product;
import com.firefly.core.product.models.repositories.product.v1.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ProductMapper mapper;

    @Override
    public Mono<PaginationResponse<ProductDTO>> getAllProducts(PaginationRequest paginationRequest) {
        return PaginationUtils.paginateQuery(
                paginationRequest,
                mapper::toDto,
                pageable -> repository.findAllBy(pageable),
                () -> repository.count()
        ).onErrorResume(e -> Mono.error(new RuntimeException("Failed to retrieve products", e)));
    }

    @Override
    public Mono<ProductDTO> createProduct(ProductDTO productDTO) {
        Product product = mapper.toEntity(productDTO);
        return repository.save(product)
                .map(mapper::toDto)
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to create product", e)));
    }

    @Override
    public Mono<ProductDTO> getProduct(UUID productId) {
        return repository.findById(productId)
                .map(mapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to retrieve product", e)));
    }

    @Override
    public Mono<ProductDTO> updateProduct(UUID productId, ProductDTO productDTO) {
        return repository.findById(productId)
                .flatMap(existingProduct -> {
                    mapper.updateEntityFromDto(productDTO, existingProduct);
                    return repository.save(existingProduct);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to update product", e)))
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> deleteProduct(UUID productId) {
        return repository.findById(productId)
                .flatMap(existingProduct -> repository.delete(existingProduct))
                .onErrorResume(e -> Mono.error(new RuntimeException("Failed to delete product", e)));
    }
}
