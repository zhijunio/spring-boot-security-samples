CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR(128) NOT NULL,
    password VARCHAR(256) NOT NULL,
    email    VARCHAR(256) NOT NULL,
    PRIMARY KEY (username)
);
