CREATE TABLE users (
    id       SERIAL PRIMARY KEY,
    email    varchar UNIQUE NOT NULL,
    name     varchar        NOT NULL,
    password varchar        NOT NULL
);