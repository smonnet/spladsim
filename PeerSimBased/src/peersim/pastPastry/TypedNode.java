/**
 * 
 */
package peersim.pastPastry;

import peersim.core.GeneralNode;

/**
 * @author vsimon
 *
 */
public class TypedNode extends GeneralNode {
	private int type;
	public long birth;
	public TypedNode(String prefix)
	{
		super(prefix);
	}
	public int getType()
	{
		return type;
	}

	public void setType(int t)
	{
		type = t;
	}
}
