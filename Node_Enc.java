package MFQ;
import java.io.Serializable;
import java.math.BigInteger;
public class Node_Enc implements Serializable{
	private int index;
	private BigInteger cap;
	private int next;
	private int from;
	
	public Node_Enc(int index,BigInteger cap,int next,int from){
		this.index=index;
		this.cap=cap;
		this.next=next;
		this.from=from;
	}
	
	public int getIndex(){
		return index;
	}
	
	public BigInteger getCap(){
		return cap;
	}
	
	public int getNext(){
		return next;
	}
	
	public int getFrom(){
		return from;
	}
	
}
