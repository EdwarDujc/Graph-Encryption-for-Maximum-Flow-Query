package MFQ;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.LinkedList;
import java.util.Queue;
import java.lang.*;
import java.util.Arrays;
import Paillier.Paillier;
import Paillier.PrivateKey;
import Paillier.PublicKey;

public class MaxFlow_File{
	
	public static final int NODE=6;
	public static final int EDGE=8;
	public static final int ZZZZ=5;
	public static final int INF=0x3f3f3f3f;
//	public static final int INF=999;
//	public static final int NODE=2394385;
//	public static final int EDGE=5021410;
//	public static final int ZZZZ=10;
	private int size;
	public Node[] A_table;
	public int[] index_per;
	public HashMap<Integer, Integer> T_table;
	public HashMap<String, Double> eps;
//	public List<Integer> to;
//	public List<Integer> from;
	public PrivateKey sk;
	public PublicKey pk;
	public List<Integer> tolist;
	public List<Integer> fromlist;
	
	public MaxFlow_File(){
		size=EDGE;
		A_table=new Node[2*EDGE+ZZZZ];
		index_per=generateRandomPermutation(2*EDGE+ZZZZ);
		T_table=new HashMap<>(2*EDGE+ZZZZ);
		eps= new HashMap<String,Double>(2*EDGE);
//		from=new ArrayList<Integer>(2*EDGE+ZZZZ);
//		to=new ArrayList<Integer>(2*EDGE+ZZZZ);
		sk=new PrivateKey(1024);
		pk=new PublicKey();
		Paillier.keyGen(sk, pk);
		tolist=new ArrayList<Integer>(2*EDGE+ZZZZ);
		fromlist=new ArrayList<Integer>(2*EDGE+ZZZZ);
		
	}
	
	public int[] generateRandomPermutation(int n) {
		Random rand = new Random();
	    int[] res = new int[n];
	    for (int i = 0; i < n; i++) {
	    	int d = rand.nextInt(i+1);
	        res[i] = res[d];
	        res[d] = i;
//	    	res[i]=i;
	    }
	    return res;
	}
	
	public void Initial() throws IOException{
		int i,j;
		int from[]=new int[2*EDGE+ZZZZ];
		int to[]=new int[2*EDGE+ZZZZ];
//		int from[]=  {1,1,2,2,2,3,3,3,3,4,4,4,5,5,6,6,0};
//		int to[]=    {2,3,1,3,4,1,2,4,5,2,3,6,3,6,4,5,0};
		int weight[]={4,5,0,2,2,0,0,0,6,0,1,1,0,5,0,0,0};
		
		File file=new File("WikiTalk2.txt");
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String line;		
		while ((line=reader.readLine())!=null){
			String []strings=line.split("\t");
			
			if(strings[0].charAt(0)!='#'){
				fromlist.add(Integer.parseInt(strings[0]));
				tolist.add(Integer.parseInt(strings[1])); 
			}
		}
		for(j=0;j<2*EDGE;j++){
			from[j]=fromlist.get(j).intValue();
			to[j]=tolist.get(j).intValue();
			}
		
		for(i=0;i<2*EDGE;i++){
			if(!T_table.containsKey(from[i])){
				T_table.put(from[i], index_per[i]);
			}
			Node a_G1;
			if(from[i]!=from[i+1]){
				a_G1=new Node(to[i],weight[i],INF,from[i]);
			}
			else {
				a_G1=new Node(to[i],weight[i],index_per[i+1],from[i]);
			}
			A_table[index_per[i]]=a_G1;
		}
	}
	
	public double MaxFlowQuery(int source,int dst ){	
	    double result=0;
		if(!T_table.containsKey(source)){
			System.out.println("cannot find the maximum flow from node"+" "+source);
			return 0;
		}
		int index=T_table.get(source);
		Node source_node=A_table[index];
		int[] level=new int[NODE+1+ZZZZ];
		while (SetLevel(source,dst,level)){
			result=result+AugmentPath(source_node,INF,dst,level);
		}
		return result;
	}

