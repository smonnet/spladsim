package peersim.pastPastry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.Scheduler;
import peersim.util.ExtendedRandom;

/**
 * This control is responsible for scheduling arrival and departures of nodes .
 * and is designed to be used in combination with the TypedNode and
 * MSPastryDamageObserver classes . It has to be scheduled at time 0 for the
 * nodes to be intialized (types are asigned to nodes). When all files are
 * sprade over the network, the MSPastryDamageObserver class create a
 * controlEvent with the unique instance of this class in both constructor
 * argument. This call initiates the recurrent execution of the control.
 *
 * @author veronique Simon
 */
public class TypedChurnGenerator extends Scheduler implements Control {

    @SuppressWarnings("unchecked")
    private int mid;
    private static final String PAR_PAST = "past";
    public int mspastid;
    //private int minnodes;
    private int[] means;
    public static int numTypes;
    private double[] leaverates;
    private double[] probabilities;//probabilités d'arrivéé d'un noeud selon le type
    private int totalmean;
    //private double totaladdrate;
    public static ArrayList<TypedNode>[] lists;//listes des noeuds actifs par type
    //  private static int numActiveNodes;
    private double instantleaverate;
    public static TypedChurnGenerator instance;
    private boolean firstime;
    public long numop;
    private static ExtendedRandom rd;
    private BufferedWriter churns;
    private String churnsfile;
    private int ts;

    // public static TypedNode leader;
    public TypedChurnGenerator(String prefix) {
        super(prefix, false);
        mspastid = Configuration.getPid(prefix + "." + PAR_PAST);
        // minnodes = Configuration.getInt(prefix + ".minnodes", 20);
        numTypes = Configuration.getInt(prefix + ".numtypes", 2);
        means = new int[numTypes];
        leaverates = new double[numTypes];
        probabilities = new double[numTypes];
        totalmean = 0;
        instantleaverate = 0;
        //  totaladdrate = 0;
        for (int i = 0; i < numTypes; ++i) {
            means[i] = Configuration.getInt(prefix + ".type" + i + ".mean");
            String str = prefix + ".type" + i + ".mtbf";
            int leave = Configuration.getInt(str);
            leaverates[i] = 1.0 / leave;
            totalmean += means[i];
            instantleaverate += means[i] * leaverates[i];
        }
        for (int i = 0; i < numTypes; ++i) {
            probabilities[i] = means[i] * leaverates[i] / instantleaverate;
        }
        lists = (ArrayList<TypedNode>[]) new ArrayList[numTypes];
        for (int i = 0; i < numTypes; ++i) {
            lists[i] = new ArrayList<TypedNode>();
        }
        instance = this;
        firstime = true;
        numop = 0;
        rd = new ExtendedRandom(Configuration.getLong("random.seed"));
        churnsfile = Configuration.getString(prefix + ".churnsfile", "");
        if(churnsfile != ""){
        try {
            churns = new BufferedWriter(new FileWriter(churnsfile));
            churns.write("#line format:\n");
            churns.write("time (sec)\t<event-type>\t<node ID>\t <# of file stored(failure only)>\n");
        } catch (IOException ex) {
            System.err.println("Failed to create churns.txt file");
        }
        }
        ts = Configuration.getInt("TS");
    }

    /**
     * simule la perte d un noeud
     */
    public void removeOneNode() {
        int type;
        int randomIndex;
        // determination du type de noeud a enlever
        do {
            type = nextleaveType();
        } while (lists[type].size() <= 1);
        TypedNode toremove;
        // choix du noeud de ce type
        randomIndex = rd.nextInt(lists[type].size());
        toremove = lists[type].get(randomIndex);
        PastFamilyProtocol random = (PastFamilyProtocol) (toremove
                .getProtocol(mspastid));
        // log de l evenement
        int numBlocks = random.getStorage().size();
        if (churns != null) {
            try {
                churns.write(CommonState.getTime() / ts + "\tFAILURE\t" + toremove.getID() + "\t" + numBlocks + "\n");
                churns.flush();
            } catch (IOException ex) {
            }
        }
        // supression effective du noeud et des telechargements
        lists[type].remove(randomIndex);
        random.clearUnfinishedDownloads();
        random.endSession();
        toremove.setFailState(Node.DOWN);
        Network.removeNondestructively(toremove.getIndex());
        
        random.getStorage().clear();
        random.reset();
        //remplacer par un noeud du meme type
        addOneNode(type);

    }

    public void addOneNode(int type) {

        TypedNode newnode = null;
        CommonState.setNode(newnode);
        newnode = (TypedNode) Network.prototype.clone();
        newnode.setType(type);
        newnode.birth = CommonState.getTime();
        lists[type].add(newnode);
        Network.add(newnode);
        ((PastFamilyProtocol) newnode.getProtocol(mspastid)).join();
        if (churns != null) {
            try {
                churns.write(CommonState.getTime() / ts + "\tADDED\t" + newnode.getID() + "\n");
                churns.flush();
            } catch (IOException ex) {
            }
        }
    }

    public void addNodeinList(TypedNode node, int type) {
        lists[type].add(node);
    }

    public TypedNode removeNodeinList(int index, int type) {
        return lists[type].remove(index);
    }

    public int getSize() {
        return numTypes;
    }

    public int getmean(int type) {
        return means[type];
    }

    private int nextleaveType() {
        int type;
        double rand = rd.nextDouble() * instantleaverate;
        double sum = 0;
        for (type = 0; type < numTypes; type++) {
            sum += lists[type].size() * leaverates[type];
            if (rand < sum) {
                break;
            }
        }
        assert type != numTypes;
        return type;
    }

    public static String getInfo() {
        String info = "";
        if (lists != null) {
            for (int i = 0; i < numTypes; ++i) {
                info += lists[i].size() + "\t";
            }
        }
        return info;
    }

    /**
     * Returns the next time point.
     */
    public long getNext() {
        return CommonState.getTime()
                + (long) Math.floor(-Math.log(1 - rd.nextDouble())
                / (instantleaverate));
    }

    @Override
    public boolean execute() {
        if (firstime) {
            initialize();
            firstime = false;
            return false;
        }
        numop++;
        removeOneNode();
        return false;
    }

    /**
     * set type of nodes in network
     */
    public void initialize() {
        int k = 0;
        for (int i = 0; i < numTypes; ++i) {
            int s = getmean(i);
            for (int j = 0; j < s; ++j) {
                if (k > Network.size()) {
                    break;
                }
                TypedNode node = (TypedNode) Network.get(k);
                node.setType(i);
                addNodeinList(node, i);
                ++k;
            }
            if (k > Network.size()) {
                break;
            }
        }

    }
}
