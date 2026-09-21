package kosynka.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class TableauPileTest {

    @Test
    void emptyPileAcceptsOnlyKing() {
        TableauPile pile = new TableauPile();
        assertEquals(ValidationReason.OK, pile.canAccept(new Card(Suit.SPADES, Rank.KING, true)));
        assertEquals(ValidationReason.NOT_KING_TO_EMPTY, pile.canAccept(new Card(Suit.HEARTS, Rank.FIVE, true)));
    }

    @Test
    void descendingAlternatingColour() {
        TableauPile pile = new TableauPile();
        pile.add(new Card(Suit.SPADES, Rank.KING, true));

        assertEquals(ValidationReason.OK, pile.canAccept(new Card(Suit.HEARTS, Rank.QUEEN, true)));
        assertEquals(ValidationReason.WRONG_COLOR, pile.canAccept(new Card(Suit.CLUBS, Rank.QUEEN, true)));
        assertEquals(ValidationReason.WRONG_RANK, pile.canAccept(new Card(Suit.HEARTS, Rank.JACK, true)));
    }

    @Test
    void rejectsFaceDownCard() {
        TableauPile pile = new TableauPile();
        assertEquals(ValidationReason.NOT_FACE_UP, pile.canAccept(new Card(Suit.SPADES, Rank.KING, false)));
    }

    @Test
    void validatesRuns() {
        List<Card> good = List.of(
                new Card(Suit.SPADES, Rank.KING, true),
                new Card(Suit.HEARTS, Rank.QUEEN, true),
                new Card(Suit.CLUBS, Rank.JACK, true));
        assertEquals(ValidationReason.OK, TableauPile.validateRun(good));

        List<Card> wrongColour = List.of(
                new Card(Suit.SPADES, Rank.KING, true),
                new Card(Suit.CLUBS, Rank.QUEEN, true));
        assertEquals(ValidationReason.WRONG_COLOR, TableauPile.validateRun(wrongColour));

        List<Card> wrongRank = List.of(
                new Card(Suit.SPADES, Rank.KING, true),
                new Card(Suit.HEARTS, Rank.JACK, true));
        assertEquals(ValidationReason.WRONG_RANK, TableauPile.validateRun(wrongRank));
    }

    @Test
    void revealTopFlipsOnlyWhenNeeded() {
        TableauPile pile = new TableauPile();
        pile.add(new Card(Suit.CLUBS, Rank.FOUR, false));
        assertEquals(true, pile.revealTop());
        assertEquals(false, pile.revealTop());
        assertEquals(true, pile.top().isFaceUp());
    }
}
