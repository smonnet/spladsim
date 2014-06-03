package peersim.pastPastry;

/**
 * Fixed Parameters of a pastry network. They have a default value and can be configured at
 * startup of the network, once only.
 *
 * <p>Title: MSPASTRY</p>
 *
 * <p>Description: MsPastry implementation for PeerSim</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: The Pastry Group</p>
 *
 * @author Elisa Bisoffi, Manuel Cortella
 * @version 1.0
 */
public class MSPastryCommonConfig {

	public static final int DIGITS = 32; /*                 default 32 */
	public static final int BITS = 128; /*                 default 128*/

	public static int B = 4; /*                 default   4*/
	public static int BASE = 16; /*   = 2^B         default  16*/

	public static int L = 32; /*  =BITS/B        default  32*/

	//public static 	  int UD     = 5000;         /* =delay between two keepalive pollings default 5000*/

	public static int BD = 5000; /* =delay between two bloom-filter-broadcasts (used in PAST)*/

	public static int RR = 5; /* this is the default replica rate for PAST */

	// public static       int UP     = 1000;         /*this is the default upload bandwidth in kilobytes*/

	//public static       int DOWN   = 10000;        /*this is the default download bandwidth in kilobytes*/

	public static int STR = 0; /*this is the strategy for the replica set maintainance protocol (see ReplicaSetStrategy class for details)*/

	public static int EXCHANGES = 0;

	public static final double bandwidthScaleFactor = 0.125; /*this factor is used to convert measures in mbit per second to kbytes per millisecond*/

	public static final boolean DEBUG = false;

	public static final boolean PASTLOG = false;

	/*
	 * these are used by the damage observer for simulation monitoring
	 */
	public static boolean storageOK = false;
	public static boolean turbulencesOK = false;
	public static long turbulenceEnd = 0;

	/**
	 * short information about current mspastry configuration
	 * @return String
	 */
	public static String info() {
		return String.format("[B=%d][L=%d][BITS=%d][DIGITS=%d]", B, L, BITS,
				DIGITS);
	}

}
