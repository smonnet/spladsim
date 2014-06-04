package test;
import org.simgrid.msg.*;
public class Master extends org.simgrid.msg.Process {
    public Master(Host host, String name, String[]args) { // mandatory constructor
	super(host,name,args);
    }
    public void main(String[ ] args) throws MsgException {
	int numberOfTasks = Integer.valueOf(args[0]).intValue();
	double taskComputeSize = Double.valueOf(args[1]).doubleValue();
	double taskCommunicateSize = Double.valueOf(args[2]).doubleValue();
	int workerCount = Integer.valueOf(args[3]).intValue();
	Msg.info("Got "+ workerCount + " workers and " + numberOfTasks + " tasks.");
	for (int i = 0; i < numberOfTasks; i++) {
	    BasicTask task = new BasicTask("Task_" + i ,taskComputeSize,taskCommunicateSize);
	    task.send("worker-" + (i % workerCount));
	    Msg.info("Send completed for the task " + task.getName() +
		     " on the mailbox worker-" + (i % workerCount) + "0");
	}
	Msg.info("Goodbye now!");
    } 
}
