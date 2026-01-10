package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class State {
    private final Cell[] board;
    private final MoveValue moveValue;
    public final Player activePlayer;

    private static final int HOUSE_OF_RETURN_INDEX = 14;
    private static final int HOUSE_OF_HAPPINESS_INDEX = 25;
    private static final int HOUSE_OF_WATER_INDEX = 26;
    private static final int HOUSE_OF_THREE_TRUTHS_INDEX = 27;
    private static final int HOUSE_OF_TWO_ATOMS_INDEX = 28;
    private static final int HOUSE_OF_END_INDEX = 29;
    private static final int PIECE_FREE_INDEX = 30;

    public State() {
        // Initialize the board
        board = new Cell[30];

        // First 14 elements: alternate between Black and White pieces
        for (int i = 0; i < 14; i++) {
            board[i] = (i % 2 == 0) ? Cell.BLACK : Cell.WHITE;
        }

        // Last 16 elements: all Empty
        for (int i = 14; i < 30; i++) {
            board[i] = Cell.EMPTY;
        }

        activePlayer = Player.MIN;
        moveValue = MoveValue.randomThrow();
    }

    public State(Cell[] board, Player activePlayer, MoveValue moveValue) {
        this.board = board;
        this.activePlayer = activePlayer;
        this.moveValue = moveValue;
    }

    public boolean canMove(Move move) {
        var start = move.start();
        var end = move.end();
        var value = moveValue.toNumber();

        // Check that start is less than end
        if (end <= start) return false;

        // Check the boundaries for the board
        if (start < 0 || end > PIECE_FREE_INDEX) return false;

        // Check for satisfaction between move and moveValue (except for the PIECE_FREE_INDEX case)
        if (
            value != end - start &&
            (start != HOUSE_OF_END_INDEX && end != PIECE_FREE_INDEX)
        ) return false;

        // Check there is no same piece at the end
        if (board[end] == activePlayer.getIdenticalCell()) return false;

        // House of happiness can't be jumped over
        if (end > HOUSE_OF_HAPPINESS_INDEX && start < HOUSE_OF_HAPPINESS_INDEX) return false;

        // House of three truths can't be moved from except when you get THREE only
        if (start == HOUSE_OF_THREE_TRUTHS_INDEX && value != 3) return false;

        // House of two atoms can't be moved from except when you get TWO only
        if (start == HOUSE_OF_TWO_ATOMS_INDEX && value != 2) return false;

        // Otherwise return true
        return true;
    }

    public State move(Move move) {
        if (!canMove(move)) return null;

        var boardCopy = board.clone();

        if (move.end() == PIECE_FREE_INDEX) {
            boardCopy[move.start()] = Cell.EMPTY;
        } else {
            // Swap the cells
            var t = boardCopy[move.start()];
            boardCopy[move.start()] = boardCopy[move.end()];
            boardCopy[move.end()] = t;
        }


        // Do the rules for the special cells (only for the active player)
        for (var i = 0; i < boardCopy.length; i++) {
            var cell = boardCopy[i];
            if (cell != activePlayer.getIdenticalCell()) continue;

            // Handle HOUSE_OF_WATER_INDEX (even it is the current move)
            if (i == HOUSE_OF_WATER_INDEX) {
                backToReturn(boardCopy, i);
            }

            if (i == move.end()) continue;

            // Handle three special cells after leaving it for one turn without moving
            if (i == HOUSE_OF_THREE_TRUTHS_INDEX || i == HOUSE_OF_TWO_ATOMS_INDEX || i == HOUSE_OF_END_INDEX) {
                backToReturn(boardCopy, i);
            }
        }

        return new State(boardCopy, activePlayer.getOtherPlayer(), MoveValue.randomThrow());
    }

    private int cellCount(Cell cell) {
        return (int)Arrays.stream(board).filter(c -> c == cell).count();
    }

    public Player checkWinner() {
        if (cellCount(Cell.BLACK) == 0) return Player.MIN;
        if (cellCount(Cell.WHITE) == 0) return Player.MAX;
        return null;
    }

    public int evaluate() {
        var winner = checkWinner();

        if (winner != null) {
            return winner == Player.MAX ? 100_000 : -100_000;
        }

        var FREE_PIECE_SCORE = 600;

        var result = 0;

        var maximizeCell = Player.MAX.getIdenticalCell();
        var minimizeCell = Player.MIN.getIdenticalCell();

        var maximizeFreePiecesCount = 7 - cellCount(maximizeCell);
        var minimizeFreePiecesCount = 7 - cellCount(minimizeCell);

        result += maximizeFreePiecesCount * FREE_PIECE_SCORE;
        result -= minimizeFreePiecesCount * FREE_PIECE_SCORE;

        for (var i = 0; i < board.length; i++) {
            if (board[i] == Cell.EMPTY) continue;

            var sign = board[i] == maximizeCell ? +1 : -1;

            result += sign * (i + 1) * 10;
        }

        return result;
    }

    public List<State> getNextStates(Player player, MoveValue value) {
        var array = new ArrayList<State>();

        for (var i = 0; i < board.length; i++) {
            var cell = board[i];
            if (cell != player.getIdenticalCell()) continue;

            if (i == HOUSE_OF_END_INDEX) {
                array.add(move(new Move(i, PIECE_FREE_INDEX)));
            } else if (canMove(new Move(i, i + value.toNumber()))) {
                array.add(move(new Move(i, i + value.toNumber())));
            }
        }

        return array;
    }

    public void printBoard() {
        System.out.println("-----------------------------");
        System.out.printf("%s's turn with move value of %d\n", activePlayer, moveValue.toNumber());

        var SEPARATOR_BETWEEN_BOARD_AND_RULER = "     ";

        // Top
        System.out.println("┌────┬────┬────┬────┬────┬────┬────┬────┬────┬────┐" + SEPARATOR_BETWEEN_BOARD_AND_RULER + "┌────┬────┬────┬────┬────┬────┬────┬────┬────┬────┐");

        // First row
        for (var i = 0; i < 10; i++) {
            System.out.printf("│ %s%s ", board[i].boardStringSymbol(), board[i].boardStringSymbol());
        }
        System.out.print("│" + SEPARATOR_BETWEEN_BOARD_AND_RULER);
        for (var i = 0; i < 10; i++) {
            System.out.printf("│ %2d ", i + 1);
        }
        System.out.println("│");

        // Separator
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┼────┤" + SEPARATOR_BETWEEN_BOARD_AND_RULER + "├────┼────┼────┼────┼────┼────┼────┼────┼────┼────┤");

        // Second row
        for (var i = 19; i >= 10; i--) {
            System.out.printf("│ %s%s ", board[i].boardStringSymbol(), board[i].boardStringSymbol());
        }
        System.out.print("│" + SEPARATOR_BETWEEN_BOARD_AND_RULER);
        for (var i = 19; i >= 10; i--) {
            System.out.printf("│ %d ", i + 1);
        }
        System.out.println("│");

        // Separator
        System.out.println("├────┼────┼────┼────┼────┼────┼────┼────┼────┼────┤" + SEPARATOR_BETWEEN_BOARD_AND_RULER + "├────┼────┼────┼────┼────┼────┼────┼────┼────┼────┤");

        // Third row
        for (var i = 20; i < 30; i++) {
            System.out.printf("│ %s%s ", board[i].boardStringSymbol(), board[i].boardStringSymbol());
        }
        System.out.print("│" + SEPARATOR_BETWEEN_BOARD_AND_RULER);
        for (var i = 20; i < 30; i++) {
            System.out.printf("│ %d ", i + 1);
        }
        System.out.println("│");

        // Bottom
        System.out.println("└────┴────┴────┴────┴────┴────┴────┴────┴────┴────┘" + SEPARATOR_BETWEEN_BOARD_AND_RULER + "└────┴────┴────┴────┴────┴────┴────┴────┴────┴────┘");

        System.out.println("-----------------------------");
    }

    private static void backToReturn(Cell[] array, int index) {
        for (var i = HOUSE_OF_RETURN_INDEX; i > 0; i--) {
            if (array[i] == Cell.EMPTY) {
                array[i] = array[index];
                array[index] = Cell.EMPTY;
                break;
            }
        }
    }
}
