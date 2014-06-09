/**
 * 
 */
package spladsim;

/**
 * @author smonnet
 *
 */
public class ControlMessage extends Message {
	public ControlMessage(String name, double computeDuration, double messageSize, int type) {
		super(name, computeDuration, messageSize, type);
	}
}
