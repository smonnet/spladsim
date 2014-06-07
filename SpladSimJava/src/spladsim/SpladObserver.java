/**
 * This class is used to observe the simulation. Intead of have everything 
 *  written by nodes, this simgrid process periodically wakes up and dump
 *  the global state.
 */
package spladsim;

import org.simgrid.msg.Host;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;

/**
 * @author smonnet
 *
 */
public class SpladObserver extends Process {

	public SpladObserver(Host host, String name, String[]args) { // Mandatory: this constructor is
		super(host,name,args); // used internally
		}
	
	@Override
	public void main(String[] arg0) throws MsgException {
		// TODO Auto-generated method stub

	}

}
