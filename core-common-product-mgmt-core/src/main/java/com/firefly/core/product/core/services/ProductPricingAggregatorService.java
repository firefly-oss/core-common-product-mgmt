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


package com.firefly.core.product.core.services;

import com.firefly.core.product.interfaces.dtos.ProductPricingDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Read-only aggregator that composes the key-value rows in
 * {@code product_configuration} into a calculator-friendly
 * {@link ProductPricingDTO}. Write operations (creation / update / deletion of
 * the underlying configuration rows) continue to be served by the existing
 * {@code ProductConfigurationController}.
 */
public interface ProductPricingAggregatorService {

    /**
     * Returns the aggregated pricing parameters of a single product.
     *
     * @param productId unique identifier of the product
     * @return a {@link Mono} that emits the aggregated pricing on success, or
     *         signals an error if the product does not exist, a required
     *         configuration row is missing, or the JSON payload cannot be
     *         parsed.
     */
    Mono<ProductPricingDTO> getProductPricing(UUID productId);

    /**
     * Streams the aggregated pricing parameters of every product, optionally
     * filtered by the derived {@code productType} label
     * (e.g. {@code "PERSONAL_LOAN"}, {@code "LEASING"}). Products that do not
     * yet have a complete pricing configuration are skipped.
     *
     * @param productType optional derived product-type label; {@code null} or
     *                    blank returns every product with pricing.
     * @return a {@link Flux} emitting one {@link ProductPricingDTO} per product
     *         with a complete pricing configuration.
     */
    Flux<ProductPricingDTO> listProductsWithPricing(String productType);
}
