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

import com.firefly.core.product.interfaces.enums.ContractingDocTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

/**
 * Entity representing a documentation requirement for a product during the contracting/opening phase.
 * This defines which documents are required from customers to complete the contracting process.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("product_documentation_requirement")
public class ProductDocumentationRequirement extends BaseEntity {
    
    @Id
    @Column("product_doc_requirement_id")
    private UUID productDocRequirementId;
    
    @Column("product_id")
    private UUID productId;
    
    @Column("doc_type")
    private ContractingDocTypeEnum docType;
    
    @Column("is_mandatory")
    private Boolean isMandatory;
    
    @Column("description")
    private String description;
}