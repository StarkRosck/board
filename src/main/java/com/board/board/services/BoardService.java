package com.board.board.services;

import com.board.board.persistence.dao.*;
import com.board.board.persistence.entity.*;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class BoardService {

    private final DataSource dataSource;

    public BoardService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ===== Boards =====
    public BoardEntity createBoard(String name, List<BoardColumnEntity> columns) throws SQLException {
        if (columns == null || columns.size() < 3)
            throw new IllegalArgumentException("Board precisa de pelo menos 3 colunas");

        long initial = columns.stream().filter(c -> c.getKind() == BoardColumnKindEnum.INITIAL).count();
        long fin     = columns.stream().filter(c -> c.getKind() == BoardColumnKindEnum.FINAL).count();
        long cancel  = columns.stream().filter(c -> c.getKind() == BoardColumnKindEnum.CANCELED).count();
        if (initial != 1 || fin != 1 || cancel != 1)
            throw new IllegalArgumentException("Deve haver 1 INITIAL, 1 FINAL e 1 CANCELED");

        columns.sort(Comparator.comparingInt(c ->
                switch (c.getKind()) { case INITIAL -> 0; case PENDING -> 1; case FINAL -> 2; case CANCELED -> 3; }));
        for (int i = 0; i < columns.size(); i++) columns.get(i).setOrder(i + 1);

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            BoardDAO bDao = new BoardDAO(conn);
            BoardColumnDAO cDao = new BoardColumnDAO(conn);

            BoardEntity b = new BoardEntity();
            b.setName(name);
            bDao.insert(b);

            for (BoardColumnEntity c : columns) {
                c.setBoard(b);
                cDao.insert(c);
            }

            conn.commit();
            return b;
        } catch (Exception e) {
            throw asSql(e);
        }
    }

    public boolean deleteBoard(Long id) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            BoardDAO bDao = new BoardDAO(conn);
            if (!bDao.exists(id)) return false;
            bDao.delete(id); // ON DELETE CASCADE recomendado nas FKs
            return true;
        }
    }

    public List<BoardEntity> listBoards() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            return new BoardDAO(conn).listAll();
        }
    }

    public List<BoardColumnEntity> listColumns(Long boardId) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            return new BoardColumnDAO(conn).listByBoardId(boardId);
        }
    }

    // ===== Cards =====
    public CardEntity createCard(Long boardId, String title, String description) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            BoardColumnDAO cDao = new BoardColumnDAO(conn);
            CardDAO cardDao = new CardDAO(conn);
            CardHistoryDAO hDao = new CardHistoryDAO(conn);

            var initial = cDao.findByBoardAndKind(boardId, BoardColumnKindEnum.INITIAL);
            if (initial == null) throw new SQLException("Board sem coluna INITIAL");

            CardEntity c = new CardEntity();
            c.setTitle(title);
            c.setDescription(description);
            c.setCreatedAt(LocalDateTime.now());
            c.setBoardColumnId(initial.getId());
            c.setBlocked(false);

            cardDao.insert(c);
            hDao.enterColumn(c.getId(), initial.getId(), LocalDateTime.now());
            conn.commit();
            return c;
        } catch (Exception e) {
            throw asSql(e);
        }
    }

    public boolean moveToNext(Long boardId, Long cardId) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            var cDao = new BoardColumnDAO(conn);
            var cardDao = new CardDAO(conn);
            var hDao = new CardHistoryDAO(conn);

            var card = cardDao.findById(cardId).orElse(null);
            if (card == null) return false;
            if (card.isBlocked()) throw new SQLException("Card bloqueado");

            var cols = cDao.listByBoardId(boardId);
            int idx = indexOfColumn(cols, card.getBoardColumnId());
            if (idx < 0) throw new SQLException("Coluna atual não pertence ao board");

            var current = cols.get(idx);
            if (current.getKind() == BoardColumnKindEnum.FINAL) throw new SQLException("Card já está na FINAL");

            var next = cols.get(idx + 1);
            hDao.leaveCurrent(cardId, LocalDateTime.now());
            cardDao.moveToColumn(cardId, next.getId());
            hDao.enterColumn(cardId, next.getId(), LocalDateTime.now());

            conn.commit();
            return true;
        } catch (Exception e) {
            throw asSql(e);
        }
    }

    public boolean cancelCard(Long boardId, Long cardId) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            var cDao = new BoardColumnDAO(conn);
            var cardDao = new CardDAO(conn);
            var hDao = new CardHistoryDAO(conn);

            var card = cardDao.findById(cardId).orElse(null);
            if (card == null) return false;
            if (card.isBlocked()) throw new SQLException("Card bloqueado");

            var cols = cDao.listByBoardId(boardId);
            var current = cols.stream().filter(x -> Objects.equals(x.getId(), card.getBoardColumnId())).findFirst().orElse(null);
            if (current == null) throw new SQLException("Coluna atual não pertence ao board");
            if (current.getKind() == BoardColumnKindEnum.FINAL) throw new SQLException("Não cancela a partir da FINAL");

            var canceled = cDao.findByBoardAndKind(boardId, BoardColumnKindEnum.CANCELED);
            if (canceled == null) throw new SQLException("Board sem coluna CANCELED");

            hDao.leaveCurrent(cardId, LocalDateTime.now());
            cardDao.moveToColumn(cardId, canceled.getId());
            hDao.enterColumn(cardId, canceled.getId(), LocalDateTime.now());

            conn.commit();
            return true;
        } catch (Exception e) {
            throw asSql(e);
        }
    }

    public void blockCard(Long cardId, String reason) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            var cardDao = new CardDAO(conn);
            var blockDao = new BlockDAO(conn);

            var card = cardDao.findById(cardId).orElseThrow(() -> new SQLException("Card não encontrado"));
            if (card.isBlocked()) return;

            cardDao.setBlocked(cardId, true);

            BlockEntity b = new BlockEntity();
            b.setCardId(cardId);
            b.setBlockedAt(LocalDateTime.now());
            b.setBlockReason(reason);
            b.setUnlockedAt(null);
            b.setUnblockReason(null);
            blockDao.insert(b);
        }
    }

    public void unlockAndUnblock(Long cardId, String reason) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            var blockDao = new BlockDAO(conn);
            var cardDao = new CardDAO(conn);
            blockDao.closeOpenBlock(cardId, reason, LocalDateTime.now());
            cardDao.setBlocked(cardId, false);
        }
    }

    public void unblockCard(Long cardId, String reason) throws SQLException {
        unlockAndUnblock(cardId, reason);
    }

    private int indexOfColumn(List<BoardColumnEntity> cols, Long columnId) {
        for (int i = 0; i < cols.size(); i++) if (Objects.equals(cols.get(i).getId(), columnId)) return i;
        return -1;
    }

    private SQLException asSql(Exception e) throws SQLException {
        if (e instanceof SQLException se) return se;
        return new SQLException(e);
    }
}
