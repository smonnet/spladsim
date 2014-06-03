package peersim.pastPastry;

import java.math.BigInteger;
import java.util.HashMap;


import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.util.ExtendedRandom;

/*
 * this initialization part of the simulation quickly fills the DHT with a given
 * number of blocks according to the specified strategy. Unlikely to the "fillStorage"
 * class, this one "cheats" to speed up loading: it rawly puts the blocks to their places
 * and do not simulate the real-network-like loading. Using this class instead of "fillStorage"
 * is strongly recommended since it is much faster and works really well
 * 
 * fillStorage3 is to be used in conjonction with BlocChurnGenerator and TypedChurnGenerator classes
 * it inserts blocs simulating arrival dates for blocs present at the time the simulation starts.
 * These blocs are given a negative birth time.
 */

public class fillStorage3 implements peersim.core.Control {

	private static final String PAR_PROT = "protocol";
	private static final String PAR_BlocS = "files";
	private final static String BlocSIZE = "fileSize";
        private final static String PAR_ARRIVAL_RATE = "arrivalRate";

	private String prefix;
	private int mspastid;
	private int Blocs;
	private int Blocsize;
        private double arrivalRate;
       private ExtendedRandom rd;

	public fillStorage3(String prefix) {
		this.prefix = prefix;
		mspastid = Configuration.getPid(this.prefix + "." + PAR_PROT);
		Blocs = Configuration.getInt(this.prefix + "." + PAR_BlocS);
		Blocsize = Configuration.getInt(prefix + "." + BlocSIZE);
                arrivalRate = Configuration.getDouble(prefix+"."+PAR_ARRIVAL_RATE);
                rd = new ExtendedRandom(Configuration.getLong("random.seed")+2);
	}

	private PastFamilyProtocol getOverlay(int index) {
		return (PastFamilyProtocol) Network.get(index).getProtocol(mspastid);
	}

	private HashMap getStorage(int index) {
		return getOverlay(index).getStorage();
	}

	private BigInteger getNodeId(int index) {
		return ((PastFamilyProtocol) Network.get(index).getProtocol(mspastid))
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
               long current_date = 0;
		for (int i = 0; i < Blocs; i++) {
			f = new Bloc(Blocsize);
			
			f.setReplicaRate(MSPastryCommonConfig.RR);
			index = determineClosestNodeIndex(f.getBlocId());
			f.setOwner(owner);
			//positionner le leafset
			PastFamilyProtocol pastproto = getOverlay(index);
			CommonState.setNode(Network.get(index));
			//PastFamilyProtocol.leafset = pastproto.routeLayer.getleafset();
			getOverlay(index).placeBloc(f);
                        // inserts the bloc in the blocsmap of MSPastryDamageObserver
                        MSPastryDamageObserver.blocsmap.put(f.getBlocId(), new Blocmapentry(current_date, 0));
                        //pick the next date at random
                     current_date -=  (long) Math.floor(-Math.log(1 - rd.nextDouble())/arrivalRate);
                       // System.err.print("age = "+current_date);
			if (Blocs>100 &&(i % (Blocs / 100)) == 0) {
                        System.out.print("-");
                    }
		}
               // System.err.println();
		System.out.println("100% Complete");
		return false;

	} //end execute()

}
