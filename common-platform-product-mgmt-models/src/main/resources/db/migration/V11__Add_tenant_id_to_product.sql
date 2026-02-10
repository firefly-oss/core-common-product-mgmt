-- Add tenant_id column to product table for multi-tenancy support
-- This column associates each product with a specific tenant instance of the Firefly CoreBanking platform

-- Add tenant_id column with a default value for existing records
-- Using a placeholder UUID for existing records that will need to be updated with actual tenant IDs
ALTER TABLE product ADD COLUMN tenant_id UUID NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000';

-- Remove the default constraint after adding the column
-- New records must explicitly provide a tenant_id
ALTER TABLE product ALTER COLUMN tenant_id DROP DEFAULT;

-- Create index on tenant_id for efficient tenant-scoped queries
CREATE INDEX idx_product_tenant_id ON product(tenant_id);

-- Create composite index on tenant_id and product_id for efficient lookups
CREATE INDEX idx_product_tenant_product ON product(tenant_id, product_id);

