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
	public static HashMap<BigInteger, SpladNode> nodes;
	public static Random rand;
	public static HashMap<BigInteger,Data> storedData;
	public static PlacementPolicy placementPolicy;

	/* called by the controller to initialize some variables */
	public static void init(String configFile) {
		config = new Configuration(configFile);
		rand = new Random(config.seed);
		ring = new Ring();
		storedData = new HashMap<BigInteger,Data>();
		nodes = new HashMap<BigInteger, SpladNode>();
		switch (config.placementPolicie) {
		case 0: placementPolicy = new RandomChoice();
		Msg.info("Random policy instanciated");
		break;
		case 1: placementPolicy = new LessChargedChoice();
		Msg.info("Less charged policy instanciated");
		break;
		case 2: placementPolicy = new PowerOfChoice();
		Msg.info("Power og choice policy instanciated");
		break;
		default: Msg.info(" XXXX Wrong placement policy choice in property file XXXX ");
			break;
		}
	}

	public static BigInteger register(SpladNode spladNode) {
		BigInteger id = new BigInteger(nbBit, rand);
		spladNode.uid = id;
		ring.insert(spladNode);
		nodes.put(id, spladNode);
		return id;
	}

	public static BigInteger genUID() {
		return new BigInteger(nbBit, rand);
	}
}

