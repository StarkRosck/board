--liquibase formatted sql

--changeset arthur:005-card-history
CREATE TABLE CARD_HISTORY (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  card_id    BIGINT NOT NULL,
  column_id  BIGINT NOT NULL,
  entered_at DATETIME NOT NULL,
  left_at    DATETIME NULL,
  CONSTRAINT fk_hist_card FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE,
  CONSTRAINT fk_hist_col  FOREIGN KEY (column_id) REFERENCES BOARD_COLUMNS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback DROP TABLE CARD_HISTORY;
