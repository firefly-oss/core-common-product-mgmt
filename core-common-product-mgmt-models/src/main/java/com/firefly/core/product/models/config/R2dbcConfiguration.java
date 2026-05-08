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


package com.firefly.core.product.models.config;

import com.firefly.core.product.interfaces.enums.ContractingDocTypeEnum;
import com.firefly.core.product.interfaces.enums.DocTypeEnum;
import com.firefly.core.product.interfaces.enums.ProductConfigTypeEnum;
import com.firefly.core.product.interfaces.enums.ProductStatusEnum;
import com.firefly.core.product.interfaces.enums.ProductTypeEnum;
import com.firefly.core.product.interfaces.enums.RelationshipTypeEnum;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.client.SSLMode;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * R2DBC configuration that registers a {@link EnumCodec} for every native
 * Postgres ENUM type used by this service. Without these codecs the driver
 * binds Java enum values as {@code VARCHAR}, and Postgres rejects the
 * comparison ({@code operator does not exist: product_status = character
 * varying}) because no implicit operator exists between {@code varchar} and
 * the native enum types declared in {@code V1__Create_enums.sql} /
 * {@code V4__Add_contracting_doc_type.sql} /
 * {@code V10__Create_product_configuration_table.sql}.
 *
 * <p>The {@code @WritingConverter} no-op converters force Spring Data R2DBC
 * to delegate the write path to the driver, which then applies the codec.
 * This pattern matches the configuration used by other Firefly core services
 * that talk to Postgres native enums (e.g. {@code core-banking-accounts},
 * {@code core-common-reference-master-data}).
 */
@Configuration
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Value("${DB_HOST:localhost}")
    private String host;

    @Value("${DB_PORT:5432}")
    private int port;

    @Value("${DB_NAME:postgres}")
    private String database;

    @Value("${DB_USERNAME:postgres}")
    private String username;

    @Value("${DB_PASSWORD:postgres}")
    private String password;

    @Value("${DB_SSL_MODE:disable}")
    private String sslMode;

    @WritingConverter
    static class ProductStatusEnumConverter implements Converter<ProductStatusEnum, ProductStatusEnum> {
        @Override
        public ProductStatusEnum convert(ProductStatusEnum source) {
            return source;
        }
    }

    @WritingConverter
    static class ProductTypeEnumConverter implements Converter<ProductTypeEnum, ProductTypeEnum> {
        @Override
        public ProductTypeEnum convert(ProductTypeEnum source) {
            return source;
        }
    }

    @WritingConverter
    static class DocTypeEnumConverter implements Converter<DocTypeEnum, DocTypeEnum> {
        @Override
        public DocTypeEnum convert(DocTypeEnum source) {
            return source;
        }
    }

    @WritingConverter
    static class ContractingDocTypeEnumConverter implements Converter<ContractingDocTypeEnum, ContractingDocTypeEnum> {
        @Override
        public ContractingDocTypeEnum convert(ContractingDocTypeEnum source) {
            return source;
        }
    }

    @WritingConverter
    static class RelationshipTypeEnumConverter implements Converter<RelationshipTypeEnum, RelationshipTypeEnum> {
        @Override
        public RelationshipTypeEnum convert(RelationshipTypeEnum source) {
            return source;
        }
    }

    @WritingConverter
    static class ProductConfigTypeEnumConverter implements Converter<ProductConfigTypeEnum, ProductConfigTypeEnum> {
        @Override
        public ProductConfigTypeEnum convert(ProductConfigTypeEnum source) {
            return source;
        }
    }

    @Override
    protected List<Object> getCustomConverters() {
        List<Object> converters = new ArrayList<>();
        converters.add(new ProductStatusEnumConverter());
        converters.add(new ProductTypeEnumConverter());
        converters.add(new DocTypeEnumConverter());
        converters.add(new ContractingDocTypeEnumConverter());
        converters.add(new RelationshipTypeEnumConverter());
        converters.add(new ProductConfigTypeEnumConverter());
        return converters;
    }

    @Bean
    @Primary
    @Override
    public ConnectionFactory connectionFactory() {
        return new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .username(username)
                .password(password)
                .database(database)
                .sslMode(SSLMode.valueOf(sslMode.toUpperCase()))
                .codecRegistrar(EnumCodec.builder()
                    .withEnum("product_status", ProductStatusEnum.class)
                    .withEnum("product_type", ProductTypeEnum.class)
                    .withEnum("doc_type", DocTypeEnum.class)
                    .withEnum("contracting_doc_type", ContractingDocTypeEnum.class)
                    .withEnum("relationship_type", RelationshipTypeEnum.class)
                    .withEnum("product_config_type", ProductConfigTypeEnum.class)
                    .build())
                .build()
        );
    }
}
