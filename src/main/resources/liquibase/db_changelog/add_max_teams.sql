-- liquibase formatted sql
-- changeset Andrew:1
ALTER TABLE hackathons
ADD COLUMN max_teams INT;