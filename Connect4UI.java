
/**
 * Connect4UI.java
 * Enhanced Swing-based User Interface for Connect4 Game
 * Handles:
 * - Game board display with modern styling
 * - User interactions
 * - AI opponent with three difficulty modes
 * - Game state management with difficulty selection
 * 
 * Difficulty Modes:
 * - EASY: Greedy Algorithm + Simple Recursion
 * - MODERATE: Greedy + Divide and Conquer + Recursion (no Backtracking)
 * - HARD: All algorithms including Backtracking with Minimax
 */

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class Connect4UI extends JFrame {

    // Game components
    private Board board;
    private DivideAndConquer winChecker;

    // UI components
    private JButton[][] gridButtons;
    private JButton[] columnButtons;
    private JLabel statusLabel;
    private JPanel boardPanel;
    private JPanel controlPanel;
    private JPanel modePanel;
    private JLabel difficultyLabel;
    private JLabel algorithmLabel;

    // Game state
    private char currentPlayer;
    private boolean gameOver;
    private boolean playWithAI;
    private GameDifficulty difficulty;

    // Difficulty Enum
    public enum GameDifficulty {
        EASY("Easy", "D&C (Basic)"),
        MODERATE("Moderate", "D&C (Standard)"),
        HARD("Hard", "D&C (Advanced)"),
        EXPERT("Expert", "D&C (Greedy+Recursive)");

        public final String displayName;
        public final String algorithms;

        GameDifficulty(String displayName, String algorithms) {
            this.displayName = displayName;
            this.algorithms = algorithms;
        }
    }

    // Colors - Modern palette
    private static final Color COLOR_RED = new Color(239, 68, 68);
    private static final Color COLOR_YELLOW = new Color(251, 146, 60);
    private static final Color COLOR_EMPTY = new Color(229, 231, 235);
    private static final Color COLOR_BOARD = new Color(37, 99, 235);
    private static final Color COLOR_DARK_BG = new Color(15, 23, 42);
    private static final Color COLOR_LIGHT_BG = new Color(248, 250, 252);
    private static final Color COLOR_ACCENT = new Color(139, 92, 246);
    private static final Color COLOR_SUCCESS = new Color(34, 197, 94);

    /**
     * Constructor
     */
    public Connect4UI() {
        // Initialize game components
        board = new Board();
        winChecker = new DivideAndConquer(board);

        // Print D&C algorithms info to console
        winChecker.printAlgorithmInfo();

        // Initialize game state
        currentPlayer = 'R'; // Red starts
        gameOver = false;
        difficulty = GameDifficulty.MODERATE;

        // Show difficulty and mode selection dialog
        showGameModeDialog();

        // Setup UI
        setupUI();

        setVisible(true);
    }

    /**
     * Show game mode and difficulty selection dialog
     */
    private void showGameModeDialog() {
        JPanel selectionPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        selectionPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        selectionPanel.setBackground(COLOR_LIGHT_BG);

        JLabel titleLabel = new JLabel("Connect 4 - Game Setup");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        selectionPanel.add(titleLabel);

        JLabel modeLabel = new JLabel("Select Game Mode:");
        modeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectionPanel.add(modeLabel);

        // Game mode buttons
        JPanel modeButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        modeButtonPanel.setBackground(COLOR_LIGHT_BG);

        JRadioButton aiButton = new JRadioButton("Play vs AI", true);
        JRadioButton pvpButton = new JRadioButton("Player vs Player");

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(aiButton);
        modeGroup.add(pvpButton);

        modeButtonPanel.add(aiButton);
        modeButtonPanel.add(pvpButton);
        selectionPanel.add(modeButtonPanel);

        JLabel diffLabel = new JLabel("Select Difficulty (AI only):");
        diffLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectionPanel.add(diffLabel);

        // Difficulty buttons (All map to D&C now)
        JPanel diffButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        diffButtonPanel.setBackground(COLOR_LIGHT_BG);

        JRadioButton easyButton = new JRadioButton("Easy", true);
        JRadioButton modButton = new JRadioButton("Moderate");
        JRadioButton hardButton = new JRadioButton("Hard");

        JRadioButton expertButton = new JRadioButton("Expert");

        ButtonGroup diffGroup = new ButtonGroup();
        diffGroup.add(easyButton);
        diffGroup.add(modButton);
        diffGroup.add(hardButton);
        diffGroup.add(expertButton);

        diffButtonPanel.add(easyButton);
        diffButtonPanel.add(modButton);
        diffButtonPanel.add(hardButton);
        diffButtonPanel.add(expertButton);
        selectionPanel.add(diffButtonPanel);

        int result = JOptionPane.showConfirmDialog(
                null,
                selectionPanel,
                "Game Setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.CANCEL_OPTION) {
            System.exit(0);
        }

        playWithAI = aiButton.isSelected();

        if (easyButton.isSelected()) {
            difficulty = GameDifficulty.EASY;
        } else if (modButton.isSelected()) {
            difficulty = GameDifficulty.MODERATE;
        } else if (hardButton.isSelected()) {
            difficulty = GameDifficulty.HARD;
        } else {
            difficulty = GameDifficulty.EXPERT;
        }
    }

    /**
     * Setup user interface
     */
    private void setupUI() {
        setTitle("Connect 4 - Divide & Conquer Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Create top status panel with modern styling
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setBackground(COLOR_LIGHT_BG);
        topPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Status section
        JPanel statusSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusSection.setBackground(COLOR_LIGHT_BG);

        statusLabel = new JLabel("Red's Turn");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        statusLabel.setForeground(COLOR_RED);
        statusSection.add(statusLabel);

        topPanel.add(statusSection, BorderLayout.WEST);

        // Difficulty info section
        JPanel infoSection = new JPanel(new GridLayout(1, 2, 20, 0));
        infoSection.setBackground(COLOR_LIGHT_BG);
        infoSection.setBorder(new RoundBorder(COLOR_ACCENT, 1, 8));

        difficultyLabel = new JLabel("Difficulty: " + difficulty.name());
        difficultyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        difficultyLabel.setForeground(COLOR_ACCENT);

        algorithmLabel = new JLabel("Algorithm: " + difficulty.algorithms);
        algorithmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        algorithmLabel.setForeground(COLOR_ACCENT);

        infoSection.add(difficultyLabel);
        infoSection.add(algorithmLabel);
        infoSection.setBorder(new EmptyBorder(10, 15, 10, 15));

        topPanel.add(infoSection, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Create board panel with modern design
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(board.getRows(), board.getCols(), 8, 8));
        boardPanel.setBackground(COLOR_BOARD);
        boardPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        gridButtons = new JButton[board.getRows()][board.getCols()];

        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                JButton button = new JButton();
                button.setBackground(COLOR_EMPTY);
                button.setPreferredSize(new Dimension(90, 90));
                button.setEnabled(false);
                button.setBorder(BorderFactory.createLineBorder(COLOR_BOARD, 0));
                button.setOpaque(true);
                button.setFocusPainted(false);
                button.setBorderPainted(false);

                // Add rounded appearance with custom painting
                button.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        if (button.isEnabled()) {
                            button.setBackground(button.getBackground().brighter());
                        }
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        updateButtonColor(button);
                    }
                });

                gridButtons[row][col] = button;
                boardPanel.add(button);
            }
        }

        add(boardPanel, BorderLayout.CENTER);

        // Create control panel with column buttons
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(1, board.getCols(), 8, 0));
        controlPanel.setBackground(COLOR_LIGHT_BG);
        controlPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        columnButtons = new JButton[board.getCols()];

        for (int col = 0; col < board.getCols(); col++) {
            final int column = col;
            JButton button = new JButton("↓");
            button.setFont(new Font("Segoe UI", Font.BOLD, 18));
            button.setPreferredSize(new Dimension(90, 45));
            button.setBackground(COLOR_ACCENT);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setRolloverEnabled(true);
            button.addActionListener(e -> handleColumnClick(column));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (button.isEnabled()) {
                        button.setBackground(COLOR_ACCENT.brighter());
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(COLOR_ACCENT);
                }
            });

            columnButtons[col] = button;
            controlPanel.add(button);
        }

        add(controlPanel, BorderLayout.SOUTH);

        // Add menu bar
        createMenuBar();

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Helper method to update button color based on disc state
     */
    private void updateButtonColor(JButton button) {
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                if (gridButtons[row][col] == button) {
                    char[][] boardState = board.getBoard();
                    if (boardState[row][col] == 'R') {
                        button.setBackground(COLOR_RED);
                    } else if (boardState[row][col] == 'Y') {
                        button.setBackground(COLOR_YELLOW);
                    } else {
                        button.setBackground(COLOR_EMPTY);
                    }
                    return;
                }
            }
        }
    }

    /**
     * Create menu bar
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_LIGHT_BG);

        JMenu gameMenu = new JMenu("Game");
        gameMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        newGameItem.addActionListener(e -> resetGame());

        JMenuItem settingsItem = new JMenuItem("Change Difficulty");
        settingsItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        settingsItem.addActionListener(e -> showGameModeDialog());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exitItem.addActionListener(e -> System.exit(0));

        gameMenu.add(newGameItem);
        gameMenu.add(settingsItem);
        gameMenu.addSeparator();
        gameMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JMenuItem aboutItem = new JMenuItem("About Algorithms");
        aboutItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        aboutItem.addActionListener(e -> showAlgorithmInfo());

        helpMenu.add(aboutItem);

        menuBar.add(gameMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Show algorithm information dialog
     */
    private void showAlgorithmInfo() {
        String info = "Connect 4 - Algorithm Implementation\n\n" +
                "DIVIDE & CONQUER STRATEGY:\n" +
                "This implementation exclusively uses a Divide & Conquer approach.\n\n" +
                "CORE ALGORITHMS:\n" +
                "1. Win Detection (4 directions)\n" +
                "   - Splits check into Horizontal, Vertical, Diagonal L/R\n" +
                "2. Best Move Finding\n" +
                "   - Recursively splits columns into halves\n" +
                "3. Board Evaluation\n" +
                "   - Splits board into top/bottom halves\n" +
                "4. Connected Count\n" +
                "   - Splits board into 4 quadrants\n" +
                "5. Find Valid Moves\n" +
                "   - Recursively finds valid columns\n" +
                "6. Recursive Winner Detection\n" +
                "   - Checks row/col segments recursively\n" +
                "9. Greedy-Guided Minimax (Expert)\n" +
                "   - Orders moves by Greedy Score, then Recurses\n\n" +
                "All difficulty levels currently utilize this optimized D&C engine.";

        JTextArea textArea = new JTextArea(info);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        textArea.setBackground(COLOR_LIGHT_BG);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Algorithm Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handle column button click
     */
    private void handleColumnClick(int col) {
        if (gameOver) {
            return;
        }

        if (!board.isValidMove(col)) {
            JOptionPane.showMessageDialog(this, "Column is full!", "Invalid Move", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Make move
        makeMove(col, currentPlayer);

        // Check win/draw
        if (checkGameEnd()) {
            return;
        }

        // Switch player
        currentPlayer = (currentPlayer == 'R') ? 'Y' : 'R';
        updateStatus();

        // If AI mode and now Yellow's turn, let AI play
        if (playWithAI && currentPlayer == 'Y') {
            disableButtons();

            // Use SwingWorker to run AI in background
            SwingWorker<Integer, Void> aiWorker = new SwingWorker<Integer, Void>() {
                @Override
                protected Integer doInBackground() {
                    try {
                        Thread.sleep(800); // Delay for better UX
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Call D&C algorithm
                    return getAIMoveByDifficulty('Y');
                }

                @Override
                protected void done() {
                    try {
                        int aiMove = get();
                        makeMove(aiMove, 'Y');

                        if (!checkGameEnd()) {
                            currentPlayer = 'R';
                            updateStatus();
                        }

                        enableButtons();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            aiWorker.execute();
        }
    }

    /**
     * Get AI move based on difficulty level
     */
    private int getAIMoveByDifficulty(char player) {
        switch (difficulty) {
            case EASY:
                // Greedy + D&C Column Split (Algo 2) - Fast, uses heuristics
                return winChecker.findBestMove(player);

            case MODERATE:
                // ✅ State-Space D&C (Algo 7) with depth 3
                return winChecker.findBestMoveModerate(player, 3);

            case HARD:
                // ✅ Minimax with Alpha-Beta (Algo 8), depth 6
                return winChecker.findBestMoveHard(player, 6);

            case EXPERT:
                // ✅ Greedy-Guided Recursive Search (Algo 9), depth 7
                return winChecker.findBestMoveGreedyRecursive(player, 7);

            default:
                return winChecker.findBestMove(player);
        }
    }

    /**
     * Make a move on the board
     */
    private void makeMove(int col, char player) {
        int row = board.insertDisc(col, player);

        if (row != -1) {
            // Update UI with animation
            Color color = (player == 'R') ? COLOR_RED : COLOR_YELLOW;
            gridButtons[row][col].setBackground(color);
        }
    }

    /**
     * Check if game has ended
     */
    private boolean checkGameEnd() {
        // Check win
        if (winChecker.checkWin(currentPlayer)) {
            gameOver = true;
            String winner = (currentPlayer == 'R') ? "Red" : "Yellow";
            statusLabel.setText(winner + " Wins!");
            statusLabel.setForeground(COLOR_SUCCESS);
            disableButtons();

            JOptionPane.showMessageDialog(
                    this,
                    winner + " wins the game!\n\nDifficulty: " + difficulty.name() +
                            "\nAlgorithms: " + difficulty.algorithms,
                    "Game Over - You Won!",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        // Check draw
        if (board.isBoardFull()) {
            gameOver = true;
            statusLabel.setText("Draw!");
            statusLabel.setForeground(COLOR_ACCENT);
            disableButtons();

            JOptionPane.showMessageDialog(
                    this,
                    "The game is a draw!\n\nDifficulty: " + difficulty.name(),
                    "Game Over - Draw",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        return false;
    }

    /**
     * Update status label
     */
    private void updateStatus() {
        if (!gameOver) {
            String playerName = (currentPlayer == 'R') ? "Red" : "Yellow";
            statusLabel.setText(playerName + "'s Turn");
            statusLabel.setForeground((currentPlayer == 'R') ? COLOR_RED : COLOR_YELLOW);

            // Update algorithm info for current player
            if (currentPlayer == 'Y' && playWithAI) {
                algorithmLabel.setText("Running: " + difficulty.algorithms);
            } else {
                algorithmLabel.setText("Algorithms: " + difficulty.algorithms);
            }
        }
    }

    /**
     * Reset game
     */
    private void resetGame() {
        board.initializeBoard();
        currentPlayer = 'R';
        gameOver = false;

        // Reset UI
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = 0; col < board.getCols(); col++) {
                gridButtons[row][col].setBackground(COLOR_EMPTY);
            }
        }

        enableButtons();
        updateStatus();
    }

    /**
     * Disable column buttons
     */
    private void disableButtons() {
        for (JButton button : columnButtons) {
            button.setEnabled(false);
            button.setBackground(COLOR_ACCENT.darker());
        }
    }

    /**
     * Enable column buttons
     */
    private void enableButtons() {
        for (int col = 0; col < columnButtons.length; col++) {
            if (board.isValidMove(col)) {
                columnButtons[col].setEnabled(true);
                columnButtons[col].setBackground(COLOR_ACCENT);
            } else {
                columnButtons[col].setEnabled(false);
                columnButtons[col].setBackground(COLOR_ACCENT.darker());
            }
        }
    }
}

/**
 * Custom rounded border for info panel
 */
class RoundBorder extends AbstractBorder {
    private Color color;
    private int thickness;
    private int radius;

    public RoundBorder(Color color, int thickness, int radius) {
        this.color = color;
        this.thickness = thickness;
        this.radius = radius;
    }

    @Override
    public void paintBorder(java.awt.Component c, java.awt.Graphics g, int x, int y, int width, int height) {
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new java.awt.BasicStroke(thickness));
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }

    @Override
    public java.awt.Insets getBorderInsets(java.awt.Component c) {
        return new java.awt.Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
    }
}
