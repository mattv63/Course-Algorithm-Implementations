import java.util.*;
import java.io.*;

public class NetworkAnalysis{
	public static void main (String[] args){
		
		Graph network = new Graph(args[0]);		// creates the graph from the file
		Scanner s = new Scanner(System.in);
			
		while(true){
				System.out.println("\nWelcome to Matthew's Computer Network Analyzer. Please select an option:");
				System.out.println("\t1. Find the lowest latency path between two vertices.");
				System.out.println("\t2. Determine whether or not the graph is copper-only connected");
				System.out.println("\t3. Find the maximum amount of data that can be transferred from one vertex to another");
				System.out.println("\t4. Find the lowest average latency spanning tree for the graph");
				System.out.println("\t5. Determine whether or not the graph would remain connected if any two vertices in the graph were to fail.");
				System.out.println("\t6. Quit");
				
				String input = s.nextLine();
				
				if (input.equals("1")){
					System.out.println("\nEnter the first vertex: ");
					int a = Integer.parseInt(s.nextLine());
					System.out.println("Enter the second vertex: ");
					int b = Integer.parseInt(s.nextLine());
					network.findLowestLatencyPath(a, b);
				}
				else if(input.equals("2")){
					network.copperOnly();
			
				}
				else if(input.equals("3")){
					System.out.println("\nEnter the first vertex: ");
					int a = Integer.parseInt(s.nextLine());
					System.out.println("Enter the second vertex: ");
					int b = Integer.parseInt(s.nextLine());
					network.maxPath(a, b);
				}
				else if(input.equals("4")){
					System.out.println("\nHere is the minimum spanning tree with the lowest average latency.");
					KruskalMST mst = new KruskalMST(network);
					ArrayList<Edge> x = mst.MST();
					for (Edge e : x){
						System.out.println("(" + e.getVertexA().getVertexIndex() + " , " + e.getVertexB().getVertexIndex() + ")");
					}
				}
				else if(input.equals("5")){
					network.failingVertices();
				}
				else if(input.equals("6")){
					System.exit(0);
				}
		}
	}	
}