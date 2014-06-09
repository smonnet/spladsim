/**
 * 
 */
package spladsim;

import org.simgrid.msg.Host;
import org.simgrid.msg.MsgException;
import org.simgrid.msg.Process;

/**
 * @author smonnet
 *
 */
public class DataTransferReceiver extends Process {

	public DataTransferReceiver(Host host, String name, String[]args) { // Mandatory: this constructor is
		super(host,name,args); // used internally
	}
	
	/* (non-Javadoc)
	 * @see org.simgrid.msg.Process#main(java.lang.String[])
	 */
	@Override
	public void main(String[] arg0) throws MsgException {
		// TODO Auto-generated method stub

	}

}
