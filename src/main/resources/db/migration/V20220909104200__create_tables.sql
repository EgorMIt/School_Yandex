CREATE TYPE system_item_type AS ENUM ('FILE', 'FOLDER');

CREATE SEQUENCE IF NOT EXISTS system_item_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS system_item_history_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE system_item
(
    id        BIGINT           NOT NULL
        constraint system_item_pk primary key,
    itemName  VARCHAR(255)     not null,
    url       VARCHAR(255),
    date      TIMESTAMP        not null,
    parent_id VARCHAR(255),
    type      system_item_type not null,
    size      BIGINT,
    UNIQUE (itemName)
);

CREATE TABLE system_item_history
(
    id      BIGINT    NOT NULL
        constraint system_item_history_pk primary key,
    item_id BIGINT    not null
        constraint FK_HISTORY_ON_SYSTEM_ITEM
            references "system_item",
    date    TIMESTAMP not null
);