	public boolean SetLevel(int source,int dst,int[] level ){
		Arrays.fill(level, 0);
		Queue queue = new LinkedList();
		int from;
		int index=T_table.get(source);
		Node next=A_table[index];
		queue.add(next);
		level[source]=1;
		while(queue.peek()!=null){
			next=(Node) queue.remove();
			from=next.getFrom();
			if(from==dst){
				return true;
			}
			while(true){
				String keyii1=Integer.toString(from)+"to"+Integer.toString(next.getIndex());
				if(!eps.containsKey(keyii1)){
					eps.put(keyii1,(double)next.getCap());
				}
				if(level[next.getIndex()]==0 && eps.get(keyii1)>0){
					level[next.getIndex()]=level[from]+1;
					queue.add(A_table[T_table.get(next.getIndex())]);
				}
				if(next.getNext()==INF)
					break;
				else
					next=A_table[next.getNext()];
			}
		}
		return false;
	}

	public double AugmentPath(Node next, double MinRes,int dst,int[] level){
		double flow=0,f;
		Node temp1,current;
		current=next;
		if(next.getFrom()==dst){
			return MinRes;
		}
		while(true){
			String keyii1=Integer.toString(next.getFrom())+"to"+Integer.toString(next.getIndex());
			if(!eps.containsKey(keyii1)){
				eps.put(keyii1,(double)next.getCap());
			}
			temp1=A_table[T_table.get(next.getIndex())];			
			if(level[next.getIndex()]==level[next.getFrom()]+1 && eps.get(keyii1)>0){
				f=AugmentPath(temp1, Math.min(MinRes,eps.get(keyii1)),dst,level);
				if(f>0){
					eps.put(keyii1, eps.get(keyii1)-f);
					
					while(true){
						if(temp1.getIndex()==current.getFrom())
							break;
						else
							temp1=A_table[temp1.getNext()];
					}
					String keyji=Integer.toString(temp1.getFrom())+"to"+Integer.toString(temp1.getIndex());
					if(!eps.containsKey(keyji)){
						eps.put(keyji,(double)temp1.getCap());
					}
					eps.put(keyji, eps.get(keyji)+f);
					return f;
				}
			}
			if(next.getNext()==INF)
				break;
			else
				next=A_table[next.getNext()];
		}
//		System.out.println("Augment flow = "+flow); 
		return flow;
	}

	
	public void similation(BigInteger dis){
		//Paillier.decrypt(dis, sk);
	}
	
	public int getSize(){
		return size;
	}
	
	public void printNode(Node node){
		System.out.println("from: "+node.getFrom()+" index: "+node.getIndex()+" cap: "+node.getCap()+" next: "+node.getNext());
	}
	
	public void printQ(Queue queue){
		Node temp;
		while(queue.peek()!=null){
			temp=(Node)queue.remove();
			printNode(temp);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String argvs[]) throws IOException, NoSuchAlgorithmException, ClassNotFoundException{
		
		MaxFlow_File maxflow=new MaxFlow_File();
		maxflow.Initial();
		Random rnd=new Random();		
		long search_begin=System.currentTimeMillis();
		
		for (int i = 0; i < 5; i++) {
			int source=1+rnd.nextInt(NODE)%NODE;
			int dst=1+rnd.nextInt(NODE)%NODE;
			double flow=maxflow.MaxFlowQuery(source, dst);
			System.out.println("the maximum flow from "+source+" to "+dst+" is: "+flow);
		}
		
//		int source=1;
//		int dst=3;
//		double flow=maxflow.MaxFlowQuery(source, dst);
//		System.out.println("MaxFlow_File");
//		System.out.println("the maximum flow from "+source+" to "+dst+" is: "+flow);
		long search_time=System.currentTimeMillis()-search_begin;
		System.out.println("total time is: "+search_time+" ms");
		System.out.println("average time is: "+search_time/5+" ms");
		
	}

}
