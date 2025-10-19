package edu.iastate.cs472.proj2;

import java.util.ArrayList;

/**
 * Node type for the Monte Carlo search tree.
 */
public class MCNode<E>
{
	E data;
	ArrayList<MCNode<E>> children;
	public MCNode(E data) {
		this.data = data;
		children = new ArrayList<>();
	}

	public void addChild(MCNode<E> child){
		children.add(child);
	}
}

