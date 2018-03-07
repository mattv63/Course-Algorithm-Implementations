import java.util.*;

// Front end of program associated with gathering user input and calling functions to perform operations

public class AptTracker{
	private static Scanner s = new Scanner(System.in);		//	Scanner for user input
	private static String userInput = "";					// Holds user choice
	private static QueueSeparator qs;						//	Queue Separator for creating different queues
	private static ApartmentDLB dlb;						//	DLB for storing Apartments for updating 
	private static int n = 0;								//	Total number a apartments
	
	public static void main(String[] args){
		qs = new QueueSeparator();
		dlb = new ApartmentDLB();
	
		while(true){
			System.out.println("Select an option");
			System.out.println("\n1) Add an apartment");
			System.out.println("2) Update an apartment");
			System.out.println("3) Remove an apartment");
			System.out.println("4) Retrieve the lowest price apartment");
			System.out.println("5) Retrieve the highest square footage apartment");
			System.out.println("6) Retrieve the lowest price apartment by city");
			System.out.println("7) Retrieve the highest square footage apartment by city");
			
			userInput = s.nextLine();
			
			if (userInput.equals("1")){
				addApartment();
			}
			else if(userInput.equals("2")){
				updateApartment();
			}
			else if(userInput.equals("3")){
				removeApartment();
			}
			else if (userInput.equals("4")){
				getLowestPricedApartment();
			}
			else if(userInput.equals("5")){
				getHighestSqaureFootApartment();
			}
			else if(userInput.equals("6")){
				getLowestRentApartmentByCity();
			}
			else if(userInput.equals("7")){
				getHighestSqftApartmentByCity();
			}
		}
	}
	
	/*******************************************
	 * addApartment()
	 *
	 * Asks user for all credentials for an apartment object. Will send apartment to Queue Separator class 
	 * and put into the DLB for all apartments. If apartment already exists, then the program won't add it
	 * and returns to the start menu.
	 ******************************************************************************************************/
	
	public static void addApartment(){
		String address = "";
		String aptNum = "";
		String city = "";
		String ZIP = "";
		int rent = 0;
		int sqft = 0;
		
		String userInput = "";
		
		System.out.print("Enter the address: ");
		address = s.nextLine();
		System.out.print("\nEnter the apartment number: ");
		aptNum = s.nextLine();
		System.out.print("\nEnter the city: ");
		city = s.nextLine();
		System.out.print("\nEnter the ZIP code: ");
		ZIP = s.nextLine();
		System.out.print("\nEnter the rent price: $");
		rent = Integer.parseInt(s.nextLine());
		System.out.print("\nEnter the total square footage: ");
		sqft = Integer.parseInt(s.nextLine());
		
		Apartment a = new Apartment(address, aptNum, city, ZIP, rent, sqft);
		
		if (dlb.contains(address + aptNum + ZIP)){
			System.out.println("\nThis apartment is already in the system");
			return;
		}
		
		qs.insert(a);
		dlb.put((address + aptNum + ZIP), a);
	}
	
	/**********************************
	 * updateApartment()
	 *
	 * Asks user for address, apartment number and ZIP code. Uses these credentials to get the apartment object
     * from the DLB. Then asks the user for the new rent and changes the rent of the apartment to the user input.
	 * If the apartment does not exist then the user is returned back to the main menu.
     ************************************************************************************************************/	 
	
	public static void updateApartment(){
		String address = "";
		String aptNum = "";
		String ZIP = "";
		int rent = 0;
		
		System.out.println(".....Update Apartment.....");
		System.out.print("Enter the address: ");
		address = s.nextLine();
		System.out.print("\nEnter the apartment number: ");
		aptNum = s.nextLine();
		System.out.print("\nEnter the ZIP code: ");
		ZIP = s.nextLine();
		
		Apartment a = dlb.get(address + aptNum + ZIP);
		
		if (a == null){
			System.out.println("The apartment is not in the system");
			return;
		}
		
		System.out.println("Current Price: " + a.getRent());
		System.out.print("Enter new price: $");
		rent = Integer.parseInt(s.nextLine());
		a.setRent(rent);
		qs.update(a);
	}
	
	/**********************************
	 * removeApartment()
	 *
	 * Asks user for address, apartment number and ZIP code to search for existence in DLB. If it exists it will then remove 
	 * it from the DLB and all the remove method from the Queue Separator. If it does not exist the user will return to the 
	 * main menu.
	 ************************************************************************************************************************/
	
	public static void removeApartment(){
		String address = "";
		String aptNum = "";
		String ZIP = "";
		int rent = 0;
		
		System.out.println(".....Delete Apartment.....");
		System.out.print("Enter the address: ");
		address = s.nextLine();
		System.out.print("\nEnter the apartment number: ");
		aptNum = s.nextLine();
		System.out.print("\nEnter the ZIP code: ");
		ZIP = s.nextLine();
		
		Apartment a = dlb.get(address + aptNum + ZIP);
		
		if (a == null){
			System.out.println("The apartment is not in the system");
			return;
		}
		
		qs.remove(a);
		dlb.delete(address + aptNum + ZIP);
	}
	
	/**************************************
	 * getLowestPricedApartment()
	 * getHighestSqaureFootApartment()
	 * getHighestSquareFootageByCity()
	 * getLowestRentApartmentByCity()
	 *
	 * The remaining functions will call their respected function from the Queue Separator and print out
	 * all of the credentials from the apartment that is returned to them. If null is returned then there were
	 * no apartments in the queue and the user goes back to the main menu.
	 ***********************************************************************************************************/
	
	public static void getLowestPricedApartment(){
		Apartment cheapest = qs.getLowestRent();
		if (cheapest == null) System.out.println("No apartments in the queue.");
		else System.out.println(cheapest.allCredentials());
	}
	
	public static void getHighestSqaureFootApartment(){
		Apartment largest = qs.getHighestSquareFootage();
		if (largest == null) System.out.println("No apartments in the queue.");
		else System.out.println(largest.allCredentials());
	}
	
	public static void getLowestRentApartmentByCity(){
		String city;
		
		System.out.print("\nEnter the city of which you would like the lowest priced apartment: ");
		city = s.nextLine();
		
		Apartment cheapest = qs.getLowestRentByCity(city);
		if (cheapest == null) System.out.println("No apartments in the queue");
		else System.out.println(cheapest.allCredentials());
		
	}
	
	public static void getHighestSqftApartmentByCity(){
		String city;
		
		System.out.print("\nEnter the city of which you would like the highest sqaure footage apartment: ");
		city = s.nextLine();
		
		Apartment largest = qs.getHighestSquareFootageByCity(city);
		if (largest == null) System.out.println("No apartments in the queue");
		else System.out.println(largest.allCredentials());
		
	}
}