-- Таблица пользователей
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       firstname VARCHAR(255),
                       lastname VARCHAR(255),
                       patronymic VARCHAR(255),
                       phone_number VARCHAR(255),
                       email VARCHAR(255) UNIQUE,
                       password VARCHAR(255),
                       role VARCHAR(255) NOT NULL,
                       additional_info TEXT
);

-- Таблица менеджеров
CREATE TABLE managers (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          CONSTRAINT fk_managers_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Таблица хакатонов
CREATE TABLE hackathons (
                            id BIGSERIAL PRIMARY KEY,
                            title VARCHAR(255) NOT NULL,
                            description TEXT,
                            start_date TIMESTAMP,
                            end_date TIMESTAMP,
                            creator_id BIGINT NOT NULL,
                            state VARCHAR(255),
                            manager_id BIGINT,
                            teams_allowed BOOLEAN DEFAULT FALSE,
                            team_size INT,
                            CONSTRAINT fk_hackathons_creator FOREIGN KEY (creator_id) REFERENCES users(id),
                            CONSTRAINT fk_hackathons_manager FOREIGN KEY (manager_id) REFERENCES managers(id)
);

-- Таблица команд
CREATE TABLE teams (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       hackathon_id BIGINT NOT NULL,
                       manager_id BIGINT,
                       status VARCHAR(255),
                       creator_id BIGINT,
                       CONSTRAINT fk_teams_hackathon FOREIGN KEY (hackathon_id) REFERENCES hackathons(id),
                       CONSTRAINT fk_teams_manager FOREIGN KEY (manager_id) REFERENCES managers(id),
                       CONSTRAINT fk_teams_creator FOREIGN KEY (creator_id) REFERENCES users(id)
);

-- Таблица администраторов
CREATE TABLE admins (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        CONSTRAINT fk_admins_user FOREIGN KEY (user_id) REFERENCES users(id)
);



-- Таблица членов команды (связь many-to-many между командами и пользователями)
CREATE TABLE team_members (
                              team_id BIGINT NOT NULL,
                              user_id BIGINT NOT NULL,
                              PRIMARY KEY (team_id, user_id),
                              CONSTRAINT fk_team_members_team FOREIGN KEY (team_id) REFERENCES teams(id),
                              CONSTRAINT fk_team_members_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Таблица уведомлений
CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               text TEXT NOT NULL,
                               status VARCHAR(255),
                               manager_id BIGINT NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               team_id BIGINT,
                               CONSTRAINT fk_notifications_manager FOREIGN KEY (manager_id) REFERENCES managers(id),
                               CONSTRAINT fk_notifications_team FOREIGN KEY (team_id) REFERENCES teams(id)
);