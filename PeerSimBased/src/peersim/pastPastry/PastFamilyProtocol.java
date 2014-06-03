package peersim.pastPastry;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Random;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;
import peersim.lip6.util.Adaptator;
import peersim.transport.Transport;

/*this is a general class implementing a past-family protocol. It cannot be used as is, because some important methods are
missing (like receive() ). It has to be extended by a class implementing the Storage overlay interface. See PastProtocol or PastaProtocol
classes for an example*/
public class PastFamilyProtocol implements Cloneable {
	static final String PAR_PASTRY = "pastry";
	//private static final String RRATE = "replicaRate";
	//private static final double SCALE = MSPastryCommonConfig.bandwidthScaleFactor * 100;
	private static final String PAR_BANDWIDTH = "bandwidth";
	private static final String PAR_KICKRANGE = "kickrange";
	private static String PAR_SELECTIONRANGE = "selectionrange";

	protected MSPastryProtocol routeLayer;
	//protected int maxReplicaRate;
	protected int defaultReplicaRate;
	private int updateDelay;
	protected static int pid;
	public static int kickRange;
	public static int selectionRange;
	//  private double upBandwidth;
	//  private double downBandwidth;
	//  private boolean upBandwidthAvailable;
	//  private int downBandwidthAvailable;

	protected static int mspastryid;
	//private HashMap downloadingFrom;//in case of node failure, we need to cancel the downloads
	//private BigInteger uploadingTo;
	protected HashMap storage;//    This hash table represents the blocs located on a PAST node
	protected HashMap acks;
	protected HashMap<BigInteger, Node> downloads;//current downloads
	protected PastMessage upload;

	private long sessionTime;
	protected Node me;
	protected Transport bandwidth;
	public static int bdwid;

	//private static UniformRandomGenerator urg;
	//public static Pair<Node[], Node[]> leafset;// attention ne supporte pas la parallelisation

	private static BigInteger half, max;

	public PastFamilyProtocol(String prefix) {
		storage = new HashMap();
		acks = new HashMap();
		//downloadingFrom=new HashMap();
		downloads = new HashMap<BigInteger, Node>();
		mspastryid = Configuration.getPid(prefix + "." + PAR_PASTRY);
		routeLayer = (MSPastryProtocol) CommonState.getNode().getProtocol(
				mspastryid);
		bdwid = Configuration.getPid(prefix + "." + PAR_BANDWIDTH);
		//maxReplicaRate = MSPastryProtocol.getLeafsetHsize() + 1;
		defaultReplicaRate = MSPastryCommonConfig.RR;

		updateDelay = MSPastryCommonConfig.BD;
		sessionTime = CommonState.getTime();

		//int hlfset = MSPastryProtocol.getLeafsetHsize();
		kickRange = Configuration.getInt(prefix + "." + PAR_KICKRANGE);//this.routeLayer.leafSet.hsize*2/3;
		selectionRange = Configuration.getInt(
				prefix + "." + PAR_SELECTIONRANGE);//this.routeLayer.leafSet.hsize/3;
		if (selectionRange > kickRange)
			throw new InvalidParameterException(
					"selection range superior to kickRange");

		pid = CommonState.getPid();
		me = CommonState.getNode();
		BigInteger deux = new BigInteger("2");
		max = deux.pow(MSPastryCommonConfig.BITS).subtract(BigInteger.ONE);
		half = max.divide(deux);
	}

