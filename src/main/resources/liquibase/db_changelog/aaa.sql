--liquibase formatted sql

-- changeset Andrew:1
-- Убедиться, что внешний ключ имеет CASCADE ON DELETE
ALTER TABLE notifications
    DROP CONSTRAINT fk_notifications_team;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_team FOREIGN KEY (team_id)
    REFERENCES teams (id) ON DELETE CASCADE;