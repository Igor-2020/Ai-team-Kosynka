package kosynka.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import kosynka.core.GameTimer;

/** Status bar section showing the elapsed game time. */
public class TimerPanel extends JPanel {

    private final JLabel label = new JLabel("Time: 00:00");

    public TimerPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x07, 0x4A, 0x21));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 14, 6, 14));
        add(label, BorderLayout.EAST);
    }

    public void update(GameTimer timer) {
        label.setText("Time: " + timer.format());
    }
}
