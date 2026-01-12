package data;

import java.util.Random;


public enum MoveValue {
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE;

    public int toNumber() {
        return switch (this) {
            case ONE -> 1;
            case TWO -> 2;
            case THREE -> 3;
            case FOUR -> 4;
            case FIVE -> 5;
        };
    }

    private static final Random r = new Random();

    private static final double TOTAL_CASES_COUNT = 2 * 2 * 2 * 2;

    /*
     * Function return a random value depending on throwing four sticks
     * with two sides, and we have those cases:
     * (B for black stick, W for white stick)
     * - zero black sticks:  1 case  WWWW
     * - one black stick:    4 cases BWWW WBWW WWBW WWWB
     * - two black sticks:   6 cases BBWW BWBW BWWB WBBW WBWB WWBB
     * - three black sticks: 4 cases BBBW BBWB BWBB WBBB
     * - four black sticks:  1 case  BBBB
     *
     * We have 16 cases in total (2 ^ 4), so we can calculate the probability like
     * this:
     *
     * P(X) is the probability of having the value X for moving the piece
     * where the X ∈ {1, 2, 3, 4, 5}:
     *
     * - P(1) = 4 / 16 (one black stick)
     * - P(2) = 6 / 16 (two black sticks)
     * - P(3) = 4 / 16 (three black sticks)
     * - P(4) = 1 / 16 (four black sticks)
     * - P(5) = 1 / 16 (zero black sticks)
     */
    public static MoveValue randomThrow() {
        // value between 1 and 16
        var randomValue = r.nextInt(1, (int)TOTAL_CASES_COUNT + 1);

        // P(1): first four values return the move value ONE
        if (randomValue <= 4) {
            return ONE;
        }
        // P(2): next six values return the move value TWO
        else if (randomValue <= 10) {
            return TWO;
        }
        // P(3): next four values return the move value THREE
        else if (randomValue <= 14) {
            return THREE;
        }
        // P(4): next value return the move value FOUR
        else if (randomValue == 15) {
            return FOUR;
        }
        // P(5): next value return the move value FIVE
        else {
            return FIVE;
        }
    }

    /*
     * Function return the probability of the value to be shown, this table shows
     * the cases that black pieces can take which we can use to calculate the probability
     * for each move value:
     *
     * +--------------------------------------------------------------------------------------------------------+
     * | Black pieces count| Move value | Combination | Case(s) | View of case(s) <B for black and W for white> |
     * +--------------------------------------------------------------------------------------------------------+
     * | one               | ONE        | C(4,1)      | 4       | BWWW WBWW WWBW WWWB                           |
     * | two               | TWO        | C(4,2)      | 6       | BBWW BWBW BWWB WBBW WBWB WWBB                 |
     * | three             | THREE      | C(4,3)      | 4       | BBBW BBWB BWBB WBBB                           |
     * | four              | FOUR       | C(4,4)      | 1       | BBBB                                          |
     * | zero              | FIVE       | C(4,0)      | 1       | WWWW                                          |
     * +--------------------------------------------------------------------------------------------------------+
     *
     * We have 16 cases in total, so we can calculate the probability like
     * this:
     *
     * P(X) is the probability of having the value X for moving the piece
     * where the X ∈ {1, 2, 3, 4, 5}:
     *
     * - P(1) = 4 / 16
     * - P(2) = 6 / 16
     * - P(3) = 4 / 16
     * - P(4) = 1 / 16
     * - P(5) = 1 / 16
     */
    public double getProbability() {
        return switch (this) {
            case ONE, THREE -> 4 / TOTAL_CASES_COUNT;
            case TWO -> 6 / TOTAL_CASES_COUNT;
            case FOUR, FIVE -> 1 / TOTAL_CASES_COUNT;
        };
    }
}
