package peersim.pastPastry;

public final class Pair<A,B> {
	public final A left;
	public final B right;
	
	private Pair(A first, B second) {
	    this.left = first;
	    this.right = second;
	}


	public static <A, B> Pair<A, B> of(A first, B second) {
	    return new Pair<A, B>(first, second);
	}

}
