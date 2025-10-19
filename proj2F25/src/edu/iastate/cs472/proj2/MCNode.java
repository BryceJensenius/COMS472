package edu.iastate.cs472.proj2;

import java.util.ArrayList;

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
	int playouts; // Total plaoyouts from this node
	int wins; // Number of wins for this player from this node
	int player; // Player who made the move to this node
	ArrayList<MCNode<E>> children;
	public MCNode(E data) {
		this.data = data;
		children = new ArrayList<>();
	}

	public void addChild(MCNode<E> child){
		children.add(child);
	}
}

