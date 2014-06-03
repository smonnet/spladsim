package peersim.pastPastry;

import java.math.BigInteger;

import peersim.core.Node;

/*
 * this class provides replica set with bulky interface
 */
public class BlocReplicaSetHolder implements Bulky{
	BigInteger blocid;
	Node []replicaSet;

	BlocReplicaSetHolder(BigInteger blocid,Node []set){
		this.blocid=blocid;
		this.replicaSet=set;
	}
	
	BlocReplicaSetHolder(){
		this.blocid=null;
		this.replicaSet=null;
	}
	
	public int getSize(){
		return 0;
	}
}
