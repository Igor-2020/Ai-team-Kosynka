package kosynka.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FoundationPileTest {

    @Test
    void emptyFoundationAcceptsOnlyItsAce() {
        FoundationPile foundation = new FoundationPile(Suit.HEARTS);
        assertEquals(ValidationReason.OK, foundation.canAccept(new Card(Suit.HEARTS, Rank.ACE, true)));
        assertEquals(ValidationReason.WRONG_SUIT, foundation.canAccept(new Card(Suit.SPADES, Rank.ACE, true)));
        assertEquals(ValidationReason.FOUNDATION_NOT_ACE, foundation.canAccept(new Card(Suit.HEARTS, Rank.TWO, true)));
    }

    @Test
    void buildsAscendingBySuit() {
        FoundationPile foundation = new FoundationPile(Suit.CLUBS);
        foundation.add(new Card(Suit.CLUBS, Rank.ACE, true));
        assertEquals(ValidationReason.OK, foundation.canAccept(new Card(Suit.CLUBS, Rank.TWO, true)));
        assertEquals(ValidationReason.WRONG_RANK, foundation.canAccept(new Card(Suit.CLUBS, Rank.THREE, true)));
    }

    @Test
    void completenessTracking() {
        FoundationPile foundation = new FoundationPile(Suit.SPADES);
        assertFalse(foundation.isComplete());
        for (Rank rank : Rank.values()) {
            foundation.add(new Card(Suit.SPADES, rank, true));
        }
        assertTrue(foundation.isComplete());
    }
}
