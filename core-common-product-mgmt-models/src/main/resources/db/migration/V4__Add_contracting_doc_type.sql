-- V4__Add_contracting_doc_type.sql

-- Create enum for contracting documentation types
CREATE TYPE contracting_doc_type AS ENUM (
    'IDENTIFICATION',
    'TAX_IDENTIFICATION',
    'PROOF_OF_ADDRESS',
    'INCOME_VERIFICATION',
    'BANK_STATEMENTS',
    'POWER_OF_ATTORNEY',
    'BUSINESS_REGISTRATION',
    'ARTICLES_OF_INCORPORATION',
    'COMPANY_BYLAWS',
    'SIGNED_CONTRACT',
    'REGULATORY_COMPLIANCE',
    'CREDIT_REPORT',
    'INSURANCE_POLICY',
    'OTHER'
);

-- Create table for product documentation requirements
CREATE TABLE IF NOT EXISTS product_documentation_requirement (
    product_doc_requirement_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    doc_type contracting_doc_type NOT NULL,
    is_mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    description TEXT,
    date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_doc_requirement_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
);

-- Create index for faster lookups by product_id
CREATE INDEX idx_product_doc_requirement_product_id ON product_documentation_requirement(product_id);