/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package peersim.pastPastry;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Protocol;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;

/**
 *
 * @author Veronique Simon <veronique.simon@lip6.fr>
 */
public class MSPastryProtocolGlobalImpl extends MSPastryProtocol implements
        Cloneable,Protocol {

    private static final String PAR_DELAY = "updateDelay";
    private static final String PAR_BDELAY = "bloomDelay";
    private static final String PAR_BANDWIDTH = "bandwidth";
    private static final String PAR_RR = "RR";
    private Pair<Node[], Node[]> leafset;
    //static int updatedelay;

    public MSPastryProtocolGlobalImpl(String prefix) {
        this.nodeId = null;
        mspastryid = CommonState.getPid();
        int b = 0, l = 0, base = 0, bloomDelay = 0, replicaRate = 0;
        final String PAR_B = "B";
        final String PAR_L = "L";

       // updatedelay = Configuration.getInt(prefix + "." + PAR_DELAY);
        bloomDelay = Configuration.getInt(prefix + "." + PAR_BDELAY);
        b = Configuration.getInt(prefix + "." + PAR_B, 4);
        l = Configuration.getInt("network.size");
        base = Util.pow2(b);
        replicaRate = Configuration.getInt(prefix + "." + PAR_RR, 5);

        MSPastryCommonConfig.B = b;
        MSPastryCommonConfig.L = l;
        MSPastryCommonConfig.BASE = base;
        MSPastryCommonConfig.BD = bloomDelay;
        MSPastryCommonConfig.RR = replicaRate;
        MSPastryCommonConfig.STR = Configuration.getInt(prefix + ".strategy");
        //leafsetsize = Network.size();
    }

    public Object clone() {
        MSPastryProtocolGlobalImpl obj = null;
        try {
            obj = (MSPastryProtocolGlobalImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        obj.nodeId = null;
        obj.leafset = null;
        return obj;
    }

    @Override
    public void join() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BlocTransport getTr(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

//    @Override
//    public Pair<Node[], Node[]> getleafset() {
//        int length = (Network.size()+1) / 2;
//        Pair<Node[], Node[]> pair = Pair.of(new Node[length], new Node[length]);
//        //Node[] tmp = new Node[length];
//        int myIndex = CommonState.getNode().getIndex();
//        //int index = 0;
//        //remplir la partie droite
//        int leftIndex = myIndex;
//        int rightIndex = myIndex;
//
//        int inserted = 0;
//        while (true) {
//            //aller vers la droite
//            do {
//                rightIndex++;
//                if (rightIndex == Network.size()) {
//                    rightIndex = 0;
//                }
//            } while (!Network.get(rightIndex).isUp());
//            if (rightIndex == leftIndex) {
//                leafset = pair;
//                return leafset;
//            }
//            pair.right[inserted / 2] = Network.get(rightIndex);
//            inserted++;
//            //aller vers la gauche
//            do {
//                leftIndex--;
//                if (leftIndex < 0) {
//                    leftIndex = Network.size() - 1;
//                }
//            } while (!Network.get(leftIndex).isUp());
//            if (leftIndex == rightIndex) {//un de plus a droite qu'a gauche
//                leafset = pair;
//                return leafset;
//            }
//            pair.left[inserted / 2] = Network.get(leftIndex);
//            inserted++;
//        }
//    }

    public static int getLeafsetHsize() {
        return (Network.size()+1) / 2;
    }
}
