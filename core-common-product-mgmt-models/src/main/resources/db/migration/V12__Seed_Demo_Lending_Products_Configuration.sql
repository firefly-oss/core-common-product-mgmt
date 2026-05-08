-- V12__Seed_Demo_Lending_Products_Configuration.sql
-- =========================================
-- Seed demo lending products and their key-value configuration rows
-- (LIMITS + PRICING) so calculators and aggregator endpoints can return
-- deterministic values for the BOM (Banking-On-Mars) demo.
-- =========================================
-- All inserts are idempotent: re-running the migration on an environment
-- that already contains the rows is a no-op.

-- =========================================
-- 1. Seed a default product category for demo lending products
-- =========================================
INSERT INTO product_category (
    product_category_id,
    category_name,
    category_description,
    parent_category_id,
    level,
    date_created,
    date_updated
)
SELECT
    '00000000-0000-0000-0000-0000000000c1'::uuid,
    'Demo Lending',
    'Default category for seeded demo lending products (BOM demo)',
    NULL,
    0,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product_category
    WHERE product_category_id = '00000000-0000-0000-0000-0000000000c1'::uuid
);

-- =========================================
-- 2. Seed PERSONAL_LOAN_DEMO product
-- =========================================
INSERT INTO product (
    product_id,
    tenant_id,
    product_category_id,
    product_type,
    product_name,
    product_code,
    product_description,
    product_status,
    launch_date,
    end_date,
    date_created,
    date_updated
)
SELECT
    '00000000-0000-0000-0000-00000000000a'::uuid,
    '00000000-0000-0000-0000-000000000000'::uuid,
    '00000000-0000-0000-0000-0000000000c1'::uuid,
    'FINANCIAL'::product_type,
    'Demo Personal Loan',
    'PERSONAL_LOAN_DEMO',
    'Seeded personal loan product used by the BOM demo for pricing aggregation and calculator flows',
    'ACTIVE'::product_status,
    CURRENT_DATE,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product
    WHERE product_id = '00000000-0000-0000-0000-00000000000a'::uuid
);

-- =========================================
-- 3. Seed LEASING_DEMO product
-- =========================================
INSERT INTO product (
    product_id,
    tenant_id,
    product_category_id,
    product_type,
    product_name,
    product_code,
    product_description,
    product_status,
    launch_date,
    end_date,
    date_created,
    date_updated
)
SELECT
    '00000000-0000-0000-0000-00000000000b'::uuid,
    '00000000-0000-0000-0000-000000000000'::uuid,
    '00000000-0000-0000-0000-0000000000c1'::uuid,
    'FINANCIAL'::product_type,
    'Demo Leasing',
    'LEASING_DEMO',
    'Seeded leasing product used by the BOM demo for pricing aggregation and calculator flows',
    'ACTIVE'::product_status,
    CURRENT_DATE,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product
    WHERE product_id = '00000000-0000-0000-0000-00000000000b'::uuid
);

-- =========================================
-- 4. Seed configuration rows for PERSONAL_LOAN_DEMO
-- =========================================
-- 4.1 LIMITS / amount_term
INSERT INTO product_configuration (
    product_id, config_type, config_key, config_value, date_created, date_updated
)
SELECT
    '00000000-0000-0000-0000-00000000000a'::uuid,
    'LIMITS'::product_config_type,
    'amount_term',
    '{"currency":"EUR","minAmount":1000,"maxAmount":60000,"minTerm":12,"maxTerm":96}',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product_configuration
    WHERE product_id = '00000000-0000-0000-0000-00000000000a'::uuid
      AND config_type = 'LIMITS'::product_config_type
      AND config_key = 'amount_term'
);

-- 4.2 PRICING / interest_rate_brackets
INSERT INTO product_configuration (
    product_id, config_type, config_key, config_value, date_created, date_updated
)
SELECT
    '00000000-0000-0000-0000-00000000000a'::uuid,
    'PRICING'::product_config_type,
    'interest_rate_brackets',
    '[{"minAmount":1000,"maxAmount":60000,"tin":7.99}]',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product_configuration
    WHERE product_id = '00000000-0000-0000-0000-00000000000a'::uuid
      AND config_type = 'PRICING'::product_config_type
      AND config_key = 'interest_rate_brackets'
);

-- 4.3 PRICING / fees
INSERT INTO product_configuration (
    product_id, config_type, config_key, config_value, date_created, date_updated
)
SELECT
    '00000000-0000-0000-0000-00000000000a'::uuid,
    'PRICING'::product_config_type,
    'fees',
    '[{"type":"OPENING_FEE","percentage":0,"fixed":0}]',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product_configuration
    WHERE product_id = '00000000-0000-0000-0000-00000000000a'::uuid
      AND config_type = 'PRICING'::product_config_type
      AND config_key = 'fees'
);

-- =========================================
-- 5. Seed configuration rows for LEASING_DEMO
-- =========================================
-- 5.1 LIMITS / amount_term
INSERT INTO product_configuration (
    product_id, config_type, config_key, config_value, date_created, date_updated
)
SELECT
    '00000000-0000-0000-0000-00000000000b'::uuid,
    'LIMITS'::product_config_type,
    'amount_term',
    '{"currency":"EUR","minAmount":5000,"maxAmount":500000,"minTerm":12,"maxTerm":84}',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product_configuration
    WHERE product_id = '00000000-0000-0000-0000-00000000000b'::uuid
      AND config_type = 'LIMITS'::product_config_type
      AND config_key = 'amount_term'
);

-- 5.2 PRICING / interest_rate_brackets
INSERT INTO product_configuration (
    product_id, config_type, config_key, config_value, date_created, date_updated
)
SELECT
    '00000000-0000-0000-0000-00000000000b'::uuid,
    'PRICING'::product_config_type,
    'interest_rate_brackets',
    '[{"minAmount":5000,"maxAmount":25000,"tin":6.90},{"minAmount":25001,"maxAmount":100000,"tin":5.90},{"minAmount":100001,"maxAmount":250000,"tin":5.50},{"minAmount":250001,"maxAmount":500000,"tin":5.20}]',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product_configuration
    WHERE product_id = '00000000-0000-0000-0000-00000000000b'::uuid
      AND config_type = 'PRICING'::product_config_type
      AND config_key = 'interest_rate_brackets'
);

-- 5.3 PRICING / fees
INSERT INTO product_configuration (
    product_id, config_type, config_key, config_value, date_created, date_updated
)
SELECT
    '00000000-0000-0000-0000-00000000000b'::uuid,
    'PRICING'::product_config_type,
    'fees',
    '[{"type":"OPENING_FEE","percentage":1.0,"fixed":0}]',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM product_configuration
    WHERE product_id = '00000000-0000-0000-0000-00000000000b'::uuid
      AND config_type = 'PRICING'::product_config_type
      AND config_key = 'fees'
);
