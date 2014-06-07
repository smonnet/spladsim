package test;
import org.simgrid.msg.*;

public class Master extends org.simgrid.msg.Process {
	public Master(Host host, String name, String[]args) { // mandatory constructor
		super(host,name,args);
	}
	public void main(String[ ] args) throws MsgException {
		//	int numberOfTasks = Integer.valueOf(args[0]).intValue();
		//	double taskComputeSize = Double.valueOf(args[1]).doubleValue();
		//	double taskCommunicateSize = Double.valueOf(args[2]).doubleValue();
		//	int workerCount = Integer.valueOf(args[3]).intValue();
		double startTime = Msg.getClock();
		Globals.pid = this.getPID();
		Msg.info(host + " sending ping - " + startTime);
		Message m = new Message("ping",0,10E9, 0);
		try{
			m.send("worker-0");
		} catch (HostFailureException e) {
			Msg.info(host + " Master - an error occured during send");
			e.printStackTrace();
			exit();
			return;
		}
		Msg.info(host + " Master - still alive !!");
		Message.receive("master");
		Msg.info(host + "+ has received ping at " + Msg.getClock());
		Msg.info("RTT between is " + (Msg.getClock() - startTime));
		//	Msg.info("Got "+ workerCount + " workers and " + numberOfTasks + " tasks.");
		//	for (int i = 0; i < numberOfTasks; i++) {
		//	    Message task = new Message("Task_" + i ,taskComputeSize,taskCommunicateSize, 0);
		//	    task.send("worker-" + (i % workerCount));
		//	    Msg.info("Send completed for the task " + task.getName() +
		//		     " on the mailbox worker-" + (i % workerCount) + "0");
		//	}
		//	for (int i = 0; i < 3; i++) {
		//		Message task = new Message("Task_" + i ,taskComputeSize,taskCommunicateSize, 1);
		//		task.send("worker-" + (i));
		//	}
		Msg.info("Goodbye now!");
	} 
}
