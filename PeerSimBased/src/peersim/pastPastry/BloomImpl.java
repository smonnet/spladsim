package peersim.pastPastry;

import java.util.*;
import java.math.BigInteger;

import peersim.core.Node;

/*
 * implementation of the bloom filters used by past
 */

public class BloomImpl implements BloomFilterITF{
	private HashMap filter;
	private Node owner;
	private int size;
	
	public BloomImpl(Node owner){
		filter=new HashMap();
		this.owner=owner;
		size=1;
	}
	
	public boolean contains(BigInteger o){
		if(filter.containsKey(o)) return true;
		else return false;
	}
	
	public void put(BigInteger o){
		filter.put(o,"");
	}
	

	public Node getOwner(){
		return owner;
	}

	public void merge(HashMap map){
		Iterator iter=map.keySet().iterator();
		while(iter.hasNext()){
			filter.put((BigInteger)iter.next(),"");
		}
	}
	
	public int getSize(){
		//return (1/800)*filter.size(); //a bloom filter needs approx 10 bits per element (the upsize is in kilobytes)
            return 0;
	}
	
	public void setSize(int size){
		this.size=size;
	}
}