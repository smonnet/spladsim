package peersim.pastPastry;

import peersim.core.Node;

/*
 * this class regroup some informative messages exchanged between DHT nodes.
 *It's main goal is to implement Bulky interface, allowing messages to have
 *non-null upsize in order to affect bandwidth 
 */

public class InformativeMessage implements Bulky{
    public static final int ERROR_NOT_FOUND = 0;
    public static final int INFO = 1;
    public int errType;
    public Object body;
    private int size;
    public Node src;
    
    InformativeMessage(int type){
        errType=type;
        size=0;
    }
    
    InformativeMessage(int type,Object body){
    	this(type);
    	this.body=body;
    }
    
    InformativeMessage(int type,Object body,Node src){
    	this(type,body);
    	this.src=src;
    }
    
    public String toString(){
        String ret=null;
        switch(errType){
            case ERROR_NOT_FOUND:
                ret="Error: File not found";
                break;
            case INFO:
            	ret="Informative message ";
            	break;
        }
        return ret;
    }
    
    public int getSize(){
    	return 0;
    }
}