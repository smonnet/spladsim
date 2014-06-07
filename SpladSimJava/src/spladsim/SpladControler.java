/**
 * This class in charge of controlling
 * the environment, like failure injection or users activity emulation. 
 */
package spladsim;

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
		GlobalKnowledge.init(new Integer(arg0[0]).intValue());
		// letting nodes register 
		this.waitFor(2);
		Msg.info("There are " + GlobalKnowledge.ring.size() + 
				" nodes registered in the ring after init");
		GlobalKnowledge.ring.print();
		Msg.info("Adding initial data");
		
		
		Msg.info("leaving - BYE");
	}
}
