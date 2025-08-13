package com.board.board.persistence.dao;

import com.board.board.persistence.entity.BlockEntity;

import java.sql.*;

public class BlockDAO {
    private final Connection connection;

    public BlockDAO(Connection connection) { this.connection = connection; }

    public BlockEntity insert(BlockEntity b) throws SQLException {
        String sql = "INSERT INTO BLOCKS (card_id, blocked_at, block_reason, unlocked_at, unblock_reason) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, b.getCardId());
            ps.setTimestamp(2, Timestamp.valueOf(b.getBlockedAt()));
            ps.setString(3, b.getBlockReason());
            ps.setTimestamp(4, b.getUnlockedAt() == null ? null : Timestamp.valueOf(b.getUnlockedAt()));
            ps.setString(5, b.getUnblockReason());
            if (ps.executeUpdate() == 0) throw new SQLException("Insert block falhou");
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) b.setId(rs.getLong(1)); }
        }
        return b;
    }

    public void closeOpenBlock(Long cardId, String reason, java.time.LocalDateTime unlockedAt) throws SQLException {
        String sql = "UPDATE BLOCKS SET unlocked_at=?, unblock_reason=? WHERE card_id=? AND unlocked_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(unlockedAt));
            ps.setString(2, reason);
            ps.setLong(3, cardId);
            ps.executeUpdate();
        }
    }
}