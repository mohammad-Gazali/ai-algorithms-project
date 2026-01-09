package data;

public enum Player {
    MAX,
    MIN;

    @Override
    public String toString() {
        return switch (this) {
            case MAX -> "Computer";
            case MIN -> "Human";
        };
    }

    public Player getOtherPlayer() {
        return switch (this) {
            case MAX -> MIN;
            case MIN -> MAX;
        };
    }

    public Cell getIdenticalCell() {
        return switch (this) {
            case MAX -> Cell.WHITE;
            case MIN -> Cell.BLACK;
        };
    }
}
