package kosynka.core;

/**
 * One of the four foundation piles. Each foundation belongs to a single suit and
 * accepts cards in ascending order starting from the Ace.
 */
public class FoundationPile extends Pile {

    private final Suit suit;

    public FoundationPile(Suit suit) {
        this.suit = suit;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public ValidationReason canAccept(Card card) {
        if (card == null) {
            return ValidationReason.NO_CARDS;
        }
        if (card.getSuit() != suit) {
            return ValidationReason.WRONG_SUIT;
        }
        if (cards.isEmpty()) {
            return card.getRank() == Rank.ACE
                    ? ValidationReason.OK
                    : ValidationReason.FOUNDATION_NOT_ACE;
        }
        Card top = top();
        if (card.getRank().getValue() != top.getRank().getValue() + 1) {
            return ValidationReason.WRONG_RANK;
        }
        return ValidationReason.OK;
    }

    public boolean isComplete() {
        return cards.size() == Rank.values().length;
    }
}
