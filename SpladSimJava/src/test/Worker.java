package test;

import org.simgrid.msg.*;

public class Worker extends org.simgrid.msg.Process {
public Worker(Host host, String name, String[]args) { // Mandatory: this constructor is
super(host,name,args); // used internally
}
public void main(String[ ] args) throws TransferFailureException, HostFailureException,
TimeoutException, TaskCancelledException {
String id = args[0];
//while (true) {
if(id.equals("0")) {
	try {
		this.waitFor(1);
		Msg.info(host + " has waited for 1 sec, it is " + Msg.getClock() + "killing master now !!");
		Msg.info(host + "  ");
		(org.simgrid.msg.Process.fromPID(Globals.pid)).kill();
} catch (NativeException e) {
	Msg.info("in catch " + Msg.getClock());
	// TODO Auto-generated catch block
	e.printStackTrace();
}
} else {
	Msg.info(host + " waiting for message from master " + Msg.getClock());
Message m = (Message) Message.receive("worker-" + id);
Msg.info(host + " has received ping at " + Msg.getClock());
m.send("master");
}
//if (t instanceof FinalizeTask)
//break;
//if(m.type == 1)
//	break;
//Msg.info("Processing " + m.getName() + "");
//m.execute();
//Msg.info("" + m.getName() + " done ");
//}
//Msg.info("Received Finalize. I'm done. See you!");
} 
}
