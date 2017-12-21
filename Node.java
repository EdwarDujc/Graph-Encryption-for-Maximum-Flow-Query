package MFQ;
import java.io.Serializable;
import java.math.BigInteger;
public class Node implements Serializable{
	private int index;
	private int cap;
	private int next;
	private int from;
	
	public Node(int index,int cap,int next,int from){
		this.index=index;
		this.cap=cap;
		this.next=next;
		this.from=from;
	}
	
	public int getIndex(){
		return index;
	}
	
	public int getCap(){
		return cap;
	}
	
	public int getNext(){
		return next;
	}
	
	public int getFrom(){
		return from;
	}
	
}
