import java.util.Random;
import java.io.*;

public class RsaKeyGen{
	public static void main(String[] args){
		Random rand = new Random();
		byte three = 3;

		// Create p and q; both 2048 bit integers
		LargeInteger p = new LargeInteger(256, rand);
		LargeInteger q = new LargeInteger(256, rand);

		//To find public key n, multiply p and q
		LargeInteger n = p.multiply(q);

		// Subtract 1 from both p and q to find phi in the next step
		LargeInteger pMinusOne = p.minusOne(); 
		LargeInteger qMinusOne = q.minusOne(); 
		
		// Find phi by multiply (p-1) and (q-1)
		LargeInteger phi = pMinusOne.multiply(qMinusOne);
		
		// Start e out as 3 (which is a low value that is co-prime to phi). Use it as starting point for find correct GCD
		LargeInteger x = new LargeInteger(null);
		LargeInteger i = new LargeInteger(new byte[] { three });
		x = x.add(i);
		LargeInteger e = x;
		
		// Initialize d
		LargeInteger d = new LargeInteger(null); 
		
		// Will find an e who's gcd(e, phi) is 1
		while(!LargeInteger.gcd(e, phi).isOne()){
			e = e.add(new LargeInteger(new byte[] { 2 })); //e += 2
		}
		
		// Performs a modular inverse to finally find the private key d
		d = e.modInverse(phi); 

		// Writes public and private keys to their respected files
		try{
			FileOutputStream publicKeyFile = new FileOutputStream("pubkey.rsa");
			ObjectOutputStream publicWrite = new ObjectOutputStream(publicKeyFile);
			publicWrite.writeObject(e);
			publicWrite.writeObject(n);
			publicWrite.close();

			FileOutputStream privateKeyFile = new FileOutputStream("privkey.rsa");
			ObjectOutputStream privateWrite = new ObjectOutputStream(privateKeyFile);
			privateWrite.writeObject(d);
			privateWrite.writeObject(n);
			privateWrite.close();

		} catch(IOException exception){
			System.out.println("Error: " + exception.toString());
		}
	}
}