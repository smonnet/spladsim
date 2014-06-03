package peersim.pastPastry;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.lang.Math;

import peersim.core.CommonState;
import peersim.core.Fallible;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.core.Network;

/*
 * This is the new Past Alternative churn resistant protocol
 */
public class PastaProtocol extends PastFamilyProtocol implements Cloneable,
        EDProtocol {

    public HashMap<BigInteger, ReplicaSetLease> leases;
    public HashMap<BigInteger, ReplicaSetLease> rootof;
    protected PlacementManager manager;

    public PastaProtocol(String prefix) {
        super(prefix);
        leases = new HashMap<BigInteger, ReplicaSetLease>();
        rootof = new HashMap<BigInteger, ReplicaSetLease>();
        manager = new PlacementManager(this);
    }

    public Object clone() {
        PastaProtocol obj = null;
        obj = (PastaProtocol) super.clone();
        obj.leases = new HashMap();
        obj.rootof = new HashMap();
        obj.manager = new PlacementManager(obj);
        return obj;
    }

    /*
     * computes the distance from this to node 
     */
    protected int distanceToNode(Node node) {
        int distance = -1;
        //BigInteger id = ((PastaProtocol) node.getProtocol(pid)).getNodeId();
        if (node.equals(me)) {
            return 0;
        } else {
            if(node.getFailState()!=Fallible.OK)
                return -1;
            int myIndex = me.getIndex();
            int otherIndex = node.getIndex();
            distance = Math.abs(otherIndex-myIndex);
            distance = Math.min(distance,Network.size()-distance);
            return distance;
        }
    }

    public int getRandomIndex(Random r) {
        int rand;
        //do {
        rand = r.nextInt(selectionRange * 2 + 1);
        //} while (rand == selectionRange);
        //return Util.getRandomIndex(r, selectionRange * 2);
        return rand - selectionRange;
    }

    protected Node pickRandomNode() {
        Node node;
        int index = getRandomIndex(CommonState.r);
        if (index == 0) {
            node = me;
        } else {
            node = Network.get(Util.modulo(me.getIndex()+index,Network.size()));
        }
        return node;
    }

    public void findNewOne(Replica[] nodes, int index) {
        boolean duplicate;
        Node current;

        do {
            duplicate = false;
            current = pickRandomNode();
                for (int i = 0; i < nodes.length; i++) {
                    if ((nodes[i] != null) && (i != index)
                            && (nodes[i].n.equals(current))) {
                        duplicate = true;
                        break;
                    }
                }
        } while (duplicate);
        nodes[index] = new Replica(current);
    }

    /*
     * returns a new random replica set where all nodes are different
     */
    public Replica[] randomReplicaSet() {
        int replicaRate = MSPastryCommonConfig.RR;
        Replica[] replicas = new Replica[replicaRate];
  //      System.out.print("[");
        for (int i = 0; i < replicas.length; i++) {
            findNewOne(replicas, i);
  //          System.out.print(replicas[i].toString()+",");
        }
   //     System.out.println("]");
        return replicas;
    }


    /*
     * The root has to renew leases of all the replicas of the blocks he is in charge, and find new replicas if some
     * are too far or not availables anymore (according to it's leafset)
     */
    protected void doRootJob(HashMap updateMessages) {
        Iterator iter = this.rootof.entrySet().iterator();
        ReplicaSetLease current;
        LinkedList removeList = new LinkedList();
        ReplicaSetList orders;
        ReplicaSetLease dolly;
        int d;
        /*
         * I call "ghostReplicaSet" a set of nodes in charge to store a block, where none of the members have the file yet.
         * This may happen (and happens) when all replicas are downloading (or are about to download) the file, but haven't finished yet
         */

        while (iter.hasNext()) {
            boolean ghostReplicaSet = true;
            current = (ReplicaSetLease) ((Map.Entry) iter.next()).getValue();
            Node rootnode = rootOf(current.blocid,current.root);
            if (rootnode.equals(me)) {
                //check if this is still the root of the block
                current.root = me;
                current.renewLease();
                //check for each replica in the set...
                for (int i = 0; i < current.replicaSet.length; i++) {
                    //if the root is one of the replica nodes,...
                    if (current.replicaSet[i].n.equals(me)) {
                        if (this.storage.containsKey(current.blocid)) {//...and has the file... 
                            ghostReplicaSet = false;//...then the replica set is good...
                            current.replicaSet[i].receivedBloc();
                            ((ReplicaSetLease) this.leases.get(current.blocid))
                                    .confirmedReplica(current.replicaSet[i].n);
                            ((ReplicaSetLease) this.leases.get(current.blocid))
                                    .renewLease();
                        }
                    } else {
                        d = distanceToNode(current.replicaSet[i].n);
                        if (d > kickRange || d < 0) {
                            findNewOne(current.replicaSet, i);

                        } else {
                            if (current.replicaSet[i].storesBloc()) {
                                ghostReplicaSet = false;//...if it is OK, we have to check if it has the block
                            }
                        }
                    }
                }

                if (ghostReplicaSet) {//...if the replica set is "ghost", the root waits a little bit...
                    current.ghostTic();
                    if (current.allowedGhostPeriodExpired()) {//...and if the ghost lease expires, it declares the block lost
                        removeList.add(current.blocid);
                    }
                } else {//if the replica set is "alive"...
                    current.renewGhostPeriod();
                    //...the lease must be renewed for each replica-set node...
                    for (int i = 0; i < current.replicaSet.length; i++) {
                        if (current.replicaSet[i].n.equals(me)) {//...when the replica-set node is on this node
                            dolly = (ReplicaSetLease) this.leases
                                    .get(current.blocid);
                            if (dolly == null) {
                                dolly = current.clone();
                                dolly.tag = ReplicaSetLease.KEEP_STORING;
                            }
                            dolly.renewLease();
                            this.leases.put(dolly.blocid, dolly);
                        } else {//...or when it is on a distant node
                            orders = (ReplicaSetList) updateMessages
                                    .get(current.replicaSet[i].n);
                            if (orders == null) {
                                orders = new ReplicaSetList();
                            }
                            dolly = current.clone();
                            dolly.tag = ReplicaSetLease.KEEP_STORING;
                            orders.add(dolly);
                            updateMessages.put(current.replicaSet[i].n, orders);
                        }
                    }
                }

            } else {
                //envoyer la lease au nouveau root
                orders = (ReplicaSetList) updateMessages.get(rootnode);
                 if (orders == null) {
                                orders = new ReplicaSetList();
                            }
                            dolly = current.clone();
                            dolly.tag = ReplicaSetLease.NEW_ROOT;
                            orders.add(dolly);
                            updateMessages.put(rootnode, orders);
                removeList.add(current.blocid);
            }
        }

        while (removeList.size() != 0) {
            BigInteger blocid = (BigInteger) removeList.removeFirst();
            this.rootof.remove(blocid);
        }
    }

    /*
     * this method checks the list of leases and downloads all the blocks that are missing 
     */
    protected void redownloadMissing() {
        ReplicaSetLease current;
        Iterator iter = this.leases.entrySet().iterator();
        while (iter.hasNext()) {
            current = (ReplicaSetLease) ((Map.Entry) iter.next()).getValue();
            if (!this.storage.containsKey(current.blocid)
                    && !downloading(current.blocid)) {
                Node dest = chooseSource(current.replicaSet);
                /*if (dest == null) {
                 System.out.println("["
                 + RoutingTable.truncateNodeId(getNodeId())
                 + "] pas de source pour bloc "
                 + RoutingTable.truncateNodeId(current.blocid));
                 }*/
                if (dest != null) {
                    sendAppl(ApplMessage.SENDBLOC_MSG, dest,
                            new InformativeMessage(InformativeMessage.INFO,
                            current.blocid, me));

                    /*	System.out
                     .println("["
                     + RoutingTable.truncateNodeId(getNodeId())
                     + "]: Requesting to ["
                     + dest.getIndex()
                     + "] Bloc {"
                     + RoutingTable
                     .truncateNodeId(current.blocid)
                     + "}");*/
                } else {
                    System.out.println("[" + me.getID() + "] no valid source for bloc " + RoutingTable.truncateNodeId(current.blocid));
                }
            }
        }
    }

    /*
     * the replicas must remove expired leases (and the corresponding blocks).
     * If half of a block lease has expired, the replica sends a warning to the root
     */
    protected void doReplicaJob(HashMap updateMessages) {
        Set st = this.storage.entrySet();
        Iterator iter = this.storage.entrySet().iterator();
       // Iterator  iter = this.leases.entrySet().iterator();
        Bloc f;
        ReplicaSetLease current;
        Node root;
        ReplicaSetList orders;
        ReplicaSetLease dolly;
        LinkedList removeList = new LinkedList();
        BigInteger blocid;

        while (iter.hasNext()) {
            f = (Bloc) ((Map.Entry) iter.next()).getValue();
           current = (ReplicaSetLease) leases.get(f.getBlocId());
           // current = (ReplicaSetLease)((Map.Entry)iter.next()).getValue();
            /*if (current.lease != current.leaseStep) {
             System.out.println("[" + me.getIndex() + "] leaseTic = "
             + current.lease);
             }*/
            if(current==null){
                removeList.add(f.getBlocId());
                continue;
            }
            current.leaseTic();

            root = rootOf(current.blocid,current.root);

            if (!current.root.equals(root) || current.halfLease()) {
                orders = (ReplicaSetList) updateMessages.get(root);
                if (orders == null) {
                    orders = new ReplicaSetList();
                }
                dolly = current.clone();
                dolly.tag = ReplicaSetLease.NEW_ROOT;
                // dolly.resetLease();
                orders.add(dolly);
                if (!root.equals(me)) {
                    if(!current.root.equals(me)) {
                        updateMessages.put(root, orders);
                    }
                } else {
                    boolean condition = !rootof.containsKey(current.blocid)
                            || (rootof.get(current.blocid).tag == ReplicaSetLease.NEW_ROOT && current.lease > rootof.get(current.blocid).lease);
                    if (condition) {
                        rootof.put(current.blocid, dolly);
                    }
                }
            }
            if (current.leaseExpired()) {
                removeList.add(current.blocid);
                 sendAppl(ApplMessage.NEGATIVE_MSG,root,
                               new InformativeMessage(InformativeMessage.INFO,
                               current.blocid,me));
            }
        }
 
        while (removeList.size() != 0) {
            blocid = (BigInteger) removeList.removeFirst();
            this.storage.remove(blocid);
            this.leases.remove(blocid);

        }
    }

    private void performReplicaSetUpdate(Node node) {
        //we send the replica list of the files of which we are root to the replica set
        Iterator iter;
        Node entry;
        ReplicaSetList current;
        HashMap updateMessages = new HashMap();
        doReplicaJob(updateMessages);
        doRootJob(updateMessages);
        redownloadMissing();
        iter = updateMessages.keySet().iterator();
        while (iter.hasNext()) {
            entry = (Node) iter.next();
            if (!entry.equals(node)) {
                current = (ReplicaSetList) updateMessages.get(entry);
                sendAppl(ApplMessage.UPDREPLICASET_MSG, entry, current);
            }
        }

    }

    /*
     * When a block is missing, this chooses a source in the replica list.
     * It chooses the first who has the block
     */
    private Node chooseSource(Replica[] replicaSet) {
        ArrayList<Node> list = new ArrayList<Node>();
        for (int i = 0; i < replicaSet.length; i++) {
            if (!(replicaSet[i].n.equals(me)) && replicaSet[i].storesBloc()) {
                list.add(replicaSet[i].n);
                //	return replicaSet[i].n;
            }
        }
        if (list.isEmpty()) {

            return null;
        }
        return list.get(CommonState.r.nextInt(list.size()));
    }

    /*
     * When the current node receives a list of maintainance orders, it has to deal with it...
     */
    private void askForMissingBlocs(ReplicaSetList list, Node node) {
        ReplicaSetLease current;

        while (list.size() != 0) {
            current = (ReplicaSetLease) list.removeFirst();
            if (current.tag == ReplicaSetLease.KEEP_STORING) {//...if it is a keep storing order, it renews the lease if it has the block, and downloads it if the block is missing...
                 int myIndex = current.indexof(node);
                 if(myIndex == -1){
                    System.out.println("["
                             + RoutingTable.truncateNodeId(getNodeId())
                             + "]: not in lease for Bloc {"
                             + RoutingTable.truncateNodeId(current.blocid));
                    continue;
                 }
                current.renewLease();
                if (!storage.containsKey(current.blocid)) {
                   if(current.replicaSet[myIndex].hasBloc){
                       sendAppl(ApplMessage.NEGATIVE_MSG,current.root,
                               new InformativeMessage(InformativeMessage.INFO,
                               current.blocid,me));
                   }
                    if (!downloading(current.blocid)) {
                        Node dest = chooseSource(current.replicaSet);
                        if (dest != null) {
                            /*System.out.println("["
                             + RoutingTable.truncateNodeId(getNodeId())
                             + "]: Requesting to [" + dest.getIndex()
                             + "] Bloc {"
                             + RoutingTable.truncateNodeId(current.blocid)
                             + "}");*/
                            sendAppl(ApplMessage.SENDBLOC_MSG, dest,
                                    new InformativeMessage(InformativeMessage.INFO,
                                    current.blocid, me));

                            downloads.put(current.blocid, dest);
                        }
                    }
                } else {
                    if (storage.containsKey(current.blocid)) {
                        sendAppl(ApplMessage.AFFIRMATIVE_MSG, current.root,
                                new InformativeMessage(InformativeMessage.INFO,
                                current.blocid, node));
                    }
                }
                leases.put(current.blocid, current);

            }
            if (current.tag == ReplicaSetLease.NEW_ROOT) {//...else if it is a new root order,it checks if it's true, and if it is, becomes the new root of the block
                //System.out
                //	.println("[" + me.getIndex() + "] receiving NEW_ROOT");
                 Node node1 = rootOf(current.blocid,current.root);
                if(!node1.equals(me)){
//                     ReplicaSetList newlist = new ReplicaSetList();
//                     newlist.add(current);
//                      sendAppl(ApplMessage.UPDREPLICASET_MSG,node1 , newlist);
                     continue;
                }
                boolean condition = !rootof.containsKey(current.blocid)
                        || (rootof.get(current.blocid).tag == ReplicaSetLease.NEW_ROOT && current.lease > ((ReplicaSetLease) rootof
                        .get(current.blocid)).lease);
               
                if ( condition) {

                    /*System.out.println("["
                     + RoutingTable.truncateNodeId(getNodeId())
                     + "] NEW_ROOT accepted for bloc "
                     + RoutingTable.truncateNodeId(current.blocid));*/
                    rootof.put(current.blocid, current);
                } else {
                    /*System.out.print("["
                     + RoutingTable.truncateNodeId(getNodeId())
                     + "] askformissingblocrootOf :calculated root ID "
                     + node1.getID() + " for bloc "
                     + RoutingTable.truncateNodeId(current.blocid));
                     if (rootof.containsKey(current.blocid))
                     System.out
                     .println(" replicatsetlease already in rootof");
                     else
                     System.out.println("autre raison");*/
                }
            }
        }
    }

    //  
  //  public ReplicaSetStrategy getStrategy() {
   //     return null;
    //}

    /*
     * This method has not been implemented on PASTA yet
     */
    protected void sendToReplicaNodes(Bloc bloc) {
        //    	int strategy=currentStrategy.getStrategy();
        System.out.println("Not Implemented yet");
        System.exit(2);
        //        LeafSet ls=routeLayer.leafSet;
        //    	switch(strategy){
        //    	case ReplicaSetStrategy.DefaultStrategy:
        //	    	int hrr=(bloc.getReplicaRate()-1)/2;//k is odd
        //	        for(int i=0;i<hrr;i++){
        //	//            we send a copy of the Bloc to each replica
        //	        	if(ls.left[i]!=null)
        //	        		routeLayer.send(ls.left[i],new ApplMessage(ApplMessage.PROPAGATE_MSG,bloc,bloc.getSize()));
        //	        	if(ls.right[i]!=null)
        //	        		routeLayer.send(ls.right[i],new ApplMessage(ApplMessage.PROPAGATE_MSG,bloc,bloc.getSize()));
        //	        }
        //	        break;
        //    	case ReplicaSetStrategy.RandomStrategy:
        ////    		Random choice=new Random(currentStrategy.getSeed(bloc.getBlocId()));
        ////    		int index;
        ////    		for(int i=0;i<bloc.getReplicaRate()-1;i++){
        ////    			index=currentStrategy.getRandomIndex(choice);
        ////    			if(index<0){
        ////    				index=-index;
        ////    				if(ls.left[index-1]!=null){
        ////    	        		routeLayer.send(ls.left[index-1],new ApplMessage(ApplMessage.PROPAGATE_MSG,bloc,bloc.getSize()));	
        ////    				}
        ////    			}else{
        ////    				if(ls.right[index-1]!=null){
        ////    					routeLayer.send(ls.right[index-1],new ApplMessage(ApplMessage.PROPAGATE_MSG,bloc,bloc.getSize()));
        ////    				}
        ////    			}
        ////    		}
        //    		break;
        //
        //    	default:
        //    		System.out.println("Replica Set Warning: Unknown Replication Strategy, using default");
        //    		currentStrategy.setStrategy(ReplicaSetStrategy.DefaultStrategy);
        //    		sendToReplicaNodes(bloc);
        //    		break;
        //    	}
    }

    private void propagateToReplicaSet(Bloc Bloc) {
        if (notCorrupted(Bloc)) {
            //            we are the root node for the Bloc so we have to send the Bloc to all replica nodes
            sendToReplicaNodes(Bloc);
        }
    }

    public void placeBloc(Bloc f) {
        manager.placeBloc(f);
    }

    public void reset() {
        leases.clear();
        rootof.clear();
    }

    public int getCharge(){
        return storage.size();
    }
    @Override
    public void processEvent(Node node, int pid, Object event) {
        BigInteger id = null;
        Bloc newBloc = null;
        Bloc f = null;
        Node src = null;
        BigInteger blocid = null;
        ReplicaSetLease lease = null;
        assert event instanceof ApplMessage;
        //leafset = routeLayer.getleafset();

        ApplMessage msg = (ApplMessage) event;
        if (msg.src != null && !msg.src.isUp()) {
            return;
        }
//        if (msg.msgType == ApplMessage.INSERT_MSG
//                || msg.msgType == ApplMessage.PROPAGATE_MSG) {
//            newBloc = (Bloc) msg.body;
//            id = newBloc.getBlocId();
//            if (id.equals(BigInteger.ZERO)) {
//                System.err.println("n = 0");
//            }
//        }

        switch (msg.msgType) {
            case ApplMessage.INSERT_MSG:
                InsertMessageContent content = (InsertMessageContent)(msg.body);
                blocid = content.bloc.getBlocId();
                leases.put(blocid, content.bloclease);
                storage.put(blocid, content.bloc);
                //prevenir le noeud root
                 content.bloclease.confirmedReplica(me);
                     sendAppl(ApplMessage.AFFIRMATIVE_MSG, content.bloclease.root,
                                new InformativeMessage(InformativeMessage.INFO,
                                blocid, node));
                break;

            case ApplMessage.PROPAGATE_MSG://just store the Bloc copy
                storage.put(id, newBloc);
                returnAck(newBloc);
                break;

            case ApplMessage.AFFIRMATIVE_MSG:
                 blocid = (BigInteger) ((InformativeMessage) msg.body).body;
                src = (Node) ((InformativeMessage) msg.body).src;
                lease = (ReplicaSetLease) rootof.get(blocid);
                if (lease != null) {
                    lease.confirmedReplica(src);
                }
                break;
            case ApplMessage.NEGATIVE_MSG:
                 blocid = (BigInteger) ((InformativeMessage) msg.body).body;
                src = (Node) ((InformativeMessage) msg.body).src;
                lease = (ReplicaSetLease) rootof.get(blocid);
                if (lease != null) {
                    lease.InfirmedReplica(src);
                }
                if(RoutingTable.truncateNodeId(blocid).equals("15e2-"))
                 System.out.println(node.getID() + " :receiving negative message from node "+src.getID()+ "for Bloc {" +RoutingTable.truncateNodeId(blocid)
                        + "}");
                break;
            case ApplMessage.REQUEST_MSG: //a distant node wants to get a Bloc stored on this node
                BigInteger key = (BigInteger) ((InformativeMessage) msg.body).body;
                src = (Node) ((InformativeMessage) msg.body).src;
                Bloc match = (Bloc) storage.get(key);
                if (match != null) {//if we have the Bloc, we send it back
                    ApplMessage reply = new ApplMessage(ApplMessage.RETR_MSG,
                            match, match.getSize());
                    bandwidth.send(me, src, reply, pid);
                } else {//else we send an error
                    InformativeMessage err = new InformativeMessage(
                            InformativeMessage.ERROR_NOT_FOUND);
                    ApplMessage reply = new ApplMessage(ApplMessage.ERR_MSG, err,
                            err.getSize());
                    bandwidth.send(me, src, reply, pid);
                }
                break;

            case ApplMessage.RETR_MSG://the requested Bloc has been received
                Bloc seeked = (Bloc) msg.body;
                pasto(Hex(routeLayer.nodeId) + ": Bloc {" + Hex(seeked.getBlocId())
                        + "} has been retrieved");
                break;

            case ApplMessage.REPLY_MSG://after an insertion attempt, the node has to count the receipts
                BigInteger Blocid = (BigInteger) ((InformativeMessage) msg.body).body;

                int nacks = ((Integer) acks.get((BigInteger) Blocid)) - 1;
                if (nacks > 0) {
                    acks.put(Blocid, nacks);
                } else {//all of them have been received, the Bloc has been stored
                    acks.remove(Blocid);
                    pasto("(Time:" + CommonState.getTime() + ")["
                            + RoutingTable.truncateNodeId(routeLayer.nodeId)
                            + "]: Bloc {" + RoutingTable.truncateNodeId(Blocid)
                            + "} successfully inserted");

                }
                break;

            case ApplMessage.UPDREPLICASET_MSG:
                askForMissingBlocs((ReplicaSetList) msg.body, node);
                break;

            case ApplMessage.ERR_MSG://an error occured
                System.out.println(routeLayer.nodeId + ": " + msg);
                break;

            case ApplMessage.REPLICASETMAINTAINANCE_MSG:
                performReplicaSetUpdate(node);
                rearm(false);
                break;

            case ApplMessage.SENDBLOC_MSG:
                blocid = (BigInteger) ((InformativeMessage) msg.body).body;
                f = (Bloc) this.storage
                        .get(blocid);
                src = (Node) ((InformativeMessage) msg.body).src;
                if (f != null) {
                    /*	System.out.println("[" + me.getID() + "]: Returning to ["
                     + src.getID() + "] Bloc {"
                     + RoutingTable.truncateNodeId(f.getBlocId()) + "}");*/
                    ApplMessage mess = new ApplMessage(ApplMessage.RCVBLOC_MSG, f,
                            f.getSize());
                    mess.src = me;
                    bandwidth.send(me, src, mess, pid);
                    MSPastryCommonConfig.EXCHANGES++;
                } else {
                    PastaProtocol distpasta = (PastaProtocol) src.getProtocol(pid);
                    distpasta.downloads
                            .remove((BigInteger) ((InformativeMessage) msg.body).body);

                    System.out.println("["+node.getID()+"] bloc "+ RoutingTable.truncateNodeId(blocid)+ "not in storage");
                }
                break;

            case ApplMessage.RCVBLOC_MSG:

                f = (Bloc) msg.body;
                /*System.out.println("[" + me.getID() + "]: Received Bloc {"
                 + RoutingTable.truncateNodeId(f.getBlocId()) + "} from "
                 + msg.src.getID());*/
                ReplicaSetLease locallease = leases.get(f.getBlocId());
                if (locallease != null) {
                    this.storage.put(f.getBlocId(), f);
                    locallease.confirmedReplica(me);
                     sendAppl(ApplMessage.AFFIRMATIVE_MSG, locallease.root,
                                new InformativeMessage(InformativeMessage.INFO,
                                locallease.blocid, node));
                }
                downloads.remove(f.getBlocId());
                break;

            default:
                break;
        }

    }
}
