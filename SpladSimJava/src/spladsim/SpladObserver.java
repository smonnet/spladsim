/**
 * This class is used to observe the simulation. Intead of have everything 
 *  written by nodes, this simgrid process periodically wakes up and dump
 *  the global state.
 */
package spladsim;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.Set;

import org.simgrid.msg.Host;
import org.simgrid.msg.Msg;
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
		int day=0;
		// observing global state each day
		this.waitFor(3);
		while ((Msg.getClock() < GlobalKnowledge.config.endTime) && 
				(GlobalKnowledge.nodes.size()!=0) && 
				(GlobalKnowledge.storedData.size()!=0)) {
			this.waitFor(GlobalKnowledge.config.observationPeriod);
			day++;
			Msg.info("Observer (day "+day+") -- # blocs remaining " + GlobalKnowledge.storedData.size());
			Set<BigInteger> keys = GlobalKnowledge.storedData.keySet();
			Iterator<BigInteger> it = keys.iterator();
			long nb0=0, nb1=0, nb2=0, nb3=0;
			while (it.hasNext()){ 
				BigInteger key = it.next();
				Data data = GlobalKnowledge.storedData.get(key);
				switch (data.storers.size()) {
				case 1 : nb1++;
				break;
				case 2 : nb2++;
				break;
				case 3 : nb3++;
				break;
				}
			}
			Msg.info(nb1 + " blocks with 1 copy, " + nb2 + " blocks with 2 copies, " + nb3 + " blocks with 3 copies");
		}
	}

}
