package peersim.pastPastry;

import java.util.HashMap;
import java.math.BigInteger;

import peersim.core.Node;

/*
 * this is the default Bloom Filter interface used by
 * the standard PAST maintainance protocol.
 * It has to extend Bulky, because bloom filters have
 * a upsize.
 */

public interface BloomFilterITF extends Bulky{
	public boolean contains(BigInteger o);
	public void put(BigInteger o);
	public void merge(HashMap map);
	public Node getOwner();
	public int getSize();
}

