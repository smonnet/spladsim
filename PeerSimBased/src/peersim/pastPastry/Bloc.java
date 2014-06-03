package peersim.pastPastry;

import java.math.BigInteger;

import peersim.config.Configuration;
import peersim.core.Node;
import peersim.util.ExtendedRandom;

/*
 * object of this class represent a block
 * it's main attributes are it's upsize and it's ID
 */

public class Bloc implements Bulky {
	private int size;
	private BigInteger blocId;
	private String type = null; //for further possible implementation of UCT/CHB pastis blocks
	private int replicaRate;
	public final int DEFAULTRR = -1;
	public final int DEFAULTSIZE = 1000;
	public Node blocOwner;
	public static UniformRandomGenerator urg;
	static {
		urg = new UniformRandomGenerator(MSPastryCommonConfig.BITS,
				new ExtendedRandom(Configuration.getLong("random.seed")));
	}

	Bloc() {
		size = DEFAULTSIZE;
		type = null;
		blocId = urg.generate();
		replicaRate = DEFAULTRR;
	}

	Bloc(int size) {
		this();
		this.size = size;
	}

	public BigInteger getBlocId() {
		return blocId;
	}

	public String toString() {
		return (type == null) ? "{" + RoutingTable.truncateNodeId(blocId)
				+ "} (" + size + " kilobytes)" : "{"
				+ RoutingTable.truncateNodeId(blocId) + "," + type + "} ("
				+ size + " kilobytes)";
	}

	public void touch() {
		blocId = urg.generate();
	}

	public void setReplicaRate(int rr) {
		replicaRate = rr;
	}

	public int getReplicaRate() {
		return replicaRate;
	}

	public void setOwner(Node id) {
		blocOwner = id;
	}

	public int getSize() {
		return size;
	}

}
