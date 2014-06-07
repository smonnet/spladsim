/**
 * This class gathers all the global knowledge used in spladSim.
 * In is used for routing messages and failure detection (it allows
 * to assume that every spladnode has a complete view)
 */
package spladsim;

import java.math.BigInteger;
import java.util.Random;

import org.simgrid.msg.Msg;

/**
 * @author smonnet
 *
 */
public final class GlobalKnowledge {
	public static final int nbBit = 128;
	public static Ring ring;
	public static Random rand;

	/* called by the controller to initialize some variables */
	public static void init(int seed) {
		Msg.info("GlobalKnowledge simulation seed: "+ seed);
		rand = new Random(seed);
		ring = new Ring();
	}
	public static BigInteger register(SpladNode spladNode) {
		BigInteger id = new BigInteger(nbBit, rand);
		spladNode.uid = id;
		ring.insert(spladNode);
		return id;
	}
}

