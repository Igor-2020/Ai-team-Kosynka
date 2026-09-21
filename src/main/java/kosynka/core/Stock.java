package kosynka.core;

/**
 * The stock pile. Cards are drawn from here to the waste pile; the stock never
 * accepts cards placed onto it by the player.
 */
public class Stock extends Pile {

    @Override
    public ValidationReason canAccept(Card card) {
        return ValidationReason.NOT_ACCEPTING;
    }
}
