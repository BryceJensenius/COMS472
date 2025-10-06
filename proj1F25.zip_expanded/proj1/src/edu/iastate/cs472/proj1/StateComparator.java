package edu.iastate.cs472.proj1;

import java.util.Comparator;

/**
 *  
 * @author Bryce Jensenius
 *
 */

/**
 * This method compares two states in the lexicographical order of the board configuration. 
 * The 3X3 array representing each board configuration is converted into a sequence of nine 
 * digits starting at the 0th row, and within each row, at the 0th column.  For example, the 
 * two states
 * 
 * 	   2 0 3        2 8 1
 *     1 8 4        7 5 3
 *     7 6 5        6 0 4
 *
 * are converted into the sequences <2,0,3,1,8,4,7,6,5>, and <2,8,1,7,5,3,6,0,4>, respectively. 
 * By definition the first state is less than the second one.  
 * 
 * The comparator will be used for maintaining the CLOSED list used in the A* algorithm. 
 */
public class StateComparator implements Comparator<State>
{
	@Override
	public int compare(State state, State state2)
	{
		int[][] b1 = state.board;
		int[][] b2 = state2.board;
		for(int row = 0; row < b1.length; row++) {
			for(int col = 0; col < b1[0].length; col++) {		
				int cmp = Integer.compare(b1[row][col], b2[row][col]);
                if (cmp != 0) {
                    return cmp;
                }
			}
		}
	    return 0; // 2 states are equal on all values
	}  		
}