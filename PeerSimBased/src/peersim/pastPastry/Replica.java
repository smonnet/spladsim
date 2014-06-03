package peersim.pastPastry;


import peersim.core.Node;

/*
 * used by PASTA in it's replica list
 * the "hasBloc" boolean is used because a node can be in a replica set of a file, and have no file yet
 */
public class Replica {
	public Node n;
	public boolean hasBloc;
	
	Replica(Node node){
		this.n=node;
		hasBloc=false;
	}
	
	public void receivedBloc(){
		hasBloc=true;
	}
	
	public boolean storesBloc(){
		return hasBloc;
	}
	
	public Replica clone(){
		Replica dolly=new Replica(this.n);
		dolly.hasBloc=this.hasBloc;
		return dolly;
	}
        public String toString(){
           return n.getID()+":"+hasBloc;
        }
}
