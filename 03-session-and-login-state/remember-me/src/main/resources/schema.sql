DROP TABLE IF EXISTS persistent_logins;
DROP TABLE IF EXISTS authorities;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    username varchar(50)  not null primary key,
    password varchar(500) not null,
    enabled  boolean      not null
);

CREATE TABLE authorities
(
    username  varchar(50) not null,
    authority varchar(50) not null,
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
);

CREATE UNIQUE INDEX ix_auth_username ON authorities (username, authority);

CREATE TABLE persistent_logins
(
    username  varchar(64) not null,
    series    varchar(64) not null primary key,
    token     varchar(64) not null,
    last_used timestamp   not null
);
