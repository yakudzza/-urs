CREATE TABLE members (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         type_dev VARCHAR(255) NOT NULL,
                         role VARCHAR(255) NOT NULL,
                         CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);