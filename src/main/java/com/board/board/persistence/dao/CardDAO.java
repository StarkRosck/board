package com.board.board.persistence.dao;

import com.board.board.persistence.entity.CardEntity;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CardDAO {
    private final Connection connection;

    public CardDAO(Connection connection) { this.connection = connection; }

    public CardEntity insert(CardEntity c) throws SQLException {
        String sql = "INSERT INTO CARDS (title, description, created_at, board_column_id, blocked) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getTitle());
            ps.setString(2, c.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(c.getCreatedAt() == null ? LocalDateTime.now() : c.getCreatedAt()));
            ps.setLong(4, c.getBoardColumnId());
            ps.setBoolean(5, c.isBlocked());
            if (ps.executeUpdate() == 0) throw new SQLException("Insert card falhou");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getLong(1)); else throw new SQLException("Sem ID gerado");
            }
        }
        return c;
    }

    public Optional<CardEntity> findById(Long id) throws SQLException {
        String sql = "SELECT id, title, description, created_at, board_column_id, blocked FROM CARDS WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CardEntity c = map(rs);
                    return Optional.of(c);
                }
                return Optional.empty();
            }
        }
    }

    public List<CardEntity> listByBoard(Long boardId) throws SQLException {
        String sql = "SELECT c.id, c.title, c.description, c.created_at, c.board_column_id, c.blocked " +
                "FROM CARDS c JOIN BOARD_COLUMNS bc ON bc.id = c.board_column_id WHERE bc.board_id=? ORDER BY c.id";
        List<CardEntity> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        }
        return out;
    }

    public void moveToColumn(Long cardId, Long newColumnId) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE CARDS SET board_column_id=? WHERE id=?")) {
            ps.setLong(1, newColumnId);
            ps.setLong(2, cardId);
            ps.executeUpdate();
        }
    }

    public void setBlocked(Long cardId, boolean blocked) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("UPDATE CARDS SET blocked=? WHERE id=?")) {
            ps.setBoolean(1, blocked);
            ps.setLong(2, cardId);
            ps.executeUpdate();
        }
    }

    private CardEntity map(ResultSet rs) throws SQLException {
        CardEntity c = new CardEntity();
        c.setId(rs.getLong("id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        Timestamp ts = rs.getTimestamp("created_at");
        c.setCreatedAt(ts == null ? null : ts.toLocalDateTime());
        c.setBoardColumnId(rs.getLong("board_column_id"));
        c.setBlocked(rs.getBoolean("blocked"));
        return c;
    }
}