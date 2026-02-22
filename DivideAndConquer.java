
/**
 * DivideAndConquer.java
 * Comprehensive Divide & Conquer implementations for Connect4
 * 
 * Contains 6 different D&C algorithms:
 * 1. D&C for Win Detection (4 directions)
 * 2. D&C for Best Move Finding
 * 3. D&C for Board Evaluation
 * 4. D&C for Connected Count
 * 5. D&C for Finding Valid Moves
 * 6. D&C for Winner Detection using Recursion
 */

import java.util.*;

public class DivideAndConquer {

    private Board board;

    public DivideAndConquer(Board board) {
        this.board = board;
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 1: WIN DETECTION (4 DIRECTIONS)
    // Divides win checking into 4 independent subproblems
    // Time Complexity: O(R*C)
    // =====================================================================

    /**
     * Main D&C function: Check if a player has won
     * Divides into 4 subproblems and conquers
     */
    public boolean checkWin(char player) {
        return checkHorizontal(player) ||
                checkVertical(player) ||
                checkDiagonalDown(player) ||
                checkDiagonalUp(player);
    }

    /**
     * Subproblem 1: Check Horizontal wins
     */
    /**
     * Subproblem 1: Check Horizontal wins (Recursive)
     */
    private boolean checkHorizontal(char player) {
        char[][] grid = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        return checkHorizontalRec(grid, player, 0, 0, rows, cols);
    }

    private boolean checkHorizontalRec(char[][] grid, char player, int row, int col, int rows, int cols) {
        if (row >= rows)
            return false;
        if (col > cols - 4)
            return checkHorizontalRec(grid, player, row + 1, 0, rows, cols);

        if (grid[row][col] == player &&
                grid[row][col + 1] == player &&
                grid[row][col + 2] == player &&
                grid[row][col + 3] == player) {
            return true;
        }
        return checkHorizontalRec(grid, player, row, col + 1, rows, cols);
    }

    /**
     * Subproblem 2: Check Vertical wins (Recursive)
     */
    private boolean checkVertical(char player) {
        char[][] grid = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        return checkVerticalRec(grid, player, 0, 0, rows, cols);
    }

    private boolean checkVerticalRec(char[][] grid, char player, int col, int row, int rows, int cols) {
        if (col >= cols)
            return false;
        if (row > rows - 4)
            return checkVerticalRec(grid, player, col + 1, 0, rows, cols);

        if (grid[row][col] == player &&
                grid[row + 1][col] == player &&
                grid[row + 2][col] == player &&
                grid[row + 3][col] == player) {
            return true;
        }
        return checkVerticalRec(grid, player, col, row + 1, rows, cols);
    }

    /**
     * Subproblem 3: Check Diagonal Down-Right (\) (Recursive)
     */
    private boolean checkDiagonalDown(char player) {
        char[][] grid = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        return checkDiagonalDownRec(grid, player, 0, 0, rows, cols);
    }

    private boolean checkDiagonalDownRec(char[][] grid, char player, int row, int col, int rows, int cols) {
        if (row > rows - 4)
            return false;
        if (col > cols - 4)
            return checkDiagonalDownRec(grid, player, row + 1, 0, rows, cols);

        if (grid[row][col] == player &&
                grid[row + 1][col + 1] == player &&
                grid[row + 2][col + 2] == player &&
                grid[row + 3][col + 3] == player) {
            return true;
        }
        return checkDiagonalDownRec(grid, player, row, col + 1, rows, cols);
    }

    /**
     * Subproblem 4: Check Diagonal Up-Right (/) (Recursive)
     */
    private boolean checkDiagonalUp(char player) {
        char[][] grid = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();
        return checkDiagonalUpRec(grid, player, 3, 0, rows, cols);
    }

    private boolean checkDiagonalUpRec(char[][] grid, char player, int row, int col, int rows, int cols) {
        if (row >= rows)
            return false;
        if (col > cols - 4)
            return checkDiagonalUpRec(grid, player, row + 1, 0, rows, cols);

        if (grid[row][col] == player &&
                grid[row - 1][col + 1] == player &&
                grid[row - 2][col + 2] == player &&
                grid[row - 3][col + 3] == player) {
            return true;
        }
        return checkDiagonalUpRec(grid, player, row, col + 1, rows, cols);
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 2: BEST MOVE FINDING
    // Divides columns into left and right halves recursively
    // Time Complexity: O(C log C) where C = number of columns
    // =====================================================================

    /**
     * Find best column using Divide & Conquer
     * Divides columns into halves and recursively finds best move
     */
    public int findBestMoveDnC(char player, int[] columns) {
        if (columns.length == 0) {
            return -1;
        }

        if (columns.length == 1) {
            return board.isValidMove(columns[0]) ? columns[0] : -1;
        }

        // Divide into two halves
        int mid = columns.length / 2;
        int[] leftHalf = Arrays.copyOfRange(columns, 0, mid);
        int[] rightHalf = Arrays.copyOfRange(columns, mid, columns.length);

        // Conquer: Evaluate both halves
        int leftBest = findBestMoveDnC(player, leftHalf);
        int rightBest = findBestMoveDnC(player, rightHalf);

        // Combine: Choose better move
        return chooseBetterMove(player, leftBest, rightBest);
    }

    /**
     * Helper: Choose better move between two columns
     */
    private int chooseBetterMove(char player, int col1, int col2) {
        if (col1 == -1)
            return col2;
        if (col2 == -1)
            return col1;

        // Simulate both moves and compare scores
        board.insertDisc(col1, player);
        int score1;
        if (checkWinRecursive(player)) {
            score1 = 100000; // Immediate win detected by Algo 6
        } else {
            score1 = evaluatePositionDnC(player);
        }
        board.removeDisc(col1);

        board.insertDisc(col2, player);
        int score2;
        if (checkWinRecursive(player)) {
            score2 = 100000; // Immediate win detected by Algo 6
        } else {
            score2 = evaluatePositionDnC(player);
        }
        board.removeDisc(col2);

        return (score1 >= score2) ? col1 : col2;
    }

    /**
     * Wrapper to find best move from all columns
     * INTEGRATED WITH GREEDY ALGORITHMS
     */
    public int findBestMove(char player) {
        char opponent = (player == 'R') ? 'Y' : 'R';

        // 1️⃣ Immediate Winning Move (Highest Priority)
        int winMove = findImmediateWin(player);
        if (winMove != -1)
            return winMove;

        // 2️⃣ Immediate Block Opponent Win (Second Priority)
        int blockMove = findImmediateWin(opponent); // If opponent can win, block it
        if (blockMove != -1)
            return blockMove;

        // Use D&C Algorithm 5 to find valid moves
        List<Integer> validMovesList = findValidMovesDnC(0, board.getCols() - 1);
        if (validMovesList.isEmpty())
            return -1;

        // Filter unsafe moves first
        List<Integer> safeMoves = new ArrayList<>();
        for (int col : validMovesList) {
            if (isSafeMove(player, col)) {
                safeMoves.add(col);
            }
        }

        // If no safe moves, use all valid moves
        if (safeMoves.isEmpty()) {
            safeMoves = validMovesList;
        }

        // ✅ USE ALGORITHM 2: D&C Column Split for move selection
        int[] safeMovesArray = new int[safeMoves.size()];
        for (int i = 0; i < safeMoves.size(); i++) {
            safeMovesArray[i] = safeMoves.get(i);
        }

        return findBestMoveDnC(player, safeMovesArray);
    }

    /**
     * Find best move using State-Space D&C (Algorithm 7)
     * Used for Moderate difficulty - explores game tree without pruning
     */
    public int findBestMoveModerate(char player, int depth) {
        char opponent = (player == 'R') ? 'Y' : 'R';

        // Check for immediate win/block first (Greedy shortcut)
        int winMove = findImmediateWin(player);
        if (winMove != -1)
            return winMove;
        int blockMove = findImmediateWin(opponent);
        if (blockMove != -1)
            return blockMove;

        List<Integer> validMoves = findValidMovesDnC(0, board.getCols() - 1);
        int bestMove = validMoves.isEmpty() ? -1 : validMoves.get(0);
        int maxScore = Integer.MIN_VALUE;

        // ✅ USE ALGORITHM 7: State-Space D&C
        for (int col : validMoves) {
            board.insertDisc(col, player);
            int score = stateSpaceDnC(player, depth - 1, false);
            board.removeDisc(col);

            if (score > maxScore) {
                maxScore = score;
                bestMove = col;
            }
        }

        return bestMove;
    }

    // =====================================================================
    // 🧠 GREEDY ALGORITHMS IMPLEMENTATION
    // =====================================================================

    /**
     * 1️⃣ & 2️⃣ Find Immediate Win / Block
     */
    private int findImmediateWin(char player) {
        for (int col = 0; col < board.getCols(); col++) {
            if (board.isValidMove(col)) {
                board.insertDisc(col, player);
                boolean wins = checkWinRecursive(player); // Uses D&C Algo 6
                board.removeDisc(col);
                if (wins)
                    return col;
            }
        }
        return -1;
    }

    /**
     * 3️⃣ Center Column Preference
     */
    private int scoreCenterPreference(int col) {
        int centerCol = board.getCols() / 2;
        int distance = Math.abs(col - centerCol);
        // Score: 3 (center), 2 (near), 1 (far), 0 (edge)
        return (3 - distance) * 20;
    }

    /**
     * 4️⃣ Maximize Immediate Connections
     */
    private int scoreMaxConnections(char player, int col) {
        board.insertDisc(col, player);
        int score = 0;
        score += countConnectedDnC(player, 3) * 100; // 3-in-a-row
        score += countConnectedDnC(player, 2) * 50; // 2-in-a-row
        board.removeDisc(col);
        return score;
    }

    /**
     * 5️⃣ Minimize Opponent Connections
     */
    private int scoreMinOpponentConnections(char player, int col) {
        char opponent = (player == 'R') ? 'Y' : 'R';
        board.insertDisc(col, player); // We play here
        int score = 0;
        // Penalize if opponent still has many connections (we failed to block/disrupt)
        score -= countConnectedDnC(opponent, 3) * 80;
        score -= countConnectedDnC(opponent, 2) * 40;
        board.removeDisc(col);
        return score;
    }

    /**
     * 6️⃣ Column Safety Check
     * Returns false if playing here allows opponent to win immediately
     */
    private boolean isSafeMove(char player, int col) {
        char opponent = (player == 'R') ? 'Y' : 'R';
        board.insertDisc(col, player);

        boolean safe = true;
        // Check if opponent can win in ANY column after our move
        for (int c = 0; c < board.getCols(); c++) {
            if (board.isValidMove(c)) {
                board.insertDisc(c, opponent);
                if (checkWinRecursive(opponent)) {
                    safe = false;
                }
                board.removeDisc(c);
                if (!safe)
                    break;
            }
        }

        board.removeDisc(col);
        return safe;
    }

    /**
     * 7️⃣ Board Evaluation Greedy
     * Combines all heuristics for a move
     */
    private int evaluateGreedy(char player, int col) {
        int score = 0;

        // Base: Board Evaluation (D&C Algo 3)
        board.insertDisc(col, player);
        score += evaluatePositionDnC(player);
        board.removeDisc(col);

        // Add specific heuristics
        score += scoreCenterPreference(col);
        score += scoreMaxConnections(player, col);
        score += scoreMinOpponentConnections(player, col);

        return score;
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 3: BOARD EVALUATION
    // Divides board into top and bottom halves
    // Time Complexity: O(R*C)
    // =====================================================================

    /**
     * Evaluate board position using Divide & Conquer
     * Divides board into top and bottom halves AND uses quadrant counts
     */
    public int evaluatePositionDnC(char player) {
        char[][] grid = board.getBoard();
        int rows = board.getRows();

        // Divide into top and bottom halves (Algo 3)
        int midRow = rows / 2;

        int topScore = evaluateHalfBoard(player, 0, midRow);
        int bottomScore = evaluateHalfBoard(player, midRow, rows);

        // Integrate D&C Algorithm 4: Connected Count
        int connectedBonus = countConnectedDnC(player, 3) * 50;
        int connectedScore = connectedBonus + (countConnectedDnC(player, 2) * 10);

        // Strategy: Combine spatial evaluation (top/bottom) with pattern evaluation
        // (quadrants)
        return topScore + bottomScore + connectedScore;
    }

    /**
     * Evaluate half of the board (from startRow to endRow)
     */
    private int evaluateHalfBoard(char player, int startRow, int endRow) {
        char opponent = (player == 'R') ? 'Y' : 'R';
        char[][] grid = board.getBoard();
        int cols = board.getCols();
        int score = 0;

        // Count pieces in this half
        for (int row = startRow; row < endRow; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] == player) {
                    score += 10;
                    // Bonus for center control
                    if (col == 3)
                        score += 5;
                } else if (grid[row][col] == opponent) {
                    score -= 10;
                    if (col == 3)
                        score -= 5;
                }
            }
        }

        return score;
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 4: CONNECTED COUNT
    // Divides grid into quadrants and counts patterns
    // Time Complexity: O(R*C)
    // =====================================================================
    // spatial dnc
    /**
     * Count connected pieces using Divide & Conquer
     * Divides board into 4 quadrants
     */
    public int countConnectedDnC(char player, int length) {
        char[][] grid = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();

        int midRow = rows / 2;
        int midCol = cols / 2;

        // Divide into 4 quadrants
        int q1 = countInRegion(player, length, 0, midRow, 0, midCol);
        int q2 = countInRegion(player, length, 0, midRow, midCol, cols);
        int q3 = countInRegion(player, length, midRow, rows, 0, midCol);
        int q4 = countInRegion(player, length, midRow, rows, midCol, cols);

        // Also check connections across boundaries
        int crossBoundary = countCrossBoundary(player, length, midRow, midCol);

        // Combine all results
        return q1 + q2 + q3 + q4 + crossBoundary;
    }

    private int countInRegion(char player, int length, int startRow, int endRow, int startCol, int endCol) {
        char[][] grid = board.getBoard();
        int count = 0;

    
        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col <= endCol - length; col++) {
                boolean valid = true;
                for (int i = 0; i < length; i++) {
                    if (grid[row][col + i] != player) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    count++;
            }
        }

      
        for (int col = startCol; col < endCol; col++) {
            for (int row = startRow; row <= endRow - length; row++) {
                boolean valid = true;
                for (int i = 0; i < length; i++) {
                    if (grid[row + i][col] != player) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    count++;
            }
        }

        return count;
    }

    /**
     * Count patterns that cross quadrant boundaries
     */
    private int countCrossBoundary(char player, int length, int midRow, int midCol) {
        // Check patterns crossing the vertical dividing line (midCol)
        int verticalCross = 0;
        for (int row = 0; row < board.getRows(); row++) {
            for (int col = midCol - length + 1; col < midCol + 1; col++) {
                if (col < 0 || col > board.getCols() - length)
                    continue;

                boolean match = true;
                for (int k = 0; k < length; k++) {
                    if (board.getBoard()[row][col + k] != player) {
                        match = false;
                        break;
                    }
                }
                if (match)
                    verticalCross++;
            }
        }

        // Check patterns crossing the horizontal dividing line (midRow)
        int horizontalCross = 0;
        for (int col = 0; col < board.getCols(); col++) {
            for (int row = midRow - length + 1; row < midRow + 1; row++) {
                if (row < 0 || row > board.getRows() - length)
                    continue;

                boolean match = true;
                for (int k = 0; k < length; k++) {
                    if (board.getBoard()[row + k][col] != player) {
                        match = false;
                        break;
                    }
                }
                if (match)
                    horizontalCross++;
            }
        }

        // Note: Diagonal boundary crossing is more complex and less critical for this
        // specific
        // quadrant split implementation, but could be added for 100% completeness.
        // For now, primary horizontal/vertical splits are covered.

        return verticalCross + horizontalCross;
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 5: FIND VALID MOVES
    // Divides columns into halves recursively
    // Time Complexity: O(C log C)
    // =====================================================================

    /**
     * Find all valid moves using Divide & Conquer
     * Divides columns into halves
     */
    public List<Integer> findValidMovesDnC(int start, int end) {
        List<Integer> validMoves = new ArrayList<>();

        // Base case
        if (start > end) {
            return validMoves;
        }

        if (start == end) {
            if (board.isValidMove(start)) {
                validMoves.add(start);
            }
            return validMoves;
        }

        // Divide
        int mid = (start + end) / 2;

        // Conquer both halves
        List<Integer> leftMoves = findValidMovesDnC(start, mid);
        List<Integer> rightMoves = findValidMovesDnC(mid + 1, end);

        // Combine results
        validMoves.addAll(leftMoves);
        validMoves.addAll(rightMoves);

        return validMoves;
    }

    /**
     * Wrapper to find all valid moves
     */
    public List<Integer> getAllValidMoves() {
        return findValidMovesDnC(0, board.getCols() - 1);
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 6: RECURSIVE WINNER DETECTION
    // Uses recursion to check win in row segments
    // Time Complexity: O(C log C) per row
    // =====================================================================

    /**
     * Check if player won using recursive D&C approach
     */
    public boolean checkWinRecursive(char player) {
        char[][] grid = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();

        return checkRowsRecursive(grid, player, 0, rows, cols) ||
                checkColsRecursive(grid, player, 0, rows, cols);
    }

    private boolean checkRowsRecursive(char[][] grid, char player, int row, int rows, int cols) {
        if (row >= rows)
            return false;
        if (checkRowWinDnC(grid[row], player, 0, cols - 1))
            return true;
        return checkRowsRecursive(grid, player, row + 1, rows, cols);
    }

    private boolean checkColsRecursive(char[][] grid, char player, int col, int rows, int cols) {
        if (col >= cols)
            return false;

        char[] column = new char[rows];
        fillColumnRecursive(grid, column, col, 0, rows);

        if (checkRowWinDnC(column, player, 0, rows - 1))
            return true;
        return checkColsRecursive(grid, player, col + 1, rows, cols);
    }

    private void fillColumnRecursive(char[][] grid, char[] column, int col, int row, int rows) {
        if (row >= rows)
            return;
        column[row] = grid[row][col];
        fillColumnRecursive(grid, column, col, row + 1, rows);
    }

    /**
     * Recursively check if a row/column segment contains 4-in-a-row
     */
    private boolean checkRowWinDnC(char[] array, char player, int start, int end) {
        // Base case: segment too small
        if (end - start < 3) {
            return false;
        }

        // Check current segment for 4-in-a-row
        for (int i = start; i <= end - 3; i++) {
            if (array[i] == player &&
                    array[i + 1] == player &&
                    array[i + 2] == player &&
                    array[i + 3] == player) {
                return true;
            }
        }

        // Divide into two halves
        int mid = (start + end) / 2;

        // Conquer: Check both halves
        boolean leftWin = checkRowWinDnC(array, player, start, mid);
        boolean rightWin = checkRowWinDnC(array, player, mid + 1, end);

        // Also check middle region (crossing boundary)
        boolean middleWin = false;
        if (mid - 2 >= start && mid + 3 <= end) {
            for (int i = mid - 2; i <= mid; i++) {
                if (i >= start && i + 3 <= end) {
                    if (array[i] == player &&
                            array[i + 1] == player &&
                            array[i + 2] == player &&
                            array[i + 3] == player) {
                        middleWin = true;
                        break;
                    }
                }
            }
        }

        return leftWin || rightWin || middleWin;
    }

    // =====================================================================
    // 📊 UTILITY: Count connected pieces (standard implementation)
    // =====================================================================

    /**
     * Standard count connected (for comparison with D&C version)
     */
    public int countConnected(char player, int length) {
        int count = 0;
        char[][] grid = board.getBoard();
        int rows = board.getRows();
        int cols = board.getCols();

        // Horizontal
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col <= cols - length; col++) {
                boolean valid = true;
                for (int i = 0; i < length; i++) {
                    if (grid[row][col + i] != player) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    count++;
            }
        }

        // Vertical
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row <= rows - length; row++) {
                boolean valid = true;
                for (int i = 0; i < length; i++) {
                    if (grid[row + i][col] != player) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    count++;
            }
        }

        // Diagonal down
        for (int row = 0; row <= rows - length; row++) {
            for (int col = 0; col <= cols - length; col++) {
                boolean valid = true;
                for (int i = 0; i < length; i++) {
                    if (grid[row + i][col + i] != player) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    count++;
            }
        }

        // Diagonal up
        for (int row = length - 1; row < rows; row++) {
            for (int col = 0; col <= cols - length; col++) {
                boolean valid = true;
                for (int i = 0; i < length; i++) {
                    if (grid[row - i][col + i] != player) {
                        valid = false;
                        break;
                    }
                }
                if (valid)
                    count++;
            }
        }

        return count;
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 7: STATE-SPACE DIVIDE & CONQUER (Minimax Foundation)
    // Explores future game states by dividing into child moves
    // Time Complexity: O(b^d) where b = branching factor, d = depth
    // =====================================================================

    /**
     * State-Space D&C: Explores all future game states
     * Divide: For each valid move, create a child state
     * Conquer: Recursively evaluate each child state
     * Combine: Return max score for AI, min for opponent
     */
    public int stateSpaceDnC(char player, int depth, boolean isMaximizing) {
        char opponent = (player == 'R') ? 'Y' : 'R';

        // Base Case: Game over or depth limit
        if (checkWin(player))
            return isMaximizing ? 10000 : -10000;
        if (checkWin(opponent))
            return isMaximizing ? -10000 : 10000;
        if (board.isBoardFull() || depth == 0) {
            return evaluatePositionDnC(player);
        }

        List<Integer> validMoves = findValidMovesDnC(0, board.getCols() - 1);

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int col : validMoves) {
                board.insertDisc(col, player);
                int eval = stateSpaceDnC(player, depth - 1, false);
                board.removeDisc(col);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col : validMoves) {
                board.insertDisc(col, opponent);
                int eval = stateSpaceDnC(player, depth - 1, true);
                board.removeDisc(col);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 8: DEPTH-LIMITED MINIMAX WITH ALPHA-BETA PRUNING
    // Optimized game tree search that prunes unneeded branches
    // Time Complexity: O(b^(d/2)) best case with pruning
    // =====================================================================

    /**
     * Minimax with Alpha-Beta Pruning
     * Divide: Current depth -> child depths
     * Conquer: Evaluate each child, prune where possible
     * Combine: Propagate max/min scores upward
     */
    public int minimaxAlphaBeta(char player, int depth, int alpha, int beta, boolean isMaximizing) {
        char opponent = (player == 'R') ? 'Y' : 'R';

        // Base Case: Terminal state or depth limit
        if (checkWin(player))
            return 10000 + depth; // Prefer faster wins
        if (checkWin(opponent))
            return -10000 - depth;
        if (board.isBoardFull() || depth == 0) {
            return evaluatePositionDnC(player);
        }

        List<Integer> validMoves = findValidMovesDnC(0, board.getCols() - 1);

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int col : validMoves) {
                board.insertDisc(col, player);
                int eval = minimaxAlphaBeta(player, depth - 1, alpha, beta, false);
                board.removeDisc(col);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha)
                    break; // Beta cut-off (Pruning!)
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col : validMoves) {
                board.insertDisc(col, opponent);
                int eval = minimaxAlphaBeta(player, depth - 1, alpha, beta, true);
                board.removeDisc(col);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break; // Alpha cut-off (Pruning!)
            }
            return minEval;
        }
    }

    /**
     * Find best move using Minimax with Alpha-Beta Pruning
     * Used for Hard difficulty
     */
    public int findBestMoveHard(char player, int depth) {
        char opponent = (player == 'R') ? 'Y' : 'R';

        // Check for immediate win/block first (Greedy shortcut)
        int winMove = findImmediateWin(player);
        if (winMove != -1)
            return winMove;
        int blockMove = findImmediateWin(opponent);
        if (blockMove != -1)
            return blockMove;

        List<Integer> validMoves = findValidMovesDnC(0, board.getCols() - 1);
        int bestMove = validMoves.isEmpty() ? -1 : validMoves.get(0);
        int maxScore = Integer.MIN_VALUE;

        for (int col : validMoves) {
            board.insertDisc(col, player);
            int score = minimaxAlphaBeta(player, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.removeDisc(col);

            if (score > maxScore) {
                maxScore = score;
                bestMove = col;
            }
        }

        return bestMove;
    }

    // =====================================================================
    // ✅ D&C ALGORITHM 9: GREEDY-GUIDED RECURSIVE SEARCH
    // Combines Greedy Heuristics (Algo 1/7) with Recursive Minimax (Algo 8)
    // Uses "Divide & Conquer" by splitting moves and ordering them by potential
    // Time Complexity: O(b^(d/2)) - Optimized due to better move ordering
    // =====================================================================

    /**
     * Find best move using Greedy-Guided Recursive Search
     * Used for Expert difficulty
     */
    public int findBestMoveGreedyRecursive(char player, int depth) {
        char opponent = (player == 'R') ? 'Y' : 'R';

        // Check for immediate win/block first
        int winMove = findImmediateWin(player);
        if (winMove != -1)
            return winMove;
        int blockMove = findImmediateWin(opponent);
        if (blockMove != -1)
            return blockMove;

        // Get valid moves
        List<Integer> validMoves = findValidMovesDnC(0, board.getCols() - 1);
        if (validMoves.isEmpty())
            return -1;

        // 🚀 KEY INNOVATION: Sort moves based on Greedy Heuristic
        // This optimizes the "Divide" step by processing best moves first
        validMoves.sort((col1, col2) -> {
            int score1 = evaluateGreedy(player, col1);
            int score2 = evaluateGreedy(player, col2);
            return Integer.compare(score2, score1); // Descending order
        });

        int bestMove = validMoves.get(0);
        int maxScore = Integer.MIN_VALUE;

        // Conquer: Evaluate sorted moves recursively
        for (int col : validMoves) {
            board.insertDisc(col, player);
            int score = minimaxGreedyRecursive(player, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.removeDisc(col);

            if (score > maxScore) {
                maxScore = score;
                bestMove = col;
            }
        }

        return bestMove;
    }

    /**
     * Recursive Minimax with Alpha-Beta Pruning AND Greedy Move Ordering
     */
    private int minimaxGreedyRecursive(char player, int depth, int alpha, int beta, boolean isMaximizing) {
        char opponent = (player == 'R') ? 'Y' : 'R';

        // Base Case
        if (checkWin(player))
            return 10000 + depth;
        if (checkWin(opponent))
            return -10000 - depth;
        if (board.isBoardFull() || depth == 0) {
            return evaluatePositionDnC(player);
        }

        List<Integer> validMoves = findValidMovesDnC(0, board.getCols() - 1);

        // 🚀 Move Ordering: Sort moves to improve pruning efficiency
        // We use the current player's greedy score to guess the best moves
        char currentPlayer = isMaximizing ? player : opponent;
        validMoves.sort((col1, col2) -> {
            int score1 = evaluateGreedy(currentPlayer, col1);
            int score2 = evaluateGreedy(currentPlayer, col2);
            return Integer.compare(score2, score1);
        });

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (int col : validMoves) {
                board.insertDisc(col, player);
                int eval = minimaxGreedyRecursive(player, depth - 1, alpha, beta, false);
                board.removeDisc(col);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha)
                    break;
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (int col : validMoves) {
                board.insertDisc(col, opponent);
                int eval = minimaxGreedyRecursive(player, depth - 1, alpha, beta, true);
                board.removeDisc(col);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha)
                    break;
            }
            return minEval;
        }
    }

    // =====================================================================
    // 🎯 DEMO: Print algorithm information
    // =====================================================================

    public void printAlgorithmInfo() {
        System.out.println("\n========================================");
        System.out.println("📚 DIVIDE & CONQUER ALGORITHMS IN USE");
        System.out.println("========================================\n");

        System.out.println("✅ Algorithm 1: Win Detection (4 Directions)");
        System.out.println("   - Divides into: Horizontal, Vertical, Diagonal↘, Diagonal↗");
        System.out.println("   - Time: O(R×C)\n");

        System.out.println("✅ Algorithm 2: Best Move Finding (Column Split)");
        System.out.println("   - Divides columns into left/right halves recursively");
        System.out.println("   - Time: O(C log C)\n");

        System.out.println("✅ Algorithm 3: Board Evaluation (Top/Bottom Split)");
        System.out.println("   - Divides board into top/bottom halves");
        System.out.println("   - Time: O(R×C)\n");

        System.out.println("✅ Algorithm 4: Connected Count (Quadrant Split)");
        System.out.println("   - Divides board into 4 quadrants + cross-boundary");
        System.out.println("   - Time: O(R×C)\n");

        System.out.println("✅ Algorithm 5: Find Valid Moves (Column Range)");
        System.out.println("   - Divides columns recursively");
        System.out.println("   - Time: O(C log C)\n");

        System.out.println("✅ Algorithm 6: Recursive Winner Detection");
        System.out.println("   - Recursive segment checking");
        System.out.println("   - Time: O(C log C) per row\n");

        System.out.println("✅ Algorithm 7: State-Space D&C (Minimax Foundation)");
        System.out.println("   - Divides into child states for each move");
        System.out.println("   - Time: O(b^d) where b=branching, d=depth\n");

        System.out.println("✅ Algorithm 8: Depth-Limited Minimax + Alpha-Beta");
        System.out.println("   - Optimized tree search with pruning");
        System.out.println("   - Time: O(b^(d/2)) best case\n");

        System.out.println("✅ Algorithm 9: Greedy-Guided Recursive Search");
        System.out.println("   - Divides moves & orders them by Greedy Heuristic");
        System.out.println("   - Conquers using Recursive Minimax");
        System.out.println("   - Time: Optimized O(b^(d/2)) due to Ordering\n");

        System.out.println("========================================\n");
    }
}
