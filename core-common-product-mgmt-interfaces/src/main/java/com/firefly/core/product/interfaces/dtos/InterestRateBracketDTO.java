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
 * Single interest-rate bracket extracted from a product's PRICING configuration.
 * Represents the nominal interest rate (TIN) applied to loans whose principal
 * falls within the inclusive {@code [minAmount, maxAmount]} range.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Interest-rate bracket applicable to a product within an amount range")
public class InterestRateBracketDTO {

    @Schema(description = "Inclusive lower bound of the principal amount", example = "1000")
    private BigDecimal minAmount;

    @Schema(description = "Inclusive upper bound of the principal amount", example = "60000")
    private BigDecimal maxAmount;

    @Schema(description = "Nominal interest rate (TIN) expressed as an annual percentage", example = "7.99")
    private BigDecimal tin;
}
