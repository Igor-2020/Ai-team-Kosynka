package kosynka.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import kosynka.core.Card;
import kosynka.core.FoundationPile;
import kosynka.core.KlondikeGame;
import kosynka.core.TableauPile;

/**
 * The main playing surface. Responsible for layout (slot coordinates and card
 * fanning), rendering, and hit-testing of screen coordinates to piles/cards.
 * All interaction logic lives in {@link DragAndDropController}.
 */
public class BoardPanel extends JPanel {

    private static final Color BACKGROUND = new Color(0x0B, 0x66, 0x2E);
    private static final Color SLOT_BORDER = new Color(255, 255, 255, 70);
    private static final Color HIGHLIGHT = new Color(255, 235, 59, 140);
    private static final Color WIN_OVERLAY = new Color(0, 0, 0, 150);

    private final KlondikeGame game;

    private int cardW = CardRenderer.DEFAULT_CARD_WIDTH;
    private int cardH = CardRenderer.DEFAULT_CARD_HEIGHT;
    private int gap = 12;
    private int margin = 20;

    private int topY;
    private int tableauY;
    private int fanDown;
    private int fanUp;

    private int stockX;
    private int wasteX;
    private int[] foundationX;
    private int[] tableauX;

    private List<Card> dragPreview;
    private Point dragPoint;
    private PileType highlightType;
    private int highlightIndex = -1;

    public BoardPanel(KlondikeGame game) {
        this.game = game;
        setBackground(BACKGROUND);
        setPreferredSize(new Dimension(760, 620));
        setOpaque(true);
    }

    public KlondikeGame getGame() {
        return game;
    }

    public void setDragPreview(List<Card> cards, Point point) {
        this.dragPreview = cards;
        this.dragPoint = point;
    }

    public void clearDragPreview() {
        this.dragPreview = null;
        this.dragPoint = null;
    }

    public void setHighlight(PileType type, int index) {
        this.highlightType = type;
        this.highlightIndex = index;
    }

    public void clearHighlight() {
        this.highlightType = null;
        this.highlightIndex = -1;
    }

    // ------------------------------------------------------------------
    // Layout
    // ------------------------------------------------------------------

    private void computeLayout() {
        int width = Math.max(getWidth(), 400);
        margin = 20;
        gap = 12;
        cardW = Math.max(46, Math.min(100, (width - 2 * margin - 6 * gap) / 7));
        cardH = cardW * 14 / 10;

        topY = margin;
        stockX = margin;
        wasteX = margin + cardW + gap;

        int foundationStart = margin + 3 * (cardW + gap);
        foundationX = new int[4];
        for (int i = 0; i < 4; i++) {
            foundationX[i] = foundationStart + i * (cardW + gap);
        }

        tableauX = new int[KlondikeGame.TABLEAU_COUNT];
        for (int col = 0; col < KlondikeGame.TABLEAU_COUNT; col++) {
            tableauX[col] = margin + col * (cardW + gap);
        }

        tableauY = topY + cardH + 42;
        fanDown = Math.max(6, cardH / 9);
        fanUp = Math.max(14, cardH / 4);
    }

    /** Y coordinate of the card at {@code index} in tableau column {@code col}. */
    public int tableauCardY(int col, int index) {
        TableauPile pile = game.getTableauPile(col);
        int y = tableauY;
        for (int i = 0; i < index && i < pile.size(); i++) {
            y += pile.get(i).isFaceUp() ? fanUp : fanDown;
        }
        return y;
    }

    /** Top-left screen coordinate of the card referred to by a hit test result. */
    public Point cardTopLeft(Hit hit) {
        if (hit == null) {
            return new Point(0, 0);
        }
        computeLayout();
        switch (hit.getType()) {
            case STOCK:
                return new Point(stockX, topY);
            case WASTE:
                return new Point(wasteX, topY);
            case FOUNDATION:
                return new Point(foundationX[hit.getPileIndex()], topY);
            case TABLEAU:
                int index = Math.max(0, hit.getCardIndex());
                return new Point(tableauX[hit.getPileIndex()], tableauCardY(hit.getPileIndex(), index));
            default:
                return new Point(0, 0);
        }
    }

    // ------------------------------------------------------------------
    // Painting
    // ------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        computeLayout();
        Graphics2D g2 = (Graphics2D) graphics.create();

        drawStock(g2);
        drawWaste(g2);
        drawFoundations(g2);
        drawTableau(g2);
        drawDragPreview(g2);

