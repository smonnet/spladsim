/**
 * A spladNode is the basic entity of Splad, it represents a process
 * running on a storage node.
 * A spladNode is a Simgrid process
 */
package spladsim;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.simgrid.msg.*;

/**
 * @author smonnet
 *
 */
public class SpladNode extends org.simgrid.msg.Process {

	BigInteger uid;
	HashMap<BigInteger,Data> dataStore;
	HashMap<BigInteger,Data> rootOf;

	public SpladNode(Host host, String name, String[]args) { // Mandatory: this constructor is
		super(host,name,args); // used internally
		dataStore = new HashMap<BigInteger,Data>();
		rootOf = new HashMap<BigInteger,Data>();
		this.getHost().setData(this);
	}

	@Override
	public void main(String[] arg0) throws MsgException {
		this.waitFor(1);
		GlobalKnowledge.register(this);
		this.waitFor(2);
		Msg.info("Storing " + dataStore.size() + " data blocks");
		Msg.info("DATA STORE OF NODE " + this.uid);
		
		Set<BigInteger> keys = dataStore.keySet();
		Iterator<BigInteger> it = keys.iterator();
		while (it.hasNext()){  // for each block
			BigInteger key = it.next();
			Data data = dataStore.get(key);
			Msg.info("data " + data.uid);
		}
		
		Msg.info("Root for " + rootOf.size() + " data blocks");

		
		
		while ((Msg.getClock() < GlobalKnowledge.config.endTime) && 
				(GlobalKnowledge.nodes.size()!=0) && 
				(GlobalKnowledge.storedData.size()!=0)) {
			this.waitFor(GlobalKnowledge.config.maintenancePeriod);
			// maintenance
			// for each data block I'm root for, check if the storers are still there
			keys = rootOf.keySet();
			it = keys.iterator();
			while (it.hasNext()){  // for each block
				BigInteger key = it.next();
				Data data = rootOf.get(key);
				Msg.info("dealing with data + "+data.uid);
				Iterator<BigInteger> its = data.storers.iterator();
				while(its.hasNext()) { // for each storer for this block
					BigInteger storer = its.next();
					if(!GlobalKnowledge.nodes.containsKey(storer)) { // perfect failure detector
						Msg.info("failure detected");
						Msg.info("nb storer A - " + data.storers.size());
						// failure detected, one block copy to repair
						data.storers.remove(storer); // update metadata
						Msg.info("nb storer B - " + data.storers.size());
						BigInteger newStorer = GlobalKnowledge.placementPolicy.getStorer(data.uid);
						
					}
				}
			}
		}
	}

	public void clearData() { // function called by the controller when the node is killed (restarted)
		rootOf = null;
		Set<BigInteger> keys = dataStore.keySet();
		Iterator<BigInteger> it = keys.iterator();
		while (it.hasNext()){ // the key comes from the local store but the updated data is the global one, for observation purpose
		   BigInteger key = it.next();
		   Data data = GlobalKnowledge.storedData.get(key);
		   data.storers.remove(uid);
		   if(data.storers.isEmpty()) {
			   Msg.info("data + "+data.uid+ "is lost");
			   GlobalKnowledge.storedData.remove(key);
		   }
		}
	}
}
