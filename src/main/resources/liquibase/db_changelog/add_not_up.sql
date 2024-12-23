-- liquibase formatted sql

-- changeset Andrew:1
ALTER TABLE notifications
ADD COLUMN were_from VARCHAR(255);