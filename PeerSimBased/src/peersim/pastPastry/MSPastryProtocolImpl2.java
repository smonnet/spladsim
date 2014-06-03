/**
 * 
 */
package peersim.pastPastry;

import java.math.BigInteger;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

/**
 * @author vsimon
 *
 */
public class MSPastryProtocolImpl2 extends MSPastryProtocol implements
		EDProtocol, Cloneable {

	private static final String PAR_DELAY = "updateDelay";
	private static final String PAR_BDELAY = "bloomDelay";
	private static final String PAR_BANDWIDTH = "bandwidth";
	private static final String PAR_RR = "RR";
	private Pair<Node[], Node[]> leafset;
	static int updatedelay;

	public MSPastryProtocolImpl2(String prefix) {
		this.nodeId = null;
		mspastryid = CommonState.getPid();
		int b = 0, l = 0, base = 0, bloomDelay = 0, replicaRate = 0;
		final String PAR_B = "B";
		final String PAR_L = "L";

		updatedelay = Configuration.getInt(prefix + "." + PAR_DELAY);
		bloomDelay = Configuration.getInt(prefix + "." + PAR_BDELAY);
		b = Configuration.getInt(prefix + "." + PAR_B, 4);
		l = Configuration.getInt(prefix + "." + PAR_L,
				MSPastryCommonConfig.BITS / b);
		base = Util.pow2(b);
		replicaRate = Configuration.getInt(prefix + "." + PAR_RR, 5);

		MSPastryCommonConfig.B = b;
		MSPastryCommonConfig.L = l;
		MSPastryCommonConfig.BASE = base;
		MSPastryCommonConfig.BD = bloomDelay;
		MSPastryCommonConfig.RR = replicaRate;
		MSPastryCommonConfig.STR = Configuration.getInt(prefix + ".strategy");
		//leafsetsize = MSPastryCommonConfig.L + (MSPastryCommonConfig.L % 2);
		if (MSPastryCommonConfig.RR > MSPastryCommonConfig.L / 2 + 1) {
			System.err.println("Init Error: The replica rate is too big");
			System.exit(0);
		}

	}

	public Object clone() {
		MSPastryProtocolImpl2 obj = null;
		try {
			obj = (MSPastryProtocolImpl2) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		obj.nodeId = null;
		obj.leafset = null;
		return obj;
	}

	/* (non-Javadoc)
	 * @see peersim.pastPastry.MSPastryProtocol#getNodeId()
	 */
	@Override
	public BigInteger getNodeId() {

		return nodeId;
	}

	/* (non-Javadoc)
	 * @see peersim.pastPastry.MSPastryProtocol#join()
	 */
	@Override
	public void join() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see peersim.pastPastry.MSPastryProtocol#getTr(int)
	 */
	@Override
	public BlocTransport getTr(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see peersim.pastPastry.MSPastryProtocol#getleafset()
	 */
//	@Override
//	public Pair<Node[], Node[]> getleafset() {
//		if (leafset == null)
//			computeLeafset(CommonState.getNode());
//		return leafset;
//	}

	public void rearm(Node node) {
		int delay = CommonState.r.nextInt(updatedelay);
		EDSimulator.add(delay, this, node, mspastryid);
	}

//	private void computeLeafset(Node node) {
//		int length = Network.size() / 2;
//		Pair<Node[], Node[]> pair = Pair.of(new Node[length], new Node[length]);
//		//Node[] tmp = new Node[length];
//		int myIndex = node.getIndex();
//		//int index = 0;
//		//remplir la partie droite
//		int leftIndex = myIndex;
//		int rightIndex = myIndex;
//
//		int inserted = 0;
//		while (true) {
//			//aller vers la droite
//			do {
//				rightIndex++;
//				if (rightIndex == Network.size())
//					rightIndex = 0;
//			} while (!Network.get(rightIndex).isUp());
//			if (rightIndex == leftIndex) {
//				leafset = pair;
//				return;
//			}
//			pair.right[inserted / 2] = Network.get(rightIndex);
//			inserted++;
//			if (inserted == leafsetsize) {//un de plus a droite qu'a gauche
//				leafset = pair;
//				return;
//			}
//			//aller vers la gauche
//			do {
//				leftIndex--;
//				if (leftIndex < 0)
//					leftIndex = Network.size() - 1;
//			} while (!Network.get(leftIndex).isUp());
//			if (leftIndex == rightIndex) {//un de plus a droite qu'a gauche
//				leafset = pair;
//				return;
//			}
//			pair.left[inserted / 2] = Network.get(leftIndex);
//			inserted++;
//			if (inserted == leafsetsize) {
//				leafset = pair;
//				return;
//			}
//		}
//	}

	/* (non-Javadoc)
	 * @see peersim.edsim.EDProtocol#processEvent(peersim.core.Node, int, java.lang.Object)
	 */
	@Override
	public void processEvent(Node node, int pid, Object event) {

		//computeLeafset(node);
		EDSimulator.add(updatedelay, event, node, pid);

	}
}
