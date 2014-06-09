/**
 * This class gathers all the global knowledge used in spladSim.
 * In is used for routing messages and failure detection (it allows
 * to assume that every spladnode has a complete view)
 */
package spladsim;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

import org.simgrid.msg.Msg;

/**
 * @author smonnet
 *
 */
public final class GlobalKnowledge {
	public static Configuration config;
	public static final int nbBit = 128;
	public static Ring ring;
	public static Random rand;
	public static HashMap<BigInteger,Data> StoredData;

	/* called by the controller to initialize some variables */
	public static void init(String configFile) {
		config = new Configuration(configFile);
		rand = new Random(config.seed);
		ring = new Ring();
	}
	
	public static BigInteger register(SpladNode spladNode) {
		BigInteger id = new BigInteger(nbBit, rand);
		spladNode.uid = id;
		ring.insert(spladNode);
		return id;
	}
	
	public static BigInteger genUID() {
		return new BigInteger(nbBit, rand);
	}
}

