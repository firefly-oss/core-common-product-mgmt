-- The API already accepts DRAFT. The database enum only had
-- PROPOSED, ACTIVE, SUSPENDED and RETIRED, so persisting DRAFT failed.
-- SUSPENDED stays; the Java enum is aligned to include both.
ALTER TYPE product_status ADD VALUE 'DRAFT';
