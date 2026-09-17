CREATE TABLE IF NOT EXISTS users
(
    username    VARCHAR(128) NOT NULL,
    password    VARCHAR(256) NOT NULL,
    totp_secret VARCHAR(256) NOT NULL,
    PRIMARY KEY (username)
);
