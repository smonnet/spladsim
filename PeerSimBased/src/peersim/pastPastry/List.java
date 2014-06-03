package peersim.pastPastry;

/*
 * a classic linked list class.
 * created to speed up the simulation: java linked list is much slower
 */

public class List{
	public Object o;
	public List next;
	public List end;

	private List(){
		o=null;
		next=null;
		end=null;
	}

	public List(Object o){
		this.o=o;
		this.next=null;
		this.end=this;
	}
	
	public void enqueue(Object o){
		List l=new List(o);
		this.end.next=l;
		this.end=l;
	}

	public int size(){
		List courant;
		int len=0;
		courant=this;
		while(courant!=null){
			len++;
			courant=courant.next;
		}
		return len;
	}
		
}