package com.board.board.persistence.dao;

import com.board.board.persistence.entity.BoardColumnEntity;
import com.board.board.persistence.entity.BoardColumnKindEnum;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoardColumnDAO {
    private final Connection connection;

    public BoardColumnDAO(Connection connection) { this.connection = connection; }

    public BoardColumnEntity insert(final BoardColumnEntity e) throws SQLException {
        final String sql = "INSERT INTO BOARD_COLUMNS (name, `order`, kind, board_id) VALUES (?,?,?,?)";
        if (e.getBoard() == null || e.getBoard().getId() == null || e.getBoard().getId() <= 0) {
            throw new SQLException("board_id inválido");
        }
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            ps.setString(i++, e.getName());
            ps.setInt(i++, e.getOrder());
            ps.setString(i++, e.getKind().name());
            ps.setLong(i++, e.getBoard().getId());
            if (ps.executeUpdate() == 0) throw new SQLException("Insert col falhou");
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getLong(1)); else throw new SQLException("Sem ID gerado");
            }
        }
        return e;
    }

    public List<BoardColumnEntity> listByBoardId(Long boardId) throws SQLException {
        String sql = "SELECT id, name, `order`, kind FROM BOARD_COLUMNS WHERE board_id=? ORDER BY `order`";
        List<BoardColumnEntity> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BoardColumnEntity c = new BoardColumnEntity();
                    c.setId(rs.getLong("id"));
                    c.setName(rs.getString("name"));
                    c.setOrder(rs.getInt("order"));
                    c.setKind(BoardColumnKindEnum.valueOf(rs.getString("kind")));
                    out.add(c);
                }
            }
        }
        return out;
    }

    public BoardColumnEntity findByBoardAndKind(Long boardId, BoardColumnKindEnum kind) throws SQLException {
        String sql = "SELECT id, name, `order`, kind FROM BOARD_COLUMNS WHERE board_id=? AND kind=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, boardId);
            ps.setString(2, kind.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BoardColumnEntity c = new BoardColumnEntity();
                    c.setId(rs.getLong("id"));
                    c.setName(rs.getString("name"));
                    c.setOrder(rs.getInt("order"));
                    c.setKind(BoardColumnKindEnum.valueOf(rs.getString("kind")));
                    return c;
                }
                return null;
            }
        }
    }
}