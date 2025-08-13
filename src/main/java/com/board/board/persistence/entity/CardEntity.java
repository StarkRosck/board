package com.board.board.persistence.entity;

import java.time.LocalDateTime;

public class CardEntity {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Long boardColumnId; // coluna atual
    private boolean blocked;

    public CardEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getBoardColumnId() { return boardColumnId; }
    public void setBoardColumnId(Long boardColumnId) { this.boardColumnId = boardColumnId; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}