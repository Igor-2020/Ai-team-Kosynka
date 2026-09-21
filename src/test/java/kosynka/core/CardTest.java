package kosynka.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void identityIgnoresFaceState() {
        assertEquals(new Card(Suit.HEARTS, Rank.ACE),
                new Card(Suit.HEARTS, Rank.ACE, true));
    }

    @Test
    void flipTogglesFaceState() {
        Card card = new Card(Suit.SPADES, Rank.SEVEN, false);
        assertTrue(card.flip().isFaceUp());
        assertFalse(card.flip().flip().isFaceUp());
    }

    @Test
    void coloursAreCorrect() {
        assertTrue(new Card(Suit.HEARTS, Rank.TEN).isRed());
        assertTrue(new Card(Suit.DIAMONDS, Rank.TEN).isRed());
        assertTrue(new Card(Suit.CLUBS, Rank.TEN).isBlack());
        assertTrue(new Card(Suit.SPADES, Rank.TEN).isBlack());
    }

    @Test
    void rankValuesAreOrdered() {
        assertEquals(1, Rank.ACE.getValue());
        assertEquals(13, Rank.KING.getValue());
        assertEquals(Rank.QUEEN, Rank.fromValue(12));
    }
}
