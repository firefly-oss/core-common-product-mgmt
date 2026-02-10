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


package com.firefly.core.product.interfaces.enums;

/**
 * Enum representing the types of documentation that may be required during the contracting/opening phase of a product.
 * These are documents that customers need to provide to complete the contracting process.
 */
public enum ContractingDocTypeEnum {
    /**
     * Legal identification documents (e.g., passport, ID card, driver's license)
     */
    IDENTIFICATION,
    
    /**
     * Tax identification documents (e.g., tax ID number, tax registration certificate)
     */
    TAX_IDENTIFICATION,
    
    /**
     * Proof of address documents (e.g., utility bills, bank statements)
     */
    PROOF_OF_ADDRESS,
    
    /**
     * Income verification documents (e.g., pay stubs, employment verification)
     */
    INCOME_VERIFICATION,
    
    /**
     * Bank statements or other financial records
     */
    BANK_STATEMENTS,
    
    /**
     * Power of attorney documents
     */
    POWER_OF_ATTORNEY,
    
    /**
     * Business registration documents (for business products)
     */
    BUSINESS_REGISTRATION,
    
    /**
     * Articles of incorporation (for business products)
     */
    ARTICLES_OF_INCORPORATION,
    
    /**
     * Bylaws of the company (for business products)
     */
    COMPANY_BYLAWS,
    
    /**
     * Signed contracts or agreements
     */
    SIGNED_CONTRACT,
    
    /**
     * Regulatory compliance documents
     */
    REGULATORY_COMPLIANCE,
    
    /**
     * Credit reports or credit history documents
     */
    CREDIT_REPORT,
    
    /**
     * Insurance policy documents
     */
    INSURANCE_POLICY,
    
    /**
     * Other miscellaneous documents
     */
    OTHER
}