drop table if exists hits;

CREATE TABLE IF NOT EXISTS hits
(
    id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app     VARCHAR(255)                            NOT NULL,
    uri     VARCHAR(255)                            NOT NULL,
    ip      VARCHAR(255)                            NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_hit PRIMARY KEY (id)
);
