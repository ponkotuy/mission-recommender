
CREATE TABLE mission (
        id INT NOT NULL,
        `name` VARCHAR(255) NOT NULL,
        intro TEXT(65535) NOT NULL,
        latitude DOUBLE NOT NULL,
        longitude DOUBLE NOT NULL,
        PRIMARY KEY (id),
        INDEX (latitude, longitude)
) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4;

CREATE TABLE portal (
        id INT NOT NULL,
        `name` VARCHAR(255) NOT NULL,
        latitude DOUBLE NOT NULL,
        longitude DOUBLE NOT NULL,
        typ CHAR(4) NOT NULL,
        PRIMARY KEY(id),
        INDEX (latitude, longitude)
) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4;

CREATE TABLE mission_portal (
        mission_id INT NOT NULL,
        `no` INT NOT NULL,
        portal_id INT NOT NULL,
        PRIMARY KEY (mission_id, `no`),
        FOREIGN KEY (mission_id) REFERENCES mission(id),
        FOREIGN KEY (portal_id) REFERENCES portal(id)
) ENGINE=InnoDB, DEFAULT CHARSET=utf8mb4;
