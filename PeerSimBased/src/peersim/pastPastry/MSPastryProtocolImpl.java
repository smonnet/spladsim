/**
 * 
 */
package peersim.pastPastry;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Protocol;

/**
 * @author vsimon
 *
 */
public class MSPastryProtocolImpl extends MSPastryProtocol implements
		Cloneable, Protocol {

	private static final String PAR_DELAY = "updateDelay";
	private static final String PAR_BDELAY = "bloomDelay";
	private static final String PAR_BANDWIDTH = "bandwidth";
	private static final String PAR_RR = "RR";
	public static int delay;

	public MSPastryProtocolImpl(String prefix) {
		this.nodeId = null;
		mspastryid = CommonState.getPid();
		int b = 0, l = 0, base = 0, bloomDelay = 0, replicaRate = 0;
		final String PAR_B = "B";
		final String PAR_L = "L";

		delay = Configuration.getInt(prefix + "." + PAR_DELAY);
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
		MSPastryProtocolImpl obj = null;
		try {
			obj = (MSPastryProtocolImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		obj.nodeId = null;
		return obj;
	}

	/* (non-Javadoc)
	 * @see peersim.pastPastry.MSPastryProtocol#join()
	 */
	@Override
	public void join() {
		 throw new UnsupportedOperationException("Not supported yet.");

	}

	/* (non-Javadoc)
	 * @see peersim.pastPastry.MSPastryProtocol#getTr(int)
	 */
	@Override
	public BlocTransport getTr(int i) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

//	@Override
//	public Pair<Node[], Node[]> getleafset() {
//		Node me = Util.nodeIdtoNode(nodeId, mspastryid);
//
//		//initialisation
//		int length = leafsetsize / 2;
//		Pair<Node[], Node[]> pair = Pair.of(new Node[length], new Node[length]);
//		//Node[] tmp = new Node[length];
//		int myIndex = me.getIndex();
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
//				return pair;
//			}
//			pair.right[inserted / 2] = Network.get(rightIndex);
//			inserted++;
//			if (inserted == leafsetsize) {//un de plus a droite qu'a gauche
//				return pair;
//			}
//			//aller vers la gauche
//			do {
//				leftIndex--;
//				if (leftIndex < 0)
//					leftIndex = Network.size() - 1;
//			} while (!Network.get(leftIndex).isUp());
//			if (leftIndex == rightIndex) {//un de plus a droite qu'a gauche
//				return pair;
//			}
//			pair.left[inserted / 2] = Network.get(leftIndex);
//			inserted++;
//			if (inserted == leafsetsize) {
//				return pair;
//			}
//		}
//	}

}
