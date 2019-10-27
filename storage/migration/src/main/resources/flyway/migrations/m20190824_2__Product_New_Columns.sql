ALTER TABLE product
ADD COLUMN user_id bigint null;

ALTER TABLE product
ADD COLUMN created_on date null;

ALTER TABLE product
ADD COLUMN updated_on date null;
