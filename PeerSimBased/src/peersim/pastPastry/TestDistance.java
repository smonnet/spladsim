package peersim.pastPastry;

import java.math.BigInteger;
import java.util.Random;

public class TestDistance {

	static BigInteger max, half;

	//backward distance from a to b 
	private static BigInteger bckDistance(BigInteger a, BigInteger b) {
		int c = a.compareTo(b);
		//BigInteger max;
		//min = BigInteger.ZERO;//((MSPastryProtocol)Network.get(0).getProtocol(mspastryid)).nodeId;
		//max = new BigInteger("ffffffffffffffffffffffffffffffff", 16);//((MSPastryProtocol)Network.get(Network.size()-1).getProtocol(mspastryid)).nodeId;
		if (c == 0)
			return BigInteger.ZERO;
		return (c > 0) ? a.subtract(b) : a.add(max.subtract(b));
	}

	//  computes the distance between two nodes
	public static BigInteger distance(BigInteger a, BigInteger b) {
		BigInteger fd = fwdDistance(a, b);
		BigInteger bd = bckDistance(a, b);
		return (fd.compareTo(bd) < 0) ? fd : bd;
	}

	public static BigInteger fastdistance(BigInteger a, BigInteger b) {

		int comp = a.compareTo(b);
		if (comp == 0)
			return BigInteger.ZERO;
		if (comp > 0) {
			BigInteger c = a.subtract(b);
			return (c.compareTo(half) > 0) ? c.subtract(half) : c;
		}
		BigInteger c = b.subtract(a);
		return (c.compareTo(half) > 0) ? c.subtract(half) : c;

	}

	//forward distance from a to b
	private static BigInteger fwdDistance(BigInteger a, BigInteger b) {
		int c = a.compareTo(b);
		if (c == 0)
			return BigInteger.ZERO;
		return (c > 0) ? (max.subtract(a)).add(b) : b.subtract(a);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Random r = new Random();
		max = new BigInteger("ffffffffffffffffffffffffffffffff", 16);
		BigInteger deux = new BigInteger("2");
		BigInteger max2 = deux.pow(128).subtract(BigInteger.ONE);
		half = max.divide(new BigInteger("2"));
		System.out.println("max= " + max + "	max2=" + max2);
		BigInteger val1 = new BigInteger(128, r);
		BigInteger val2 = new BigInteger(128, r);

		long time = System.currentTimeMillis();
		for (int i = 0; i < 1; i++) {
			//BigInteger val1 = new BigInteger(128, r);
			//BigInteger val2 = new BigInteger(128, r);

			BigInteger dist = distance(val1, val2);

			System.out.println("distance = " + dist);
		}
		long time2 = System.currentTimeMillis();
		long duree = time2 - time;
		System.out.println("temps distance = " + duree);
		time = System.currentTimeMillis();

		for (int i = 0; i < 1; i++) {
			//BigInteger val1 = new BigInteger(128, r);
			//BigInteger val2 = new BigInteger(128, r);

			BigInteger dist2 = fastdistance(val1, val2);

			System.out.println("distance = " + dist2);
		}
		time2 = System.currentTimeMillis();
		duree = time2 - time;
		System.out.println("temps fast distance = " + duree);
	}

}
