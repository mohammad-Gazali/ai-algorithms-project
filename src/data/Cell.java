package data;

public enum Cell {
    EMPTY,
    BLACK,
    WHITE;

    public String boardStringSymbol() {
        return switch (this) {
            case EMPTY -> " ";
            case BLACK -> "□";
            case WHITE -> "■";
        };
    }
}
