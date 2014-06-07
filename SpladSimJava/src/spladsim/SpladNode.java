/**
 * A spladNode is the basic entity of Splad, it represents a process
 * running on a storage node.
 * A spladNode is a Simgrid process
 */
package spladsim;

import java.math.BigInteger;

import org.simgrid.msg.*;

/**
 * @author smonnet
 *
 */
public class SpladNode extends org.simgrid.msg.Process {

	BigInteger uid;

	public SpladNode(Host host, String name, String[]args) { // Mandatory: this constructor is
		super(host,name,args); // used internally
	}

	@Override
	public void main(String[] arg0) throws MsgException {
		this.waitFor(1);
		GlobalKnowledge.register(this);
		Msg.info(uid + "::registered");
		this.waitFor(2);
		
		
		Msg.info(uid + "::leaving - BYE");
	}
}
