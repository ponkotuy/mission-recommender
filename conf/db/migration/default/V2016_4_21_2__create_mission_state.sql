
CREATE TABLE mission_state(
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        mission_id INT NOT NULL,
        user_id BIGINT NOT NULL,
        is_clear BOOLEAN NOT NULL DEFAULT false,
        feedback INT NOT NULL DEFAULT 0,
        UNIQUE KEY (user_id, mission_id),
        FOREIGN KEY (mission_id) REFERENCES mission(id),
        FOREIGN KEY (user_id) REFERENCES account(id)
) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4;