	public Object clone() {
		PastFamilyProtocol obj = null;
		try {
			obj = (PastFamilyProtocol) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		obj.storage = new HashMap();
		obj.acks = new HashMap();
		//obj.downloadingFrom=new HashMap();
		obj.downloads = new HashMap<BigInteger, Node>();
		obj.sessionTime = CommonState.getTime();
		obj.me = CommonState.getNode();
		obj.bandwidth = (Transport) CommonState.getNode().getProtocol(bdwid);
		obj.routeLayer = (MSPastryProtocol) CommonState.getNode().getProtocol(
				mspastryid);
		return obj;
	}

	//    Here we have the 3 functions described in the PAST paper as the basic PAST interface

	public BigInteger insert(String name, int k, Bloc Bloc) { //k isn't used
		System.out.println("Not Implemented yet");
		System.exit(3);
		return null;
	}

	public Bloc lookup(BigInteger BlocId) {
		System.out.println("Not Implemented yet");
		System.exit(3);
		return null;
	}

	public void Reclaim(BigInteger BlocId, BigInteger credentials) {
		//		not useful for the simulation
	}

	public void sendAppl(int type, Node dest, Bulky data) {
		ApplMessage msg = new ApplMessage(type, data, data.getSize());
		msg.src = me;
		msg.timestamp = CommonState.getTime();
		bandwidth.send(me, dest, msg, pid);
	}

	//this is used to ensure periodicity of the maintainance protocols (for both storage and route layers)
	protected void rearm(boolean firstTime) {
		//Random r2 = new Random();
		int delay = this.updateDelay;
		ApplMessage msg = new ApplMessage(
				ApplMessage.REPLICASETMAINTAINANCE_MSG, null, 0);
		if (firstTime)
			delay = CommonState.r.nextInt(updateDelay);
		Adaptator.add(delay, msg, me, pid); //rearm the alarm 			
	}

	public HashMap getStorage() {
		return storage;
	}

	public MSPastryProtocol getRouteLayer() {
		return routeLayer;
	}

	//shortcut used to return an ack when received a block
	protected void returnAck(Bloc Bloc) {
		Node owner = Bloc.blocOwner;
		InformativeMessage msg = new InformativeMessage(
				InformativeMessage.INFO, Bloc.getBlocId(), me);
		ApplMessage confirm = new ApplMessage(ApplMessage.REPLY_MSG, msg,
				msg.getSize());

		bandwidth.send(me, owner, confirm, pid);
	}

	public boolean IAmRoot(BigInteger Blocid) {
		return (rootOf(Blocid,me).equals(me));
	}

	public Node rootOf(BigInteger blocid, Node hint) {
		BigInteger disthint, nextdistance, currentdistance;
                if(!hint.isUp()){
                    hint = me;
                }
		disthint = fastdistance(((MSPastryProtocol) (hint.getProtocol(MSPastryProtocol.mspastryid))).getNodeId(), blocid);
                nextdistance = disthint;
                int nextIndex=hint.getIndex();
                int currentIndex;
                // on cherche  droite
                do{
                   currentdistance = nextdistance;
                   currentIndex = nextIndex;
                   nextIndex = Util.modulo(nextIndex+1,Network.size());
                   nextdistance = fastdistance(((MSPastryProtocol) (Network.get(nextIndex).getProtocol(MSPastryProtocol.mspastryid)))
							.getNodeId(), blocid); 
                }
                while(nextdistance.compareTo(currentdistance)<0);
                
                // faut il chercher a gauche ?
                if(currentIndex == hint.getIndex()){
                    nextdistance = disthint;
                    nextIndex = currentIndex;
                    do{
                        currentdistance = nextdistance;
                        currentIndex = nextIndex;
                        nextIndex = Util.modulo(nextIndex-1,Network.size());
                         
                   nextdistance = fastdistance(((MSPastryProtocol) (Network.get(nextIndex).getProtocol(MSPastryProtocol.mspastryid)))
							.getNodeId(),blocid); 
                }
                while(nextdistance.compareTo(currentdistance)<0);
                }
                return Network.get(currentIndex);
	}

	public String Hex(BigInteger i) {
		return i.toString(16);
	}

	public BigInteger getNodeId() {
		return routeLayer.getNodeId();
	}

	//not really used, but described in the past paper
	protected boolean notCorrupted(Bloc Bloc) {
		return true;
	}

	public boolean ImUp() {
		return me.isUp();
	}

	public void freeDownBandwidth(ApplMessage m) {
		
		if (m.body instanceof Bloc) {
			Bloc f = (Bloc) m.body;
			this.downloads.remove(f.getBlocId());
		}
	}

	public void clearUnfinishedDownloads() {
		storage.clear();
		acks.clear();
	}

	public HashMap getDownloads() {
		return downloads;
	}

	public boolean downloading(BigInteger bloc) {
		Node n = downloads.get(bloc);
		if (n == null)
			return false;
		if (!n.isUp()) {
			downloads.remove(bloc);
			return false;
		}
		return true;
	}

	public void endSession() {
		sessionTime = CommonState.getTime() - sessionTime;
	}

	public long getSessionTime() {
		return sessionTime;
	}

	public void setSessionTime() {
		sessionTime = CommonState.getTime();
	}

	public static void paste(Object o) {
		if (MSPastryCommonConfig.PASTLOG)
			System.err.println(o);
	}

	public static void pasto(Object o) {
		if (MSPastryCommonConfig.PASTLOG)
			System.out.println(o);
	}

	public int getRandomIndex(Random random) {
		return 0;
	}


	public void placeBloc(Bloc bloc) {

	}

	public void reset() {
	}

	public void join() {
		if (routeLayer.nodeId == null) {
			routeLayer.setNodeId(Bloc.urg.generate());
			routeLayer.sortNet();
		}
		me.setFailState(Node.OK);
		rearm(true);
		routeLayer.rearm(me);
	}

//	public Pair<Node[], Node[]> getleafset() {
//		if (CommonState.getNode() == me)
//			return leafset;
//		else
//			return ((MSPastryProtocol) CommonState.getNode().getProtocol(
//					mspastryid)).getleafset();
//	}

//	public Node[] listallleafsetnodes() {
//		int numLeft = countNonEmpty(leafset.left);
//		int numRight = countNonEmpty(leafset.right);
//		Node[] result = new Node[numLeft + numRight];
//		for (int i = 0; i < numLeft; i++)
//			result[i] = leafset.left[numLeft - i - 1];
//		for (int i = 0; i < numRight; i++)
//			result[numLeft + i] = leafset.right[i];
//		return result;
//	}

	private int countNonEmpty(Node[] a) {
		int count = 0;
		for (count = 0; (count < a.length) && (a[count] != null); count++)
			/*NOOP*/;
		return count;
	}

	//  computes the distance between two nodes
	public static BigInteger distance(BigInteger a, BigInteger b) {
		BigInteger fd = fwdDistance(a, b);
		BigInteger bd = bckDistance(a, b);
		return (fd.compareTo(bd) < 0) ? fd : bd;
	}

	//forward distance from a to b
	private static BigInteger fwdDistance(BigInteger a, BigInteger b) {
		int c = a.compareTo(b);
		if (c == 0)
			return BigInteger.ZERO;
		return (c > 0) ? (max.subtract(a)).add(b) : b.subtract(a);
	}

	//backward distance from a to b 
	private static BigInteger bckDistance(BigInteger a, BigInteger b) {
		int c = a.compareTo(b);
		if (c == 0)
			return BigInteger.ZERO;
		return (c > 0) ? a.subtract(b) : a.add(max.subtract(b));
	}

	public static BigInteger fastdistance(BigInteger a, BigInteger b) {

		int comp = a.compareTo(b);
		if (comp == 0)
			return BigInteger.ZERO;
		BigInteger c;
		if (comp > 0) {
			c = a.subtract(b);
		} else
			c = b.subtract(a);
		return (c.compareTo(half) > 0) ? max.subtract(c) : c;
	}
}
