-- V10__Create_product_configuration_table.sql

-- =========================================
-- Create product_config_type enum
-- =========================================
CREATE TYPE product_config_type AS ENUM (
    'PRICING',
    'LIMITS',
    'FEATURES',
    'CUSTOM'
);

-- =========================================
-- PRODUCT_CONFIGURATION
-- =========================================
CREATE TABLE IF NOT EXISTS product_configuration (
    product_configuration_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id               UUID NOT NULL,
    config_type              product_config_type NOT NULL,
    config_key               VARCHAR(255) NOT NULL,
    config_value             TEXT,
    date_created             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_configuration_product
        FOREIGN KEY (product_id)
        REFERENCES product(product_id)
);

-- =========================================
-- Create indexes for efficient querying
-- =========================================
CREATE INDEX idx_product_configuration_product_id ON product_configuration(product_id);
CREATE INDEX idx_product_configuration_product_type ON product_configuration(product_id, config_type);
CREATE INDEX idx_product_configuration_product_key ON product_configuration(product_id, config_key);

