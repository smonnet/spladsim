/**
 * This class contains all the SpladNodes organized in Ring
 * nodes can access it to get global knowledge (simulate a 
 * perfect failure detector for instance
 */
package spladsim;

import java.math.BigInteger;
import org.simgrid.msg.Msg;


/**
 * @author smonnet
 *
 */
public class Ring {
	RingElement head;
	long size;
	private static BigInteger half, max;
	
	public Ring() {
		size = 0;
		head = new RingElement();
		head.next = head;
		head.prev = head;
		max = new BigInteger("2").pow(GlobalKnowledge.nbBit).subtract(BigInteger.ONE);
		half = max.divide(new BigInteger("2"));
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
		size++;
	}
	
	/* removes the node associated with identifier id 
	 * returns true if the node has been found and removed */
	public boolean remove(BigInteger id) {
		RingElement re = head.next;
		while((re!=head) && (id.compareTo(re.node.uid)!=0)) {
			re = re.next;
		}
		if(re==head) {
			return false;
		} else {
			re.prev.next=re.next;
			re.next.prev=re.prev;
			size--;
			return true;
		}
	}
	
	/* return an array of the n closest neighbors - usefull to get leafsets */
	public SpladNode[] getSelectionRange(int n, SpladNode node) {
		RingElement re = head.next;
		RingElement f,b;
		int n2 = (n-1)/2;
		SpladNode[] result = new SpladNode[2*n2+1];
		while((re!=head) && (node.uid.compareTo(re.node.uid)!=0)) {
			re = re.next;
		}
		if (node.uid.compareTo(re.node.uid)==0) {
			result[n2]=re.node;
			f=re.next;
			b=re.prev;
			for (int i = 1; i<=n2; i++) {
				if(f==head)
					f=f.next;
				if(b==head)
					b=b.prev;
				result[n2+i]=f.node;
				result[n2-i]=b.node;
				f=f.next;
				b=b.prev;
			}
			return result;
		} else {
			Msg.info(" XXXX Error Failed to find a root node in getSelectionRange XXXX ");
			return null;
		}
	}
	
	public static BigInteger fastdistance(BigInteger a, BigInteger b) {
		int comp = a.compareTo(b);
		if (comp == 0)
			return BigInteger.ZERO;
		BigInteger c;
		if (comp > 0) {
			c = a.subtract(b);
		} else
			c = b.subtract(a);
		return (c.compareTo(half) > 0) ? max.subtract(c) : c;
	}
	
	public BigInteger getRoot(BigInteger dataUID) {
		RingElement re = head.next;
		BigInteger f,b;
		while((re!=head) && (dataUID.compareTo(re.node.uid)==1)) {
			re = re.next;
		}
			if((re == head.next)||(re == head)) { // ID is greater that the greatest or smaller than the smallest
			f = fastdistance(head.next.node.uid,dataUID);
			b = fastdistance(head.prev.node.uid,dataUID);
			if(f.compareTo(b)>0) {
				return head.prev.node.uid;
			} else {
				return head.next.node.uid;
			}
		} else { // normal case
			f = fastdistance(re.node.uid,dataUID);
			b = fastdistance(re.prev.node.uid,dataUID);
			if(f.compareTo(b)>0) {
				return re.prev.node.uid;
			} else {
				return re.node.uid;
			}
		}
	}
	
	public SpladNode getRandomNode() {
		long index = Math.round(GlobalKnowledge.rand.nextDouble() * (size-1));
		RingElement elt=head.next; 
		for(long i=0; i<index; i++) {
			elt=elt.next;
		}
		return elt.node;
	}
	
	public boolean isEmpty() {
		return (size==0);
	}
	
	
	public long size() {
		return size;
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
