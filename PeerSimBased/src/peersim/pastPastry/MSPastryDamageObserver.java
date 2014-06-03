package peersim.pastPastry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.ControlEvent;
import peersim.lip6.Transport.TwoWayBandwidth2;
import peersim.lip6.pedsim.PControlEvent;

//______________________________________________________________________________________________
public class MSPastryDamageObserver implements Control {

    /**
     * Parameter of the protocol we want to observe
     */
    private static final String PAR_PROT = "protocol";//past
    private static final String PAR_FILES = "files";
    private static final String PAR_BW = "bw";
    private static final String PAR_STEP = "ts";
    private static final String PAR_CHURNPROT = "churnprot";
    private static final String PAR_CTRL = "control";
    private static final String PAR_GNUPLOT_FILE = "gnuplotfile";
    private static final String PAR_LOADS_FILE = "loadsfile";
    private static final String PAR_QUEUES_FILE = "queuesfile";
    private static final String PAR_BLOCS_AGES = "blocagefile";
    private static final String PAR_STOP_PERCENT = "stoppercent";
    //______________________________________________________________________________________________
    /**
     * Protocol n
     */
    private int pid;
    private int nbfiles;
    private String churnname;
    private int bwid;
    //private String prefix;
    private File gnuplotFile;
    private FileWriter results;
    private FileWriter loads;
    private FileWriter queues;
    private FileWriter ages;
    private int ts;
    private int tday;
    private String gnuplotfile_path;
    private String loadsfile_path;
    private String queuesfile_path;
    private String blocagefile_path;
    public static HashMap<BigInteger,Blocmapentry> blocsmap;
    private long occurence;
    // attribut pour surveiller le pourcentage de perte
    
    //pourcentage de perte auquel arreter la simulation
    private int stoppercent;
    //  remaining blocs  number at which simulation stops
    private int waitedremained;
    
    private boolean stopAtFirstlost;

    //  public static HashMap precfiles;
    //______________________________________________________________________________________________
    static{
     blocsmap = new HashMap<BigInteger, Blocmapentry>();
}
    public MSPastryDamageObserver(String prefix) {
        pid = Configuration.getPid(prefix + "." + PAR_PROT);//past id
        nbfiles = Configuration.getInt(prefix + "." + PAR_FILES);
        bwid = Configuration.getPid(prefix + "." + PAR_BW);
        stoppercent = Configuration.getInt(prefix + "."+ PAR_STOP_PERCENT, 0);
        waitedremained = nbfiles - stoppercent*nbfiles/100;
        ts = Configuration.getInt("TS");
        tday = ts * 24 * 3600;
        churnname = "control."
                + Configuration.getString(prefix + "." + PAR_CHURNPROT);
        gnuplotfile_path = Configuration.getString(prefix + "." + PAR_GNUPLOT_FILE, "churnPast.txt");
        loadsfile_path = Configuration.getString(prefix + "." + PAR_LOADS_FILE, "");
        queuesfile_path = Configuration.getString(prefix + "." + PAR_QUEUES_FILE, "");
        blocagefile_path = Configuration.getString(prefix + "." + PAR_BLOCS_AGES, "");
        stopAtFirstlost = Configuration.getBoolean(prefix+".stopAtFirstLost", false);
        
        try {       
            occurence = 0;
            gnuplotFile = new File(gnuplotfile_path);
            results = new FileWriter(gnuplotFile);
            //headers of log files
            results.write("# line format:\n");
            results.write("# simulation time(s\t <# remaining files>");
            for (int index = 1; index < MSPastryCommonConfig.RR; index++) {
                results.write("<#files with " + index + " copies>\t");
            }
            results.write("<network size>\t");

            for (int i = 0; i < TypedChurnGenerator.numTypes; ++i) {
                results.write("#of node of type " + i + " \t");
            }
            results.write("<min storage size>\t<max storage size>\t<blocs mean age>\n");
             } catch (Exception e) {
            System.out.println(e);
        }
         if(loadsfile_path != ""){
         try {
            
            loads = new FileWriter(new File(loadsfile_path));
            loads.write("# measured loads of nodes  during a simulation\n");
            loads.write("# line format:\n");
            loads.write("#<simulation time>(day)\t<node ID>:<load of node>\t<node ID>:<load of node>.... \n");
             } catch (Exception e) {
            System.out.println(e);
        }
         }
         if(queuesfile_path != ""){
         try {
            queues = new FileWriter(new File(queuesfile_path));
            queues.write("#measured bandwidth queues during a simulation\n");
            queues.write("#line format:\n");
            queues.write("#<simulation time>(day)\t <node ID>:<uploads queue size>:<downloads queue size>\t.... \n");
            
        } catch (Exception e) {
            System.out.println(e);
        }
         }
         if(!"".equals(blocagefile_path)){
          try {
            ages = new FileWriter(new File(blocagefile_path));
        } catch (Exception e) {
            System.out.println(e);
        }
         }
    }

