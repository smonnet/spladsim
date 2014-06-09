/**
 * 
 */
package spladsim;

/**
 * @author smonnet
 *
 */
public class DataTransfer extends Message {
	public DataTransfer(String name, double computeDuration, double messageSize, int type) {
		super(name, computeDuration, messageSize, type);
	}
}
