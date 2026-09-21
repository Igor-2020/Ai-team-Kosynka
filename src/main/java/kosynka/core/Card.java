package kosynka.core;

import java.util.Objects;

/**
 * Immutable playing card. Identity of a card is determined by its suit and rank.
 * The face-up / face-down state is stored in the card as well; flipping returns a
 * new immutable instance instead of mutating the current one.
 *
 * This class has no dependency on any UI toolkit.
 */
public final class Card {

    private final Suit suit;
    private final Rank rank;
    private final boolean faceUp;

    public Card(Suit suit, Rank rank, boolean faceUp) {
        this.suit = Objects.requireNonNull(suit, "suit");
        this.rank = Objects.requireNonNull(rank, "rank");
        this.faceUp = faceUp;
    }

    public Card(Suit suit, Rank rank) {
        this(suit, rank, false);
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public boolean isRed() {
        return suit.isRed();
    }

    public boolean isBlack() {
        return suit.isBlack();
    }

    /** Returns a copy of this card with the face-up flag inverted. */
    public Card flip() {
        return new Card(suit, rank, !faceUp);
    }

    /** Returns a copy of this card with the given face-up flag. */
    public Card withFaceUp(boolean up) {
        return new Card(suit, rank, up);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Card)) {
            return false;
        }
        Card card = (Card) other;
        return suit == card.suit && rank == card.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }

    @Override
    public String toString() {
        return rank.getSymbol() + suit.getSymbol();
    }
}
