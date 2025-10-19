package edu.iastate.cs472.proj2;

/**
 * 
 * @author Bryce Jensenius
 *
 */

/**
 * This class is to be extended by the classes AlphaBetaSearch and MonteCarloTreeSearch.
 */
public abstract class AdversarialSearch {

    static final int // For Passing in Player Information
        EMPTY = 0,
        RED = 1,
        RED_KING = 2,
        BLACK = 3,
        BLACK_KING = 4;

    protected CheckersData board;

    // An instance of this class will be created in the Checkers.Board
    // It would be better to keep the default constructor.

    protected void setCheckersData(CheckersData board) {
        this.board = board;
    }
    
    /** 
     * 
     * @return an array of valid moves
     */
    protected CheckersMove[] legalMoves() {

    	return board.getLegalMoves(BLACK); // Get legal moves for the black player (the computer)
    }
	
    /**
     * Return a move returned from either the alpha-beta search or the Monte Carlo tree search.
     * 
     * @param legalMoves
     * @return CheckersMove 
     */
    public abstract CheckersMove makeMove(CheckersMove[] legalMoves);
}
