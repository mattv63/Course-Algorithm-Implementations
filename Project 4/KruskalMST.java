import java.util.*;
import java.io.*;

// Kruskal Algorithm modified from book example
public class KruskalMST {
	private ArrayList<Edge> MST;	// Minimum Spanning Tree to be returned
	private ArrayList<Edge> Edges;	// Array List of edges from a grap
	private Graph network;
	private int[] parent;
	private byte[] rank;
	private double weight;
	
	KruskalMST(Graph _network){
		network = _network;
		parent = new int[network.getTotalVertices()];
		rank = new byte[network.getTotalVertices()];
		for (int i = 0; i < network.getTotalVertices(); i++){
			parent[i] = i;
			rank[i] = 0;
		}
		Edges = network.getEdges();
		Collections.sort(Edges, (e1, e2) -> e1.compareTo(e2));
		MST = new ArrayList<Edge>();
		
	}
	
	public ArrayList<Edge> MST(){
		int current = 0;
		while (current != Edges.size() - 1 && MST.size() < network.getTotalVertices() - 1){
			Edge e = Edges.get(current);
			int v = e.getVertexA().getVertexIndex();
			int w = e.getVertexB().getVertexIndex();
			if (!connected(v, w, parent)){
				union(v, w, parent, rank);
				MST.add(e);
				weight += e.getTravelTime();
			}
			current ++;
		}
		return MST;
	}
	
	
	private void union(int a, int b, int[] parent, byte[] rank){
		int rootA = find(a, parent);
		int rootB = find(b, parent);
		if (rootA == rootB) return;
		
		if (rank[rootA] < rank[rootB]) parent[rootA] = rootB;
		else if (rank[rootA] > rank[rootB]) parent[rootB] = rootA;
        else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }
	}
	
	private boolean connected(int a, int b, int[] parent){
		return find(a, parent) == find(b, parent);
	}
	
	private int find(int a, int[] parent){
		while (a != parent[a]){
			parent[a] = parent[parent[a]];
			a = parent[a];
		}
		
		return a;
	}
	
}