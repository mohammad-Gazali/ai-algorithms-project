import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import data.Move;
import data.MoveValue;
import data.Player;
import data.State;

public class Game {
    static final int MAX_DEPTH_LIMIT = 5;

    static int visitedStatesCount_debug = 0;
    static List<String> tracingStringsList_debug = new ArrayList<>();

    static Result expectMinimax(State state, int depth, Player player, double alpha, double beta, boolean debug) {
        visitedStatesCount_debug++;

        var winner = state.checkWinner();

        if (winner != null || depth >= MAX_DEPTH_LIMIT) {
            return new Result(state, state.evaluate());
        }

        var indentationSpaces_debug = "\t".repeat(depth * 2);

        switch (player) {
            case MAX -> {
                var bestValue = -(double)State.MAXIMUM_VALUE - 1;
                var bestState = state;

                for (var s : state.getNextStates(Player.MAX, state.moveValue)) {
                    var result = chanceTurnScore(s, depth, Player.MAX, alpha, beta, debug);
                    if (result > bestValue) {
                        bestValue = result;
                        bestState = s;
                    }
                    alpha = Math.max(alpha, bestValue);

                    // pruning
                    if (alpha >= beta) {
                        tracingStringsList_debug.add(indentationSpaces_debug + "\t[PRUNING BRANCH]");
                        break;
                    }
                }

                if (debug) {
                    tracingStringsList_debug.add(indentationSpaces_debug + "Max node with score of " + bestValue);
                }
                return new Result(bestState, bestValue);
            }
            case MIN -> {
                var bestValue = (double)State.MAXIMUM_VALUE + 1;
                var bestState = state;


                for (var s : state.getNextStates(Player.MIN, state.moveValue)) {
                    var result = chanceTurnScore(s, depth, Player.MIN, alpha, beta, debug);
                    if (result < bestValue) {
                        bestValue = result;
                        bestState = s;
                    }
                    beta = Math.min(beta, bestValue);

                    // pruning
                    if (alpha >= beta) {
                        tracingStringsList_debug.add(indentationSpaces_debug + "\t[PRUNING BRANCH]");
                        break;
                    }
                }

                if (debug) {
                    tracingStringsList_debug.add(indentationSpaces_debug + "Min node with score of " + bestValue);
                }
                return new Result(bestState, bestValue);
            }
        }

        throw new RuntimeException("Issue with the algorithm");
    }

    static double chanceTurnScore(State state, int seekPlayerDepth, Player seekChancePlayer, double alpha, double beta, boolean debug) {
        var expectedValue = 0.0;


        for (var value: MoveValue.values()) {
            for (var s: state.getNextStates(seekChancePlayer.getOtherPlayer(), value)) {
                expectedValue += value.getProbability() * expectMinimax(s, seekPlayerDepth + 1, seekChancePlayer.getOtherPlayer(), alpha, beta, debug).score();
            }
        }

        if (debug) {
            var indentationSpaces_debug = "\t".repeat(seekPlayerDepth * 2 + 1);
            tracingStringsList_debug.add(indentationSpaces_debug + "Chance node with expected value of " + expectedValue);
        }

        return expectedValue;
    }

    public static void playGame(State curr, boolean debug) {
        Scanner scanner = new Scanner(System.in);
        var player = Player.MIN;

        while (true) {
            // Check Global Win Status
            var winner = curr.checkWinner();
            if (winner != null) {
                System.out.println("Player " + winner + " wins!");
                break;
            }

            System.out.println("-----------------------------");
            System.out.printf("%s's turn with move value of %d\n", player, curr.moveValue.toNumber());

            curr.printBoard();

            // Computer Turn
            if (player == Player.MAX) {
                System.out.println("Computer is thinking...");

                if (debug) {
                    System.out.println("==================== DEBUG MODE TRACING ====================");
                }

                var result = expectMinimax(curr, 0, Player.MAX, -State.MAXIMUM_VALUE, State.MAXIMUM_VALUE, debug);

                if (debug) {
                    Collections.reverse(tracingStringsList_debug);
                    var tracingString = String.join("\n", tracingStringsList_debug);

                    System.out.println(tracingString);
                    System.out.printf("\nVisited states count: %d\n", visitedStatesCount_debug);
                    System.out.printf("Score for the best move: %.2f\n", result.score());
                    System.out.println("============================================================");

                    // reset debug indicators
                    visitedStatesCount_debug = 0;
                    tracingStringsList_debug.clear();
                }

                // Move to new state (Java GC handles the old 'curr')
                curr = result.state();
                player = Player.MIN;
                System.out.printf("Computer moved from %d to %d\n", curr.previousMove.start() + 1, curr.previousMove.end() + 1);
                continue;
            }


            // Human Turn
            System.out.print("Enter: start (1-30), end (2-31) OR -1 -1 to quit: ");

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
            
            var move = new Move(start - 1, end - 1);

            if (!curr.canMove(move, player)) {
                System.out.println("Invalid Move!");
                continue;
            }

            curr = curr.move(move, player);
            player = player.getOtherPlayer();
        }
        scanner.close();
    }

    public static void playGame(State curr) {
        playGame(curr, false);
    }
}
