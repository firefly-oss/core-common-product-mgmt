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


package com.firefly.core.product.core.mappers;

import com.firefly.core.product.interfaces.dtos.ProductDocumentationRequirementDTO;
import com.firefly.core.product.models.entities.ProductDocumentationRequirement;
import org.mapstruct.*;

/**
 * Mapper for converting between ProductDocumentationRequirement entity and DTO.
 */
@Mapper(componentModel = "spring")
public interface ProductDocumentationRequirementMapper {
    /**
     * Convert entity to DTO.
     *
     * @param entity The entity to convert
     * @return The DTO
     */
    ProductDocumentationRequirementDTO toDto(ProductDocumentationRequirement entity);

    /**
     * Convert DTO to entity.
     *
     * @param dto The DTO to convert
     * @return The entity
     */
    ProductDocumentationRequirement toEntity(ProductDocumentationRequirementDTO dto);

    /**
     * Update an existing entity from a DTO.
     *
     * @param dto The DTO with updated values
     * @param entity The entity to update
     */
    @Mapping(target = "productDocRequirementId", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductDocumentationRequirementDTO dto, @MappingTarget ProductDocumentationRequirement entity);
}