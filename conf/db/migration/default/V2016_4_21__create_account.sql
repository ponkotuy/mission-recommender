
CREATE TABLE account(
        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        `name` VARCHAR(32) NOT NULL,
        hash binary(32) NOT NULL,
        salt binary(32) NOT NULL,
        created BIGINT NOT NULL
) ENGINE = InnoDB, DEFAULT CHARSET=utf8mb4;
