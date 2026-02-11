-- V5__Create_Casts_Contracting_Doc_Type.sql

-- contracting_doc_type
CREATE CAST (VARCHAR AS contracting_doc_type)
    WITH INOUT
    AS IMPLICIT;