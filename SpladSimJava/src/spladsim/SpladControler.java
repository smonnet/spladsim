/**
 * This class in charge of controlling
 * the environment, like failure injection or users activity emulation. 
 */
package spladsim;

import java.math.BigInteger;
import java.util.Iterator;

import org.simgrid.msg.*;
import org.simgrid.msg.Process;

/**
 * @author smonnet
 *
 */
public class SpladControler extends Process {

	double globalMTBF;
	
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
				data.storers.add(GlobalKnowledge.placementPolicy.getStorer(data.uid));
			}
			Iterator<BigInteger> it = data.storers.iterator();
			while(it.hasNext()) {
				GlobalKnowledge.nodes.get(it.next()).dataStore.put(data.uid, data);
				data = data.clone();
			}
			GlobalKnowledge.storedData.put(data.uid, data);		
		}
		Msg.info("There are " + GlobalKnowledge.storedData.size() + " pieces of data in the ring");
		
		// injecting failures
		globalMTBF = GlobalKnowledge.config.mtbf / GlobalKnowledge.ring.size();
		while ((Msg.getClock() < GlobalKnowledge.config.endTime) && 
				(GlobalKnowledge.nodes.size()!=0) && 
				(GlobalKnowledge.storedData.size()!=0)) {
			double nextDeath = getNext();
			this.waitFor(nextDeath);
			SpladNode node = GlobalKnowledge.ring.getRandomNode();
			node.clearData();
			GlobalKnowledge.nodes.remove(node.uid);
			GlobalKnowledge.ring.remove(node.uid);
			node.kill();
			Msg.info("node " + node.uid + " dies " + GlobalKnowledge.nodes.size() + " nodes left");
			globalMTBF = GlobalKnowledge.config.mtbf / GlobalKnowledge.ring.size();
		}
		
		Msg.info("leaving - BYE");
	}
	
	/**
     * Returns the next time point.
     */
    public double getNext() {
        return Math.floor(-Math.log(1 - GlobalKnowledge.rand.nextDouble()) * globalMTBF);
    }   
}
