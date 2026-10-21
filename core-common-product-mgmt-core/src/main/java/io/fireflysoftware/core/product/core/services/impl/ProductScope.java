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

import org.fireflyframework.core.filters.FilterRequest;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Forces a nested product query to the product id in the URL.
 */
final class ProductScope {

    private ProductScope() {
    }

    static <T> void scope(FilterRequest<T> filterRequest, Supplier<T> empty, BiConsumer<T, UUID> bind, UUID productId) {
        if (filterRequest.getFilters() == null) {
            filterRequest.setFilters(empty.get());
        }
        bind.accept(filterRequest.getFilters(), productId);
    }
}
