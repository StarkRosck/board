--liquibase formatted sql

--changeset arthur:001-boards
CREATE TABLE BOARDS (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

--rollback DROP TABLE BOARDS;
