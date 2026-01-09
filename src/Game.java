import java.util.Scanner;
import data.Move;
import data.Player;
import data.State;

public class Game {
    static final int MAX_DEPTH_LIMIT = 3;

    static Result maxMove(State state, int alpha, int beta, int depth) {
        var winner = state.checkWinner();

        if (winner != null || depth >= MAX_DEPTH_LIMIT) {
            throw new RuntimeException("TODO Return");
        }

        var bestValue = Integer.MIN_VALUE;
        var bestState = state;

        for (var s : state.getNextStates()) {
            var result = minMove(s, alpha, beta, depth + 1);
            if (result.score() > bestValue) {
                bestValue = result.score();
                bestState = s;
            }
            alpha = Math.max(alpha, bestValue);
            if (alpha >= beta) break; // pruning
        }

        return new Result(bestState, bestValue);
    }

    static Result minMove(State state, int alpha, int beta, int depth) {
        var bestValue = Integer.MAX_VALUE;
        var bestState = state;

        for (var s : state.getNextStates()) {
            var result = maxMove(s, alpha, beta, depth + 1);
            if (result.score() < bestValue) {
                bestValue = result.score();
                bestState = s;
            }
            beta = Math.min(beta, bestValue);
            if (alpha >= beta) break; // pruning
        }

        return new Result(bestState, bestValue);
    }

    public static void playGame(State curr) {
        Scanner scanner = new Scanner(System.in);
        var player = curr.activePlayer;
        curr.printBoard();

        while (true) {
            // Check Global Win Status
            var winner = curr.checkWinner();
            if (winner != null) {
                System.out.println("Player " + winner + " wins!");
                break;
            }

            // Computer Turn
            if (player == Player.MAX) {
                System.out.println("Computer is thinking...");
                var result = maxMove(curr, Integer.MIN_VALUE, Integer.MAX_VALUE, 0);

                // Move to new state (Java GC handles the old 'curr')
                curr = result.state();
                curr.printBoard();
                player = Player.MIN; // Switch to Human
                continue;
            }

            // Human Turn
            System.out.println("Player " + player + " Turn (Human):");
            System.out.print("Enter: start (0-29), end (1-30) OR -1 -1 to quit: ");

            int start, end;
            try {
                if (scanner.hasNextInt()) {
                    start = scanner.nextInt();
                    end = scanner.nextInt();
                } else {
                    System.out.println("Invalid input. Exiting.");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Input Error.");
                break;
            }

            if (start == -1 && end == -1) {
                System.out.println("Exit");
                break;
            }
            
            var move = new Move(start, end);

            if (!curr.canMove(move)) {
                System.out.println("Invalid Move! (No pieces left, out of bounds, or blocked)");
                continue;
            }

            curr = curr.move(move);
            curr.printBoard();
            player = player.getOtherPlayer();
        }
        scanner.close();
    }
}
