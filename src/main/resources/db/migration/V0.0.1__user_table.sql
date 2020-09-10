
CREATE SEQUENCE user_id_sequence INCREMENT 1 START 1000 CACHE 10;

CREATE TABLE IF NOT EXISTS USERS
(
    ID        bigint       NOT NULL DEFAULT nextval('user_id_sequence'::regclass),
    USERNAME  varchar(200) NOT NULL UNIQUE,
    PASSWORD  varchar(250) NOT NULL,
    LAST_SEEN bigint       NOT NULL,
    REG_DATE  bigint       not null,
    USER_ROLE smallint     NOT NULL,
    SECRET_CODE varchar(250) DEFAULT NULL,
    PRIMARY KEY (ID)
);