/**
 * This class is the maestro of the simulation. It is in charge of controlling
 * the environment, like injecting failures or emulate users activity. 
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
		Msg.info("Splad::controler started");
	}
}
