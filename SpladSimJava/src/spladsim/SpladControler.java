/**
 * This class in charge of controlling
 * the environment, like failure injection or users activity emulation. 
 */
package spladsim;

import java.math.BigInteger;

import org.simgrid.msg.*;
import org.simgrid.msg.Process;

/**
 * @author smonnet
 *
 */
public class SpladControler extends Process {

	public SpladControler(Host host, String name, String[]args) { // Mandatory: this constructor is
		super(host,name,args); // used internally
		}
	
	@Override
	public void main(String[] arg0) throws MsgException {
		// initializing simulation
		GlobalKnowledge.init(arg0[0]);
		// letting nodes register 
		this.waitFor(2);
		Msg.info("There are " + GlobalKnowledge.ring.size() + 
				" nodes registered in the ring after init");
		GlobalKnowledge.ring.print();	
		Msg.info("Adding initial data...");
		for(long i=0; i<GlobalKnowledge.config.nbFiles; i++) {
			Data data = new Data();
			data.root = GlobalKnowledge.ring.getRoot(data.uid);
			GlobalKnowledge.nodes.get(data.root).rootOf.put(data.uid, data);
			for(int j=0; j<data.rf; j++) {
				data.storers[j] = GlobalKnowledge.placementPolicy.getStorer(data.uid);
				GlobalKnowledge.nodes.get(data.storers[j]).dataStore.put(data.uid, data);
			}
			
			GlobalKnowledge.storedData.put(data.uid, data);		
		}
		Msg.info("There are " + GlobalKnowledge.storedData.size() + " pieces of data in the ring");
		Msg.info("leaving - BYE");
	}
}
