/*

 * To change this template, choose Tools | Templates
 * and open the template in the editor.
Lesscharged policy
 */
package peersim.pastPastry;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import peersim.core.CommonState;
import peersim.core.Node;
import java.util.Comparator;
import peersim.core.Network;

/**
 *
 * @author Veronique Simon <veronique.simon@lip6.fr>
 */
public class ExactSelectionPastaProtocol extends PastaProtocol implements Cloneable{
    private class NodeCharge implements Comparator<NodeCharge>{
        Node n;
       int charge;
        NodeCharge(Node n,int charge){
            this.n = n;
            this.charge = charge;
        }

        @Override
        public int compare(NodeCharge o1, NodeCharge o2) {
          return o1.charge-o2.charge;
        }
      
      
        
    }
     protected  ArrayList<NodeCharge> nodesCharge;
   
    public ExactSelectionPastaProtocol(String prefix){
        super(prefix);
    }
    public Object clone(){
        ExactSelectionPastaProtocol obj = null;
       
        obj = (ExactSelectionPastaProtocol)super.clone();
         obj.nodesCharge = new ArrayList<NodeCharge>(selectionRange*2+1);
        return obj;
  }
    
      protected void doRootJob(HashMap updateMessages) {  
        Iterator iter = this.rootof.entrySet().iterator();
        ReplicaSetLease current;
        LinkedList removeList = new LinkedList();
        ReplicaSetList orders;
        ReplicaSetLease dolly;
        int d;
       computeCharges();
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
                            if (current.replicaSet[i].n.getID() == 72 && RoutingTable.truncateNodeId(current.blocid).equals("15e2-")) {
                                System.out.println("bloc " + RoutingTable.truncateNodeId(current.blocid) + " choosed as new replica ");
                            }
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
     * select the less possible charged node 
     * @param nodes array of replicas for this lease
     * @param index index in the array nodes of the new node
     */
    public void findNewOne(Replica[] nodes, int index) {
       
       int i = 0;
       boolean duplicate;
       Collections.sort(nodesCharge,nodesCharge.get(0) );
       for( i = 0;i<nodesCharge.size();i++){
           duplicate = false;
           for(int j = 0;j<nodes.length ;++j){
               if(nodes[j]!=null && nodesCharge.get(i).n.equals(nodes[j].n)){
                   duplicate = true;
                   break;
               }
           }
           if(duplicate == false)
               break;
       }
        nodes[index] = new Replica(nodesCharge.get(i).n);
        nodesCharge.get(i).charge++;
    }

    protected void computeCharges() {
       nodesCharge.clear();
        Node node;
        ExactSelectionPastaProtocol prot;
        for (int i = 1; i <=selectionRange; ++i) {
            node = Network.get(Util.modulo(me.getIndex()+i,Network.size()));
             prot = (ExactSelectionPastaProtocol) node.getProtocol(pid);
            nodesCharge.add(new NodeCharge(node,prot.storage.size()+prot.downloads.size() ));
   
            node = Network.get(Util.modulo(me.getIndex()-i,Network.size()));
            prot = (ExactSelectionPastaProtocol) node.getProtocol(pid);
            nodesCharge.add(new NodeCharge(node, prot.storage.size()+prot.downloads.size()));

        }
        nodesCharge.add(new NodeCharge(me, storage.size()+downloads.size()));
    }
}
