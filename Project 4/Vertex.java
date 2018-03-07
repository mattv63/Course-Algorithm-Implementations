import java.util.*;

// Vertex object
public class Vertex{
		private int vertexIndex;		// vertex ID
		private LinkedList<Edge> edgesConnectedToVertice;	// LinkedList that holds all edges connected to this vertex
		
		
		public Vertex(int v){
			vertexIndex = v;
			edgesConnectedToVertice = new LinkedList<Edge>();
		}
		
		public int getVertexIndex(){
			return vertexIndex;
		}
		
		public LinkedList<Edge> getEdgesConnectedToVertice(){
			return edgesConnectedToVertice;
		}
		
		public void setVertexIndex(int v){
			vertexIndex = v;
		}
}