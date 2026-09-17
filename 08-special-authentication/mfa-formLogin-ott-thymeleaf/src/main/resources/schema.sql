CREATE TABLE IF NOT EXISTS users
(
    username VARCHAR(128) NOT NULL,
    password VARCHAR(256) NOT NULL,
    email    VARCHAR(256) NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS one_time_tokens
(
    token_value VARCHAR(36)  NOT NULL,
    username    VARCHAR(128) NOT NULL,
    expires_at  TIMESTAMP    NOT NULL,
    PRIMARY KEY (token_value)
);
