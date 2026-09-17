CREATE TABLE IF NOT EXISTS users
(
    username            VARCHAR(128) NOT NULL,
    password            VARCHAR(256) NOT NULL,
    email               VARCHAR(256) NOT NULL,
    totp_secret           VARCHAR(256) NOT NULL,
    totp_mfa_enabled      boolean      NOT NULL DEFAULT false,
    email_mfa_enabled     boolean      NOT NULL DEFAULT false,
    mnemonic_phrase_hash  VARCHAR(256) NOT NULL DEFAULT '',
    mnemonic_mfa_enabled  boolean      NOT NULL DEFAULT false,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS email_mfa_codes
(
    username    VARCHAR(128) NOT NULL,
    code_hash   VARCHAR(256) NOT NULL,
    expires_at  DATETIME(6)  NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE IF NOT EXISTS user_entities
(
    id           varchar(1000) character set ascii collate ascii_bin not null,
    name         varchar(100)  not null,
    display_name varchar(200),
    primary key (id)
);

CREATE TABLE IF NOT EXISTS user_credentials
(
    credential_id                varchar(1000) character set ascii collate ascii_bin not null,
    user_entity_user_id          varchar(1000) not null,
    public_key                   BLOB          not null,
    signature_count              bigint,
    uv_initialized               boolean,
    backup_eligible              boolean       not null,
    authenticator_transports     varchar(1000),
    public_key_credential_type   varchar(100),
    backup_state                 boolean       not null,
    attestation_object           BLOB,
    attestation_client_data_json BLOB,
    created                      timestamp,
    last_used                    timestamp,
    label                        varchar(1000) not null,
    primary key (credential_id)
);
