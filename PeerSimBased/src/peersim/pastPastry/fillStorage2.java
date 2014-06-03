package peersim.pastPastry;

import java.math.BigInteger;
import java.util.HashMap;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;

/*
 * this initialization part of the simulation quickly fills the DHT with a given
 * number of blocks according to the specified strategy. Unlikely to the "fillStorage"
 * class, this one "cheats" to speed up loading: it rawly puts the blocks to their places
 * and do not simulate the real-network-like loading. Using this class instead of "fillStorage"
 * is strongly recommended since it is much faster and works really well
 */

public class fillStorage2 implements peersim.core.Control {

	private static final String PAR_PROT = "protocol";
	private static final String PAR_BlocS = "files";
	private final static String BlocSIZE = "fileSize";

	private String prefix;
	private int mspastid;
	private int Blocs;
	private int Blocsize;

	public fillStorage2(String prefix) {
		this.prefix = prefix;
		mspastid = Configuration.getPid(this.prefix + "." + PAR_PROT);
		Blocs = Configuration.getInt(this.prefix + "." + PAR_BlocS);
		Blocsize = Configuration.getInt(prefix + "." + BlocSIZE);
	}

	private PastaProtocol getOverlay(int index) {
		return (PastaProtocol) Network.get(index).getProtocol(mspastid);
	}

	private HashMap getStorage(int index) {
		return getOverlay(index).getStorage();
	}

	private BigInteger getNodeId(int index) {
		return ((PastaProtocol) Network.get(index).getProtocol(mspastid))
				.getNodeId();
	}

	private int determineClosestNodeIndex(BigInteger blocid) {
		int sz = Network.size();
		BigInteger min, curr;
		int index = 0;
		BigInteger[] net = new BigInteger[sz];
		for (int i = 0; i < sz; ++i) {
			net[i] = getNodeId(i);
		}
		min = PastFamilyProtocol.fastdistance(blocid, getNodeId(0));
		for (int i = 1; i < sz; i++) {
			curr = PastFamilyProtocol.distance(blocid, getNodeId(i));
			if (min.compareTo(curr) > 0) {
				min = curr;
				index = i;
			}
		}
		return index;
	}

	//______________________________________________________________________________________________
	/*
	 * this
	 */
	public boolean execute() {
		//Node insertionPoint;
		//PastFamilyProtocol past;
		Bloc f;
		int index;
		//LeafSet ls;
		//BigInteger owner=((MSPastryProtocolImpl)Network.get(0).getProtocol(mspastryid)).nodeId;
		Node owner = Network.get(0);
		System.out.println("Filling DHT with " + Blocs + " files...");
		for (int i = 0; i < Blocs; i++) {
			f = new Bloc(Blocsize);
			
			f.setReplicaRate(MSPastryCommonConfig.RR);
			index = determineClosestNodeIndex(f.getBlocId());
			f.setOwner(owner);
			//positionner le leafset
			PastaProtocol pastproto = getOverlay(index);
			CommonState.setNode(Network.get(index));
                      
			//PastFamilyProtocol.leafset = pastproto.routeLayer.getleafset();
			getOverlay(index).placeBloc(f);

			//if ((i % (Blocs / 100)) == 0)
			//	System.out.print("-");
		}
		System.out.println("100% Complete");
		return false;

	} //end execute()

}
