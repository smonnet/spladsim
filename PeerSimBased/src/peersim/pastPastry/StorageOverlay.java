package peersim.pastPastry;

import java.math.BigInteger;
import java.util.*;

import peersim.core.Node;

//this is the generic interface for a storage overlay
public interface StorageOverlay{
	
	//standard routines of the DHT (also described in the PAST paper)
	BigInteger insert(String name,int k,Bloc file);
	Bloc lookup(BigInteger blocId);//this hasn't been checked
	void Reclaim(BigInteger blocId,BigInteger credentials);//this isn't used
	void receive(PastMessage m);
	
	public HashMap getStorage();//returns the table of stored blocks on this node
	public BigInteger getNodeId();//returns the node n of the node
	public MSPastryProtocol getRouteLayer();//returns the route layer, typically the pastry DHT layer
	//public ReplicaSetStrategy getStrategy();//returns the strategy used for replication (unused for PASTA)
	
	//these are used to simulate bandwidth, it works pretty much like malloc/free for the memory: to transfer a message, you have to allocate bandwidth on both sides and then free it when it's over
	public void allocUpBandwidth(BigInteger dest, PastMessage m);//allocates upload bandwidth between this and dest for message m
	public void allocDownBandwidth(BigInteger source,PastMessage m);//allocates download bandwidth between source and this for message m
	public void freeUpBandwidth();//used to free upload bandwidth
	public void freeDownBandwidth(BigInteger source,PastMessage m);//used to free download bandwidth
	public boolean upBandwidthAvailable();//checks up bandwidth
	public boolean downBandwidthAvailable();//checks down bandwidth
	//public int delayUp(int upsize);//calculates time needed to transfer a file of upsize "upsize"
	public void clearUnfinishedDownloads();//used when a node is down, the bandwidth has to be cleared
	public int ongoingDownloads();
	public HashMap getDownloads();//returns the list of current downloads
	
	//this is useful to calculate session times
	public void endSession();
	public long getSessionTime();
	
	//this is useful for placement of replicas
	public int getRandomIndex(Random r);//returns a random index in the replica set (used for random strategy)
	public boolean IAmRoot(BigInteger Blocid);//tells if this is root of the file Blocid
	public Node rootOf(BigInteger Blocid);//tells who is the root of the file Blocid (within the nodes known by this, not absolutely)
	public void placeBloc(Bloc bloc);//places a bloc and it's replicas refering to current strategy of the DHT (useful to quickly and massively fill a DHT)
	public void reset();
}
