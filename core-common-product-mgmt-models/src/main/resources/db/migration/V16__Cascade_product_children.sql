-- Intra-service foreign keys delete with their parent so a removed
-- proposed product does not leave configuration, versions or documents behind.
-- A product also cannot be related to itself.

ALTER TABLE product DROP CONSTRAINT IF EXISTS fk_product_category;
ALTER TABLE product ADD CONSTRAINT fk_product_category
    FOREIGN KEY (product_category_id) REFERENCES product_category(product_category_id) ON DELETE CASCADE;

ALTER TABLE product_category DROP CONSTRAINT IF EXISTS fk_parent_category;
ALTER TABLE product_category ADD CONSTRAINT fk_parent_category
    FOREIGN KEY (parent_category_id) REFERENCES product_category(product_category_id) ON DELETE CASCADE;

ALTER TABLE product_documentation DROP CONSTRAINT IF EXISTS fk_doc_product;
ALTER TABLE product_documentation ADD CONSTRAINT fk_doc_product
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE;

ALTER TABLE product_documentation_requirement DROP CONSTRAINT IF EXISTS fk_doc_requirement_product;
ALTER TABLE product_documentation_requirement ADD CONSTRAINT fk_doc_requirement_product
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE;

ALTER TABLE product_relationship DROP CONSTRAINT IF EXISTS fk_relationship_product;
ALTER TABLE product_relationship ADD CONSTRAINT fk_relationship_product
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE;

ALTER TABLE product_relationship DROP CONSTRAINT IF EXISTS fk_related_product;
ALTER TABLE product_relationship ADD CONSTRAINT fk_related_product
    FOREIGN KEY (related_product_id) REFERENCES product(product_id) ON DELETE CASCADE;

ALTER TABLE product_relationship DROP CONSTRAINT IF EXISTS chk_product_relationship_not_self;
ALTER TABLE product_relationship ADD CONSTRAINT chk_product_relationship_not_self
    CHECK (product_id <> related_product_id);

ALTER TABLE product_version DROP CONSTRAINT IF EXISTS fk_version_product;
ALTER TABLE product_version ADD CONSTRAINT fk_version_product
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE;

ALTER TABLE product_localization DROP CONSTRAINT IF EXISTS fk_localization_product;
ALTER TABLE product_localization ADD CONSTRAINT fk_localization_product
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE;

ALTER TABLE product_configuration DROP CONSTRAINT IF EXISTS fk_configuration_product;
ALTER TABLE product_configuration ADD CONSTRAINT fk_configuration_product
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE;
