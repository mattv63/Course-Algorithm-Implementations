import java.util.*;
import java.io.*;

public class Graph{
	private int totalVertices;		// Total number of vertices in the graph
	private Vertex[] vertices;		// Adjacency List
	private ArrayList<Edge> edges;	// 
	boolean onlyCopper = true;		// Stays true until buildGraph finds an edge that is not copper
	
	public Graph(String f){
		buildGraph(f);
	}

		// takes the input file and creates the graph
	private void buildGraph(String _f){
		File f = new File(_f);
		Scanner s;
		try{
			s = new Scanner(f);
		}
		catch(FileNotFoundException e){
			System.out.println("No file was found");
			return;
		}
			
		totalVertices = Integer.parseInt(s.nextLine());		// reads in first line to get the number of vertices
		System.out.println(totalVertices);
		
		vertices = new Vertex[totalVertices];			// Creates array of Vertex objects
		for (int i = 0; i < vertices.length; i++){		// Gives each Vertex a numerical value
				vertices[i] = new Vertex(i);
		}
			
		edges = new ArrayList<Edge>();		// hold the edges
		while(s.hasNextLine()){
			char c;
			String currentLine = s.nextLine();
			String[] lineCredentials = currentLine.split(" ");		// Separates the contents on each line into a String array for easier distribution
			
			// Distributes contents of the current line into the correct variable
			if(lineCredentials.length != 5) continue;
			Vertex a = vertices[Integer.parseInt(lineCredentials[0])];	
			Vertex b = vertices[Integer.parseInt(lineCredentials[1])];
			String cable = lineCredentials[2];
			if(cable.equals("copper")) c = 'c';
			else c = 'o';
			int bandwidth = Integer.parseInt(lineCredentials[3]);
			int length = Integer.parseInt(lineCredentials[4]);
			
			// Each pair of vertices will have two edges made between them
			Edge edgeForward = new Edge(a, b, c, bandwidth, length);	// creates forward edge
			Edge edgeBackwards = new Edge(b, a, c, bandwidth, length);  // creates backwards edge
			a.getEdgesConnectedToVertice().addFirst(edgeForward);		
			b.getEdgesConnectedToVertice().addFirst(edgeBackwards);	
			
			// adds edge to our list of edges and checks for what material that the cable is
			edges.add(edgeForward);
			if (c == 'o') onlyCopper = false;
		}	
	}

	// Finds path between two vertices with the lowest latnecy (latency is calculated in Edge.java)
	public void findLowestLatencyPath(int startID, int endID){
		Vertex start = vertices[startID];		// vertex source
		Vertex end = vertices[endID];			// vertex destination
		
		Object[] p = findLowestLatencyPathHelper(start, end, "" + start.getVertexIndex(), 0L, -1);	// calls helper function that returns the minimum path, length and bandwidth

		String path = (String) p[0];	//	stores the minimum path
		String correctPath = "";
		for(int i=0; i<path.length(); i++){			// creates the path for printing to screen
			if (i < path.length() - 1){
				correctPath += path.charAt(i) + "->"; //
			}
			else{
				correctPath += path.charAt(i);
			}
		}
		
		path = correctPath;
		
		// gather length and bandwidth to pritn
		double time = (double) p[1];
		int minBandwidth = (int) p[2];
		System.out.println("\nHere is the shortest path from Vertex " + startID + " to " + endID);
		System.out.println("\n" + path);
		System.out.println("\nBandwidth: " + minBandwidth);
	}
	
	// Finds the shortest path between two vertices and returns the path, length and bandwidth
	private Object[] findLowestLatencyPathHelper(Vertex current, Vertex destination, String path, double length, int bandwidth){

		if (current == destination){ return new Object[] {path, length, bandwidth};}		// base case
		
		LinkedList<Edge> viewingEdges = current.getEdgesConnectedToVertice();	// gets all edges that are connected to the current vertex
		double minLength = -1.0;
		String minPath = "";
		for (Edge edge: viewingEdges){				// will iterate through every possible path that the graph offers
			Vertex nextVertex = edge.getVertexB();	// gets destination vertex
			if (path.contains("" + nextVertex.getVertexIndex())) continue;	// if edge has already been visited
			
			String newPath = path + nextVertex.getVertexIndex();
			double newLength = length + edge.getTravelTime();
			
			int newBandwidth = bandwidth;
			if (bandwidth == -1 || edge.getBandwidth() < bandwidth){	// if the current edge we are on has a lower bandwidth then we must change what the minimm bandwidth will be
				newBandwidth = edge.getBandwidth();	
			}
			
			Object[] p = findLowestLatencyPathHelper(nextVertex, destination, newPath, newLength, newBandwidth);  // recursive function moves onto next vertex
			if (p == null) continue;
			
			
			String edgePath = (String) p[0];		// new path
			double pathLength = (double) p[1];		// new length
			int pathBandwidth = (int) p[2];			// new bandwidth
			
			if(minLength == -1 || pathLength < minLength){
				minLength = pathLength;
				minPath = edgePath;
				bandwidth = pathBandwidth;
			}
			else if(pathLength == minLength && pathBandwidth > bandwidth){
				minLength = pathLength;
				minPath = edgePath;
				bandwidth = pathBandwidth;
			}
		}
		if (minLength > -1.0){		// once we have reached the destintation
				return new Object[] {minPath, minLength, bandwidth};
		}
		
		return null; // no valid edges and not at destination
	}
	
