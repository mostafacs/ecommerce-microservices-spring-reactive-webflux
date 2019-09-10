ALTER TABLE shopping_cart
ADD COLUMN created_on date null;

ALTER TABLE shopping_cart
ADD COLUMN updated_on date null;

CREATE INDEX idx_cart_order_created_on ON shopping_cart USING btree (created_on);
CREATE INDEX idx_cart_order_updated_on ON shopping_cart USING btree (updated_on);
