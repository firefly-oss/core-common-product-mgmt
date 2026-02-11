-- V8__Refactor_category_hierarchy.sql
-- =========================================
-- Refactor ProductCategory to support hierarchical parent-child relationships
-- and eliminate the separate ProductSubtype entity
-- =========================================

-- Step 1: Add level column to product_category
ALTER TABLE product_category ADD COLUMN IF NOT EXISTS level INTEGER DEFAULT 0;

-- Step 2: Set level for existing root categories (those without parent)
UPDATE product_category SET level = 0 WHERE parent_category_id IS NULL;

-- Step 3: Migrate product_subtype data to product_category as child categories
-- Insert subtypes as child categories with level = 1
INSERT INTO product_category (product_category_id, category_name, category_description, parent_category_id, level, date_created, date_updated)
SELECT 
    product_subtype_id,
    subtype_name,
    subtype_description,
    product_category_id,
    1,
    date_created,
    date_updated
FROM product_subtype;

-- Step 4: Update product table to reference product_category instead of product_subtype
-- First, drop the existing foreign key constraint
ALTER TABLE product DROP CONSTRAINT IF EXISTS fk_product_subtype;

-- Rename the column from product_subtype_id to product_category_id
ALTER TABLE product RENAME COLUMN product_subtype_id TO product_category_id;

-- Add new foreign key constraint to product_category
ALTER TABLE product ADD CONSTRAINT fk_product_category
    FOREIGN KEY (product_category_id)
    REFERENCES product_category(product_category_id);

-- Step 5: Drop the product_subtype table
DROP TABLE IF EXISTS product_subtype;

