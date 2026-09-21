package kosynka.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A full 52 card deck. Cards start face down. Shuffling is driven by a
 * {@link Random} instance so that a seed can be supplied for reproducible tests.
 */
public class Deck {

    private final List<Card> cards = new ArrayList<>();
    private final Random random;

    public Deck() {
        this(new Random().nextLong());
    }

    public Deck(long seed) {
        this.random = new Random(seed);
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank, false));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    /** Removes and returns the top (last) card, or {@code null} if empty. */
    public Card deal() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(cards.size() - 1);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }
}
