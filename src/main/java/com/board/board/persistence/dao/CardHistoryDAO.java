package com.board.board.persistence.dao;

import com.board.board.persistence.entity.CardHistoryEntity;

import java.sql.*;

public class CardHistoryDAO {
    private final Connection connection;

    public CardHistoryDAO(Connection connection) { this.connection = connection; }

    public void enterColumn(Long cardId, Long columnId, java.time.LocalDateTime enteredAt) throws SQLException {
        String sql = "INSERT INTO CARD_HISTORY (card_id, column_id, entered_at, left_at) VALUES (?,?,?,NULL)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cardId);
            ps.setLong(2, columnId);
            ps.setTimestamp(3, Timestamp.valueOf(enteredAt));
            ps.executeUpdate();
        }
    }

    public void leaveCurrent(Long cardId, java.time.LocalDateTime leftAt) throws SQLException {
        String sql = "UPDATE CARD_HISTORY SET left_at=? WHERE card_id=? AND left_at IS NULL";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(leftAt));
            ps.setLong(2, cardId);
            ps.executeUpdate();
        }
    }
}