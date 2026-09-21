package kosynka.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import kosynka.core.Card;

/**
 * Draws playing cards with Java2D. Every card (face and back) is painted in code,
 * so the game ships without any external image resources.
 */
public final class CardRenderer {

    public static final int DEFAULT_CARD_WIDTH = 80;
    public static final int DEFAULT_CARD_HEIGHT = 112;

    private static final Color CARD_FACE = new Color(0xFA, 0xFA, 0xF5);
    private static final Color CARD_EDGE = new Color(0x40, 0x40, 0x40);
    private static final Color RED = new Color(0xC6, 0x28, 0x28);
    private static final Color BLACK = new Color(0x1E, 0x1E, 0x1E);
    private static final Color BACK_DARK = new Color(0x14, 0x3C, 0x86);
    private static final Color BACK_LIGHT = new Color(0x2E, 0x6F, 0xD0);
    private static final Color SHADOW = new Color(0, 0, 0, 60);

    private CardRenderer() {
    }

    public static void drawCard(Graphics2D g2, Card card, int x, int y, int width, int height) {
        Object antialias = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        Object textAntialias = g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(SHADOW);
        g2.fill(new RoundRectangle2D.Double(x + 3, y + 3, width, height, corner(width), corner(width)));

        if (card.isFaceUp()) {
            drawFaceUp(g2, card, x, y, width, height);
        } else {
            drawFaceDown(g2, x, y, width, height);
        }

        if (antialias != null) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias);
        }
        if (textAntialias != null) {
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, textAntialias);
        }
    }

    private static void drawFaceUp(Graphics2D g2, Card card, int x, int y, int width, int height) {
        RoundRectangle2D shape = new RoundRectangle2D.Double(x, y, width, height, corner(width), corner(width));
        g2.setColor(CARD_FACE);
        g2.fill(shape);
        g2.setColor(CARD_EDGE);
        g2.setStroke(new java.awt.BasicStroke(Math.max(1f, width / 60f)));
        g2.draw(shape);

        Color suitColor = card.isRed() ? RED : BLACK;
        String rank = card.getRank().getSymbol();
        String suit = String.valueOf(card.getSuit().getSymbol());

        int smallSize = Math.max(9, height / 6);
        Font small = new Font("SansSerif", Font.BOLD, smallSize);
        g2.setFont(small);
        g2.setColor(suitColor);

        int pad = Math.max(4, width / 12);
        int ascent = g2.getFontMetrics().getAscent();
        g2.drawString(rank, x + pad, y + pad + ascent);
        g2.drawString(suit, x + pad, y + pad + ascent + smallSize);

        // Mirrored rank/suit in the bottom-right corner.
        int textWidth = g2.getFontMetrics().stringWidth(rank);
        g2.drawString(rank, x + width - pad - textWidth, y + height - pad - smallSize - 2);
        g2.drawString(suit, x + width - pad - textWidth, y + height - pad);

        // Large central pip.
        int bigSize = Math.max(16, (int) (height * 0.34));
        Font big = new Font("SansSerif", Font.PLAIN, bigSize);
        g2.setFont(big);
        int bigWidth = g2.getFontMetrics().stringWidth(suit);
        int bigAscent = g2.getFontMetrics().getAscent();
        g2.drawString(suit,
                x + (width - bigWidth) / 2,
                y + (height + bigAscent) / 2 - bigSize / 8);
    }

    private static void drawFaceDown(Graphics2D g2, int x, int y, int width, int height) {
        RoundRectangle2D shape = new RoundRectangle2D.Double(x, y, width, height, corner(width), corner(width));
        g2.setColor(BACK_DARK);
        g2.fill(shape);
        g2.setColor(CARD_EDGE);
        g2.setStroke(new java.awt.BasicStroke(Math.max(1f, width / 60f)));
        g2.draw(shape);

        int inset = Math.max(4, width / 10);
        RoundRectangle2D inner = new RoundRectangle2D.Double(
                x + inset, y + inset, width - 2 * inset, height - 2 * inset,
                corner(width), corner(width));
        g2.setColor(BACK_LIGHT);
        g2.fill(inner);
        g2.setColor(CARD_FACE);
        g2.setStroke(new java.awt.BasicStroke(Math.max(1f, width / 50f)));
        g2.draw(inner);

        // Decorative cross-hatch pattern inside the back.
        int step = Math.max(6, width / 8);
        java.awt.Stroke old = g2.getStroke();
        g2.setStroke(new java.awt.BasicStroke(1f));
        for (int offset = -height; offset < width; offset += step) {
            g2.drawLine(x + inset + offset, y + inset,
                    x + inset + offset + height, y + height - inset);
        }
        g2.setStroke(old);
    }

    /** Draws an empty slot outline (used for the tableau / foundation placeholders). */
    public static void drawSlot(Graphics2D g2, int x, int y, int width, int height, Color border) {
        g2.setColor(border);
        g2.setStroke(new java.awt.BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Double(x, y, width, height, corner(width), corner(width)));
    }

    private static double corner(int width) {
        return Math.max(6, width / 6.0);
    }
}
