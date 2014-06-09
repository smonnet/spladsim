/**
 * 
 */
package spladsim;

import org.simgrid.msg.Task;

/**
 * @author smonnet
 *
 */
public class Message extends Task {
	int type;
	public Message(String name, double computeDuration, double messageSize, int type) {
		super(name, computeDuration, messageSize);
		this.type = type;
	}
}
