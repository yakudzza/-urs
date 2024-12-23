-- liquibase formatted sql

-- changeset Andrew:1
ALTER TABLE notifications ADD COLUMN member_id BIGINT;
ALTER TABLE notifications ADD CONSTRAINT fk_member FOREIGN KEY (member_id) REFERENCES members(id);