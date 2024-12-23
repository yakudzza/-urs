-- liquibase formatted sql
-- changeset Andrew:1
ALTER TABLE hackathons
ADD COLUMN  full_description TEXT,
ADD COLUMN photo_url TEXT;