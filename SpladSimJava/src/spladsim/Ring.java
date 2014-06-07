/**
 * This class contains all the SpladNodes organized in Ring
 * nodes can access it to get global knowledge (simulate a 
 * perfect failure detector for instance
 */
package spladsim;

import java.math.BigInteger;
import java.util.Arrays;

import org.simgrid.msg.Msg;

/**
 * @author smonnet
 *
 */
public class Ring {
	RingElement head;
	long eltNum;
	public Ring() {
		eltNum = 0;
		head = new RingElement();
		head.next = head;
		head.prev = head;
	}
	
	/* returns the node associated with identifier id */
	public SpladNode get(BigInteger id) {
		
		return null;
	}
	
	/* inserts the given node into the sorted ring - placed according to its id */
	public void insert(SpladNode node) {
		RingElement re = new RingElement();
		re.node = node;
		/* searching for the right place to insert */
		re.next = head.next;
		while((re.next!=head) && (node.uid.compareTo(re.next.node.uid)==1)) {
			re.next = re.next.next;
		}
		re.prev=re.next.prev;
		re.next.prev=re;
		re.prev.next=re;
		eltNum++;
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
	
	public boolean isEmpty() {
		return (eltNum==0);
	}
	
	public long size() {
		return eltNum;
	}
	
	public void print() {
		RingElement r = head.next;
		Msg.info("***** ***** RING CONTENT (nodes UIDs) ***** *****");
		while(r!=head) {
			Msg.info("UID -- " + r.node.uid);
			r=r.next;
		}
	}
}
