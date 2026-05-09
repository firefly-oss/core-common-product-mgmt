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


package com.firefly.core.product.interfaces.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Aggregated, calculator-friendly view of a product's pricing parameters.
 * Composed by reading the underlying {@code product_configuration} rows
 * (LIMITS / PRICING) and parsing their JSON values. Read-only contract: write
 * operations must continue to go through the existing
 * {@code ProductConfigurationController}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Read-only aggregated pricing parameters for a product, used by calculators and quote services")
public class ProductPricingDTO {

    @Schema(description = "Unique identifier of the product", example = "00000000-0000-0000-0000-00000000000a")
    private UUID productId;

    @Schema(description = "Business code of the product", example = "PERSONAL_LOAN_DEMO")
    private String productCode;

    @Schema(description = "Derived product type label (e.g. PERSONAL_LOAN, LEASING)", example = "PERSONAL_LOAN")
    private String productType;

    @Schema(description = "Commercial name of the product", example = "Préstamo Personal")
    private String name;

    @Schema(description = "Short description shown on the product card",
            example = "Financiación para particulares, sin necesidad de justificar la finalidad.")
    private String description;

    @Schema(description = "Whether the product is currently offered to customers (derived from product_status = ACTIVE)",
            example = "true")
    private Boolean available;

    @Schema(description = "Commercial bullet list shown on the product card",
            example = "[\"From 1.000 to 60.000 euros\", \"Term 12-96 months\"]")
    private List<String> features;

    @Schema(description = "ISO 4217 currency code", example = "EUR")
    private String currency;

    @Schema(description = "Inclusive minimum principal amount the product accepts", example = "1000")
    private BigDecimal minAmount;

    @Schema(description = "Inclusive maximum principal amount the product accepts", example = "60000")
    private BigDecimal maxAmount;

    @Schema(description = "Inclusive minimum term, in months", example = "12")
    private Integer minTerm;

    @Schema(description = "Inclusive maximum term, in months", example = "96")
    private Integer maxTerm;

    @Schema(description = "Ordered list of interest-rate brackets")
    private List<InterestRateBracketDTO> interestRates;

    @Schema(description = "List of applicable fees")
    private List<FeeDefinitionDTO> fees;
}
