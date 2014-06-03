package peersim.pastPastry;

import java.math.BigInteger;

import peersim.core.Node;

/*
 * this is for PASTA: it is the replica Set lease.
 * the root of each file has to renew leases of it's replica Set.
 * When a lease is no longer renewed, the file is deleted.
 */

public class ReplicaSetLease {

	public static final int leaseStep = 5;
        public static final int ghostperiod = 3;
	public static final int halfLife = leaseStep / 2;
	public static final int KEEP_STORING = 0;
	public static final int NEW_ROOT = 1;

	public BigInteger blocid;
	public Replica[] replicaSet;
	public Node root;
	public int lease;
	public int tag;
	private int ghostLease;

	ReplicaSetLease(int replicaRate) {
		lease = leaseStep;
		replicaSet = new Replica[replicaRate];
		blocid = null;
		root = null;
                ghostLease = ghostperiod;
	}

	ReplicaSetLease(BigInteger blocid, Node root, Replica[] replicaSet) {
		lease = leaseStep;
		this.replicaSet = replicaSet;
		this.blocid = blocid;
		this.root = root;
		this.ghostLease = ghostperiod;
	}

	public ReplicaSetLease clone() {
		ReplicaSetLease dolly = new ReplicaSetLease(this.replicaSet.length);
		dolly.blocid = this.blocid;
		dolly.root = this.root;
		dolly.tag = this.tag;
                dolly.ghostLease = this.ghostLease;
		for (int i = 0; i < dolly.replicaSet.length; i++) {
			dolly.replicaSet[i] = this.replicaSet[i].clone();
		}
		return dolly;
	}

	public void renewLease() {
		lease = leaseStep;
	}

	public void resetLease() {
		lease = 0;
	}

	public boolean leaseExpired() {
		return lease == 0;
	}

	public boolean halfLease() {
		return lease == halfLife;
	}

	public void leaseTic() {
		lease--;
	}

        public int indexof(Node node){
            int res = -1;
            for (int i = 0; i < replicaSet.length; i++) {
			if (replicaSet[i].n.equals(node)) {
				res = i;
				break;
			}
		}
            return res;
        }
	/*
	 * when a node is in the replica set AND actually stores the file, it
	 * is called "confirmed Replica".
	 * Some nodes are in the set, but haven't downloaded the file yet: these
	 * aren't confirmed
	 */
	public void confirmedReplica(Node replica) {
		for (int i = 0; i < replicaSet.length; i++) {
			if (replicaSet[i].n.equals(replica)) {
				replicaSet[i].receivedBloc();
				break;
			}
		}
	}

	/*
	 * see the PASTA maintainance protocol for more details
	 */
	public void ghostTic() {
		ghostLease--;
	}

	public void renewGhostPeriod() {
		ghostLease = ghostperiod;
	}

	public boolean allowedGhostPeriodExpired() {
		return ghostLease <= 0;
	}

    void InfirmedReplica(Node src) {
        for (int i = 0; i < replicaSet.length; i++) {
			if (replicaSet[i].n.equals(src)) {
				replicaSet[i].hasBloc = false;
				break;
			}
		}
    }

}
