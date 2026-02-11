-- V7__Remove_lifecycle_fees_features_pricing_limit.sql
-- This migration removes tables and enums related to lifecycle, fees, features, pricing, and limits

-- =========================================
-- DROP TABLES (in order of dependencies)
-- =========================================

-- Drop fee-related tables (in dependency order)
DROP TABLE IF EXISTS fee_application_rule CASCADE;
DROP TABLE IF EXISTS product_fee_structure CASCADE;
DROP TABLE IF EXISTS fee_component CASCADE;
DROP TABLE IF EXISTS fee_structure CASCADE;

-- Drop pricing-related tables
DROP TABLE IF EXISTS product_pricing_localization CASCADE;
DROP TABLE IF EXISTS product_pricing CASCADE;

-- Drop feature-related tables
DROP TABLE IF EXISTS product_feature CASCADE;

-- Drop lifecycle-related tables
DROP TABLE IF EXISTS product_lifecycle CASCADE;

-- Drop limit-related tables
DROP TABLE IF EXISTS product_limit CASCADE;

-- =========================================
-- DROP ENUMS
-- =========================================

-- Drop fee-related enums
DROP TYPE IF EXISTS fee_unit CASCADE;
DROP TYPE IF EXISTS fee_type CASCADE;
DROP TYPE IF EXISTS fee_structure_type CASCADE;

-- Drop pricing-related enums
DROP TYPE IF EXISTS pricing_type CASCADE;

-- Drop feature-related enums
DROP TYPE IF EXISTS feature_type CASCADE;

-- Drop lifecycle-related enums
DROP TYPE IF EXISTS lifecycle_status CASCADE;

-- Drop limit-related enums
DROP TYPE IF EXISTS limit_type CASCADE;
DROP TYPE IF EXISTS time_period CASCADE;