	// simple function that will test if there is a path connected to every vertice that where every edge is copper
	public void copperOnly(){
		if (onlyCopper){	// when building the graph, there were only copper wires
			System.out.println("\nThis network is copper-only connected.");
		}
		else{
			boolean copperConnected = true;
			
			for (int i = 0; i < totalVertices; i++){
				LinkedList<Edge> vEdges = vertices[i].getEdgesConnectedToVertice();	// test all edges connected to current vertex
				
				boolean t = false;
				for (Edge edge : vEdges){
						if (edge.getCable() == 'c'){		// ensures path is all copper
								t = true;
								break;
						}
				}
					
				if(!t){
						copperConnected = false;
						break;
				}
		}
		if (copperConnected) System.out.println("\nThe network has a copper wire connection, but also contains optical wires.");
		if (!copperConnected) System.out.println("\nThere is no copper wire connection for this network.");
		}
	}
	
	// finds the maximum amount of data that can be transmitted from one vertex to another
	public void maxPath(int startID, int endID){
		Vertex start = vertices[startID];
		Vertex end = vertices[endID];
		
		int max = maxPathHelper(start, end, "" + start.getVertexIndex(), -1);		// calls recursive helper function that determines the max amount of data
		System.out.println("\nThe maxmimum amount of data that can be transmitted from Vertex " + start.getVertexIndex() + " to Vertex " + end.getVertexIndex() + " is " + max);
	}
	
	private int maxPathHelper(Vertex current, Vertex destination, String path, int max){
		if (current == destination){	// base case
			return max;
		}
		
		LinkedList<Edge> e = current.getEdgesConnectedToVertice();	// grabs all edges connected to current vertex
		int m = -1;			// keeps track of max data
		for(Edge edge : e){
			Vertex edgeDest = edge.getVertexB();		// gets destination vertex from edge
			if (path.contains("" + edgeDest.getVertexIndex())) continue;
			
			int newMax = max;
			if(newMax == -1 || edge.getBandwidth() < newMax) newMax = edge.getBandwidth();
			
			String newPath = path + edgeDest.getVertexIndex();
			int pathBandwidth = maxPathHelper(edgeDest, destination, newPath, newMax);
			if(pathBandwidth == -1) continue;
			if(pathBandwidth > m) m = pathBandwidth;
		}
		return m;
	}
	
	// fucntion that determines if the graph can remain intact when any two vertices are removed
	public void failingVertices(){
		for (int i = 0; i < totalVertices - 1; i++){		// the for loops will account for every possible pair of vertices in the graph
			for (int j = i+1; j < totalVertices; j++){
				Vertex start = null;
				Vertex fail1 = vertices[i];
				Vertex fail2 = vertices[j];
				boolean[] visited = new boolean[totalVertices];
				
				visited[fail1.getVertexIndex()] = true;		// keeps track of which vertices have been checked
				visited[fail2.getVertexIndex()] = true;
				
				// check vertex 0 and go from there
				if (i != 0){
					start = vertices[0];
				}
				
				else{
					if (j!=totalVertices-1){
						start = vertices[j+1];
					}
					else if(j-i != 1){
						start = vertices[j-1];
					} else{
						System.out.println("\nThe network is not connected when any two vertices fail."); 
						return;
					}
				}
				
				// calls helper function that checks that all vertices have been visited
				failingVerticesHelper(start, fail1, fail2, visited);
				
				boolean connected = true;
				
				for (int k = 0; k < visited.length; k++){
					if (visited[k] == false){
						connected = false;
						break;
					}
				}
				
				if(!connected){
					System.out.println("\nThe network is not connected when any two vertices fail.");
					return;
				}
			}
		}
		System.out.println("\nThis network IS connected when any two vertices fail.");
		return;
		
	}
	
	private void failingVerticesHelper(Vertex current, Vertex one, Vertex two, boolean[] visited){
		if (current == null || one == null || two == null || visited == null) return;
		
		if (visited[current.getVertexIndex()] == true) return;
		
		visited[current.getVertexIndex()] = true;
		
		LinkedList<Edge> currEdges = current.getEdgesConnectedToVertice();
		
		for (Edge edge: currEdges){
			Vertex dest = edge.getVertexB();
			if (visited[dest.getVertexIndex()] == true) continue;
			
			failingVerticesHelper(dest, one, two, visited);
		}
		
		return;
	}
	
	public int getTotalVertices(){
		return totalVertices;
	}
	
	public ArrayList<Edge> getEdges(){
		return edges;
	}
}