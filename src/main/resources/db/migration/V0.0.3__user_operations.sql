
CREATE SEQUENCE operation_id_sequence INCREMENT 1 START 148000 CACHE 10;

CREATE TABLE IF NOT EXISTS USER_OPERATIONS
(
    ID             bigint   NOT NULL DEFAULT nextval('operation_id_sequence'::regclass),
    SENDER_ID  bigint   NOT NULL,
    RECEIVER_ID bigint   NOT NULL,
    OP_TYPE        smallint NOT NULL,
    INCOME         real     NOT NULL,
    OUTCOME        real     NOT NULL,
    FEE            real     NOT NULL,
    CREATED       bigint   NOT NULL,
    UPDATED        bigint   NOT NULL,
    OP_STATE       smallint NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (SENDER_ID) REFERENCES USERS (ID),
    FOREIGN KEY (RECEIVER_ID) REFERENCES USERS (ID)
);