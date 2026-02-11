-- V9: Remove ProductBundle and ProductBundleItem tables
-- This migration drops the product bundle functionality

-- Drop product_bundle_item table first (has foreign key to product_bundle)
DROP TABLE IF EXISTS product_bundle_item;

-- Drop product_bundle table
DROP TABLE IF EXISTS product_bundle;

