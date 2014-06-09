/**
 * 
 */
package spladsim;

import java.math.BigInteger;

/**
 * @author smonnet
 *
 */
public class Data {
	BigInteger uid;
	BigInteger[] storers;
	BigInteger root;
	long size = GlobalKnowledge.config.fileSize; // default value
	int rf = GlobalKnowledge.config.replFactor; // default value
	
	public Data() {
		uid = GlobalKnowledge.genUID();
		storers = new BigInteger[rf];
	}
}
