public class ByCityDLB<Value>{
	private static final int R = 62;
	
	private Node root;			// root of trie
	private int n;
	
	private static class Node{
		private char c;
		private Object val;
		private Node down, right;
	}
	
	public ByCityDLB(){
		
	}
	
	public void put (String key, Value val){
		if (key == null) throw new IllegalArgumentException("first argument to put() is null");
		else root = put(root, key, val, 0);
	}
	
	private Node put(Node x, String key, Value val, int d){
		if (x == null) x = new Node();
		if (d == key.length()){
			n++;
			x.val = val;
			return x;
		}
		
		char currentLetter = key.charAt(d);
		if(x.c == 0){
			x.c = currentLetter;
			x.down = put(x.down, key, val, d+1);
		}
		else if (x.c == currentLetter){
			x.down = put(x.down, key, val, d + 1);
		}
		else{
			x.right = put(x.right, key, val, d);
		}
		return x;
		
	}
	
	public PQ get(String key){
		if (key == null) throw new IllegalArgumentException("Argument to get() is null");
		Node x = get(root, key, 0);
		if (x == null) return null;
		return (PQ) x.val;
	}
	
	private Node get(Node x, String key, int d){
		if (x == null) return null;
		if (d == key.length()) return x;
		char currentLetter = key.charAt(d);
		if (x.c != currentLetter){
			return get(x.right, key, d);
		}
		else{
			return get(x.down, key, d+1);
		}	
	}
	
	public boolean contains(String key){
		if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
	}
	
	public boolean isEmpty(){
		return size() == 0;
	}
	
	public Queue<String> keysWithPrefix(String prefix){
		Queue<String> results = new Queue<String>();
		Node x = get(root, prefix, 0);
		collect(x, new StringBuilder(prefix), results);
		return results;
	}
	
	private void collect(Node x, StringBuilder prefix, Queue<String> results){
		if (x == null || x.c == 0) return;
		if (x.val != null){
			results.enqueue(prefix.toString());
		}

		if (x.c != 0) prefix.append(x.c);
		
		collect(x.down, prefix, results);
		prefix.deleteCharAt(prefix.length()-1);
		
		
		collect(x.right, prefix, results);
		
	}
	
	public int size(){
		return n;
	}
	
	public Iterable<String> keys(){
		return keysWithPrefix("");
	}
	
	public void delete(String key){
		if (key == null) throw new IllegalArgumentException("argument to delete() is null");
		root = delete(root, key, 0);
	}
	
	private Node delete(Node x, String key, int d){
		if (x == null) return null;
		if (d == key.length()){
			if (x.val != null) n--;
			x.val = null;
		}
		char currentLetter = key.charAt(d);
		if (x.c == currentLetter){
			delete(x.down, key, d+1);
		}
		else{
			delete(x.right, key, d+1);
		}
		return x;
	}
}	