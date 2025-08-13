--liquibase formatted sql

--changeset arthur:002-board-columns
CREATE TABLE BOARD_COLUMNS (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  `order`  INT NOT NULL,
  kind     VARCHAR(32) NOT NULL,
  board_id BIGINT NOT NULL,
  CONSTRAINT fk_board_columns_board
    FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,
  CONSTRAINT uk_board_order UNIQUE (board_id, `order`)
) ENGINE=InnoDB;

--rollback DROP TABLE BOARD_COLUMNS;
