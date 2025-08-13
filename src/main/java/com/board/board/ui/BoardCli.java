package com.board.board.ui;

import com.board.board.persistence.entity.BoardColumnEntity;
import com.board.board.persistence.entity.BoardColumnKindEnum;
import com.board.board.persistence.entity.BoardEntity;
import com.board.board.services.BoardService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class BoardCli implements CommandLineRunner {

    private final BoardService svc;

    public BoardCli(BoardService svc) {
        this.svc = svc;
    }

    @Override
    public void run(String... args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("==== MENU ====");
            System.out.println("1) Criar novo board");
            System.out.println("2) Selecionar board");
            System.out.println("3) Excluir board");
            System.out.println("4) Sair");
            System.out.print("Escolha: ");
            String opt = sc.nextLine();
            try {
                switch (opt) {
                    case "1" -> criarBoard(sc);
                    case "2" -> selecionarBoard(sc);
                    case "3" -> excluirBoard(sc);
                    case "4" -> { return; }
                    default -> System.out.println("Opção inválida\n");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private void criarBoard(Scanner sc) throws Exception {
        System.out.print("Nome do board: ");
        String name = sc.nextLine();

        List<BoardColumnEntity> cols = new ArrayList<>();

        System.out.print("Nome da coluna INICIAL: ");
        BoardColumnEntity cInit = new BoardColumnEntity();
        cInit.setName(sc.nextLine());
        cInit.setKind(BoardColumnKindEnum.INITIAL);
        cols.add(cInit);

        System.out.print("Quantas colunas PENDENTES? ");
        int n = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < n; i++) {
            System.out.print("Nome da PENDENTE #" + (i+1) + ": ");
            BoardColumnEntity cp = new BoardColumnEntity();
            cp.setName(sc.nextLine());
            cp.setKind(BoardColumnKindEnum.PENDING);
            cols.add(cp);
        }

        System.out.print("Nome da coluna FINAL: ");
        BoardColumnEntity cFinal = new BoardColumnEntity();
        cFinal.setName(sc.nextLine());
        cFinal.setKind(BoardColumnKindEnum.FINAL);
        cols.add(cFinal);

        System.out.print("Nome da coluna CANCELADA: ");
        BoardColumnEntity cCancel = new BoardColumnEntity();
        cCancel.setName(sc.nextLine());
        cCancel.setKind(BoardColumnKindEnum.CANCELED);
        cols.add(cCancel);

        BoardEntity b = svc.createBoard(name, cols);
        System.out.println("Board criado com ID: " + b.getId() + "\n");
    }

    private void selecionarBoard(Scanner sc) throws Exception {
        var boards = svc.listBoards();
        if (boards.isEmpty()) { System.out.println("Nenhum board cadastrado\n"); return; }
        for (var b : boards) System.out.println(b.getId() + ") " + b.getName());
        System.out.print("ID do board: ");
        Long id = Long.parseLong(sc.nextLine());
        manipularBoard(sc, id);
    }

    private void excluirBoard(Scanner sc) throws Exception {
        var boards = svc.listBoards();
        if (boards.isEmpty()) { System.out.println("Nenhum board cadastrado\n"); return; }
        for (var b : boards) System.out.println(b.getId() + ") " + b.getName());
        System.out.print("ID do board a excluir: ");
        Long id = Long.parseLong(sc.nextLine());
        boolean ok = svc.deleteBoard(id);
        System.out.println(ok ? "Excluído" : "Não encontrado");
        System.out.println();
    }

    private void manipularBoard(Scanner sc, Long boardId) throws Exception {
        while (true) {
            System.out.println("==== BOARD " + boardId + " ====");
            System.out.println("1) Criar card");
            System.out.println("2) Mover card para próxima coluna");
            System.out.println("3) Cancelar card");
            System.out.println("4) Bloquear card");
            System.out.println("5) Desbloquear card");
            System.out.println("6) Fechar board");
            System.out.print("Escolha: ");
            String opt = sc.nextLine();

            try {
                switch (opt) {
                    case "1" -> {
                        System.out.print("Título: "); String t = sc.nextLine();
                        System.out.print("Descrição: "); String d = sc.nextLine();
                        var card = svc.createCard(boardId, t, d);
                        System.out.println("Card criado com ID: " + card.getId());
                    }
                    case "2" -> {
                        System.out.print("ID do card: "); Long id = Long.parseLong(sc.nextLine());
                        boolean ok = svc.moveToNext(boardId, id);
                        System.out.println(ok ? "Movido" : "Não foi possível mover");
                    }
                    case "3" -> {
                        System.out.print("ID do card: "); Long id = Long.parseLong(sc.nextLine());
                        boolean ok = svc.cancelCard(boardId, id);
                        System.out.println(ok ? "Cancelado" : "Não foi possível cancelar");
                    }
                    case "4" -> {
                        System.out.print("ID do card: "); Long id = Long.parseLong(sc.nextLine());
                        System.out.print("Motivo do bloqueio: "); String r = sc.nextLine();
                        svc.blockCard(id, r);
                        System.out.println("Card bloqueado");
                    }
                    case "5" -> {
                        System.out.print("ID do card: "); Long id = Long.parseLong(sc.nextLine());
                        System.out.print("Motivo do desbloqueio: "); String r = sc.nextLine();
                        svc.unblockCard(id, r);
                        System.out.println("Card desbloqueado");
                    }
                    case "6" -> { return; }
                    default -> System.out.println("Opção inválida");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }

            System.out.println();
            var cols = svc.listColumns(boardId); // agora vem pelo service
            cols.forEach(c -> System.out.println("[" + c.getOrder() + "] " + c.getKind() + " - " + c.getName()));
            System.out.println();
        }
    }
}
