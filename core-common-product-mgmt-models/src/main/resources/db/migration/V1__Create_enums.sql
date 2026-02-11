-- V1__Create_enums.sql

-- For product type
CREATE TYPE product_type AS ENUM (
    'FINANCIAL',
    'NON_FINANCIAL'
);

-- For product status
CREATE TYPE product_status AS ENUM (
    'PROPOSED',
    'ACTIVE',
    'SUSPENDED',
    'RETIRED'
);

-- For feature type
CREATE TYPE feature_type AS ENUM (
    'STANDARD',
    'OPTIONAL',
    'PREMIUM'
);

-- For pricing type
CREATE TYPE pricing_type AS ENUM (
    'INTEREST_RATE',
    'FEE',
    'COVERAGE',
    'SUBSCRIPTION'
);

-- For documentation type
CREATE TYPE doc_type AS ENUM (
    'TNC',
    'BROCHURE',
    'POLICY_DOC'
);

-- For product lifecycle status
CREATE TYPE lifecycle_status AS ENUM (
    'PROPOSED',
    'ACTIVE',
    'SUSPENDED',
    'RETIRED'
);

-- For limit type
CREATE TYPE limit_type AS ENUM (
    'CREDIT_LIMIT',
    'WITHDRAWAL_LIMIT',
    'SUBSCRIPTION_LIMIT'
);

-- For time period in product_limit
CREATE TYPE time_period AS ENUM (
    'DAILY',
    'MONTHLY',
    'PER_TRANSACTION',
    'NONE'
);

-- For bundle status
CREATE TYPE bundle_status AS ENUM (
    'ACTIVE',
    'RETIRED',
    'PROMO'
);

-- For product relationship type
CREATE TYPE relationship_type AS ENUM (
    'PRE_REQUISITE',
    'COMPLIMENTARY',
    'UPGRADE',
    'CROSS_SELL'
);

-- For fee structure type
CREATE TYPE fee_structure_type AS ENUM (
    'VERTICAL',
    'HORIZONTAL'
);

-- For fee type in fee_component
CREATE TYPE fee_type AS ENUM (
    'ORIGINATION_FEE',
    'LATE_PAYMENT_FEE',
    'TRANSACTION_FEE',
    'SERVICE_FEE'
);

-- For fee unit in fee_component
CREATE TYPE fee_unit AS ENUM (
    'FIXED',
    'PERCENTAGE'
);