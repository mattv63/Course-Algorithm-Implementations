// Class that creates apartment objects

public class Apartment{
	private String address;
	private String aptNumber;
	private String city;
	private String ZIP;
	private int rent;
	private int sqft;
	private int rentIndex = -1;
	private int sqftIndex = -1;
	private int rentByCityIndex = -1;
	private int sqftByCityIndex = -1;
	
	public Apartment(String _address, String _aptNumber, String _city, String _ZIP, int _rent, int _sqft){
		address = _address;
		aptNumber = _aptNumber;
		city = _city;
		ZIP = _ZIP;
		rent = _rent;
		sqft = _sqft;
	}
	
	public String getAddress(){
		return address;
	}
	
	public String getApartmentNumber(){
		return aptNumber;
	}
	
	public String getCity(){
		return city;
	}
	
	public String getZIP(){
		return ZIP; 
	}
	
	public int getRent(){
		return rent;
	}
	
	public int getSquareFeet(){
		return sqft;
	}
	
	public int getRentIndex(){
		return rentIndex;
	}
	
	public int getSqftIndex(){
		return sqftIndex;
	}
	
	public int getRentByCityIndex(){
		return rentByCityIndex;
	}
	
	public int getSqftByCityIndex(){
		return sqftByCityIndex;
	}
	
	public void setAddress(String _address){
		address = _address;
	}
	
	public void setApartmentNumber(String _aptNumber){
		aptNumber = _aptNumber;
	}
	
	
	public void setCity(String _city){
		city = _city;
	}
	
	public void setZIP(String _ZIP){
		ZIP = _ZIP;
	}
	
	public void setRent(int _rent){
		rent = _rent;
	}
	
	public void setSquareFeet(int _sqft){
		sqft = _sqft;
	}
	
	public void setRentIndex(int i){
		rentIndex = i;
	}
	
	public void setSqftIndex(int i){
		sqftIndex = i;
	}
	
	public void setRentByCityIndex(int i){
		rentByCityIndex = i;
	}
	
	public void setSqftByCityIndex(int i){
		sqftByCityIndex = i;
	}
	
	public String allCredentials(){
		String x = "\n" + address + " Apt #" + aptNumber + " \n" + city + " " + ZIP + "\n Rent: $" + rent + "\n Square feet: " + sqft + "ft^2";
		return x;
	}
}