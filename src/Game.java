import java.util.Scanner;
import data.Move;
import data.MoveValue;
import data.Player;
import data.State;

public class Game {
    static final int MAX_DEPTH_LIMIT = 6;

    static Result expectMinimax(State state, int depth) {
        var winner = state.checkWinner();

        if (winner != null || depth >= MAX_DEPTH_LIMIT) {
            return new Result(state, state.evaluate());
        }

        switch (state.activePlayer) {
            case MAX -> {
                var bestValue = Double.MIN_VALUE;
                var bestState = state;

                for (var value: MoveValue.values()) {
                    for (var s : state.getNextStates(Player.MIN, value)) {
                        var result = chanceTurn(s, depth + 1, value);
                        if (result.score() > bestValue) {
                            bestValue = result.score();
                            bestState = s;
                        }
                    }
                }

                return new Result(bestState, bestValue);
            }
            case MIN -> {
                var bestValue = Double.MIN_VALUE;
                var bestState = state;

                for (var value: MoveValue.values()) {
                    for (var s : state.getNextStates(Player.MAX, value)) {
                        var result = chanceTurn(s, depth + 1, value);
                        if (result.score() > bestValue) {
                            bestValue = result.score();
                            bestState = s;
                        }
                    }
                }

                return new Result(bestState, bestValue);
            }
        }

        throw new RuntimeException("Issue with the algorithm");
    }

    static Result chanceTurn(State state, int depth, MoveValue value) {
        var expectedValue = 0.0;

        for (var s: state.getNextStates(state.activePlayer.getOtherPlayer(), value)) {
            expectedValue += value.getProbability() * expectMinimax(s, depth + 1).score();
        }

        return new Result(state, expectedValue);
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
                var result = expectMinimax(curr, 0);

                // Move to new state (Java GC handles the old 'curr')
                curr = result.state();
                curr.printBoard();
                player = Player.MIN; // Switch to Human
                continue;
            }

            // Human Turn
            System.out.println("Player " + player + " Turn (Human):");
            System.out.print("Enter: start (1-30), end (2-31) OR -1 -1 to quit: ");

            int start, end;
            try {
                if (scanner.hasNextInt()) {
                    start = scanner.nextInt() - 1;
                    end = scanner.nextInt() - 1;
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
