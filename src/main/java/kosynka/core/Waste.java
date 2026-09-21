package kosynka.core;

/**
 * The waste pile. Holds the cards drawn from the stock, face up. The player may
 * move only the top card of the waste pile; the pile never accepts cards directly.
 */
public class Waste extends Pile {

    @Override
    public ValidationReason canAccept(Card card) {
        return ValidationReason.NOT_ACCEPTING;
    }
}
