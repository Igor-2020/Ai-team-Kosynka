package kosynka.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Full Klondike (Solitaire) game model. Contains no UI code: it deals the cards,
 * validates and applies moves, keeps score, detects victory and tracks the timer.
 *
 * <p>The default draw count is one card. Use {@link #setDrawCount(int)} to switch
 * to the three-card draw variant.</p>
 */
public class KlondikeGame {

    public static final int TABLEAU_COUNT = 7;
    public static final int FOUNDATION_COUNT = 4;
    public static final int TOTAL_CARDS = 52;
    public static final int STOCK_CARDS = 24;

    private TableauPile[] tableau;
    private FoundationPile[] foundations;
    private Stock stock;
    private Waste waste;
    private Deck deck;
    private Score score;
    private GameTimer timer;

    private int moves;
    private boolean won;
    private int drawCount = 1;

    public KlondikeGame() {
        newGame();
    }

    public KlondikeGame(long seed) {
        newGame(seed);
    }

    // ------------------------------------------------------------------
    // Game lifecycle
    // ------------------------------------------------------------------

    public void newGame() {
        newGame(new Random().nextLong());
    }

    /** Starts a fresh game using a deterministic seed (used by tests). */
    public void newGame(long seed) {
        tableau = new TableauPile[TABLEAU_COUNT];
        for (int i = 0; i < TABLEAU_COUNT; i++) {
            tableau[i] = new TableauPile();
        }

        Suit[] suits = {Suit.CLUBS, Suit.DIAMONDS, Suit.HEARTS, Suit.SPADES};
        foundations = new FoundationPile[FOUNDATION_COUNT];
        for (int i = 0; i < FOUNDATION_COUNT; i++) {
            foundations[i] = new FoundationPile(suits[i]);
        }

        stock = new Stock();
        waste = new Waste();
        score = new Score();
        timer = new GameTimer();
        moves = 0;
        won = false;

        deck = new Deck(seed);
        deck.shuffle();

        // Deal 7 columns of 1..7 cards; only the top card of each column is face up.
        for (int col = 0; col < TABLEAU_COUNT; col++) {
            for (int row = 0; row <= col; row++) {
                tableau[col].add(deck.deal());
            }
            tableau[col].revealTop();
        }

        // The remaining 24 cards go to the stock, face down.
        while (!deck.isEmpty()) {
            stock.add(deck.deal());
        }

        timer.start();
    }

    // ------------------------------------------------------------------
    // Stock / waste
    // ------------------------------------------------------------------

    /**
     * Draws cards from the stock to the waste. If the stock is empty the waste is
     * recycled back into the stock with the order reversed.
     */
    public MoveResult drawFromStock() {
        if (stock.isEmpty()) {
            if (waste.isEmpty()) {
                return MoveResult.fail(ValidationReason.NO_CARDS);
            }
            while (!waste.isEmpty()) {
                Card card = waste.removeTop();
                stock.add(card.withFaceUp(false));
            }
            score.add(Score.RECYCLE_WASTE);
            moves++;
            return MoveResult.ok();
        }

        int available = Math.min(drawCount, stock.size());
        for (int i = 0; i < available; i++) {
            Card card = stock.removeTop();
            waste.add(card.withFaceUp(true));
        }
        score.add(Score.STOCK_DRAW);
        moves++;
        return MoveResult.ok();
    }

    // ------------------------------------------------------------------
    // Moves
    // ------------------------------------------------------------------

    /** Moves a single card or a valid run from one tableau column to another. */
    public MoveResult moveTableauToTableau(int fromCol, int cardIndex, int toCol) {
        if (!isValidTableau(fromCol) || !isValidTableau(toCol) || fromCol == toCol) {
            return MoveResult.fail(ValidationReason.PILE_NOT_FOUND);
        }
        TableauPile from = tableau[fromCol];
        TableauPile to = tableau[toCol];
        if (cardIndex < 0 || cardIndex >= from.size()) {
            return MoveResult.fail(ValidationReason.NOTHING_TO_MOVE);
        }

        List<Card> sequence = new ArrayList<>(from.getCards().subList(cardIndex, from.size()));
        ValidationReason run = TableauPile.validateRun(sequence);
        if (run != ValidationReason.OK) {
            return MoveResult.fail(run);
        }
        ValidationReason accept = to.canAcceptSequence(sequence);
        if (accept != ValidationReason.OK) {
            return MoveResult.fail(accept);
        }

        List<Card> moving = from.removeFrom(cardIndex);
        to.addAll(moving);

        if (from.revealTop()) {
            score.add(Score.REVEAL_CARD);
        }
        moves++;
        checkWin();
        return MoveResult.ok();
    }

    /** Moves the top card of a tableau column to its foundation (if legal). */
    public MoveResult moveTableauToFoundation(int fromCol) {
        if (!isValidTableau(fromCol)) {
            return MoveResult.fail(ValidationReason.PILE_NOT_FOUND);
        }
        TableauPile from = tableau[fromCol];
        if (from.isEmpty()) {
            return MoveResult.fail(ValidationReason.NOTHING_TO_MOVE);
        }
        Card card = from.top();
        if (!card.isFaceUp()) {
            return MoveResult.fail(ValidationReason.NOT_FACE_UP);
        }
        FoundationPile foundation = foundationFor(card);
        if (foundation == null) {
            return MoveResult.fail(ValidationReason.WRONG_SUIT);
        }
        ValidationReason accept = foundation.canAccept(card);
        if (accept != ValidationReason.OK) {
            return MoveResult.fail(accept);
        }

        from.removeTop();
        foundation.add(card);
        if (from.revealTop()) {
            score.add(Score.REVEAL_CARD);
        }
        score.add(Score.TO_FOUNDATION);
        moves++;
        checkWin();
        return MoveResult.ok();
    }

    /** Moves the top card of the waste pile to its foundation (if legal). */
    public MoveResult moveWasteToFoundation() {
        if (waste.isEmpty()) {
            return MoveResult.fail(ValidationReason.NOTHING_TO_MOVE);
        }
        Card card = waste.top();
        FoundationPile foundation = foundationFor(card);
        if (foundation == null) {
            return MoveResult.fail(ValidationReason.WRONG_SUIT);
        }
        ValidationReason accept = foundation.canAccept(card);
        if (accept != ValidationReason.OK) {
            return MoveResult.fail(accept);
        }

        waste.removeTop();
        foundation.add(card);
        score.add(Score.TO_FOUNDATION);
        moves++;
        checkWin();
        return MoveResult.ok();
    }

    /** Moves the top card of the waste pile onto a tableau column (if legal). */
    public MoveResult moveWasteToTableau(int toCol) {
        if (!isValidTableau(toCol)) {
            return MoveResult.fail(ValidationReason.PILE_NOT_FOUND);
        }
        if (waste.isEmpty()) {
            return MoveResult.fail(ValidationReason.NOTHING_TO_MOVE);
        }
        Card card = waste.top();
        ValidationReason accept = tableau[toCol].canAccept(card);
        if (accept != ValidationReason.OK) {
            return MoveResult.fail(accept);
        }

        waste.removeTop();
        tableau[toCol].add(card);
        score.add(Score.WASTE_TO_TABLEAU);
        moves++;
        return MoveResult.ok();
    }

    /** Moves the top card of a foundation back onto a tableau column (if legal). */
    public MoveResult moveFoundationToTableau(int foundationIndex, int toCol) {
        if (!isValidFoundation(foundationIndex) || !isValidTableau(toCol)) {
            return MoveResult.fail(ValidationReason.PILE_NOT_FOUND);
        }
        FoundationPile from = foundations[foundationIndex];
        if (from.isEmpty()) {
            return MoveResult.fail(ValidationReason.NOTHING_TO_MOVE);
        }
        Card card = from.top();
        ValidationReason accept = tableau[toCol].canAccept(card);
        if (accept != ValidationReason.OK) {
            return MoveResult.fail(accept);
        }

        from.removeTop();
        tableau[toCol].add(card);
        score.add(Score.FOUNDATION_TO_TABLEAU);
        moves++;
        return MoveResult.ok();
    }

    /** Convenience move used by double-click: top tableau card to its foundation. */
    public MoveResult autoMoveTableauToFoundation(int fromCol) {
        return moveTableauToFoundation(fromCol);
    }

    /** Convenience move used by double-click: top waste card to its foundation. */
    public MoveResult autoMoveWasteToFoundation() {
        return moveWasteToFoundation();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    public FoundationPile foundationFor(Card card) {
        if (card == null) {
            return null;
        }
        for (FoundationPile foundation : foundations) {
            if (foundation.getSuit() == card.getSuit()) {
                return foundation;
            }
        }
        return null;
    }

    private void checkWin() {
        int total = 0;
        for (FoundationPile foundation : foundations) {
            total += foundation.size();
        }
        if (total == TOTAL_CARDS) {
            won = true;
            timer.pause();
        }
    }

    private boolean isValidTableau(int index) {
        return index >= 0 && index < TABLEAU_COUNT;
    }

    private boolean isValidFoundation(int index) {
        return index >= 0 && index < FOUNDATION_COUNT;
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public TableauPile getTableauPile(int index) {
        return tableau[index];
    }

    public TableauPile[] getTableauPiles() {
        return tableau;
    }

    public FoundationPile getFoundation(int index) {
        return foundations[index];
    }

    public FoundationPile[] getFoundations() {
        return foundations;
    }

    public Stock getStock() {
        return stock;
    }

    public Waste getWaste() {
        return waste;
    }

    public Score getScore() {
        return score;
    }

    public GameTimer getTimer() {
        return timer;
    }

    public int getMoves() {
        return moves;
    }

    public boolean isWon() {
        return won;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        if (drawCount != 1 && drawCount != 3) {
            throw new IllegalArgumentException("drawCount must be 1 or 3");
        }
        this.drawCount = drawCount;
    }

    /** Total number of cards currently in play (tableau + stock + waste + foundations). */
    public int totalCards() {
        int total = stock.size() + waste.size();
        for (TableauPile pile : tableau) {
            total += pile.size();
        }
        for (FoundationPile pile : foundations) {
            total += pile.size();
        }
        return total;
    }
}
