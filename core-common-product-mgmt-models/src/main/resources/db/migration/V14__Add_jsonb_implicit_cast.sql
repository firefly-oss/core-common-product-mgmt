-- V14__Add_jsonb_implicit_cast.sql
--
-- Spring Data R2DBC binds Java String values as VARCHAR. The marketing_features
-- column added in V13 is JSONB and Postgres has no built-in implicit cast from
-- VARCHAR to JSONB, so every INSERT/UPDATE coming from the mapper's
-- stringListToJson() qualifier blew up at write time:
--
--   column "marketing_features" is of type jsonb but expression is of type character varying
--
-- Mirror the same pattern V3 already uses for the native enum columns: register
-- an implicit INOUT cast so the binding round-trips through the JSONB input
-- function transparently. The cast is global to the database, but it only
-- becomes visible when the source value is bound as VARCHAR — explicit JSONB
-- bindings (psql, pgAdmin, jdbc with `?::jsonb`) keep their normal behaviour.

CREATE CAST (VARCHAR AS jsonb)
    WITH INOUT
    AS IMPLICIT;
