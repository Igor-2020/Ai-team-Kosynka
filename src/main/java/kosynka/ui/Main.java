package kosynka.ui;

import javax.swing.SwingUtilities;

/** Application entry point. Starts the Swing UI on the Event Dispatch Thread. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
