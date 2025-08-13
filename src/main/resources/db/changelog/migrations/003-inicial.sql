--liquibase formatted sql

--changeset arthur:003-cards
CREATE TABLE CARDS (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  title           VARCHAR(255) NOT NULL,
  description     TEXT,
  created_at      DATETIME NOT NULL,
  board_column_id BIGINT NOT NULL,
  blocked         BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_cards_column
    FOREIGN KEY (board_column_id) REFERENCES BOARD_COLUMNS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback DROP TABLE CARDS;
