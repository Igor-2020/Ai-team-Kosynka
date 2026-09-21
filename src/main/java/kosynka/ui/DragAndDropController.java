package kosynka.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import kosynka.core.Card;
import kosynka.core.KlondikeGame;
import kosynka.core.TableauPile;
import kosynka.core.ValidationReason;

/**
 * Translates mouse input on the {@link BoardPanel} into domain moves:
 * clicking the stock draws, dragging moves a card or a valid sequence, and a
 * double click sends a card to its foundation. Illegal moves are simply not
 * applied (they behave as an automatic rollback) and the board is repainted.
 */
public class DragAndDropController extends MouseAdapter {

    private final BoardPanel board;
    private final KlondikeGame game;

    private boolean dragging;
    private PileType dragType;
    private int dragPileIndex;
    private int dragCardIndex;
    private List<Card> dragCards = new ArrayList<>();

    private Point pressPoint;
    private Point currentPoint;
    private int dragOffsetX;
    private int dragOffsetY;
    private boolean movedEnough;

    public DragAndDropController(BoardPanel board) {
        this.board = board;
        this.game = board.getGame();
    }

    @Override
    public void mousePressed(MouseEvent event) {
        movedEnough = false;
        dragging = false;
        dragCards = new ArrayList<>();
        pressPoint = event.getPoint();
        currentPoint = pressPoint;

        BoardPanel.Hit hit = board.hitTest(pressPoint);
        if (hit == null) {
            return;
        }

        if (hit.getType() == PileType.STOCK) {
            game.drawFromStock();
            board.repaint();
            return;
        }

        Card card = hit.getCard();
        if (card == null || !card.isFaceUp()) {
            return;
        }

        if (hit.getType() == PileType.WASTE) {
            dragCards.add(card);
            dragType = PileType.WASTE;
            dragPileIndex = 0;
            dragCardIndex = hit.getCardIndex();
        } else if (hit.getType() == PileType.TABLEAU) {
            List<Card> sequence = board.cardsFrom(hit.getPileIndex(), hit.getCardIndex());
            if (TableauPile.validateRun(sequence) != ValidationReason.OK) {
                return;
            }
            dragCards = sequence;
            dragType = PileType.TABLEAU;
            dragPileIndex = hit.getPileIndex();
            dragCardIndex = hit.getCardIndex();
        } else {
            return;
        }

        if (!dragCards.isEmpty()) {
            dragging = true;
            Point cardTopLeft = board.cardTopLeft(hit);
            dragOffsetX = pressPoint.x - cardTopLeft.x;
            dragOffsetY = pressPoint.y - cardTopLeft.y;
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        currentPoint = event.getPoint();
        if (!dragging) {
            return;
        }
        if (Math.abs(currentPoint.x - pressPoint.x) > 3 || Math.abs(currentPoint.y - pressPoint.y) > 3) {
            movedEnough = true;
        }
        board.setDragPreview(dragCards, currentTopLeft());
        updateHighlight();
        board.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (dragging && movedEnough) {
            BoardPanel.Hit target = board.hitTest(event.getPoint());
            applyDrop(target);
        }
        clearDrag();
        board.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() != 2) {
            return;
        }
        BoardPanel.Hit hit = board.hitTest(event.getPoint());
        if (hit == null) {
            return;
        }
        if (hit.getType() == PileType.TABLEAU && hit.getCardIndex() >= 0) {
            game.autoMoveTableauToFoundation(hit.getPileIndex());
            board.repaint();
        } else if (hit.getType() == PileType.WASTE) {
            game.autoMoveWasteToFoundation();
            board.repaint();
        }
    }

    private void applyDrop(BoardPanel.Hit target) {
        if (target == null) {
            return;
        }
        if (target.getType() == PileType.TABLEAU) {
            int toCol = target.getPileIndex();
            if (dragType == PileType.TABLEAU) {
                if (toCol != dragPileIndex) {
                    game.moveTableauToTableau(dragPileIndex, dragCardIndex, toCol);
                }
            } else if (dragType == PileType.WASTE) {
                game.moveWasteToTableau(toCol);
            }
        } else if (target.getType() == PileType.FOUNDATION && dragCards.size() == 1) {
            if (dragType == PileType.TABLEAU) {
                game.moveTableauToFoundation(dragPileIndex);
            } else if (dragType == PileType.WASTE) {
                game.moveWasteToFoundation();
            }
        }
        // Any other target is simply ignored -> the drag is effectively rolled back.
    }

    private Point currentTopLeft() {
        return new Point(currentPoint.x - dragOffsetX, currentPoint.y - dragOffsetY);
    }

    private void updateHighlight() {
        Point probe = new Point(currentTopLeft().x + 10, currentTopLeft().y + 10);
        BoardPanel.Hit target = board.hitTest(probe);
        if (target == null) {
            board.clearHighlight();
        } else if (target.getType() == PileType.TABLEAU) {
            board.setHighlight(PileType.TABLEAU, target.getPileIndex());
        } else if (target.getType() == PileType.FOUNDATION) {
            board.setHighlight(PileType.FOUNDATION, target.getPileIndex());
        } else {
            board.clearHighlight();
        }
    }

    private void clearDrag() {
        dragging = false;
        dragCards = new ArrayList<>();
        board.clearDragPreview();
        board.clearHighlight();
    }
}
