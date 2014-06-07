package test;
public class Message extends org.simgrid.msg.Task {
	int type;
	public Message(String name, double computeDuration, double messageSize, int type) {
		super(name, computeDuration, messageSize);
		this.type = type;
	}
}
