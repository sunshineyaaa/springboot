create table USER
(
  ID           int AUTO_INCREMENT primary key NOT NULL,
  NAME         VARCHAR(50),
  ACCOUNT_ID   VARCHAR(100),
  TOKEN        CHAR(36),
  GMT_CREATE   BIGINT,
  GMT_MODIFIED BIGINT
);
