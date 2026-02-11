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


package com.firefly.core.product.models.entities;

import com.firefly.core.product.interfaces.enums.ProductStatusEnum;
import com.firefly.core.product.interfaces.enums.ProductTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table("product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {
    @Id
    @Column("product_id")
    private UUID productId;

    @Column("tenant_id")
    private UUID tenantId;

    @Column("product_category_id")
    private UUID productCategoryId;

    @Column("product_type")
    private ProductTypeEnum productType;

    @Column("product_name")
    private String productName;

    @Column("product_code")
    private String productCode;

    @Column("product_description")
    private String productDescription;

    @Column("product_status")
    private ProductStatusEnum productStatus;

    @Column("launch_date")
    private LocalDate launchDate;

    @Column("end_date")
    private LocalDate endDate;
}
