-- V2__Create_tables.sql
-- =========================================
-- PRODUCT_CATEGORY
-- =========================================
CREATE TABLE IF NOT EXISTS product_category (
                                                product_category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                category_name          VARCHAR(255) NOT NULL,
    category_description   TEXT,
    parent_category_id     UUID,
    date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    -- Optional self-reference constraint:
    -- CONSTRAINT fk_parent_category
    --   FOREIGN KEY (parent_category_id) REFERENCES product_category(product_category_id)
    );

-- =========================================
-- PRODUCT_SUBTYPE
-- =========================================
CREATE TABLE IF NOT EXISTS product_subtype (
                                               product_subtype_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               product_category_id    UUID NOT NULL,
                                               subtype_name           VARCHAR(255) NOT NULL,
    subtype_description    TEXT,
    date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category
    FOREIGN KEY (product_category_id)
    REFERENCES product_category(product_category_id)
    );

-- =========================================
-- PRODUCT
-- =========================================
CREATE TABLE IF NOT EXISTS product (
                                       product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       product_subtype_id     UUID NOT NULL,
                                       product_type           product_type NOT NULL,
                                       product_name           VARCHAR(255) NOT NULL,
    product_code           VARCHAR(100),
    product_description    TEXT,
    product_status         product_status NOT NULL,
    launch_date            TIMESTAMP,
    end_date               TIMESTAMP,
    date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_subtype
    FOREIGN KEY (product_subtype_id)
    REFERENCES product_subtype(product_subtype_id)
    );

