CREATE INDEX idx_product_sku ON product USING btree (sku);
CREATE INDEX idx_product_user_id ON product USING btree (user_id);
CREATE INDEX idx_product_created_on ON product USING btree (created_on);
CREATE INDEX idx_product_updated_on ON product USING btree (updated_on);


CREATE INDEX idx_sc_user_id ON shopping_cart USING btree (user_id);

CREATE INDEX idx_users_email ON users USING btree (email);