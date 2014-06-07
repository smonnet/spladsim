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

	BigInteger id;	

	public SpladNode(Host host, String name, String[]args) { // Mandatory: this constructor is
		super(host,name,args); // used internally
	}

	@Override
	public void main(String[] arg0) throws MsgException {

		// TODO Auto-generated method stub
	}

}
