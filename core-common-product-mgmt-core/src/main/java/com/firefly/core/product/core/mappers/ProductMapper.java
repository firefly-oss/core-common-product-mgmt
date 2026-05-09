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


package com.firefly.core.product.core.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firefly.core.product.interfaces.dtos.ProductDTO;
import com.firefly.core.product.models.entities.Product;
import io.r2dbc.postgresql.codec.Json;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ProductMapper {

    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {};

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(source = "marketingFeatures", target = "marketingFeatures", qualifiedByName = "jsonToStringList")
    public abstract ProductDTO toDto(Product entity);

    @Mapping(source = "marketingFeatures", target = "marketingFeatures", qualifiedByName = "stringListToJson")
    public abstract Product toEntity(ProductDTO dto);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(source = "marketingFeatures", target = "marketingFeatures", qualifiedByName = "stringListToJson")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateEntityFromDto(ProductDTO dto, @MappingTarget Product entity);

    @Named("jsonToStringList")
    protected List<String> jsonToStringList(Json marketingFeatures) {
        if (marketingFeatures == null) {
            return null;
        }
        String raw = marketingFeatures.asString();
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(raw, STRING_LIST_TYPE);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing product marketing_features JSON", e);
        }
    }

    @Named("stringListToJson")
    protected Json stringListToJson(List<String> marketingFeatures) {
        if (marketingFeatures == null) {
            return null;
        }
        try {
            return Json.of(objectMapper.writeValueAsString(marketingFeatures));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serialising product marketing_features", e);
        }
    }
}
