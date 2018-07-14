CREATE TABLE ACCOUNT (
    ID bigint not null auto_increment,
    NUMBER char(20) not null,
    NAME varchar(100),
    DESCRIPTION varchar(100),


    CREATION timestamp not null,
    LAST_UPDATE timestamp not null,
    PRIMARY KEY (ID)

);

CREATE TABLE  ACCOUNT_BALANCE (
    ID bigint not null ,
    VALUE decimal(16.4) not null,

    LAST_UPDATE timestamp not null,
    PRIMARY KEY (ID)

);

CREATE TABLE TRANSFER_HISTORY (
    ID bigint not null auto_increment,
    FROM_ACCOUNT bigint not null,
    TO_ACCOUNT bigint not null,
    VALUE decimal(16.4) not null,
    STATUS char(10),
    DESCRIPTION varchar(100),


    CREATION timestamp not null,
    LAST_UPDATE timestamp not null,
    PRIMARY KEY (ID)

);

ALTER TABLE TRANSFER_HISTORY
    ADD FOREIGN KEY (FROM_ACCOUNT)
    REFERENCES ACCOUNT(ID);

ALTER TABLE TRANSFER_HISTORY
    ADD FOREIGN KEY (TO_ACCOUNT)
    REFERENCES ACCOUNT(ID);
