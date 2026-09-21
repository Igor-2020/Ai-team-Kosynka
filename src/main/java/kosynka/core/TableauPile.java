package kosynka.core;

import java.util.List;

/**
 * One of the seven tableau columns. Accepts a King on an empty pile and otherwise
 * cards of descending rank with alternating colour. Also accepts valid sequences.
 */
public class TableauPile extends Pile {

    @Override
    public ValidationReason canAccept(Card card) {
        if (card == null) {
            return ValidationReason.NO_CARDS;
        }
        if (!card.isFaceUp()) {
            return ValidationReason.NOT_FACE_UP;
        }
        if (cards.isEmpty()) {
            return card.getRank() == Rank.KING
                    ? ValidationReason.OK
                    : ValidationReason.NOT_KING_TO_EMPTY;
        }
        Card top = top();
        if (!top.isFaceUp()) {
            return ValidationReason.NOT_FACE_UP;
        }
        if (card.getRank().getValue() != top.getRank().getValue() - 1) {
            return ValidationReason.WRONG_RANK;
        }
        if (card.isRed() == top.isRed()) {
            return ValidationReason.WRONG_COLOR;
        }
        return ValidationReason.OK;
    }

    @Override
    public ValidationReason canAcceptSequence(List<Card> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return ValidationReason.NOTHING_TO_MOVE;
        }
        ValidationReason run = validateRun(sequence);
        if (run != ValidationReason.OK) {
            return run;
        }
        return canAccept(sequence.get(0));
    }

    /**
     * Verifies that the given cards form a valid descending, alternating-colour run
     * of face-up cards (as is required for a movable tableau sequence).
     */
    public static ValidationReason validateRun(List<Card> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return ValidationReason.NOTHING_TO_MOVE;
        }
        for (int i = 0; i < sequence.size(); i++) {
            Card current = sequence.get(i);
            if (!current.isFaceUp()) {
                return ValidationReason.NOT_FACE_UP;
            }
            if (i > 0) {
                Card previous = sequence.get(i - 1);
                if (current.getRank().getValue() != previous.getRank().getValue() - 1) {
                    return ValidationReason.WRONG_RANK;
                }
                if (current.isRed() == previous.isRed()) {
                    return ValidationReason.WRONG_COLOR;
                }
            }
        }
        return ValidationReason.OK;
    }
}
