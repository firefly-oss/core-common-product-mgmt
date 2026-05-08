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


package com.firefly.core.product.models.repositories;

import com.firefly.core.product.interfaces.enums.ProductStatusEnum;
import com.firefly.core.product.models.entities.Product;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ProductRepository extends BaseRepository<Product, UUID> {

    /**
     * Finds all products belonging to a specific tenant.
     *
     * @param tenantId the unique identifier of the tenant
     * @return a Flux emitting all products for the specified tenant
     */
    Flux<Product> findByTenantId(UUID tenantId);

    /**
     * Finds all products with the given lifecycle status. Use this method
     * (rather than {@code findAll()}) for any read flow that feeds calculators,
     * pricing engines or external consumers, so that {@code RETIRED},
     * {@code DRAFT} or {@code PROPOSED} products are never mixed in with the
     * active catalog.
     *
     * @param status the lifecycle status to filter by (typically
     *               {@link ProductStatusEnum#ACTIVE})
     * @return a Flux emitting only products in the requested status
     */
    Flux<Product> findByProductStatus(ProductStatusEnum status);

    /**
     * Finds all products with the given lifecycle status, scoped to a single
     * tenant. Preferred over {@link #findByProductStatus(ProductStatusEnum)} in
     * any flow that already carries a tenant context, so that catalogs from
     * other tenants are never visible.
     *
     * @param status   the lifecycle status to filter by
     * @param tenantId the unique identifier of the tenant
     * @return a Flux emitting only products in the requested status that belong
     *         to the given tenant
     */
    Flux<Product> findByProductStatusAndTenantId(ProductStatusEnum status, UUID tenantId);
}
