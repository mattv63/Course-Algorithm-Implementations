// This class is in charge of creating and managing all of the different queues for each apartment

public class QueueSeparator{
	private static PQ RentQueue;		// Min priority queue for rent
	private static PQ SquareFootQueue;	// Max priority queue for total square feet
	private static ByCityDLB rentByCity = new ByCityDLB();		// DLB that holds queues for each city based on rent
	private static ByCityDLB sqftByCity = new ByCityDLB();		// DLB that holds queues for each city based on square footage
	
	public QueueSeparator(){
		RentQueue = new PQ(1, false);
		SquareFootQueue = new PQ(2, false);
	}	
	
	/*********************************
	 * insert()
	 *
	 * Starts off by inserting the apartment into both of the queues that contain all apartments. Then the method checks
	 * if a queue has been created for the city that the apartment resides in. If not, then it creates one. If so, then the
	 * apartment is added to the existing queue.
	 ************************************************************************************************************************/
	
	public void insert(Apartment x){
		RentQueue.insert(x);
		SquareFootQueue.insert(x);
		
		String city = x.getCity();
		
		PQ cityRent = rentByCity.get(city);
		PQ citySqft = sqftByCity.get(city);
		if (cityRent == null){
			PQ pq = new PQ(1, true);
			rentByCity.put(city, pq);
			cityRent = pq;
		}
		if (citySqft == null){
			PQ pq = new PQ(2, true);
			sqftByCity.put(city, pq);
			citySqft = pq;
		}
		
		cityRent.insert(x);
		citySqft.insert(x);
	}
	
	/************************************
	 * update()
	 *
	 * Starts off by getting the index of the apartment by getting the rent index variable from the apartment object.
	 * Then calls the update function within PQ to move the position of the apartment. Then updates the rent by city queue
	 **********************************************************************************************************************/
	
	public void update(Apartment x){
		int rentIndex = x.getRentIndex();
		RentQueue.update(rentIndex);
		
		String city = x.getCity();
		PQ pq = rentByCity.get(city);
		if (pq == null){
			return;
		}
		else{
			int rentByCityIndex = x.getRentByCityIndex();
			pq.update(rentByCityIndex);
		}
	}
	
	/*************************************
	 * remove()
	 *
	 * Remove will gather the index of the apartment in all four of it's respected queues. And call the delete functions from
	 * each queue.
	 **************************************************************************************************************************/
	
	public void remove(Apartment x){
		int rentIndex = x.getRentIndex();
		int sqftIndex = x.getSqftIndex();
		int rentByCityIndex = x.getRentByCityIndex();
		int sqftByCityIndex = x.getSqftByCityIndex();
		String city = x.getCity();
		
		RentQueue.delete(rentIndex);
		SquareFootQueue.delete(sqftIndex);
		
		PQ cityRent = rentByCity.get(city);
		PQ citySqft = sqftByCity.get(city);
		if (cityRent != null){
			cityRent.delete(rentByCityIndex);
		}
		if (citySqft != null){
			citySqft.delete(sqftByCityIndex);
		}
	}
	
	public Apartment getLowestRent(){
		return RentQueue.maxApartment();
	}
	
	public Apartment getHighestSquareFootage(){
		return SquareFootQueue.maxApartment();
	}
	
	public Apartment getLowestRentByCity(String city){
		PQ pq = rentByCity.get(city);
		return pq.maxApartment();
	}
	
	public Apartment getHighestSquareFootageByCity(String city){
		PQ pq = sqftByCity.get(city);
		return pq.maxApartment();
	}
}