-- V13__Add_Marketing_Features_To_Product.sql
-- ------------------------------------------
-- Adds the marketing_features column on the product table so the experience
-- tier can render the commercial bullet list (text only) for each product
-- card without coupling the schema to any specific product type. The column
-- is nullable JSONB to keep the migration backwards compatible.
--
-- Stored as a JSON-encoded array of strings, e.g.
--   ["From 1.000 to 60.000 euros", "Term 12-96 months", ...]
-- Mappers serialise/deserialise the array on the wire.

ALTER TABLE product
    ADD COLUMN IF NOT EXISTS marketing_features JSONB;
