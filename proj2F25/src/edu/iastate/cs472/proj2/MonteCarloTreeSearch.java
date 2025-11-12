package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * 
 * @author Bryce Jensenius
 *
 */

/**
 * This class implements the Monte Carlo tree search method to find the best
 * move at the current state using UCB1 formula with C = √2.
 * 
 * The algorithm follows these steps:
 * 1. Selection: Use UCB1 to select best nodes to explore
 * 2. Expansion: Create child nodes for unexplored moves
 * 3. Simulation: Randomly play out the game from the expanded node
 * 4. Backpropagation: Update statistics for nodes on the path from root
 */
public class MonteCarloTreeSearch extends AdversarialSearch {

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
    public CheckersMove makeMove(CheckersMove[] legalMoves) { // *TODO* What is the purpose of legal Moves?
    	// *TODO* do we perform the search for every legal Move, or perform the search to find which is the best to take
        if (legalMoves == null || legalMoves.length == 0) {
            return null;
        }

        MCTree<CheckersData> tree = new MCTree<>();
        tree.root = new MCNode<>(copyBoard(board), BLACK); // Set root as the starting board, BLACK since it is the agents move

        int iterations = 4000; // Number of iterations to perform in MonteCarlo Tree Search
        for (int i = 0; i < iterations; i++) {
            ArrayList<MCNode<CheckersData>> path = new ArrayList<>(); // Path taken to be used in back propagation
            MCNode<CheckersData> selectedNode = selection(tree.root, path); // Select unexpanded node
            CheckersMove[] curLegalMoves = selectedNode.data.getLegalMoves(selectedNode.player); // Get the legal moves from the selected node
            MCNode<CheckersData> expandedNode = (curLegalMoves != null) ? expansion(selectedNode, curLegalMoves)
					: selectedNode; // Add all the children to MCTree, returning a random one for simulation
            if (expandedNode != selectedNode) { // Could be a leaf node with no children so we don't add it again
                path.add(expandedNode); // include simulation node in backprop path
            }
            double result = simulation(expandedNode); // Simulate a random game playout from the selected node, returning the Utility on completion
            backpropagation(path, result); // Propagate the result up
        }
        
        // Choose the best move based on most playouts
        MCNode<CheckersData> bestChild = null;
        int maxVisits = -1;
        for (MCNode<CheckersData> child : tree.root.children) {
            if (child.playouts > maxVisits) { // Update with highest number of playouts
                maxVisits = child.playouts;
                bestChild = child;
            }
        }
        
        return bestChild.move; // Return the move which led to this best child
    }
    
    /*
     * Traverse through the Monte Carlo Tree, picking the child with the highest UCB value
     * Returns the first unexpanded node found on this path
     * Updates path with a list of nodes we took to arrive as the selected node
     */
    private MCNode<CheckersData> selection(MCNode<CheckersData> node, ArrayList<MCNode<CheckersData>> path) {
        path.add(node); // First add the root
        
        while (!node.children.isEmpty()) { // Traverse until we reach a leaf or an unexpanded child
            MCNode<CheckersData> selectedChild = null;
            double bestUCB = Double.NEGATIVE_INFINITY;
            
            for (MCNode<CheckersData> child : node.children) { // Check UCB of every child
                if (child.playouts == 0) {
                    path.add(child);
                    return child; // Select unexpanded nodes first
                }
                
                // UCB formula with C = sqrt(2)
                double exploitation = child.wins / (double) child.playouts; // average result for the child
                double exploration = Math.sqrt(Math.log(Math.max(1, node.playouts)) / child.playouts);
                double ucb = exploitation + Math.sqrt(2) * exploration;
                if (ucb > bestUCB) { // Update best child if we found a higher UCB value
                    bestUCB = ucb;
                    selectedChild = child;
                }
            }
                        
            node = selectedChild; // Traverse down the tree to the child with the highest UCB value
            path.add(node); // Add that child to the path we took
        }
        
        return node; // Return the first unexpanded node found while following the path of highest UCB values
    }
    
    /*
     * Input of a selected node to expand and the legal moves from that node
     * Add all the children of the inputed node to the MCTree
     * Pick one newly added node randomly to return for simulation
     */
    private MCNode<CheckersData> expansion(MCNode<CheckersData> node, CheckersMove[] legalMoves) {
        // Create a new board state for each legal move
    	int childPlayer = (node.player == RED) ? BLACK : RED; // The opponent to node.player is the child nodes
        for (CheckersMove move : legalMoves) {
            CheckersData newBoard = copyBoard(node.data);
            newBoard.makeMove(move);       
            MCNode<CheckersData> child = new MCNode<>(newBoard, childPlayer); // Create a new node for this specific move
            child.move = move; // Set the move which resulted in this child node
            node.addChild(child); // Add each node for each move to the MCTree
        }
        
        // Return a randomly selected child node
        if (!node.children.isEmpty()) {
            return node.children.get((int)(Math.random() * node.children.size())); // Pick a random child for simulation
        }
        return node; // No children so return this node for simulation
    }
    
    /*
     * Simulate game play starting from node through taking random moves until reaching a terminal node
     * Returns the utility at the terminal node for node.player
     */
    private double simulation(MCNode<CheckersData> node) {
        CheckersData currentBoard = copyBoard(node.data);
        int currentPlayer = node.player;
        int steps = 150; // Draw after 150 steps to avoid infinite games
        while (steps > 0) { // Simulate until terminal condition (Player has no moves)
            CheckersMove[] legalMoves = currentBoard.getLegalMoves(currentPlayer);
            if (legalMoves == null || legalMoves.length == 0) {
                // currentPlayer cannot move so they lose and the previous player wins
                return (currentPlayer == node.player) ? 0.0 : 1.0;
            }
            
            // Choose a random move of those available and take it
            CheckersMove randomMove = legalMoves[(int)(Math.random() * legalMoves.length)];
            currentBoard.makeMove(randomMove);
            currentPlayer = (currentPlayer == RED) ? BLACK : RED; // alternate (kings treated by base color)
            steps--;
        }
        return 0.5; // draw result
    }
    
    /*
     * Path is the nodes we took down the MCTree
     * Result is 1 if the final node in path is the winner, 0 otherwise
     */
    private void backpropagation(ArrayList<MCNode<CheckersData>> path, double result) {
        for (MCNode<CheckersData> node : path) {
            node.playouts++; // Increment playouts for every node
            node.wins += result; // Add result to the players wins
            result = (result == 0.5) ? 0.5 : (1.0 - result); // Flip the result for the next player
        }
    }
    
    /*
     * Creates a deep copy of the inputed checkers data
     * Returns a CheckersData Object with the same board configuration
     */
    private CheckersData copyBoard(CheckersData sourceBoard){
    	int[][] original = sourceBoard.board;
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone(); // Deep copy of each row
        }
        CheckersData copyBoard = new CheckersData();
        copyBoard.board = copy;
        return copyBoard;
    }
}
