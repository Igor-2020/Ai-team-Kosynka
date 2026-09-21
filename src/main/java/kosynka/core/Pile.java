package kosynka.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for every card pile in the game. Keeps the list of cards and the
 * common stack operations, while leaving placement rules to subclasses.
 */
public abstract class Pile {

    protected final List<Card> cards = new ArrayList<>();

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /** The top (last) card of the pile, or {@code null} if the pile is empty. */
    public Card top() {
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
    }

    public Card get(int index) {
        return cards.get(index);
    }

    public void add(Card card) {
        cards.add(card);
    }

    public void addAll(List<Card> newCards) {
        cards.addAll(newCards);
    }

    /** Removes and returns the top card, or {@code null} if the pile is empty. */
    public Card removeTop() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(cards.size() - 1);
    }

    /** Removes all cards from {@code index} (inclusive) up to the top. */
    public List<Card> removeFrom(int index) {
        if (index < 0 || index >= cards.size()) {
            return new ArrayList<>();
        }
        List<Card> removed = new ArrayList<>(cards.subList(index, cards.size()));
        cards.subList(index, cards.size()).clear();
        return removed;
    }

    public void clear() {
        cards.clear();
    }

    /**
     * Turns the top card face up if it is currently face down.
     *
     * @return {@code true} if the top card was actually flipped
     */
    public boolean revealTop() {
        if (cards.isEmpty()) {
            return false;
        }
        int index = cards.size() - 1;
        Card top = cards.get(index);
        if (!top.isFaceUp()) {
            cards.set(index, top.withFaceUp(true));
            return true;
        }
        return false;
    }

    /** Placement rule for a single card. */
    public abstract ValidationReason canAccept(Card card);

    /** Placement rule for a sequence of cards (defaults to the first card check). */
    public ValidationReason canAcceptSequence(List<Card> sequence) {
        if (sequence == null || sequence.isEmpty()) {
            return ValidationReason.NOTHING_TO_MOVE;
        }
        return canAccept(sequence.get(0));
    }
}
