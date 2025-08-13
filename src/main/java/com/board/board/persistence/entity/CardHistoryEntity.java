package com.board.board.persistence.entity;

import java.time.LocalDateTime;

public class CardHistoryEntity {
    private long id;
    private Long cardId;
    private Long columnId;
    private LocalDateTime enteredAt;
    private LocalDateTime leftAt;

    public CardHistoryEntity() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public Long getColumnId() { return columnId; }
    public void setColumnId(Long columnId) { this.columnId = columnId; }

    public LocalDateTime getEnteredAt() { return enteredAt; }
    public void setEnteredAt(LocalDateTime enteredAt) { this.enteredAt = enteredAt; }

    public LocalDateTime getLeftAt() { return leftAt; }
    public void setLeftAt(LocalDateTime leftAt) { this.leftAt = leftAt; }
}