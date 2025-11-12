package edu.iastate.cs472.proj2;

/**
 * 
 * @author Bryce Jensenius
 *
 */


/**
 * This class implements the Alpha-Beta pruning algorithm to find the best 
 * move at current state.
*/
public class AlphaBetaSearch extends AdversarialSearch {
	int maxDepth = 13; // Max depth or moves to explore before using heuristic
	
    /**
     * The input parameter legalMoves contains all the possible moves.
     * It contains four integers:  fromRow, fromCol, toRow, toCol
     * which represents a move from (fromRow, fromCol) to (toRow, toCol).
     * It also provides a utility method `isJump` to see whether this
     * move is a jump or a simple move.
     * 
     * Each legalMove in the input now contains a single move
     * or a sequence of jumps: (rows[0], cols[0]) -> (rows[1], cols[1]) ->
     * (rows[2], cols[2]).
     *
     * @param legalMoves All the legal moves for the agent at current step.
     */
    public CheckersMove makeMove(CheckersMove[] legalMoves) {
        // The checker board state can be obtained from this.board,
        // which is a int 2D array. The numbers in the `board` are
        // defined as
        // 0 - empty square,
        // 1 - red man
        // 2 - red king
        // 3 - black man
        // 4 - black king
        System.out.println(board);
        System.out.println();
  
        // Here, we simply return the first legal move for demonstration.
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        CheckersMove bestMove = null;
        
        for (CheckersMove move : legalMoves) { // Evaluate for every move
            CheckersData newBoardData = new CheckersData();
            newBoardData.board = copyBoard(this.board.board);
            newBoardData.makeMove(move); // Perform this initial move
            int value = minValue(newBoardData, alpha, beta, 1); // Opponent's turn next (MIN)
            if (value > alpha) { // Keep the largest alpha found
                alpha = value;
                bestMove = move;
                System.out.println("Alpha " + alpha);
            }
        }
        return bestMove; // Return the best of the moves available
    }
    
    // Max node in the Search, gives the max value
    private int maxValue(CheckersData boardData, int a, int b, int depth){
    	CheckersMove[] legalMoves = boardData.getLegalMoves(BLACK);
    	if(!bothPlayersHavePieces(boardData.board) || legalMoves == null) return utility(boardData.board); // Terminal State, return utility
    	if(depth >= maxDepth) return evaluateBoard(boardData.board); // Max depth, return heuristic value
    	int v = Integer.MIN_VALUE;
    	for (CheckersMove move : legalMoves) { // Evaluate for every move
            CheckersData newBoardData = new CheckersData();
            newBoardData.board = copyBoard(boardData.board);
            newBoardData.makeMove(move); // Perform this initial move
            v = Math.max(v, minValue(newBoardData, a, b, depth + 1)); // Keep max of recursive MIN calls
            if(v >= b) return v; // Prune
            a = Math.max(a, v); // Update alpha if new max is larger
        }
        return v; // Return the highest value obtained
    }
    
    // Min node in the Search, gives the min value
    private int minValue(CheckersData boardData, int a, int b, int depth){
    	CheckersMove[] legalMoves = boardData.getLegalMoves(RED);
    	if(!bothPlayersHavePieces(boardData.board) || legalMoves == null) return utility(boardData.board); // Terminal State, return utility
    	if(depth >= maxDepth) return evaluateBoard(boardData.board); // Max depth, return heuristic value
    	int v = Integer.MAX_VALUE;
    	for (CheckersMove move : legalMoves) { // Evaluate for every move
            CheckersData newBoardData = new CheckersData();
            newBoardData.board = copyBoard(boardData.board);
            newBoardData.makeMove(move); // Perform this initial move
            v = Math.min(v, maxValue(newBoardData, a, b, depth + 1)); // Keep max of recursive MIN calls
            if(v <= a) return v; // Prune
            b = Math.min(b, v); // Update beta if new min is larger
        }
        return v; // Return the smallest value obtained
    }

    /*
     * Creates a copy of the inputted board 2D array
     */
    private int[][] copyBoard(int[][] original){
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone(); // Deep copy of each row
        }
        return copy;
    }
    
    /*
     * Check if both players still have pieces in the inputed board
     * Returns true if both players have pieces
     * returns false otherwise
     */
    private boolean bothPlayersHavePieces(int[][] board) {
    	if(board == null || board.length == 0) throw new RuntimeException("Cannot check terminal status of an empty board");
    	
		boolean foundRed = false;
		boolean foundBlack = false;
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				int cur = board[i][j];
				if(!foundRed && (cur == RED || cur == RED_KING)) { // Found a red piece
					foundRed = true;
					if(foundBlack) return true; // Found both, exit early
				}else if(!foundBlack && (cur == BLACK || cur == BLACK_KING)) { // Found a black piece
					foundBlack = true;
					if(foundRed) return true; // Found both, exit early
				}
			}
		}
		return foundRed && foundBlack; // True if we found pieces for both players
    }
    
    /*
     * If called, board is assumed to be in a terminal state
     * Returns utility for the board
     * 40 if the agent wins
     * -40 if the agent loses
     * 0 if there is a draw
     * evaluateBoard returns value from -39 to 39 so this will always be chosen over a heuristic one
     */
    private int utility(int[][] board) {
        if (bothPlayersHavePieces(board)) return 0; // **TODO** Winner is the one that can't move or draw?

        boolean redAlive = false, blackAlive = false;
        for (int[] row : board)
            for (int cell : row) {
                if (cell == RED || cell == RED_KING) redAlive = true;
                if (cell == BLACK || cell == BLACK_KING) blackAlive = true;
            }

        if (blackAlive && !redAlive) return 40;   // Win
        if (redAlive && !blackAlive) return -40;  // Loss
        return 0;                                   // Draw
    }
    
    /*
     * Heuristic for a Checkers Board configuration
     * 2 points are given for each regular checkers piece
     * 3 points are given for each king piece
     * Positive for the agent (BLACK), negative for the player (RED)
     * returns score
     * returns score from -39 to 39
     */
    public int evaluateBoard(int[][] board) {
		int score = 0;
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				int cur = board[i][j];
				switch(cur) {
    				case RED: // Regular pieces count as two points
    					score -= 2;
    					break;
    				case BLACK:
    					score += 2;
    					break;
    				case RED_KING: // Kings count as 3 points
    					score -= 3;
    					break;
    				case BLACK_KING:
    					score += 3;
    					break;
				}
			}
		}
		return Math.max(-39, Math.min(39, score));
	}
}
