import java.util.Scanner;
import data.Move;
import data.MoveValue;
import data.Player;
import data.State;

public class Game {
    static final int MAX_DEPTH_LIMIT = 4;

    static Result expectMinimax(State state, int depth, Player player) {
        var winner = state.checkWinner();

        if (winner != null || depth >= MAX_DEPTH_LIMIT) {
            return new Result(state, state.evaluate());
        }

        switch (player) {
            case MAX -> {
                var bestValue = -(double)State.MAXIMUM_VALUE - 1;
                var bestState = state;

                for (var s : state.getNextStates(Player.MAX, state.moveValue)) {
                    var result = chanceTurnScore(s, depth, Player.MAX);
                    if (result > bestValue) {
                        bestValue = result;
                        bestState = s;
                    }
                }

                return new Result(bestState, bestValue);
            }
            case MIN -> {
                var bestValue = (double)State.MAXIMUM_VALUE + 1;
                var bestState = state;


                for (var s : state.getNextStates(Player.MIN, state.moveValue)) {
                    var result = chanceTurnScore(s, depth, Player.MIN);
                    if (result < bestValue) {
                        bestValue = result;
                        bestState = s;
                    }
                }

                return new Result(bestState, bestValue);
            }
        }

        throw new RuntimeException("Issue with the algorithm");
    }

    static double chanceTurnScore(State state, int seekPlayerDepth, Player seekChancePlayer) {
        var expectedValue = 0.0;

        for (var value: MoveValue.values()) {
            for (var s: state.getNextStates(seekChancePlayer.getOtherPlayer(), value)) {
                expectedValue += value.getProbability() * expectMinimax(s, seekPlayerDepth + 1, seekChancePlayer.getOtherPlayer()).score();
            }
        }

        return expectedValue;
    }

    public static void playGame(State curr) {
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
                var result = expectMinimax(curr, 0, Player.MAX);

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
}
