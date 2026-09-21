package kosynka.core;

/**
 * Simple time model for a running game. It is intentionally free of any UI code:
 * the UI polls {@link #getElapsedMillis()} (or {@link #format()}) and repaints.
 */
public class GameTimer {

    private long accumulatedMillis;
    private long lastStartMillis;
    private boolean running;

    public void start() {
        if (!running) {
            running = true;
            lastStartMillis = System.currentTimeMillis();
        }
    }

    public void pause() {
        if (running) {
            accumulatedMillis += System.currentTimeMillis() - lastStartMillis;
            running = false;
        }
    }

    public void reset() {
        accumulatedMillis = 0;
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public long getElapsedMillis() {
        long total = accumulatedMillis;
        if (running) {
            total += System.currentTimeMillis() - lastStartMillis;
        }
        return total;
    }

    public long getElapsedSeconds() {
        return getElapsedMillis() / 1000L;
    }

    /** Returns the elapsed time formatted as {@code mm:ss}. */
    public String format() {
        long seconds = getElapsedSeconds();
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }
}
