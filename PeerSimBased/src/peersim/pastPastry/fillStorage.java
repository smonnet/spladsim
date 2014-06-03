package peersim.pastPastry;

import peersim.config.*;
import peersim.core.*;

/*
 * this initialization class fills the DHT with a given number of blocks.
 *It fully simulates the network, i.e. each file is actually inserted, routed
 *then replicated as in the real DHT. Although, this is very slow with more than 1000 blocks,
 *and won't work with PASTA DHT, because its sendToReplicaNodes hasn't been implemented.
 *Using "fillStorage2" class is recommended 
 */

public class fillStorage implements peersim.core.Control {

    private static final String PAR_PROT = "protocol";
    private static final String PAR_BlocS = "files";
    private final static String BlocSIZE = "fileSize";

    private String prefix;
    private int mspastid;
    private int Blocs;
    private int Blocsize;

    public fillStorage(String prefix) {
        this.prefix = prefix;
        mspastid = Configuration.getPid(this.prefix + "." + PAR_PROT);
        Blocs = Configuration.getInt(this.prefix + "." + PAR_BlocS);       
        Blocsize=Configuration.getInt(prefix+"."+BlocSIZE);
    }

    //______________________________________________________________________________________________
    public boolean execute() {
    	Node insertionPoint;
    	PastFamilyProtocol past;
    	Bloc f;
		for(int i=0;i<Blocs;i++){
	    	do {
		          insertionPoint = Network.get(CommonState.r.nextInt(Network.size()));
	        }  while ((insertionPoint==null)||(!insertionPoint.isUp())) ;
	       
	        past=(PastFamilyProtocol)insertionPoint.getProtocol(mspastid);
	        f=new Bloc(Blocsize);
	        past.insert("",5,f);
		}
		return false;

	} //end execute()

}

