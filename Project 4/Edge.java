
// Edge Object
public class Edge{
	private Vertex a;					// First endpoint
	private Vertex b;					// Second endpoint
	private char cable;					// c for copper, o for optical
	private int bandwidth;				// bandwidth of the cable in megabits per second
	private int length;					// length of the cable in meters
	private double travelTime;			// total time to travel on the edge
	
	public Edge(Vertex _a, Vertex _b, char _cable, int _bandwidth, int _length){
		a = _a;
		b = _b;
		cable = _cable;
		bandwidth = _bandwidth;
		length = _length;
		
		// calculation for determining time for a single packet of data to be transferred
		if (cable == 'c'){ 
			travelTime = (((double) 1/230000000) * length);
		}
		if (cable == 'o'){ 
			travelTime = (((double) 1/200000000) * length);
		}
	}

	public void setVertexA(Vertex _a){
		a = _a;
	}
	
	public void setVertexB(Vertex _b){
		b = _b;
	}
	
	public void setCable(char _cable){
		cable = _cable;
	}
	
	public void setBandwidth(int _bandwidth){
		bandwidth = _bandwidth;
	}
	
	public void setLength(int _length){
		length = _length;
	}
	
	public Vertex getVertexA(){
		return a;
	}
	
	public Vertex getVertexB(){
		return b;
	}
	
	public char getCable(){
		return cable;
	}
	
	public int getBandwidth(){
		return bandwidth;
	}
	
	public int getLength(){
		return length;
	}
	
	public double getTravelTime(){
		return travelTime;
	}
	
	public int compareTo(Edge x){
		if (travelTime > x.getTravelTime()) return 1;
		else if (travelTime == x.getTravelTime()) return 0;
		else return -1;
	}
	
	public int other(int v){
			if (v == a.getVertexIndex()) return b.getVertexIndex();
			return a.getVertexIndex();
	}
	
	public int either(){
			return a.getVertexIndex();
	}
}