/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.pastPastry;

import java.math.BigInteger;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Scheduler;
import peersim.edsim.ControlEvent;
import peersim.transport.Transport;
import peersim.util.ExtendedRandom;

/**
 *
 * @author Veronique Simon <veronique.simon@lip6.fr>
 */
public class BlocChurnGenerator extends Scheduler implements Control {

    private static final String PAR_CTRL = "control";
    private static final String PAR_PUTRATE = "putrate";
   // private static final String PAR_DELRATE = "delrate";
    //private static final String PAR_PROT = "pastaprotocol";
    private double putrate;
   // private double delrate;
    private  boolean firstTime;
    protected  ExtendedRandom rd;
    private static final String PAR_BlOCSIZE = "blocsize";
    private int blocsize;
    private int srcIndex;
    private static String name;

    public BlocChurnGenerator(String prefix) {
        super(prefix, false);
        name = prefix;
        putrate = Configuration.getDouble(prefix + "." + PAR_PUTRATE);
        blocsize = Configuration.getInt(prefix + "." + PAR_BlOCSIZE);
        //   firstTime = true;
        rd = new ExtendedRandom(Configuration.getLong("random.seed") + 1);
        //mspastid = Configuration.getPid(prefix + "." + PAR_PROT);
        srcIndex = 0;
        firstTime = true;
    }

    @Override
    public boolean execute() {
        if(firstTime){
             String[] names = Configuration.getNames(PAR_CTRL);
            int i = 0;
            for (i = 0; i < names.length; ++i) {
                if (name.equals(names[i])) {
                    new ControlEvent(this, this, i);
                    break;
                }
            } 
            firstTime= false;
        }else{
            System.err.println("ajout d'un bloc");
            addOneBloc();  
        }
        return false;
    }

   

    private void addOneBloc() {

        //tirer un id de bloc
        Bloc bl = new Bloc(blocsize);
        bl.setReplicaRate(MSPastryCommonConfig.RR);
        // determiner le noeud root
        int index = determineClosestNodeIndex(bl.getBlocId());
        srcIndex = rd.nextInt(Network.size());
        bl.setOwner(Network.get(0));
        Node root = Network.get(index);
        PastaProtocol pastaproto = (PastaProtocol) root.getProtocol(PastFamilyProtocol.pid);

        CommonState.setNode(root);
        buildandsendLease(bl, pastaproto);
        // construire la metadonnée
        // l'ajouter a la structure des blocs dont le noeud est root
        // simuler l'envoi du message d'insertion
    }

    public long getNext() {
        return CommonState.getTime() +(long) Math.floor(-Math.log(1 - rd.nextDouble())
                / putrate);
    }

    private int determineClosestNodeIndex(BigInteger blocid) {
       int sz = Network.size();
		BigInteger min, curr;
		int index = 0;
		BigInteger[] net = new BigInteger[sz];
		for (int i = 0; i < sz; ++i) {
			net[i] = getNodeId(i);
		}
		min = PastFamilyProtocol.fastdistance(blocid, getNodeId(0));
		for (int i = 1; i < sz; i++) {
			curr = PastFamilyProtocol.distance(blocid, getNodeId(i));
			if (min.compareTo(curr) > 0) {
				min = curr;
				index = i;
			}
		}
		return index;
    }

    private BigInteger getNodeId(int index) {
		return ((PastFamilyProtocol) Network.get(index).getProtocol(PastFamilyProtocol.pid))
				.getNodeId();
	}
    
    private void buildandsendLease(Bloc bl, PastaProtocol pasta) {
        Replica[] replicaSet;
        ApplMessage message;
        //construire le replica
        // le noeud de commonstate est le noeud root
        replicaSet = pasta.randomReplicaSet();
        ReplicaSetLease lease = new ReplicaSetLease(bl.getBlocId(),
                pasta.me, replicaSet);
        ReplicaSetLease duplease;
        lease.tag = ReplicaSetLease.NEW_ROOT;
        lease.renewLease();
        pasta.rootof.put(bl.getBlocId(), lease);
        duplease = lease.clone();
        duplease.tag = ReplicaSetLease.KEEP_STORING;
        duplease.renewLease();
        Node srcNode = Network.get(srcIndex);
        CommonState.setNode(srcNode);
        Transport transp = (Transport) srcNode.getProtocol(PastFamilyProtocol.bdwid);
        for (int i = 0; i < replicaSet.length; i++) {
            //construire et envoyer les messages
            duplease = duplease.clone();
            if (srcNode != replicaSet[i].n) {
                message = new ApplMessage(ApplMessage.INSERT_MSG, new InsertMessageContent(duplease, bl), bl.getSize());
                message.src = srcNode;
                transp.send(srcNode, replicaSet[i].n, message, PastFamilyProtocol.pid);
            } else {
                //ajouter le bloc localement
                PastaProtocol srcpasta = (PastaProtocol) srcNode.getProtocol(PastFamilyProtocol.pid);
                srcpasta.leases.put(bl.getBlocId(), duplease);
                srcpasta.storage.put(bl.getBlocId(), bl);
                duplease.replicaSet[i].hasBloc = true;
                // mettre a jour egalement la lease du noeud root
                lease.replicaSet[i].hasBloc = true;
            }
        }
    }
}
