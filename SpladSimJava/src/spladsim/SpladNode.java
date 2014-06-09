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
	}

	@Override
	public void main(String[] arg0) throws MsgException {
		this.waitFor(1);
		GlobalKnowledge.register(this);
		this.waitFor(2);
		Msg.info("Storing " + dataStore.size() + " data blocks");
		
	}
}
