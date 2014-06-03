

 package peersim.pastPastry;

import peersim.config.*;
import peersim.core.*;
import peersim.edsim.*;


/**
 * Implement a transport layer that reliably delivers messages with a random
 * delay, that is drawn from the configured interval according to the uniform
 * distribution.
 * According to the upsize of the message, it calculates the transfer delay added
 * to the latency in order to simulate bandwidth
 * @author Alberto Montresor and Sergey Legtchenko
 * @version $Revision: 4 $
 */
public class pastPastryTransport implements BlocTransport
{

//---------------------------------------------------------------------
//Parameters
//---------------------------------------------------------------------

/** 
 * String name of the parameter used to configure the minimum latency.
 * @config
 */	
private static final String PAR_MINDELAY = "mindelay";	
	
/** 
 * String name of the parameter used to configure the maximum latency.
 * Defaults to {@value #PAR_MINDELAY}, which results in a constant delay.
 * @config 
 */	
private static final String PAR_MAXDELAY = "maxdelay";	
	
//---------------------------------------------------------------------
//Fields
//---------------------------------------------------------------------

/** Minimum delay for message sending */
private final long min;
	
/** Difference between the max and min delay plus one. That is, max delay is
* min+range-1.
*/
private final long range;

	
//---------------------------------------------------------------------
//Initialization
//---------------------------------------------------------------------

/**
 * Reads configuration parameter.
 */
public pastPastryTransport(String prefix)
{
	min = Configuration.getLong(prefix + "." + PAR_MINDELAY);
	long max = Configuration.getLong(prefix + "." + PAR_MAXDELAY,min);
	if (max < min) 
	   throw new IllegalParameterException(prefix+"."+PAR_MAXDELAY, 
	   "The maximum latency cannot be smaller than the minimum latency");
	range = max-min+1;
}

//---------------------------------------------------------------------

/**
* Retuns <code>this</code>. This way only one instance exists in the system
* that is linked from all the nodes. This is because this protocol has no
* node specific state.
*/
public Object clone()
{
	return this;
}

//---------------------------------------------------------------------
//Methods
//---------------------------------------------------------------------

/**
 * Delivers the message with a random
 * delay, that is drawn from the configured interval according to the uniform
 * distribution.
*/
public void send(Node src, Node dest, Object msg, int pid)
{
	// avoid calling nextLong if possible
	long delay = (range==1?min:min + CommonState.r.nextLong(range));
	if(dest == null)
		return;
	EDSimulator.add(delay, msg, dest, pid);
}

public void send(Node src, Node dest, Object msg, int pid,int delay)
{
	EDSimulator.add(getLatency(src,dest)+delay, msg, dest, pid);
}

/**
 * Returns a random
 * delay, that is drawn from the configured interval according to the uniform
 * distribution.
*/
public long getLatency(Node src, Node dest)
{
	return (range==1?min:min + CommonState.r.nextLong(range));
}


}

