--liquibase formatted sql

--changeset arthur:004-blocks
CREATE TABLE BLOCKS (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  card_id        BIGINT NOT NULL,
  blocked_at     DATETIME NOT NULL,
  block_reason   TEXT,
  unlocked_at    DATETIME NULL,
  unblock_reason TEXT NULL,
  CONSTRAINT fk_blocks_card
    FOREIGN KEY (card_id) REFERENCES CARDS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback DROP TABLE BLOCKS;
