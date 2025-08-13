package com.board.board.persistence.dao;

import com.board.board.persistence.entity.BoardEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardDAO {
    private final Connection connection;

    public BoardDAO(Connection connection) { this.connection = connection; }

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        final String sql = "INSERT INTO BOARDS (name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            if (ps.executeUpdate() == 0) throw new SQLException("Insert board falhou");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entity.setId(rs.getLong(1)); else throw new SQLException("Sem ID gerado");
            }
        }
        return entity;
    }

    public void delete(final Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM BOARDS WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM BOARDS WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BoardEntity b = new BoardEntity();
                    b.setId(rs.getLong("id"));
                    b.setName(rs.getString("name"));
                    return Optional.of(b);
                }
                return Optional.empty();
            }
        }
    }

    public List<BoardEntity> listAll() throws SQLException {
        List<BoardEntity> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM BOARDS ORDER BY id")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BoardEntity b = new BoardEntity();
                    b.setId(rs.getLong("id"));
                    b.setName(rs.getString("name"));
                    out.add(b);
                }
            }
        }
        return out;
    }

    public boolean exists(final Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM BOARDS WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}