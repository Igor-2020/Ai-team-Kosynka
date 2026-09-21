package kosynka.core;

/**
 * Card suits. A suit knows its display symbol and its colour.
 * This class has no dependency on any UI toolkit.
 */
public enum Suit {

    CLUBS('\u2663', false),
    DIAMONDS('\u2666', true),
    HEARTS('\u2665', true),
    SPADES('\u2660', false);

    private final char symbol;
    private final boolean red;

    Suit(char symbol, boolean red) {
        this.symbol = symbol;
        this.red = red;
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean isRed() {
        return red;
    }

    public boolean isBlack() {
        return !red;
    }
}
