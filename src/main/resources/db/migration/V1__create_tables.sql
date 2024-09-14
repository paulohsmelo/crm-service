CREATE TABLE IF NOT EXISTS users
(
    id          bigint  NOT NULL,
    username    TEXT    NOT NULL,
    password    TEXT    NOT NULL,
    role        TEXT    NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS users_seq
    INCREMENT 50
    START 1
    MINVALUE 1;

CREATE TABLE IF NOT EXISTS customers
(
    id          bigint  NOT NULL,
    name        TEXT    NOT NULL,
    surname     TEXT    NOT NULL,
    photo_url   TEXT,
    created_by  bigint  NOT NULL,
    modified_by bigint,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);

CREATE SEQUENCE IF NOT EXISTS customers_seq
    INCREMENT 50
    START 1
    MINVALUE 1;

ALTER TABLE customers ADD CONSTRAINT fk_customers_created_by_on_users FOREIGN KEY (created_by) REFERENCES users (id);
ALTER TABLE customers ADD CONSTRAINT fk_customers_modified_by_on_users FOREIGN KEY (modified_by) REFERENCES users (id);