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

/**
 * Single fee definition extracted from a product's PRICING configuration.
 * Captures both the percentage component (relative to the principal) and the
 * fixed component, expressed in the product's currency.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Fee definition applied to a product (percentage and/or fixed amount)")
public class FeeDefinitionDTO {

    @Schema(description = "Fee type code (e.g. OPENING_FEE, EARLY_REPAYMENT_FEE)", example = "OPENING_FEE")
    private String type;

    @Schema(description = "Percentage component of the fee, expressed as a percentage of the principal", example = "1.0")
    private BigDecimal percentage;

    @Schema(description = "Fixed component of the fee, expressed in the product currency", example = "0")
    private BigDecimal fixed;
}
