import java.util.*;

public class PQ{
	private int n;				// number of elements in the heap
	private int mode;			// 1 for rent 2 for square footage
	private Apartment[] pq;		// the heap
	private boolean byCity;		// determines if current queue is specific to a city
	
	public PQ(int m, boolean _byCity){
		n = 0;
		pq = new Apartment[200];
		mode = m;
		byCity = _byCity;
	}
	
	public boolean isEmpty(){
		return n == 0;
	}
	
	public boolean contains(int i){		//	checks if index of apartment is contained in the queue
		return pq[i] != null;
	}
	
	public int size(){		// returns number of apartments in the queue
		return n;
	}
	
	public void insert(Apartment key){		// inserts apartment into the queue
		if (key == null) return;
		n++;
		pq[n] = key;
		swim(n);
	}
	
	public void update(int index){		//	update position of apartment in the heap
		swim(index);
		sink(index);
	}
	
	public void delete(int index){		//	deletes apartment from the heap
		if (!contains(index)) throw new NoSuchElementException("index is not in the priority queue");
		exch(index, n--);
		swim(index);
		sink(index);
		pq[n+1] = null;
	}
	
	public Apartment maxApartment(){		// retrieves apartment at the top of the heap
		if (n == 0) throw new NoSuchElementException("Priority queue underflow");
		if (mode == 1) return pq[1];
		if (mode == 2) return pq[1];
		return null;
	}
	
	/***************************************
	 * General Helper Functions
	 ***************************************/
	private boolean less(int i, int j){
		if (mode == 1) return pq[i].getRent() > pq[j].getRent();
		else if (mode == 2) return pq[i].getSquareFeet() < pq[j].getSquareFeet();
		else return false;
	}
	
	private void exch(int i, int j){
		Apartment swap = pq[i];
		pq[i] = pq[j];
		pq[j] = swap;
		setApartmentIndex(i);
		setApartmentIndex(j);
		
	}
	
	private void setApartmentIndex(int i){
		if (mode == 1){
			if (byCity){
				pq[i].setRentByCityIndex(i);
			}
			else{ 
				pq[i].setRentIndex(i);
			}
		}
		else if (mode == 2){
			if (byCity){
				pq[i].setRentByCityIndex(i);
			}
			else{
				pq[i].setSqftIndex(i);
			}
		}
	}
	
	/************************************
	 * Heap Helper Functions
	 ************************************/
	
	private void swim(int k){
		while (k > 1 && less(k/2, k)){
			exch(k, k/2);
			k = k/2;
		}
		
		setApartmentIndex(k);
	}
	
	private void sink(int k){
		while (2*k <= n){
			int j = 2*k;
			if (j < n && less(j, j+1)) j++;
			if (!less(k, j)) break;
			exch(k, j);
			k = j;
		}
		
		setApartmentIndex(k);
	}
}