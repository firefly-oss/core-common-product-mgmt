-- V17__Create_Casts_Product_Config_Type.sql

-- product_config_type
CREATE CAST (VARCHAR AS product_config_type)
    WITH INOUT
    AS IMPLICIT;
