package kosynka.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import kosynka.core.Score;

/** Status bar section showing the current score. */
public class ScorePanel extends JPanel {

    private final JLabel label = new JLabel("Score: 0");

    public ScorePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(0x07, 0x4A, 0x21));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 14, 6, 14));
        add(label, BorderLayout.WEST);
    }

    public void update(Score score) {
        label.setText("Score: " + score.getValue());
    }
}
