package kosynka.core;

/**
 * Reasons why a move may be rejected (or accepted). Used by the domain layer to
 * communicate move validation outcomes to callers (including the UI).
 */
public enum ValidationReason {

    OK,
    NOT_KING_TO_EMPTY,
    WRONG_COLOR,
    WRONG_RANK,
    WRONG_SUIT,
    FOUNDATION_NOT_ACE,
    INVALID_SEQUENCE,
    NOT_FACE_UP,
    NOTHING_TO_MOVE,
    NO_CARDS,
    PILE_NOT_FOUND,
    NOT_ACCEPTING
}
