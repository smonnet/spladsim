
package peersim.pastPastry;

import peersim.config.*;
import peersim.core.*;

/*
 * this transport layer cares about the upsize of the message, unlike
 * peersim native transport protocol
 */

public class pastPastryUnreliableTransport implements BlocTransport 
{

//	---------------------------------------------------------------------
//	Parameters
//	---------------------------------------------------------------------

	/**
	 * The name of the underlying transport protocol. This transport is
	 * extended with dropping messages.
	 * @config
	 */
	private static final String PAR_TRANSPORT = "transport";

	/** 
	 * String name of the parameter used to configure the probability that a 
	 * message sent through this transport is lost.
	 * @config
	 */
	private static final String PAR_DROP = "drop";


//	---------------------------------------------------------------------
//	Fields
//	---------------------------------------------------------------------

	/** Protocol identifier for the support transport protocol */
	private final int transport;

	/** Probability of dropping messages */
	private final float loss;

//	---------------------------------------------------------------------
//	Initialization
//	---------------------------------------------------------------------

	/**
	 * Reads configuration parameter.
	 */
	public pastPastryUnreliableTransport(String prefix)
	{
		transport = Configuration.getPid(prefix+"."+PAR_TRANSPORT);
		loss = (float) Configuration.getDouble(prefix+"."+PAR_DROP);
	}

//	---------------------------------------------------------------------

	public Object clone()
	{
		return this;
	}

//	---------------------------------------------------------------------
//	Methods
//	---------------------------------------------------------------------

	/** Sends the message according to the underlying transport protocol.
	* With the configured probability, the message is not sent (ie the method does
	* nothing).
	*/
	public void send(Node src, Node dest, Object msg, int pid,int delay)
	{
		if(dest == null)
			return;
		try
		{
			if (CommonState.r.nextFloat() >= loss)
			{
				// PastMessage is not lost
				BlocTransport t = (BlocTransport) src.getProtocol(transport);
				t.send(src, dest, msg, pid,delay);
			}
		}
		catch(ClassCastException e)
		{
			throw new IllegalArgumentException("Protocol " +
					Configuration.lookupPid(transport) + 
					" does not implement Transport");
		}
	}
	
	public void send(Node src, Node dest, Object msg, int pid)
	{
		send(src,dest,msg,pid,0);
	}
	
	

	/** Returns the latency of the underlying protocol.*/
	public long getLatency(Node src, Node dest)
	{
		BlocTransport t = (BlocTransport) src.getProtocol(transport);
		return t.getLatency(src, dest);
	}


}
