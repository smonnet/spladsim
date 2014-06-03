package peersim.pastPastry;

import peersim.core.CommonState;
import peersim.core.Node;

/**
 *
 * PastMessage class provide all functionalities to magage the various messages, principally LOOKUP
 * messages (messages from application level sender destinated to another application level).<br>
 *
 * Types Of messages:<br>
 * (application messages)<BR>
 * - MSG_LOOKUP: indicates that the body Object containes information to application level of the
 * recipient<BR>
 * <br>
 * (service internal protocol messages)<br>
 * - MSG_JOINREQUEST: message containing a join request of a node, the message is passed between
 * many pastry nodes accorting to the protocol<br>
 * - MSG_JOINREPLY: according to protocol, the body transport information related to a join reply message <br>
 * - MSG_LSPROBEREQUEST:according to protocol, the body transport information related to a probe request message  <br>
 * - MSG_LSPROBEREPLY: not used in the current implementation<br>
 * - MSG_SERVICEPOLL: internal message used to provide cyclic cleaning service of dead nodes<br>
 *
 * The body for message types MSG_JOINREQUEST and MSG_JOINREPLY if defined by the class
 * PastMessage.BodyJoinRequestReply<br>
 * <p>Title: MSPASTRY</p>
 *
 * <p>Description: MsPastry implementation for PeerSim</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: The Pastry Group</p>
 *
 * @author Elisa Bisoffi, Manuel Cortella
 * @version 1.0
 */
//______________________________________________________________________________________
public class PastMessage {

	//______________________________________________________________________________________

	/**
	 * internal generator for unique ISs
	 */
	private static long ID_GENERATOR = 0;

	/**
	 * PastMessage Type
	 */
	public static final int MSG_LOOKUP = 0;

	/**
	 * PastMessage Type
	 */
	public static final int MSG_LSPROBEREQUEST = 3;

	/**
	 * PastMessage Type
	 */
	public static final int MSG_LSPROBEREPLY = 4;

	/**
	 * Internal PastMessage: polling cleaner
	 */
	public static final int MSG_SERVICEPOLL = 5;

	public static final int MSG_ALARM = 6;

	public static final int MSG_POLLREQUEST = 7;

	public static final int MSG_POLLREPLY = 8;

	public static final int MSG_LEAFSETMAINTENANCEREQUEST = 9;

	public static final int MSG_LEAFSETMAINTENANCEREPLY = 10;

	/**
	 * Identify the type of this message
	 */
	public int messageType = MSG_LOOKUP;

	//______________________________________________________________________________________________
	/**
	 * This Object contains the body of the message, no matter what it contains
	 */
	public Object body = null;

	/**
	 * ID of the message. this is automatically generated univocally, and should not change
	 */
	public long id;

	/**
	 * Recipient address of the message
	 */
	public Node dest;

	/**
	 * Source address of the message: has to be filled ad application level
	 */
	public Node src;

	/**
	 * Available to contains the timestamp of the (creation date of the) message
	 */
	protected long timestamp = 0;

	//______________________________________________________________________________________________
	/**
	 * Creates a lookup message with the specified body
	 *
	 * @param body Object body to assign (shallow copy)
	 */
	public PastMessage(Object body) {
		this(MSG_LOOKUP, body);
	}

	//______________________________________________________________________________________________
	/**
	 * Creates an empty message by using default values (message type = MSG_LOOKUP
	 * and <code>new String("")</code> value for the body of the message)
	 */
	public PastMessage() {
		this(MSG_LOOKUP, "");
	}

	//______________________________________________________________________________________________
	/**
	 * Creates an empty message by using default values (message messageType = MSG_LOOKUP and null
	 * value for the body of the message)
	 *
	 * @param messageType int type of the message
	 * @param body Object body to assign (shallow copy)
	 */
	public PastMessage(int messageType, Object body) {
		this.id = (ID_GENERATOR++);
		this.messageType = messageType;
		this.body = body;
		this.timestamp = CommonState.getTime();
	}

	public long getTimeStamp() {
		return timestamp;
	}

	//______________________________________________________________________________________________
	/**
	 * Encapsulates the creation of a join request
	 * @param body Object
	 * @return PastMessage
	 */
	public static final PastMessage makeLookUp(Object body) {
		return new PastMessage(MSG_LOOKUP, body);
	}

	//______________________________________________________________________________________________
	public String toString() {
		String s = "[ID=" + id + "][DEST=" + dest + "]";
		return s + "[Type=" + messageTypetoString() + "] BODY=(...)";
	}

	//______________________________________________________________________________________________
	public PastMessage copy() {
		PastMessage dolly = new PastMessage();
		dolly.messageType = this.messageType;
		dolly.dest = this.dest;
		dolly.body = this.body; // deep cloning?

		return dolly;

	}

	//______________________________________________________________________________________________
	public String messageTypetoString() {
		switch (messageType) {
		case MSG_LOOKUP:
			return "MSG_LOOKUP";
		case MSG_SERVICEPOLL:
			return "MSG_SERVICEPOLL";
		default:
			return "" + messageType;
		}
	}
}
