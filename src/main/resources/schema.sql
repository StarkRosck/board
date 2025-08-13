-- CRIE AS TABELAS (não dá DROP para não arriscar em prod)
-- Se o schema já tiver tabelas conflitantes, comente os CREATE e ajuste manualmente.

CREATE TABLE IF NOT EXISTS BOARDS (
  id   BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS BOARD_COLUMNS (
  id       BIGINT AUTO_INCREMENT PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  `order`  INT NOT NULL,
  kind     VARCHAR(32) NOT NULL,
  board_id BIGINT NOT NULL,
  CONSTRAINT fk_board_columns_board
    FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,
  CONSTRAINT uk_board_order UNIQUE (board_id, `order`)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS CARDS (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  title           VARCHAR(255) NOT NULL,
  description     TEXT,
  created_at      DATETIME NOT NULL,
  board_column_id BIGINT NOT NULL,
  blocked         BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fk_cards_column
    FOREIGN KEY (board_column_id) REFERENCES BOARD_COLUMNS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS BLOCKS (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  card_id        BIGINT NOT NULL,
  blocked_at     DATETIME NOT NULL,
  block_reason   TEXT,
  unlocked_at    DATETIME NULL,
  unblock_reason TEXT NULL,
  CONSTRAINT fk_blocks_card
    FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS CARD_HISTORY (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  card_id    BIGINT NOT NULL,
  column_id  BIGINT NOT NULL,
  entered_at DATETIME NOT NULL,
  left_at    DATETIME NULL,
  CONSTRAINT fk_hist_card FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE,
  CONSTRAINT fk_hist_col  FOREIGN KEY (column_id) REFERENCES BOARD_COLUMNS(id) ON DELETE CASCADE
) ENGINE=InnoDB;
