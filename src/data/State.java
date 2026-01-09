package data;

import java.util.Arrays;
import java.util.List;

public class State {
    private final Cell[] board;
    private final MoveValue moveValue;
    public final Player activePlayer;

    public State() {
        // Initialize the board
        board = new Cell[30];

        // First 14 elements: alternate between Black and White pieces
        for (int i = 0; i < 14; i++) {
            board[i] = (i % 2 == 0) ? Cell.WHITE : Cell.BLACK;
        }

        // Last 16 elements: all Empty
        for (int i = 14; i < 30; i++) {
            board[i] = Cell.EMPTY;
        }

        activePlayer = Player.HUMAN;
        moveValue = MoveValue.randomThrow();
    }

    public State(Cell[] board, Player activePlayer, MoveValue moveValue) {
        this.board = board;
        this.activePlayer = activePlayer;
        this.moveValue = moveValue;
    }

    public boolean canMove(Move move) {
        throw new RuntimeException("TODO");
    }

    public State move(Move move) {
        throw new RuntimeException("TODO");
    }

    public Player checkWinner() {
        if (Arrays.stream(board).noneMatch(c -> c == Cell.BLACK)) return Player.HUMAN;
        if (Arrays.stream(board).noneMatch(c -> c == Cell.WHITE)) return Player.AI;
        return null;
    }

    public int evaluate() {
        throw new RuntimeException("TODO");
    }

    public List<State> getNextStates() {
        throw new RuntimeException("TODO");
    }

    public void printBoard() {
        System.out.println("-----------------------------");
        System.out.printf("%s's turn with move value of %d\n", activePlayer, moveValue.toNumber());

        // Top
        System.out.println("┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┐");

        // First row
        for (var i = 0; i < 10; i++) {
            System.out.printf("│ %s ", board[i].boardStringSymbol());
        }
        System.out.println("│");

        // Separator
        System.out.println("├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤");

        // Second row
        for (var i = 19; i >= 10; i--) {
            System.out.printf("│ %s ", board[i].boardStringSymbol());
        }
        System.out.println("│");

        // Separator
        System.out.println("├───┼───┼───┼───┼───┼───┼───┼───┼───┼───┤");

        // Third row
        for (var i = 20; i < 30; i++) {
            System.out.printf("│ %s ", board[i].boardStringSymbol());
        }
        System.out.println("│");

        // Bottom
        System.out.println("└───┴───┴───┴───┴───┴───┴───┴───┴───┴───┘");

        System.out.println("-----------------------------");
    }
}
