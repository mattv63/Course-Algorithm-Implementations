import java.util.*;

public class ApartmentKeeper{
	private static Scanner s = new Scanner(System.in);
	private static String userInput = "";					// Holds user choice
	
	public static void main(String[] args){

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
	}
	
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
		
	}
}