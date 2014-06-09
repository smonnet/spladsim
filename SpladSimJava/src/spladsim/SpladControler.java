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
		
		BigInteger id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		id = GlobalKnowledge.genUID();
		Msg.info("Testing getRoot with " + id + " -- Root is " + GlobalKnowledge.ring.getRoot(id));
		
		Msg.info("Adding initial data");
		
		
		Msg.info("leaving - BYE");
	}
}
