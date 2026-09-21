package kosynka.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import kosynka.core.KlondikeGame;

/**
 * Top-level window: menu bar (New Game), the playing board and the status bar.
 * A lightweight {@link Timer} refreshes the status bar so the clock keeps ticking
 * without a repaint storm.
 */
public class MainFrame extends JFrame {

    private final KlondikeGame game;
    private final BoardPanel board;
    private final ScorePanel scorePanel;
    private final TimerPanel timerPanel;

    public MainFrame() {
        super("Kosynka - Klondike Solitaire");
        game = new KlondikeGame();
        board = new BoardPanel(game);
        scorePanel = new ScorePanel();
        timerPanel = new TimerPanel();

        DragAndDropController controller = new DragAndDropController(board);
        board.addMouseListener(controller);
        board.addMouseMotionListener(controller);

        setJMenuBar(buildMenuBar());
        setLayout(new BorderLayout());
        add(board, BorderLayout.CENTER);

        JPanel status = new JPanel(new BorderLayout());
        status.add(scorePanel, BorderLayout.WEST);
        status.add(timerPanel, BorderLayout.EAST);
        add(status, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(720, 560));
        pack();
        setLocationRelativeTo(null);

        refreshStatus();
        Timer refreshTimer = new Timer(300, event -> refreshStatus());
        refreshTimer.start();
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGame = new JMenuItem("New Game");
        newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newGame.addActionListener(event -> startNewGame());

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(event -> dispose());

        gameMenu.add(newGame);
        gameMenu.addSeparator();
        gameMenu.add(exit);
        menuBar.add(gameMenu);
        return menuBar;
    }

    private void startNewGame() {
        game.newGame();
        board.repaint();
        refreshStatus();
    }

    private void refreshStatus() {
        scorePanel.update(game.getScore());
        timerPanel.update(game.getTimer());
    }

    public KlondikeGame getGame() {
        return game;
    }
}