    public String Hex(BigInteger i) {
        return i.toString(16);
    }

    //______________________________________________________________________________________________
    /**
     * print the statistical snapshot of the current situation
     *
     * @return boolean
     */
    public boolean execute() {

        PastFamilyProtocol past;
        BigInteger id;
       // HashMap nblocs = new HashMap();
        HashMap storage;
        Iterator iter;
        Integer count;
        int k;
        int[] counts = new int[MSPastryCommonConfig.RR];
        System.out.println();
        System.out.println("***Damage Observer***");
        k = 0;
        int messagesEnQueued = 0;
       // int totaldownloads = 0;
        int minstorageSize = nbfiles;
        int maxstorageSize = 0;
        long sumAges = 0;
        String loadsString = "";
        String queuesString = "";
       

        /*
         * this loop counts all the block copies stored in the DHT and the downloads in progress
         */
        int numTypes = TypedChurnGenerator.numTypes;
        
        for (int type = 0; type < numTypes; type++) {
            ArrayList<TypedNode> li = TypedChurnGenerator.lists[type];
            int len = li.size();
            System.out.println("liste typesnode size=" + len);
            for (int index = 0; index < len; index++) {
                TypedNode n = li.get(index);
                if (n.isUp()) {
                    k++;
                    past = ((PastFamilyProtocol) n.getProtocol(pid));
                   // totaldownloads += past.downloads.size();
                    storage = past.getStorage();
                    int stsize = storage.size();
                    loadsString += n.getID() + ":" + stsize + "\t";
                    if (stsize > maxstorageSize) {
                        maxstorageSize = stsize;
                    }
                    if (stsize < minstorageSize) {
                        minstorageSize = stsize;
                    }
                    iter = storage.entrySet().iterator();
                    TwoWayBandwidth2 band = (TwoWayBandwidth2) past.bandwidth;
                    messagesEnQueued += band.getSize();
                    queuesString +=n.getID() + ":" + band.getUpSize() + ":"+band.getDownSize()+"\t";
                    while (iter.hasNext()) {
                        id = ((Bloc) ((Map.Entry) iter.next()).getValue())
                                .getBlocId();
                        if(!blocsmap.containsKey(id)){
                           
                            blocsmap.put(id,new Blocmapentry(occurence, 1));
                        }else {
                            blocsmap.get(id).nbCopies++;
                        }
//                        if (!nblocs.containsKey(id)) {
//                            nblocs.put(id, new Integer(1));
//                        } else {
//                            count = (Integer) nblocs.remove(id);
//                            nblocs.put(id, count + 1);
//                        }
                    }
                    //pour ne pas avoir de renaissance de blocs
                    // on compte une copie quand un bloc est en train d'etre downloade
                    iter = past.downloads.keySet().iterator();
                     while (iter.hasNext()) {
                         id = (BigInteger)iter.next();
                          if(!blocsmap.containsKey(id)){
                            blocsmap.put(id,new Blocmapentry(occurence, 1));
                            System.err.println("downloading unknown bloc");
                        }else {
                            blocsmap.get(id).nbCopies++;
                        }
                     }
                }

            }
        }
        /*	for(int i=0;i<Network.size();i++){
         if(Network.get(i).isUp()){
         k++;
         past=((MSPastryProtocol)Network.get(i).getProtocol(pid)).getStorageOverlay();
         storage=past.getStorage();
         iter=storage.entrySet().iterator();
         while(iter.hasNext()){
         n=((Bloc)((Map.Entry)iter.next()).getValue()).getBlocId();
         if(!nblocs.containsKey(n)){
         nblocs.put(n,new Integer(1));
         }else{  
         count=(Integer)nblocs.remove(n);
         nblocs.put(n,count+1);
         }
         }

         pasto("["+RoutingTable.truncateNodeId(((MSPastryProtocol)Network.get(i).getProtocol(pid)).nodeId)+"]: "+past.getDownloads().size()+" downloads in progress ("+past.getStorage().size()+" files are already stored)");
         }
         }*/
      //  iter = nblocs.keySet().iterator();
        iter = blocsmap.keySet().iterator();
        int nb = 0, damage = 0, average = 0;
        /*
         * this loop counts how many files are damaged (i.e. have a wrong number of replicas) 
         * and print age for blocs with 0 copies before deleting corresponding entry
         */
        Blocmapentry blocentry;
        while (iter.hasNext()) {
            id = (BigInteger) iter.next();
            blocentry = blocsmap.get(id);
            count = blocentry.nbCopies;
            //reset du compteur
            blocentry.nbCopies = 0;
            
            if (count < MSPastryCommonConfig.RR) {
                damage++;
                if(count == 0){
                    long age = occurence - blocentry.birth;
                    if(ages!=null){
                    try { 
                        ages.write(id.toString(16)+" : "+ age+"\n");
                        ages.flush();
                    } catch (IOException ex) {
                         System.err.println(ex);
                    }
                    }
                    iter.remove();
                } else{
                    sumAges +=occurence - blocentry.birth;
                     nb++;
                }
                average += count;
                /*	System.out.println("Damage: File {"
                 + RoutingTable.truncateNodeId(id) + "} has " + count
                 + " replicas");*/
                counts[count]++;
            } else{
                    sumAges +=occurence - blocentry.birth;
                     nb++;
                }
        }
        

        System.out.println("After " + CommonState.getTime() / ts
                + "sec, the DHT contains " + damage + " damaged files (total: "
                + nb + "/" + nbfiles + ") # numTransert = "
                + MSPastryCommonConfig.EXCHANGES);
        System.out.println("There are " + messagesEnQueued
                + " messages enqueued and " + k + " active nodes, min storage size = " + minstorageSize + " max storage size = " + maxstorageSize);

        //loads logging
        loadsString = CommonState.getTime() / tday + "\t" + loadsString+"\n";
        queuesString = CommonState.getTime() / tday + "\t" +queuesString+"\n";
        if(loads!= null){
        try {
            loads.write(loadsString);
            loads.flush();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        }
         if(queues!= null){
        try {
           
            queues.write(queuesString);
            queues.flush();
        } catch (Exception ex) {
            System.err.println(ex);
        }
         }
        
        /*
         * when the DHT has been correctly filled, this launches the periodic update on all nodes
         * (the maintainance protocol is started only here, because otherwise it would slow down the DHT loading)
         */
        if (!MSPastryCommonConfig.storageOK && nb == nbfiles) {
            MSPastryCommonConfig.storageOK = true;
            for (int i = 0; i < Network.size(); i++) {
                Node node = Network.get(i);
                ((PastFamilyProtocol) node.getProtocol(pid)).rearm(true);
                ((MSPastryProtocol) node
                        .getProtocol(PastFamilyProtocol.mspastryid))
                        .rearm(node);
            }
            String[] names = Configuration.getNames(PAR_CTRL);
            int i = 0;
            for (i = 0; i < names.length; ++i) {
                if (churnname.equals(names[i])) {

                    TypedChurnGenerator churngen = TypedChurnGenerator.instance;
                    assert churngen != null;
                    new ControlEvent(churngen, churngen, i);
                }
            }
        }
        if (MSPastryCommonConfig.storageOK) {
            try {

                results.write(CommonState.getTime() / ts + "\t" + nb + "\t");
                for (int index = 1; index < MSPastryCommonConfig.RR; index++) {
                    results.write(counts[index] + "\t");
                }
                long meanage = sumAges/nb;
                results.write(k + "\t");
                results.write(TypedChurnGenerator.getInfo() + "\t");
                results.write(minstorageSize + "\t" + maxstorageSize + "\t");
                results.write(meanage +"\n");
                results.flush();
               
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        System.out.println("**********************");
        System.out.println();
        //	precfiles = nblocs;
        //incrementation pour le calcul de l'age des blocs perdus
        occurence++;
        if( (stoppercent>0 && waitedremained >= nb)||(MSPastryCommonConfig.storageOK && stopAtFirstlost && nb!=nbfiles)) {
            // stop when a given pourcentage of lost files or first fike lost if configured for.  
            return true;
        }
        else {
            return false;
        }
    }



  
} // enf of class
//______________________________________________________________________________________________

