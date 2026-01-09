package data;

public enum Player {
    // Maximizer
    AI,
    // Minimizer
    HUMAN;

    @Override
    public String toString() {
        return switch (this) {
            case AI -> "Computer";
            case HUMAN -> "Human";
        };
    }

    public Player getOtherPlayer() {
        return switch (this) {
            case AI -> HUMAN;
            case HUMAN -> AI;
        };
    }
}
