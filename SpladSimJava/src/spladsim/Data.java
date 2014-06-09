/**
 * 
 */
package spladsim;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author smonnet
 *
 */
public class Data {
	BigInteger uid;
	ArrayList<BigInteger> storers;
	BigInteger root;
	long size = GlobalKnowledge.config.fileSize; // default value
	int rf = GlobalKnowledge.config.replFactor; // default value
	
	public Data() {
		uid = GlobalKnowledge.genUID();
		storers = new ArrayList<BigInteger>();
	}
	
	public Data(BigInteger uid) {
		this.uid = uid;
		storers = new ArrayList<BigInteger>();
	}
	
	public Data clone() {
		Data data = new Data(uid);
		data.root = root;
		data.size = size;
		data.rf = rf;
		Iterator<BigInteger> it = storers.iterator(); 
		while(it.hasNext()) {
			data.storers.add(it.next());
		}
		return data;
	}
}
