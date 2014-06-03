package peersim.pastPastry;

import java.math.BigInteger;
import java.util.Comparator;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

public abstract class MSPastryProtocol {

	//static fields
	//static final String PAR_TRANSPORT = "transport";
	//static String prefix;

	//protected int tid;
	/**
	 * nodeId of this pastry node
	 */
	public BigInteger nodeId;

	public static int mspastryid;

	//public static int leafsetsize;

	// instance fields

	public BigInteger getNodeId() {
		return nodeId;
	}

	public int getPid() {
		return mspastryid;
	}

	//public static MersenneTwister r;

	//public abstract Node nodeIdtoNode(BigInteger searchNodeId);

	//public abstract void receiveRoute(PastMessage m);

	//public void route(PastMessage m, Node srcNode);

	static {
		long seed = Configuration.getLong(CommonState.PAR_SEED,
				System.currentTimeMillis());
		//	r = new MersenneTwister(seed);
	}

	public void sortNet() {
		Network.sort(new Comparator() {
			//______________________________________________________________________________________
			public int compare(Object o1, Object o2) {
				Node n1 = (Node) o1;
				Node n2 = (Node) o2;
				MSPastryProtocol p1 = (MSPastryProtocol) (n1
						.getProtocol(mspastryid));
				MSPastryProtocol p2 = (MSPastryProtocol) (n2
						.getProtocol(mspastryid));
				return Util.put0(p1.nodeId).compareTo(Util.put0(p2.nodeId));
			}

			//______________________________________________________________________________________
			public boolean equals(Object obj) {
				return compare(this, obj) == 0;
			}
		});
	}

	public abstract void join();

	public MSPastryProtocol get(int i) {
		return ((MSPastryProtocol) (Network.get(i)).getProtocol(mspastryid));
	}

	public abstract BlocTransport getTr(int i);

	public void setNodeId(BigInteger tmp) {
		nodeId = tmp;

	}

	//public abstract Pair<Node[], Node[]> getleafset();

	//public abstract Node[] listallleafsetnodes();
	//  here are the functions that enable modulo support for the node ID's universe, which hasn't been performed in the MSPastry implem

	//  forward distance from a to b
	static BigInteger fwdDistance(BigInteger a, BigInteger b) {
		int c = a.compareTo(b);
		BigInteger min, max;
		min = BigInteger.ZERO;//((MSPastryProtocol)Network.get(0).getProtocol(mspastryid)).nodeId;
		max = new BigInteger("ffffffffffffffffffffffffffffffff", 16);//((MSPastryProtocol)Network.get(Network.size()-1).getProtocol(mspastryid)).nodeId;
		if (c == 0)
			return BigInteger.ZERO;
		return (c > 0) ? (max.subtract(a)).add(b) : b.subtract(a);
	}

	//backward distance from a to b 
	static BigInteger bckDistance(BigInteger a, BigInteger b) {
		int c = a.compareTo(b);
		BigInteger min, max;
		min = BigInteger.ZERO;//((MSPastryProtocol)Network.get(0).getProtocol(mspastryid)).nodeId;
		max = new BigInteger("ffffffffffffffffffffffffffffffff", 16);//((MSPastryProtocol)Network.get(Network.size()-1).getProtocol(mspastryid)).nodeId;
		if (c == 0)
			return BigInteger.ZERO;
		return (c > 0) ? a.subtract(b) : a.add(max.subtract(b));
	}

	//  computes the half upsize of the universe
	static BigInteger halfRingDistance() {
		BigInteger min, max;
		min = ((MSPastryProtocolImpl) Network.get(0).getProtocol(mspastryid)).nodeId;
		max = ((MSPastryProtocolImpl) Network.get(Network.size() - 1)
				.getProtocol(mspastryid)).nodeId;
		return max.subtract(min).divide(new BigInteger("2"));
	}

	//  computes the distance between two nodes
	public static BigInteger distance(BigInteger a, BigInteger b) {
		BigInteger fd = fwdDistance(a, b);
		BigInteger bd = bckDistance(a, b);
		return (fd.compareTo(bd) < 0) ? fd : bd;
	}

	public int indexOf(Node nodeToFind, Pair<Node[], Node[]> ls) {
		if (nodeToFind == null)
			return -1;
		//cerca a destra
		for (int index = 0; (index < ls.right.length); index++) {
			if (ls.right[index] != null && ls.right[index].equals(nodeToFind))
				return index;
		}
		//cerca a sinistra
		for (int index = 0; index < ls.left.length; index++) {
			if (ls.left[index] != null && ls.left[index].equals(nodeToFind))
				return index;
		}
		return -1;
	}

	public static boolean left(Node nodeToFind, Pair<Node[], Node[]> ls) {
		if (nodeToFind == null)
			return false;
		for (int index = 0; index < ls.left.length; index++) {
			if (ls.left[index] != null && ls.left[index].equals(nodeToFind))
				return true;
		}
		return false;
	}

	public void rearm(Node node) {
		return;
	}
}
