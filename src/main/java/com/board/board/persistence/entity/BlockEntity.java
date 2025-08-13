package com.board.board.persistence.entity;

import java.time.LocalDateTime;

public class BlockEntity {
    private long id;
    private Long cardId;
    private LocalDateTime blockedAt;
    private String blockReason;
    private LocalDateTime unlockedAt;
    private String unblockReason;

    public BlockEntity() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }

    public LocalDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }

    public String getBlockReason() { return blockReason; }
    public void setBlockReason(String blockReason) { this.blockReason = blockReason; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }

    public String getUnblockReason() { return unblockReason; }
    public void setUnblockReason(String unblockReason) { this.unblockReason = unblockReason; }
}