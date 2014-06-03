package peersim.pastPastry;

import java.math.BigInteger;
import java.util.Random;

import peersim.core.Node;

/*
 * The placement manager is used at the beginning of the
 * simulation to quickly dispatch blocks on their replica set conforming to 
 * the current replica set strategy. It is only used to speed up the simulation
 * and could not be implemented in the real life
 */

public class PlacementManager {

	private PastFamilyProtocol overlay;
	private int mspastryid;

	PlacementManager(PastFamilyProtocol overlay) {
		this.overlay = overlay;
		//this.selectionRange = MSPastryProtocol.getLeafsetHsize() / 3;
		this.mspastryid = MSPastryProtocol.mspastryid;
	}

	public Node nodeIdtoNode(BigInteger searchNodeId) {
		return Util.nodeIdtoNode(searchNodeId, mspastryid);
	}

	private long getSeed(BigInteger id) {
		return id.longValue();
	}

	public void placeBloc(Bloc bloc) {

		if (overlay instanceof PastaProtocol) {
			Replica[] replicaSet;
			PastaProtocol pastaOverlay = (PastaProtocol) overlay;
                        if(pastaOverlay instanceof ExactSelectionPastaProtocol){
                            ((ExactSelectionPastaProtocol)pastaOverlay).computeCharges();
                        }
                         if(pastaOverlay instanceof ExactSelectionPastaProtocolModified){
                            ((ExactSelectionPastaProtocolModified)pastaOverlay).computeCharges();
                        }
			replicaSet = pastaOverlay.randomReplicaSet();
			ReplicaSetLease lease = new ReplicaSetLease(bloc.getBlocId(),
					pastaOverlay.me, replicaSet);
			lease.tag = ReplicaSetLease.NEW_ROOT;
			lease.resetLease();
			for (int i = 0; i < lease.replicaSet.length; i++) {
				lease.replicaSet[i].receivedBloc();
			}
			pastaOverlay.rootof.put(bloc.getBlocId(), lease);
			lease = lease.clone();
			lease.tag = ReplicaSetLease.KEEP_STORING;
			lease.renewLease();
			for (int i = 0; i < replicaSet.length; i++) {
				pastaOverlay = (PastaProtocol) replicaSet[i].n
						.getProtocol(PastFamilyProtocol.pid);
				pastaOverlay.getStorage().put(bloc.getBlocId(), bloc);
				pastaOverlay.leases.put(bloc.getBlocId(), lease.clone());
			}
		}
	}
}
