package peersim.pastPastry;

import peersim.core.*;


public interface BlocTransport extends Protocol
{

public void send(Node src, Node dest, Object msg, int pid,int delay);

public void send(Node src, Node dest, Object msg, int pid);

public long getLatency(Node src, Node dest);


}
