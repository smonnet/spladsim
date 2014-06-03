/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.pastPastry;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.core.Network;

/**
 *
 * @author Veronique Simon <veronique.simon@lip6.fr>
 */
/*
 * This is the new Past Alternative churn resistant protocol
 */
public class SelectivePastaProtocol extends PastaProtocol implements Cloneable {

    //protected HashMap<Node, Integer> nodesCharge;
    //protected ArrayList<Node> selectionNodes;
    
    public SelectivePastaProtocol(String prefix) {
        super(prefix);   

    }

    public Object clone() {
        SelectivePastaProtocol obj = null;
        obj = (SelectivePastaProtocol) super.clone();
        return obj;
    }

    protected void doRootJob(HashMap updateMessages) {
        Node node;
        int myIndex = me.getIndex();
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

    /**
     * Find a new replica node in selection Range. Picks randomly two new nodes in selection range
     * and selects the node with minimum storage size
     *
     * @param nodes array of replicas for this lease
     * @param index index in the array nodes of the new node
     */
    public void findNewOne(Replica[] nodes, int index) {  
        Node firstCandidat = null;
        Node secondCandidat = null;
        boolean duplicate ;
        do {
            duplicate = false;
            firstCandidat = pickRandomNode();
            for (int i = 0; i < nodes.length; i++) {
                    if ((nodes[i] != null) && (i != index)
                            && (nodes[i].n.equals(firstCandidat))) {
                        duplicate = true;
                        break;
                    }
                }
        }while (duplicate);
        //choix du deuxieme candidat
        //on suppose le selection range suffisamment grand pour
        //qu'il y ait toujours deux candidats
         do {
            duplicate = false;
            secondCandidat = pickRandomNode();
            if(secondCandidat == firstCandidat){
                duplicate = true;
                break;
            }
            for (int i = 0; i < nodes.length; i++) {
                    if ((nodes[i] != null) && (i != index)
                            && (nodes[i].n.equals(secondCandidat))) {
                        duplicate = true;
                        break;
                    }
                }
        }while (duplicate);
        
       // on garde le moins charge

       PastaProtocol firstPast = (PastaProtocol) firstCandidat.getProtocol(pid);
       PastaProtocol secondPast = (PastaProtocol) secondCandidat.getProtocol(pid);
        if (firstPast.getStorage().size() > secondPast.getStorage().size()) {
            nodes[index] = new Replica(secondCandidat);
        } else {
            nodes[index] = new Replica(firstCandidat);
        }     
    }
}