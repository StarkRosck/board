package com.board.board.persistence.entity;

public class BoardColumnEntity {
    private long id;
    private String name;
    private int order; // mantenha `order` no SQL com crases: `order`
    private BoardColumnKindEnum kind;
    private BoardEntity board;

    public BoardColumnEntity() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    public BoardColumnKindEnum getKind() { return kind; }
    public void setKind(BoardColumnKindEnum kind) { this.kind = kind; }

    public BoardEntity getBoard() { return board; }
    public void setBoard(BoardEntity board) { this.board = board; }
}