        if (game.isWon()) {
            g2.setColor(WIN_OVERLAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(20, cardW / 2)));
            String message = "You win!";
            int textWidth = g2.getFontMetrics().stringWidth(message);
            g2.drawString(message, (getWidth() - textWidth) / 2, getHeight() / 2);
        }

        g2.dispose();
    }

    private void drawStock(Graphics2D g2) {
        if (game.getStock().isEmpty()) {
            CardRenderer.drawSlot(g2, stockX, topY, cardW, cardH, SLOT_BORDER);
            g2.setColor(SLOT_BORDER);
            g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, cardW / 5)));
            g2.drawString("R", stockX + cardW / 2 - cardW / 12, topY + cardH / 2);
        } else {
            CardRenderer.drawCard(g2, game.getStock().top().withFaceUp(false), stockX, topY, cardW, cardH);
        }
        highlightIfNeeded(g2, PileType.STOCK, 0, stockX, topY);
    }

    private void drawWaste(Graphics2D g2) {
        CardRenderer.drawSlot(g2, wasteX, topY, cardW, cardH, SLOT_BORDER);
        Card top = game.getWaste().top();
        if (top != null) {
            CardRenderer.drawCard(g2, top, wasteX, topY, cardW, cardH);
        }
        highlightIfNeeded(g2, PileType.WASTE, 0, wasteX, topY);
    }

    private void drawFoundations(Graphics2D g2) {
        for (int i = 0; i < 4; i++) {
            int x = foundationX[i];
            CardRenderer.drawSlot(g2, x, topY, cardW, cardH, SLOT_BORDER);
            FoundationPile foundation = game.getFoundation(i);
            Card top = foundation.top();
            if (top != null) {
                CardRenderer.drawCard(g2, top, x, topY, cardW, cardH);
            }
            highlightIfNeeded(g2, PileType.FOUNDATION, i, x, topY);
        }
    }

    private void drawTableau(Graphics2D g2) {
        for (int col = 0; col < KlondikeGame.TABLEAU_COUNT; col++) {
            TableauPile pile = game.getTableauPile(col);
            int x = tableauX[col];
            if (pile.isEmpty()) {
                CardRenderer.drawSlot(g2, x, tableauY, cardW, cardH, SLOT_BORDER);
            } else {
                for (int i = 0; i < pile.size(); i++) {
                    CardRenderer.drawCard(g2, pile.get(i), x, tableauCardY(col, i), cardW, cardH);
                }
            }
            highlightIfNeeded(g2, PileType.TABLEAU, col, x, tableauY);
        }
    }

    private void drawDragPreview(Graphics2D g2) {
        if (dragPreview == null || dragPoint == null) {
            return;
        }
        int y = dragPoint.y;
        for (Card card : dragPreview) {
            CardRenderer.drawCard(g2, card, dragPoint.x, y, cardW, cardH);
            y += fanUp;
        }
    }

    private void highlightIfNeeded(Graphics2D g2, PileType type, int index, int x, int y) {
        if (highlightType == type && highlightIndex == index) {
            g2.setColor(HIGHLIGHT);
            g2.fillRoundRect(x, y, cardW, cardH, 12, 12);
        }
    }

    // ------------------------------------------------------------------
    // Hit testing
    // ------------------------------------------------------------------

    /** Finds the pile/card under a screen point, or {@code null}. */
    public Hit hitTest(Point point) {
        if (point == null) {
            return null;
        }
        computeLayout();

        if (rect(stockX, topY, cardW, cardH).contains(point)) {
            return new Hit(PileType.STOCK, 0, -1, null);
        }
        if (rect(wasteX, topY, cardW, cardH).contains(point)) {
            Card top = game.getWaste().top();
            return new Hit(PileType.WASTE, 0, top == null ? -1 : game.getWaste().size() - 1, top);
        }
        for (int i = 0; i < 4; i++) {
            if (rect(foundationX[i], topY, cardW, cardH).contains(point)) {
                Card top = game.getFoundation(i).top();
                return new Hit(PileType.FOUNDATION, i, top == null ? -1 : 0, top);
            }
        }
        for (int col = 0; col < KlondikeGame.TABLEAU_COUNT; col++) {
            if (point.x >= tableauX[col] && point.x <= tableauX[col] + cardW && point.y >= tableauY) {
                return tableauHit(col, point);
            }
        }
        return null;
    }

    private Hit tableauHit(int col, Point point) {
        TableauPile pile = game.getTableauPile(col);
        if (pile.isEmpty()) {
            return new Hit(PileType.TABLEAU, col, -1, null);
        }
        for (int i = pile.size() - 1; i >= 0; i--) {
            int y = tableauCardY(col, i);
            if (rect(tableauX[col], y, cardW, cardH).contains(point)) {
                return new Hit(PileType.TABLEAU, col, i, pile.get(i));
            }
        }
        return new Hit(PileType.TABLEAU, col, pile.size() - 1, pile.top());
    }

    private static Rectangle rect(int x, int y, int w, int h) {
        return new Rectangle(x, y, w, h);
    }

    /** Cards of a tableau column starting from {@code index} (null-safe copy). */
    public List<Card> cardsFrom(int col, int index) {
        TableauPile pile = game.getTableauPile(col);
        if (index < 0 || index >= pile.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(pile.getCards().subList(index, pile.size()));
    }

    /** Immutable result of a hit test. */
    public static final class Hit {
        private final PileType type;
        private final int pileIndex;
        private final int cardIndex;
        private final Card card;

        public Hit(PileType type, int pileIndex, int cardIndex, Card card) {
            this.type = type;
            this.pileIndex = pileIndex;
            this.cardIndex = cardIndex;
            this.card = card;
        }

        public PileType getType() {
            return type;
        }

        public int getPileIndex() {
            return pileIndex;
        }

        public int getCardIndex() {
            return cardIndex;
        }

        public Card getCard() {
            return card;
        }

        public boolean hasCard() {
            return card != null;
        }
    }
}
