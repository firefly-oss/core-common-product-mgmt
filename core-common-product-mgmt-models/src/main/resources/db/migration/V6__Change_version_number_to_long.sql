-- Change version_number column from UUID to BIGINT (Long) in product_version table
ALTER TABLE product_version ALTER COLUMN version_number TYPE BIGINT USING NULL;