package peersim.pastPastry;

import java.math.BigInteger;

import peersim.core.Node;
import peersim.lip6.Transport.Message;

/*
 * As "PastMessage" class simulates route-layer messages, this class
 * is for applicative layer messages, used by the storage level.
 * (replica set maintainance protocols, block insertion...)
 */

public class ApplMessage extends Message implements Bulky {
	public static final int INSERT_MSG = 0;
	public static final int REQUEST_MSG = 1;
	public static final int REPLY_MSG = 2;
	public static final int ERR_MSG = 3;
	public static final int RETR_MSG = 4;
	public static final int PROPAGATE_MSG = 6;

	public static final int BLOOMBDCAST_MSG = 5;
	public static final int BLOOMRCV_MSG = 10;
	public static final int SENDBLOC_MSG = 8;
	public static final int RCVBLOC_MSG = 7;
	public static final int REPLICASETMAINTAINANCE_MSG = 9;
	public static final int UPDREPLICASET_MSG = 11;
	public static final int AFFIRMATIVE_MSG = 12;
        public static final int NEGATIVE_MSG = 13;

	public int msgType;
	//  private int upsize;
	public Object body;
	public Node src;
	/**
	 * Available to contains the timestamp of the (creation date of the) message
	 */
	protected long timestamp = 0;

	ApplMessage(int type) {
		super(0, -1);
		this.msgType = type;
	}

	ApplMessage(int type, Object content, int size) {
		this(type);
		this.body = content;
		super.setSize(size);
	}

	public int getSize() {
		return super.getSize();
	}

	public String toString() {
		String ret = null;
		switch (msgType) {
		case INSERT_MSG:
			System.out.println("[INSERTION:" + ((Bloc) body) + "]");
			break;
		case REQUEST_MSG:
			System.out.println("[REQUEST of file " + ((BigInteger) body) + "]");
			break;
		case REPLY_MSG:
			System.out.println("[REPLY: a node has stored file "
					+ ((Bloc) body) + "]");
			break;
		case ERR_MSG:
			System.out.println("[ERROR:" + ((Error) body) + "]");
			break;
		}
		return ret;
	}
}
