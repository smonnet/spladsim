package peersim.pastPastry;

import java.math.BigInteger;
import java.util.Comparator;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;

/**
 * <p>Title: MSPASTRY</p>
 *
 * <p>Description: MsPastry implementation for PeerSim</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: The Pastry Group</p>
 *
 * @author Elisa Bisoffi, Manuel Cortella
 * @version 1.0
 */
public class StateBuilder implements peersim.core.Control {

	private static final String PAR_PROT = "protocol";
	private static final String PAR_TRANSPORT = "transport";
	private static final String PAR_BW = "bandwidth";

	private String prefix;
	private int mspastryid;
	private int transportid;
	private int bwid;

	public StateBuilder(String prefix) {

		mspastryid = Configuration.getPid(prefix + ".pastry");
	}

	//______________________________________________________________________________________________
	public final MSPastryProtocolImpl get(int i) {
		return ((MSPastryProtocolImpl) (Network.get(i)).getProtocol(mspastryid));
	}

	//______________________________________________________________________________________________
	public final BlocTransport getTr(int i) {
		return ((BlocTransport) (Network.get(i)).getProtocol(transportid));
	}

	//______________________________________________________________________________________________
	/*   public void fillLevel(int curLevel, int begin, int end, int nodo) {

	       int B = MSPastryCommonConfig.B;
	       int BASE = MSPastryCommonConfig.BASE;
	       int sz = Network.size();

	       if (curLevel >= 10)
	           return;

	       if (curLevel >= MSPastryCommonConfig.BITS / B)
	           return;
	      
	       long[] minlatencies = new long[BASE]; // in associazione con i nodeid
	       int[] minindeces = new int[BASE]; // in associazione con i nodeid

	       for (int i = 0; i < minlatencies.length; i++)
	           minlatencies[i] = Long.MAX_VALUE;

	       for (int i = 0; i < 10* BASE; i++) {
	           int randomIndex = begin + CommonState.r.nextInt(end - begin);
	           long lat = getTr(randomIndex).getLatency(Network.get(nodo),
	                   Network.get(randomIndex));
	           //o(get(randomIndex).nodeId.toString() +  " --> level " +curLevel);

	           int nextch = Util.charToIndex(Util.put0(get(randomIndex).nodeId).charAt(curLevel));
	           if (lat < minlatencies[nextch]) {
	               minlatencies[nextch] = lat;
	               minindeces[nextch] = randomIndex;
	               get(nodo).routingTable.set(curLevel, nextch,
	                                          get(randomIndex).nodeId);
	           }

	       }

	       for (int i = begin; i < end; i++)
	           get(i).routingTable.table[curLevel] = get(nodo).routingTable.table[curLevel].clone();

	       int subbegin = begin;
	       int subend = begin;
	       for (int i = 0; i < BASE; i++) {
	           char curChar = Util.DIGITS[i];

	           if (!Util.hasDigitAt(get(subbegin).nodeId, curLevel, curChar))
	               continue;

	          subend = subbegin;

	           while ((subend<sz)&&(Util.hasDigitAt(get(subend).nodeId, curLevel, curChar)))
	               subend++;


	           x("Entering level:" + (curLevel + 1));
	           fillLevel(curLevel + 1, subbegin, subend,minindeces[Util.charToIndex(curChar)]);
	           x("      Exiting level:" + (curLevel + 1));

	           if ( subend >= sz) break;
	           subbegin = subend;

	       }


	   }*/

	//______________________________________________________________________________________________
	public static void o(Object o) {
		System.out.println(o);
	}

	public static void x(Object o) {
	}

	//______________________________________________________________________________________________
	public boolean execute() {
		//  ((BandwidthInterface)(Network.prototype).getProtocol(bwid)).setPastryId(mspastryid);
		// !!! segna tempo iniziale

		/* Sort the network by nodeId (Ascending) */
		//o("SORTING NODES");
		Network.sort(new Comparator() {

			public int compare(Object o1, Object o2) {
				Node n1 = (Node) o1;
				Node n2 = (Node) o2;
				MSPastryProtocol p1 = (MSPastryProtocol) (n1
						.getProtocol(mspastryid));
				MSPastryProtocol p2 = (MSPastryProtocol) (n2
						.getProtocol(mspastryid));
				BigInteger id1 = p1.nodeId;
				BigInteger id2 = p2.nodeId;
				return Util.put0(p1.nodeId).compareTo(Util.put0(p2.nodeId));
				// return p1.nodeId.compareTo(p2.nodeId);
			}

			public boolean equals(Object obj) {
				return compare(this, obj) == 0;
			}
		});

		return false;

	} //end execute()

}
