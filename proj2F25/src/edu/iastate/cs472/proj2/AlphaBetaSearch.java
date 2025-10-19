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

        // TODO 
        
        // Return the move for the current state.
        // Here, we simply return the first legal move for demonstration.
        return legalMoves[0];
        // CheckersData boardData = new CheckersData();
        // boardData.board = board.board;
        // int alpha = Integer.MIN_VALUE;
        // int beta = Integer.MAX_VALUE;
        // CheckersMove bestMove = null;
        // for (CheckersMove move : legalMoves) {
        //     // Make the move on a copy of the board
        //     boardData.makeMove(move);
        //     int value = minValue(newBoard, alpha, beta, 3); // depth can be adjusted
        //     if (value > alpha) {
        //         alpha = value;
        //         bestMove = move;
        //     }
        // }
    }
    
    // TODO
    // Implement your helper methods here.

}
