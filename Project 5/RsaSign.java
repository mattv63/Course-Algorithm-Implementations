import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;

public class RsaSign{
	private static File file;

	public static void main(String[] args){
		
		char flag = args[0].charAt(0);
		String filename = args[1];
		file = new File(filename);
		if(!file.exists()){
			System.out.println("This file does not exist in the current directory");
			return;
		}

		if(flag == 's'){
			try{
				Path path = file.toPath();
				byte[] data = Files.readAllBytes(path);

				//Do a SHA-256 Hash
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(data);
				byte[] digest = md.digest(); 

				File f = new File("privkey.rsa");		// private key
			
				FileInputStream privateKey = new FileInputStream("privkey.rsa");
				ObjectInputStream keys = new ObjectInputStream(privateKey);
				LargeInteger d = (LargeInteger) keys.readObject();	//Read in d
				LargeInteger n = (LargeInteger) keys.readObject();	//Read in n
				keys.close();
				
				// Turn the hash of the input file into a large integer for operations
				LargeInteger hash = new LargeInteger(n.increaseByOneByte(digest)); 
				LargeInteger decryptedData = hash.modPow(d, n);

				FileOutputStream signedFile = new FileOutputStream(file.getName() + ".sig"); // add extension to the file
				
				// Write to the new signed file
				ObjectOutputStream signedW = new ObjectOutputStream(signedFile);
				signedW.writeObject(data); 
				signedW.writeObject(decryptedData); 
				signedW.close();
			} catch(Exception e){
				System.out.println("Error: " + e);
				return;
			}
		}
		else if(flag == 'v'){
			try{
				//Do a SHA-256 hash
				MessageDigest mdOne = MessageDigest.getInstance("SHA-256");
				mdOne.update(Files.readAllBytes(file.toPath()));
				byte[] hash = mdOne.digest();

				File f = new File(file.getName() + ".sig");

				// Read in the hashed file
				FileInputStream signedFile = new FileInputStream(file.getName() + ".sig");
				ObjectInputStream signedReader = new ObjectInputStream(signedFile);
				byte[] data = (byte[]) signedReader.readObject();
				LargeInteger decryptedData = (LargeInteger) signedReader.readObject();

				signedReader.close();

				
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(data);
				byte[] hashTwo = md.digest();
				LargeInteger originalHash = new LargeInteger(LargeInteger.increaseByOneByte(hashTwo)); //The original hash of the file

				File f2 = new File("pubkey.rsa");

				//Read in the public keys
				FileInputStream publicKey = new FileInputStream("pubkey.rsa");
				ObjectInputStream keys = new ObjectInputStream(publicKey);
				LargeInteger e = (LargeInteger) keys.readObject();
				LargeInteger n = (LargeInteger) keys.readObject();

				keys.close();

				LargeInteger encryptedData = decryptedData.modPow(e, n); 

				// Makes comparison a lot easier
				encryptedData = encryptedData.trimLeadingZeros();
				originalHash = originalHash.trimLeadingZeros();

				boolean validSignature = encryptedData.areNumbersEqual(originalHash);
				
				if(validSignature){
					System.out.println("The signature is valid! Whoopie!");
				}	
				else System.out.println{
					("Aw man. The signature is NOT valid!");
				}
			} catch(Exception exception){
				System.out.println("Error: " + exception);
			}
		}
	}
}