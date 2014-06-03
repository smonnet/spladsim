package peersim.pastPastry;

import peersim.core.Fallible;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;

public abstract class ChurnGeneratorInterface  {
	  
	public abstract void scheduleNextOP(Node node);
	public abstract void removeOneNode(Node me);
	public abstract void addOneNode();
	
	public void processEvent(Node myNode, int myPid, Object event) {
		
		ChurnMessage m = (ChurnMessage)event;
		 switch (m.messageType) {
		 case ChurnMessage.MSG_ADD_NODE:
			addOneNode();
			break;
		 case ChurnMessage.MSG_STOP_NODE:
			removeOneNode(myNode);
			break;
		 }
		 scheduleNextOP(myNode);
	}
	 public int getNumUpNodes()
	    {
	    	int k= 0;
	    	for(int i=0;i<Network.size();i++){
	    		if(Network.get(i).getFailState()==Fallible.OK ){
	    			k++;
	    		}
	    	}
	    	return k;
	    }
	
}
