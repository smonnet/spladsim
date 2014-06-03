package peersim.pastPastry;

import java.util.LinkedList;

/*
 * this class simulates a Set of objects.
 * The set can have a non null upsize (in simulation terms),
 * so it can be used to simulation bandwidth issues between nodes.
 */

public class BulkyList implements Bulky{
	private LinkedList list;
	
	public int getSize(){
		return 0;
	}
	
	BulkyList(){
		list=new LinkedList();
	}
	
	public void add(Object o){
		list.add(o);
	}
	
	public Object removeFirst(){
		return list.removeFirst();
	}
	
	public int size(){
		return list.size();
	}
}

