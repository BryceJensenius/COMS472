package edu.iastate.cs472.proj2;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * An object of this class holds data about a game of checkers.
 * It knows what kind of piece is on each square of the checkerboard.
 * Note that RED moves "up" the board (i.e. row number decreases)
 * while BLACK moves "down" the board (i.e. row number increases).
 * Methods are provided to return lists of available legal moves.
 */
public class CheckersData {

  /*  The following constants represent the possible contents of a square
      on the board.  The constants RED and BLACK also represent players
      in the game. */

    static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;


    int[][] board;  // board[r][c] is the contents of row r, column c.


    /**
     * Constructor.  Create the board and set it up for a new game.
     */
    CheckersData() {
        board = new int[8][8];
        setUpGame();
    }

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            int[] row = board[i];
            sb.append(8 - i).append(" ");
            for (int n : row) {
                if (n == 0) {
                    sb.append(" ");
                } else if (n == 1) {
                    sb.append(ANSI_RED + "R" + ANSI_RESET);
                } else if (n == 2) {
                    sb.append(ANSI_RED + "K" + ANSI_RESET);
                } else if (n == 3) {
                    sb.append(ANSI_YELLOW + "B" + ANSI_RESET);
                } else if (n == 4) {
                    sb.append(ANSI_YELLOW + "K" + ANSI_RESET);
                }
                sb.append(" ");
            }
            sb.append(System.lineSeparator());
        }
        sb.append("  a b c d e f g h");

        return sb.toString();
    }

    /**
     * Set up the board with checkers in position for the beginning
     * of a game.  Note that checkers can only be found in squares
     * that satisfy  row % 2 == col % 2.  At the start of the game,
     * all such squares in the first three rows contain black squares
     * and all such squares in the last three rows contain red squares.
     */
    void setUpGame() {
        board = new int[8][8]; // Create new board to reset all pieces to empty
        for(int row = 0; row < 3; row++) {
        	for(int col = 0; col < 8; col++) {
        		if(row % 2 == col % 2) {
        			board[row][col] = BLACK; // Top 3 rows
        			board[row+5][7-col] = RED; // Bottom 3 rows in opposite pattern
        		}
        	}
        }
    }


    /**
     * Return the contents of the square in the specified row and column.
     */
    int pieceAt(int row, int col) {
        return board[row][col];
    }


    /**
     * Make the specified move.  It is assumed that move
     * is non-null and that the move it represents is legal.
     *
     * Make a single move or a sequence of jumps
     * recorded in rows and cols.
     *
     */
    void makeMove(CheckersMove move) {
        int l = move.rows.size();
        for(int i = 0; i < l-1; i++)
            makeMove(move.rows.get(i), move.cols.get(i), move.rows.get(i+1), move.cols.get(i+1));
    }


    /**
     * Make the move from (fromRow,fromCol) to (toRow,toCol).  It is
     * assumed that this move is legal.  If the move is a jump, the
     * jumped piece is removed from the board.  If a piece moves to
     * the last row on the opponent's side of the board, the
     * piece becomes a king.
     *
     * @param fromRow row index of the from square
     * @param fromCol column index of the from square
     * @param toRow   row index of the to square
     * @param toCol   column index of the to square
     */
    void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
    	// Update the board for the given move. You need to take care of the following situations:
        // 1. move the piece from (fromRow,fromCol) to (toRow,toCol)
        // 2. if this move is a jump, remove the captured piece
        // 3. if the piece moves into the kings row on the opponent's side of the board, crowned it as a king
    	int player = board[fromRow][fromCol];
    	if(player == RED && toRow == 0) { // Red becomes king, at top row
    		player = RED_KING;
    	} else if (player == BLACK && toRow == 7) { // Black Becomes king, at bottom row
    		player = BLACK_KING;
    	}
    	
    	if((fromRow - toRow == 2 || fromRow - toRow == -2)) { // Move was a jump 
    		board[(fromRow + toRow) / 2][(fromCol + toCol) / 2] = 0; //  Set jumped spot to empty
    	}
    	board[toRow][toCol] = player; // Set player at new position
    	board[fromRow][fromCol] = 0; // Set old position to empty
    }

    /**
     * Return an array containing all the legal CheckersMoves
     * for the specified player on the current board.  If the player
     * has no legal moves, null is returned.  The value of player
     * should be one of the constants RED or BLACK; if not, null
     * is returned.  If the returned value is non-null, it consists
     * entirely of jump moves or entirely of regular moves, since
     * if the player can jump, only jumps are legal moves.
     *
     * @param player color of the player, RED or BLACK
     */
    CheckersMove[] getLegalMoves(int player) { // **TODO** I took a screen show of a null pointer exception that came up
        ArrayList<CheckersMove> moves = new ArrayList<>();
        boolean jumpMoveAvailable = false; // Whether any pieces can jump
        for(int row = 0; row < 8; row++) {
        	for(int col = 0; col < 8; col++) {
        		if(isPlayerPiece(player, row, col)) { // This is a piece of the current player
        			ArrayList<CheckersMove> piecesMoves = getPiecesMoves(row, col);
        			if(piecesMoves == null || piecesMoves.size() == 0) continue; // No moves for this piece so skip
        			if(piecesMoves.get(0).isJump()) { // If this is a jump move
        				if(!jumpMoveAvailable) {
        					jumpMoveAvailable = true;
        					moves.clear();; // Discard the non-jumping moves when we find the first jump
        				}
        				moves.addAll(piecesMoves);
        			} else if(!jumpMoveAvailable) { // Only add regular moves if no jump moves have been found
        				moves.addAll(piecesMoves);
        			}
        		}
        	}
        }

        return moves.size() == 0 ? null : moves.toArray(new CheckersMove[moves.size()]);
    }

    /*
     * Return true if the piace at (row, col) belongs to the player given
     */
    private boolean isPlayerPiece(int player, int row, int col){
        return board[row][col] == player || (player == RED && board[row][col] == RED_KING) || (player == BLACK && board[row][col] == BLACK_KING);
    }
    
    /*
     * Add the legal moves for the piece at (row, col) to the moves list
     */
    private ArrayList<CheckersMove> getPiecesMoves(int row, int col) {
    	ArrayList<CheckersMove> moves = new ArrayList<>();
        int player;
        if(board[row][col] == RED || board[row][col] == RED_KING) { // Red piece
            player = RED;
        } else {
            player = BLACK;
        }
        // Add all the valid jump moves
        CheckersMove[] m = getLegalJumpsFrom(player, row, col);
        if(m != null && m.length != 0){
            moves.addAll(Arrays.asList(m));
            return moves; // If we have a jump moves, we must take one
        }
    	if(player == RED) { // Red piece
    		if(isValidPosition(row-1, col-1) && board[row-1][col-1] == EMPTY) { // Regular Move Up Left
                moves.add(new CheckersMove(row, col, row-1, col-1));
    		}
            if(isValidPosition(row-1, col+1) && board[row-1][col+1] == EMPTY) { // Regular move Up right
    			moves.add(new CheckersMove(row, col, row-1, col+1));
    		}
            if(board[row][col] == RED_KING) { // Red King can also move up
                if(isValidPosition(row+1, col-1) && board[row+1][col-1] == EMPTY) { // Regular Move Down Left
                    moves.add(new CheckersMove(row, col, row+1, col-1));
        		}
                if(isValidPosition(row+1, col+1) && board[row+1][col+1] == EMPTY) { // Regular move Down right
        			moves.add(new CheckersMove(row, col, row+1, col+1));
        		}
            }
    	} else {
            player = BLACK;
            if(isValidPosition(row+1, col-1) && board[row+1][col-1] == EMPTY) { // Regular Move Down Left
                moves.add(new CheckersMove(row, col, row+1, col-1));
    		}
            if(isValidPosition(row+1, col+1) && board[row+1][col+1] == EMPTY) { // Regular move down right
    			moves.add(new CheckersMove(row, col, row+1, col+1));
    		}
            if(board[row][col] == BLACK_KING) { // Black King can also move up
                if(isValidPosition(row-1, col-1) && board[row-1][col-1] == EMPTY) { // Regular Move Up Left
                    moves.add(new CheckersMove(row, col, row-1, col-1));
        		}
                if(isValidPosition(row-1, col+1) && board[row-1][col+1] == EMPTY) { // Regular move up right
        			moves.add(new CheckersMove(row, col, row-1, col+1));
        		}
            }
        }
    	return moves;
    }

    /*
     * Return true if the given position is within the bounds of the board
     */
    private boolean isValidPosition(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    /**
     * Return a list of the legal jumps that the specified player can
     * make starting from the specified row and column.  If no such
     * jumps are possible, null is returned.  The logic is similar
     * to the logic of the getLegalMoves() method.
     *
     * Note that each CheckerMove may contain multiple jumps. 
     * Each move returned in the array represents a sequence of jumps 
     * until no further jump is allowed.
     *
     * @param player The player of the current jump, either RED or BLACK.
     * @param row    row index of the start square.
     * @param col    col index of the start square.
     */
    CheckersMove[] getLegalJumpsFrom(int player, int row, int col) {
        ArrayList<CheckersMove> moves = new ArrayList<>();
        int opponent1 = (player == RED) ? BLACK : RED; // Possible opponents to jump over
        int opponent2 = (player == RED) ? BLACK_KING : RED_KING;

        // Possible jump directions this player can jump
        int[][] directions;
        int playerVal = board[row][col];
        if(playerVal == RED_KING || playerVal == BLACK_KING) {
            directions = new int[][]{{1, -1}, {1, 1}, {-1, -1}, {-1, 1}}; // All 4 directions for kings
        } else if(player == RED) {
            directions = new int[][]{{-1, -1}, {-1, 1}}; // Up left and Up right for red
        } else {
            directions = new int[][]{{1, -1}, {1, 1}}; // Down left and Down right for black
        }

        for(int[] dir : directions) {
            int midRow = row + dir[0]; // mid is the piece we are jumping over
            int midCol = col + dir[1];
            int toRow = row + 2 * dir[0]; // The position we land at
            int toCol = col + 2 * dir[1];

            if(isValidPosition(toRow, toCol) && board[toRow][toCol] == EMPTY && // Landing spot is valid and empty
               (board[midRow][midCol] == opponent1 || board[midRow][midCol] == opponent2)) { // There is actually an opponent to jump over at mid, king or regular piece
                CheckersMove jumpMove = new CheckersMove(row, col, toRow, toCol);
                // Set values after jump so we can continue recursively from there
                board[row][col] = 0; // Temporarily move player to new position to check further jumps
                board[toRow][toCol] = playerVal; // Move player to new location
                int opponent = board[midRow][midCol]; // Save what the opponent was
                board[midRow][midCol] = 0; // Remove the opponent
                CheckersMove[] furtherJumps = getLegalJumpsFrom(player, toRow, toCol); // Recursively check for further jumps
                // Reset value back to what they were before the jump
                board[row][col] = playerVal;
                board[toRow][toCol] = 0;
                board[midRow][midCol] = opponent;
                if(furtherJumps != null) {
                    for(CheckersMove furtherJump : furtherJumps) {
                        // Add the initial jump to the front of the further jumps
                        CheckersMove fullJump = jumpMove.clone();
                        // Add all the further jumps, discluding the start which was already added
                        fullJump.rows.addAll(furtherJump.rows.subList(1, furtherJump.rows.size()));
                        fullJump.cols.addAll(furtherJump.cols.subList(1, furtherJump.cols.size()));
                        moves.add(fullJump);
                    }
                }else {
                    moves.add(jumpMove);
                }
            }
        }
        return moves.size() == 0 ? null : moves.toArray(new CheckersMove[moves.size()]);
    }
}
