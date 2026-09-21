package kosynka.core;

/**
 * Outcome of a domain move. Either successful, or rejected together with the
 * concrete {@link ValidationReason}.
 */
public final class MoveResult {

    private final boolean success;
    private final ValidationReason reason;

    private MoveResult(boolean success, ValidationReason reason) {
        this.success = success;
        this.reason = reason;
    }

    public static MoveResult ok() {
        return new MoveResult(true, ValidationReason.OK);
    }

    public static MoveResult fail(ValidationReason reason) {
        return new MoveResult(false, reason == null ? ValidationReason.NOTHING_TO_MOVE : reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public ValidationReason getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return success ? "OK" : "FAILED(" + reason + ")";
    }
}
