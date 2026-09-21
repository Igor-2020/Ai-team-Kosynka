package kosynka.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class DeckTest {

    @Test
    void newDeckHas52UniqueCards() {
        Deck deck = new Deck(1L);
        assertEquals(52, deck.size());
        Set<String> unique = new HashSet<>();
        for (Card card : deck.getCards()) {
            unique.add(card.getSuit() + "-" + card.getRank());
        }
        assertEquals(52, unique.size());
    }

    @Test
    void shuffleKeepsAllCards() {
        Deck deck = new Deck(2L);
        deck.shuffle();
        assertEquals(52, deck.size());
    }

    @Test
    void dealingReducesSize() {
        Deck deck = new Deck(3L);
        deck.deal();
        assertEquals(51, deck.size());
    }

    @Test
    void emptyDeckReturnsNull() {
        Deck deck = new Deck(4L);
        for (int i = 0; i < 52; i++) {
            deck.deal();
        }
        assertEquals(0, deck.size());
        assertNull(deck.deal());
    }
}
