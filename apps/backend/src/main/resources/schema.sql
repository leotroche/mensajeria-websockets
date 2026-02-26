
CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS users (
                       username CITEXT NOT NULL PRIMARY KEY,
                       password VARCHAR(500) NOT NULL,
                       enabled BOOLEAN NOT NULL,
                       id SERIAL NOT NULL
);

CREATE TABLE IF NOT EXISTS authorities (
                             username CITEXT NOT NULL,
                             authority CITEXT NOT NULL,
                             CONSTRAINT fk_authorities_users
                                 FOREIGN KEY (username) REFERENCES users (username)
);

CREATE UNIQUE INDEX IF NOT EXISTS ix_auth_username
    ON authorities (username, authority);