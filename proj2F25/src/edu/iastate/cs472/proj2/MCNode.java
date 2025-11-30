package edu.iastate.cs472.proj2;

import java.util.ArrayList;
/**
 * 
 * @author Bryce Jensenius
 *
 */

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode<E>
{
	static final int
            EMPTY = 0,
            RED = 1,
            RED_KING = 2,
            BLACK = 3,
            BLACK_KING = 4;

	E data; // Game Configuration at this node
	int playouts; // Total playouts from this node
	double wins; // Win count for the player of this node
	int player; // Player who made the move to this node
	CheckersMove move; // the move performed to get this board;
	ArrayList<MCNode<E>> children;
	public MCNode(E data, int player) {
		this.data = data;
		this.player = player;
		children = new ArrayList<>();
	}

	public void addChild(MCNode<E> child){
		children.add(child);
	}
}

