package kosynka.core;

/**
 * Score model for Klondike. Holds the current score value together with the
 * scoring constants. The UI only reads {@link #getValue()}.
 */
public class Score {

    public static final int TO_FOUNDATION = 10;
    public static final int WASTE_TO_TABLEAU = 5;
    public static final int REVEAL_CARD = 5;
    public static final int FOUNDATION_TO_TABLEAU = -15;
    public static final int RECYCLE_WASTE = -20;
    public static final int STOCK_DRAW = -5;

    private int value;

    public int getValue() {
        return value;
    }

    public void add(int points) {
        value += points;
    }

    public void reset() {
        value = 0;
    }
}
