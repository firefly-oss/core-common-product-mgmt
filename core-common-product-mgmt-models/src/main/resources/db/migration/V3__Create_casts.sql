-- V3__Create_casts.sql

-- Example for product_type
CREATE CAST (VARCHAR AS product_type)
    WITH INOUT
    AS IMPLICIT;

-- product_status
CREATE CAST (VARCHAR AS product_status)
    WITH INOUT
    AS IMPLICIT;

-- feature_type
CREATE CAST (VARCHAR AS feature_type)
    WITH INOUT
    AS IMPLICIT;

-- pricing_type
CREATE CAST (VARCHAR AS pricing_type)
    WITH INOUT
    AS IMPLICIT;

-- doc_type
CREATE CAST (VARCHAR AS doc_type)
    WITH INOUT
    AS IMPLICIT;

-- lifecycle_status
CREATE CAST (VARCHAR AS lifecycle_status)
    WITH INOUT
    AS IMPLICIT;

-- limit_type
CREATE CAST (VARCHAR AS limit_type)
    WITH INOUT
    AS IMPLICIT;

-- time_period
CREATE CAST (VARCHAR AS time_period)
    WITH INOUT
    AS IMPLICIT;

-- bundle_status
CREATE CAST (VARCHAR AS bundle_status)
    WITH INOUT
    AS IMPLICIT;

-- relationship_type
CREATE CAST (VARCHAR AS relationship_type)
    WITH INOUT
    AS IMPLICIT;

-- fee_structure_type
CREATE CAST (VARCHAR AS fee_structure_type)
    WITH INOUT
    AS IMPLICIT;

-- fee_type
CREATE CAST (VARCHAR AS fee_type)
    WITH INOUT
    AS IMPLICIT;

-- fee_unit
CREATE CAST (VARCHAR AS fee_unit)
    WITH INOUT
    AS IMPLICIT;
