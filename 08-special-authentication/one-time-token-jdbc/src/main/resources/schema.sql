DROP table if exists authorities;
DROP table if exists users;

create table if not exists users
(
    username
    varchar
(
    50
) not null primary key,
    name varchar
(
    100
) not null,
    email varchar
(
    50
) not null,
    password varchar
(
    500
) not null,
    enabled boolean not null
    );

create table if not exists authorities
(
    username
    varchar
(
    50
) not null,
    authority varchar
(
    50
) not null,
    CONSTRAINT fk_authorities_users FOREIGN KEY
(
    username
) REFERENCES users
(
    username
)
    );

create unique index ix_auth_username on authorities (username, authority);

create table if not exists one_time_tokens
(
    token_value varchar(36) not null primary key,
    username varchar(50) not null,
    expires_at timestamp not null
);
