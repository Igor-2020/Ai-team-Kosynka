package kosynka.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KlondikeGameTest {

    @Test
    void dealsSevenColumnsWithOneToSevenCards() {
        KlondikeGame game = new KlondikeGame(42L);
        for (int col = 0; col < 7; col++) {
            assertEquals(col + 1, game.getTableauPile(col).size());
        }
        assertEquals(24, game.getStock().size());
        assertEquals(0, game.getWaste().size());
        assertEquals(52, game.totalCards());
    }

    @Test
    void onlyTopTableauCardsAreFaceUp() {
        KlondikeGame game = new KlondikeGame(7L);
        for (int col = 0; col < 7; col++) {
            TableauPile pile = game.getTableauPile(col);
            assertTrue(pile.top().isFaceUp());
            for (int i = 0; i < pile.size() - 1; i++) {
                assertFalse(pile.get(i).isFaceUp());
            }
        }
    }

    @Test
    void sameSeedProducesSameDeal() {
        KlondikeGame a = new KlondikeGame(123L);
        KlondikeGame b = new KlondikeGame(123L);
        for (int col = 0; col < 7; col++) {
            assertEquals(a.getTableauPile(col).getCards(), b.getTableauPile(col).getCards());
        }
    }

    @Test
    void drawingMovesOneFaceUpCardToWaste() {
        KlondikeGame game = new KlondikeGame(5L);
        int stockBefore = game.getStock().size();
        game.drawFromStock();
        assertEquals(stockBefore - 1, game.getStock().size());
        assertEquals(1, game.getWaste().size());
        assertTrue(game.getWaste().top().isFaceUp());
    }

    @Test
    void recyclesWasteBackToStock() {
        KlondikeGame game = new KlondikeGame(9L);
        while (!game.getStock().isEmpty()) {
            game.drawFromStock();
        }
        assertTrue(game.getStock().isEmpty());
        assertEquals(24, game.getWaste().size());

        game.drawFromStock();
        assertEquals(24, game.getStock().size());
        assertEquals(0, game.getWaste().size());
    }

    @Test
    void newGameResetsState() {
        KlondikeGame game = new KlondikeGame(11L);
        game.drawFromStock();
        game.newGame(11L);
        assertEquals(0, game.getWaste().size());
        assertEquals(24, game.getStock().size());
        assertEquals(0, game.getMoves());
        assertFalse(game.isWon());
    }

    @Test
    void cannotMoveWithinSameColumn() {
        KlondikeGame game = new KlondikeGame(3L);
        assertFalse(game.moveTableauToTableau(0, 0, 0).isSuccess());
    }

    @Test
    void foundationIsResolvedBySuit() {
        KlondikeGame game = new KlondikeGame(1L);
        Card ace = new Card(Suit.SPADES, Rank.ACE, true);
        assertEquals(Suit.SPADES, game.foundationFor(ace).getSuit());
    }

    @Test
    void totalCardCountStaysConstant() {
        KlondikeGame game = new KlondikeGame(21L);
        game.drawFromStock();
        assertEquals(52, game.totalCards());
    }
}
