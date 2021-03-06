CREATE TABLE IF NOT EXISTS USER_INFO
(
    USER_ID        bigint       NOT NULL,
    FIRST_NAME     varchar(100) NOT NULL,
    LAST_NAME      varchar(100) NOT NULL,
    EMAIL          varchar(100) NOT NULL,
    PRIMARY KEY (USER_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);

CREATE TABLE IF NOT EXISTS USER_BALANCE
(
    USER_ID bigint NOT NULL,
    BALANCE real,
    PRIMARY KEY (USER_ID),
    FOREIGN KEY (USER_ID) REFERENCES USERS (ID)
);