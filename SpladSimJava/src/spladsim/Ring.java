/**
 * This class contains all the SpladNodes organized in Ring
 * nodes can access it to get global knowledge (simulate a 
 * perfect failure detector for instance
 */
package spladsim;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author smonnet
 *
 */
public class Ring {
	RingElement head;
	
	/* returns the node associated with identifier id */
	public SpladNode get(BigInteger id) {
		
		return null;
	}
	
	/* inserts the given node into the sorted ring - placed according to its id */
	public void insert(SpladNode node) {
		
	}
	
	/* removes the node associated with identifier id 
	 * returns true if the node hes been found and removed */
	public boolean remove(BigInteger id) {
		
		return false;
	}
	
	/* return an array of the n closest neighbors - usefull to get leafsets */
	public SpladNode[] getClosestNeighbors(long n) {
		
		return null;
	}
}
