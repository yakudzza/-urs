CREATE TABLE admins_hackathons (
                                   admin_id BIGINT NOT NULL,
                                   hackathon_id BIGINT NOT NULL,
                                   PRIMARY KEY (admin_id, hackathon_id),
                                   FOREIGN KEY (admin_id) REFERENCES admins(id) ON DELETE CASCADE,
                                   FOREIGN KEY (hackathon_id) REFERENCES hackathons(id) ON DELETE CASCADE
);
