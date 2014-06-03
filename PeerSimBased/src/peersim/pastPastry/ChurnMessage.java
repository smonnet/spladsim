package peersim.pastPastry;


public class ChurnMessage {
	public static final int MSG_ADD_NODE = 1;
	public static final int MSG_STOP_NODE = 2;
	
	public int messageType;
	ChurnMessage(int type)
	{
		messageType = type;
	}

}
