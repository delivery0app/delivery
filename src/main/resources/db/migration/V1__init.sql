CREATE TABLE courier
(
    id             INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    inn            VARCHAR(12)  NOT NULL UNIQUE,
    phone_number   VARCHAR(20)  NOT NULL UNIQUE,
    email          VARCHAR(100) NOT NULL UNIQUE,
    password       VARCHAR(60),
    courier_status VARCHAR
);

CREATE TABLE customer
(
    id           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL UNIQUE,
    email        VARCHAR(100) NOT NULL UNIQUE,
    password     VARCHAR(60)
);

CREATE TABLE order_table
(
    id               INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    sender_address   VARCHAR(100) NOT NULL,
    delivery_address VARCHAR(100) NOT NULL,
    weight           INT CHECK (weight > 0),
    description      VARCHAR(200),
    payment_method   VARCHAR      NOT NULL,
    delivery_date    DATE         NOT NULL,
    order_status     VARCHAR      NOT NULL,
    creation_date    TIMESTAMP    NOT NULL,
    fragile_cargo    BOOLEAN,
    price            NUMERIC      NOT NULL,
    courier_id       INT          REFERENCES courier (id) ON DELETE SET NULL,
    customer_id      INT          NOT NULL REFERENCES customer (id) ON DELETE SET NULL
);