-- =========================================
-- PRODUCT_FEATURE
-- =========================================
CREATE TABLE IF NOT EXISTS product_feature (
                                               product_feature_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               product_id             UUID NOT NULL,
                                               feature_name           VARCHAR(255) NOT NULL,
    feature_description    TEXT,
    feature_type           feature_type NOT NULL,
    is_mandatory           BOOLEAN NOT NULL DEFAULT FALSE,
    date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product
    FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_PRICING
-- =========================================
CREATE TABLE IF NOT EXISTS product_pricing (
                                               product_pricing_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               product_id             UUID NOT NULL,
                                               pricing_type           pricing_type NOT NULL,
                                               amount_value           DECIMAL(18, 4) NOT NULL,
    amount_unit            VARCHAR(50) NOT NULL,  -- e.g. 'PERCENT', 'EUR', 'MONTHLY'
    pricing_condition      TEXT,
    effective_date         TIMESTAMP NOT NULL,
    expiry_date            TIMESTAMP,
    date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pricing_product
    FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_DOCUMENTATION
-- =========================================
CREATE TABLE IF NOT EXISTS product_documentation (
                                                     product_documentation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     product_id               UUID NOT NULL,
                                                     doc_type                 doc_type NOT NULL,
                                                     document_manager_ref     BIGINT,
                                                     date_added               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                     date_created             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                     date_updated             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                     CONSTRAINT fk_doc_product
                                                     FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_LIFECYCLE
-- =========================================
CREATE TABLE IF NOT EXISTS product_lifecycle (
                                                 product_lifecycle_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 product_id             UUID NOT NULL,
                                                 lifecycle_status       lifecycle_status NOT NULL,
                                                 status_start_date      TIMESTAMP NOT NULL,
                                                 status_end_date        TIMESTAMP,
                                                 reason                 TEXT,
                                                 date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 CONSTRAINT fk_lifecycle_product
                                                 FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_LIMIT
-- =========================================
CREATE TABLE IF NOT EXISTS product_limit (
                                             product_limit_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             product_id             UUID NOT NULL,
                                             limit_type             limit_type NOT NULL,
                                             limit_value            DECIMAL(18, 4) NOT NULL,
    limit_unit             VARCHAR(50),  -- e.g. 'EUR', 'USD', 'TXN_COUNT'
    time_period            time_period NOT NULL,
    effective_date         TIMESTAMP NOT NULL,
    expiry_date            TIMESTAMP,
    date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_limit_product
    FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_BUNDLE
-- =========================================
CREATE TABLE IF NOT EXISTS product_bundle (
                                              product_bundle_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              bundle_name            VARCHAR(255) NOT NULL,
    bundle_description     TEXT,
    bundle_status          bundle_status NOT NULL,
    date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- =========================================
-- PRODUCT_BUNDLE_ITEM
-- =========================================
CREATE TABLE IF NOT EXISTS product_bundle_item (
                                                   product_bundle_item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                   product_bundle_id      UUID NOT NULL,
                                                   product_id             UUID NOT NULL,
                                                   special_conditions     TEXT,
                                                   date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                   date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                   CONSTRAINT fk_bundle
                                                   FOREIGN KEY (product_bundle_id)
    REFERENCES product_bundle(product_bundle_id),
    CONSTRAINT fk_bundle_product
    FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_RELATIONSHIP
-- =========================================
CREATE TABLE IF NOT EXISTS product_relationship (
                                                    product_relationship_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    product_id              UUID NOT NULL,
                                                    related_product_id      UUID NOT NULL,
                                                    relationship_type       relationship_type NOT NULL,
                                                    description             TEXT,
                                                    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                    date_updated            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                    CONSTRAINT fk_relationship_product
                                                    FOREIGN KEY (product_id)
    REFERENCES product(product_id),
    CONSTRAINT fk_related_product
    FOREIGN KEY (related_product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_VERSION
-- =========================================
CREATE TABLE IF NOT EXISTS product_version (
                                               product_version_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               product_id             UUID NOT NULL,
                                               version_number         UUID NOT NULL,
                                               version_description    TEXT,
                                               effective_date         TIMESTAMP NOT NULL,
                                               date_created           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               date_updated           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                               CONSTRAINT fk_version_product
                                               FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_LOCALIZATION
-- =========================================
CREATE TABLE IF NOT EXISTS product_localization (
                                                    product_localization_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    product_id               UUID NOT NULL,
                                                    language_code            VARCHAR(10) NOT NULL,  -- e.g., 'en', 'de'
    localized_name           VARCHAR(255),
    localized_description    TEXT,
    date_created             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_localization_product
    FOREIGN KEY (product_id)
    REFERENCES product(product_id)
    );

-- =========================================
-- PRODUCT_PRICING_LOCALIZATION
-- =========================================
CREATE TABLE IF NOT EXISTS product_pricing_localization (
                                                            product_pricing_localization_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                            product_pricing_id              UUID NOT NULL,
                                                            currency_code                   VARCHAR(10) NOT NULL,  -- e.g., 'EUR', 'USD'
    localized_amount_value          DECIMAL(18, 4),
    date_created                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated                    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pricing_loc
    FOREIGN KEY (product_pricing_id)
    REFERENCES product_pricing(product_pricing_id)
    );

-- =========================================
-- FEE_STRUCTURE
-- =========================================
CREATE TABLE IF NOT EXISTS fee_structure (
                                             fee_structure_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             fee_structure_name      VARCHAR(255) NOT NULL,
    fee_structure_description TEXT,
    fee_structure_type      fee_structure_type NOT NULL,
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- =========================================
-- FEE_COMPONENT
-- =========================================
CREATE TABLE IF NOT EXISTS fee_component (
                                             fee_component_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             fee_structure_id        UUID NOT NULL,
                                             fee_type                fee_type NOT NULL,
                                             fee_description         TEXT,
                                             fee_amount              DECIMAL(18, 4) NOT NULL,
    fee_unit                fee_unit NOT NULL,
    applicable_conditions   TEXT,
    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    date_updated            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_component_fee_struct
    FOREIGN KEY (fee_structure_id)
    REFERENCES fee_structure(fee_structure_id)
    );

-- =========================================
-- PRODUCT_FEE_STRUCTURE
-- =========================================
CREATE TABLE IF NOT EXISTS product_fee_structure (
                                                     product_fee_structure_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     product_id               UUID NOT NULL,
                                                     fee_structure_id         UUID NOT NULL,
                                                     priority                 INT,
                                                     date_created             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                     date_updated             TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                     CONSTRAINT fk_prod_fee_struct
                                                     FOREIGN KEY (product_id)
    REFERENCES product(product_id),
    CONSTRAINT fk_fee_structure
    FOREIGN KEY (fee_structure_id)
    REFERENCES fee_structure(fee_structure_id)
    );

-- =========================================
-- FEE_APPLICATION_RULE
-- =========================================
CREATE TABLE IF NOT EXISTS fee_application_rule (
                                                    fee_application_rule_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    fee_component_id        UUID NOT NULL,
                                                    rule_description        TEXT,
                                                    rule_conditions         TEXT,    -- could store JSON
                                                    effective_date          TIMESTAMP NOT NULL,
                                                    expiry_date             TIMESTAMP,
                                                    date_created            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                    date_updated            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                    CONSTRAINT fk_app_rule_component
                                                    FOREIGN KEY (fee_component_id)
    REFERENCES fee_component(fee_component_id)
    );
