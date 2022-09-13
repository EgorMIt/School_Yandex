CREATE SEQUENCE IF NOT EXISTS system_item_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE system_item
(
    id        BIGINT                   NOT NULL
        constraint system_item_pk primary key,
    item_name VARCHAR(255)             not null,
    url       VARCHAR(255),
    date      TIMESTAMP with time zone not null,
    parent_id VARCHAR(255),
    type      VARCHAR(255)             not null,
    size      BIGINT,
    is_active BOOLEAN                  not null
);