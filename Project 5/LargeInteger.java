import java.io.Serializable;
import java.util.*;
import java.math.*;

public class LargeInteger implements Serializable{
	private byte[] val; //Store the byte array of the val in the class
	private final static LargeInteger ONE = new LargeInteger(new byte[] { 1 }); //Global constant object  containing the value 1

	public LargeInteger(byte[] arr){
		if(arr == null) val = new byte[65]; //Default class is 65*8 0 bits
		else val = arr;
	}
	
	public LargeInteger(int n, Random r){
		val = BigInteger.probablePrime(n, r).toByteArray();
	}
	
	public LargeInteger add(LargeInteger other) {
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length < other.length()) {
			a = other.getVal();
			b = val;
		}
		else {
			a = val;
			b = other.getVal();
		}

		// ... and normalize size for convenience
		if (b.length < a.length) {
			int diff = a.length - b.length;

			byte pad = (byte) 0;
			if (b[0] < 0) {
				pad = (byte) 0xFF;
			}

			byte[] newb = new byte[a.length];
			for (int i = 0; i < diff; i++) {
				newb[i] = pad;
			}

			for (int i = 0; i < b.length; i++) {
				newb[i + diff] = b[i];
			}

			b = newb;
		}

		// Actually compute the add
		int c = 0;
		byte[] res = new byte[a.length];
		for (int i = a.length - 1; i >= 0; i--) {
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			c = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + c;

			// Assign to next byte
			res[i] = (byte) (c & 0xFF);

			// c remainder over to next byte (always want to shift in 0s)
			c = c >>> 8;
		}

		LargeInteger res_li = new LargeInteger(res);
	
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative() && !other.isNegative()) {
			// If we have either a leftover c value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()) {
				res_li.extend((byte) c);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative() && other.isNegative()) {
			if (!res_li.isNegative()) {
				res_li.extend((byte) 0xFF);
			}
		}

		// Note that result will always be the same size as biggest input
		//  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
		return res_li;
	}
	
	public LargeInteger subtract(LargeInteger other) {
		return this.add(other.negate());
	}
	
	//This method will multiply two large binary numbers by continuosly calling add()
	public LargeInteger multiply(LargeInteger num){
		LargeInteger product = new LargeInteger(null); 							// Place that the product will be stored
		LargeInteger thisCopy = new LargeInteger(copyVal(val));				// Copy of the multiplicand
		LargeInteger numCopy = new LargeInteger(copyVal(num.getval()));		// Copy of the multiplier

		while(!numCopy.isZero()){ //While keeping looping until every bit in the multiplier has been accounted for
			if(!numCopy.isEven()){ 	// Only add another instances of the multiplicand if the current least significant bit of the multiplier is 1
				product = product.add(new LargeInteger(increaseByOneByte(thisCopy.getval())));	// LSB of multiplier was 1 so add current multiplicand to product
			}

			thisCopy = thisCopy.shiftLeft(1);	// shift multiplicand to the left 
			numCopy = numCopy.shiftRight(1);	// shift multiplier to the right
		}

		return product;		// return final product
	}
	
	//Divide two large binary numbers
	public LargeInteger divide(LargeInteger num){
		if(compare(num) == -1){
			return new LargeInteger(null);
		}
		else if(compare(num) == 0){ 
			return ONE; 
		}
		LargeInteger r = new LargeInteger(null);
		LargeInteger q = new LargeInteger(null);
		q = q.setNumBits(this.getBitLength()/8); 
		for(int i = 0; i < this.getBitLength(); i++){ 
			r = r.shiftLeft(1);
			if(getIthBit(i) == true){
				r = r.setLSB(true);
			}
			LargeInteger remainderCopy = r.trimLeadingZeros();
			if(remainderCopy.compare(num) >= 0){
				r = r.subtract(num);
				q = q.setIthBit(i);
			}
		}
		return q;
	}
	
	public LargeInteger negate() {
		byte[] neg = new byte[val.length];
		int offset = 0;

		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0] == (byte) 0x80) { // 0x80 is 10000000
			boolean needs_ex = true;
			for (int i = 1; i < val.length; i++) {
				if (val[i] != (byte) 0) {
					needs_ex = false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex) {
				neg = new byte[val.length + 1];
				neg[0] = (byte) 0;
				offset = 1;
			}
		}

		// flip all bits
		for (int i  = 0; i < val.length; i++) {
			neg[i + offset] = (byte) ~val[i];
		}

		LargeInteger neg_li = new LargeInteger(neg);
	
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(new byte[] { 1 }));
	}
	
	//Logical AND with an unsigned binary number
	public LargeInteger andOne(){
		byte[] newval = copyVal(val);
		boolean andIsOne = (newval[newval.length-1] & 0x1) != 0;

		for(int i = 0; i < newval.length; i++){ //Copy the old val into the new byte array
			newval[i] = 0;
		}

		if(andIsOne) newval[newval.length-1] = 0x1; //If the AND produced a 1, write a 1 to the appropriate byte

		return new LargeInteger(newval);
	}

	// or operation with large binary numbers
	public LargeInteger or(LargeInteger num){
		byte[] copy = num.getval();
		byte[] output = new byte[val.length];

		for(int i = 0; i < val.length; i++){ 
			int currBitVal = 1;

			for(int j = 7; j >= 0; j--){ 
				byte b1 = 0;
				byte b2 = 0;

				if(i >= copy.length){ 
					b1 = (byte) (val[val.length-i-1] & currBitVal);
					b2 = 0;
				} else{
					b1 = (byte) (val[val.length-i-1] & currBitVal);
					b2 = (byte) (copy[copy.length-i-1] & currBitVal);
				}

				byte bitWrite = 0; 
				if(b1 == 0 && b2 == 0){
					bitWrite = 0; 
				}
				else if(b1 != 0 && b2 == 0){
					bitWrite = (byte) currBitVal; 
				}	
				else if(b1 != 0 && b2 != 0){ 
					bitWrite = (byte) currBitVal;
				}
				else if(b1 == 0 && b2 != 0){
					bitWrite = (byte) currBitVal; 
				}
				output[val.length-i-1] |= bitWrite; 

				currBitVal *= 2;
			}
		}

		return new LargeInteger(output);
	}
	
	//Find the gcd of two large binary numbers, e and phi
	public static LargeInteger gcd(LargeInteger e, LargeInteger phi){
		int shift; //shift count for determing gcd to be returned later
		
		for(shift = 0; e.or(phi).andOne().isZero(); shift++){
			e = e.shiftRight(1);
			phi = phi.shiftRight(1);
		}
		while (e.andOne().isZero()){ //make the number odd
			e = e.shiftRight(1);
		}
		while(!phi.isZero()){
			while (phi.andOne().isZero()){ 
				phi = phi.shiftRight(1);
		    }
			if (e.compare(phi) == 1) { // ensures we are only using positive numbers. Helps make a lot easier
		
				LargeInteger temp = phi;
				phi = e;
				e = temp;
			}
			phi = phi.subtract(e); 
		} 
		return e.shiftLeft(shift);
	}
	
	//Modular exponentiation
	public LargeInteger modPow(LargeInteger y, LargeInteger n){
		boolean useMod = (n != null);

		// Start with 1
		LargeInteger result = new LargeInteger(null);
		result = result.add(ONE); 

		LargeInteger base = new LargeInteger(copyVal(val));		// what is being exponentiated
		LargeInteger powerCopy = new LargeInteger(copyVal(y.getval()));	// copy of exponent

		while(!powerCopy.isZero()){ 
			if(!powerCopy.isEven()){ //if the power is odd, multiply the result by the base and do a mod
				if(useMod) result = result.multiply(base).modulus(n);
				else result = result.multiply(base);
			}

			//Divide exponent in half
			powerCopy = powerCopy.shiftRight(1);

			//Multiply the base by itself and then perform mod if applicable
			if(useMod) base = base.multiply(base).modulus(n);
			else base = base.multiply(base);
		}

		return result;
	}
	
	//Do a modulus operation on two large binary numbers
	public LargeInteger modulus(LargeInteger num){
		if(compare(num) == -1){
			return this;
		}
		else if(compare(num) == 0){
			return new LargeInteger(null);
		}
		LargeInteger divResult = this.divide(new LargeInteger(num.getval()));
		LargeInteger mulResult = divResult.multiply(new LargeInteger(num.getval()));
		LargeInteger remResult = this.subtract(new LargeInteger(mulResult.getval()));
		return remResult;
	}
	
	//Find the modular inverse of two large numbers
	public LargeInteger modInverse(LargeInteger num){
		LargeInteger phiPlusOne = new LargeInteger(copyVal(num.getval()));
		phiPlusOne = phiPlusOne.add(ONE);
		while(!phiPlusOne.modulus(this).isZero()){ // remainder must be zero
			phiPlusOne = phiPlusOne.add(num);
		}

		return phiPlusOne.divide(this); 
	}

	public boolean areNumbersEqual(LargeInteger num){
		if(num == null) return false;
		byte[] numval = num.getval();
		if(val.length != numval.length || val == null || num == null) return false;

		int maxLength = Math.max(val.length, numval.length);

		for(int i = 0; i < maxLength; i++){ 
			if(i < val.length && i < numval.length){ 
				if(val[i] != numval[i]) return false;
			} else{
				if(i >= val.length){
					if(numval[i] != 0) return false;
				} else if(i >= numval.length){
					if(val[i] != 0) return false;
				}
			}
		}

		return true; 
	}

	// Subtract one from a large number. We will only be subtracting one from odd numbers so simply erase the least significant bit
	public LargeInteger minusOne(){
		byte[] minusOneCopy = copyVal(val);
		minusOneCopy[minusOneCopy.length-1] &= 0xFE; //Changes the lsb to 0
		return new LargeInteger(minusOneCopy);	// return new large integer with 1 subtracted from the previous
	}
	public int compare(LargeInteger num){
		LargeInteger thisShort = this.trimLeadingZeros();
		LargeInteger numShort = num.trimLeadingZeros();
		int lengthOfval = thisShort.getval().length;
		byte[] thisval = thisShort.getval();
		byte[] numval = numShort.getval();
		byte currBitVal = (byte) 0x40; 

		if(thisShort.getBitLength() > numShort.getBitLength()){
			return 1;
		} else if(thisShort.getBitLength() < numShort.getBitLength()){
			return -1;
		}

		for(int i = 0; i < lengthOfval; i++){ 
			byte b1Pre = (byte) ((thisval[i] & 0x80) == 0 ? 0 : 1);
			byte b2Pre = (byte) ((numval[i] & 0x80) == 0 ? 0 : 1);

			if(b1Pre > b2Pre){
				return 1;
			} else if(b1Pre < b2Pre){
				return -1;
			}

			for(int j = 6; j >= 0; j--){ 
				byte b1 = (byte) ((thisval[i] & currBitVal) == 0 ? 0 : 1);
				byte b2 = (byte) ((numval[i] & currBitVal) == 0 ? 0 : 1);

				if(b1 > b2){
					return 1;
				} else if(b1 < b2){
					return -1;
				}

				currBitVal >>= 1; 
			}

			currBitVal = (byte) 0x40; 
		}

		return 0; 
	}

	public byte[] getval(){
		return val;
	}

	public int getBitLength(){
		return val.length * 8;
	}

	//Checks to see if the value of a large binary number is one
	public boolean isOne(){
		if(val[val.length-1] != 1) return false; 
		for(int i = 0; i < val.length-1; i++){ 
			if(val[i] != 0){
				return false;
			}
		}

		return true;
	}

	//Checks to see if the value of a large binary number is zero
	public boolean isZero(){
		for(int i = 0; i < val.length; i++){ 	// looks at all indexes in the byte array
			if(val[i] != 0){
				return false;
			}
		}

		return true;	// the binary number is in fact zero
	}

	//Checks to see in the least significant bit in a large binary number is 0
	public boolean isEven(){
		byte lsb = (byte) (val[val.length-1] & 0x1); // Grab the least significant bit from the byte array
		return lsb == 0; //Return true if the lsb is zero (even number), false if it is not equal to zero (odd number)
	}

	//Checks if the most significant bit of a large binary number is 1
	public boolean msbIsOne(){
		byte msb = (byte) (val[0] & 0x80);
		return msb != 0;
	}

	//Set the least significant bit of a large binary number. true == 1 false == 0
	public LargeInteger setLSB(boolean set){
		byte[] newval = copyVal(val);
		if(set){
			newval[newval.length-1] |= 0x1;		// sets lsb to 1
		}	
		else{
			newval[newval.length-1] &= 0xFE;	// sets lsb to 0
		}	
		return new LargeInteger(newval);
	}

	//Set a bit of a large binary number to 1
	public LargeInteger setIthBit(int bitNum){
		int byteNumber = bitNum/8;
		int bitIndex = 7 - (bitNum - (byteNumber*8));
		int bitIndexVal = (int) Math.pow(2, bitIndex); 

		byte[] newval = copyVal(val);
		newval[byteNumber] |= bitIndexVal; 
		return new LargeInteger(newval);
	}

	//Get a bit in a large binary number
	public boolean getIthBit(int bitNum){
		int byteNumber = bitNum/8;
		int bitIndex = 7 - (bitNum - (byteNumber*8));
		int bitIndexVal = (int) Math.pow(2, bitIndex);

		byte result = (byte) (val[byteNumber] & bitIndexVal); 
		return result != 0;
	}

	//Set the number of bytes/bits
	public LargeInteger setNumBits(int num){
		byte[] newval = new byte[num];
		return new LargeInteger(newval);
	}

	//Shift the byte array of a LargeInteger to the left a specified number of bytes
	public LargeInteger shiftLeft(int n){
		byte[] shiftCopy = val;		// create copy of current number
		boolean c = false;

		for(int i = 0; i < n; i++){ // Shift n number of times
			c = false;
			int grow = shiftCopy[0] & 128; 		// Must account for the most signifcant bit. If it is 1, we must pad out another byte to the left
			
			if(grow == 0){			// if grow is zero then there are already zeros to the left of the msb so we don't have to allocate more space
				shiftCopy = copyVal(shiftCopy);
			}
			else {
				shiftCopy = increaseByOneByte(shiftCopy);	// grow was not zero so we must allocate more space
			}
			
			for(int j = shiftCopy.length-1; j >= 0; j--){ //time to shift! Do it for every byte
				byte msb = (byte) (shiftCopy[j] & 128); //stores the most significant bit so we do not lose it by shifting to the left
				shiftCopy[j] <<= 1; //SHIFT
				
				if(c) {
					shiftCopy[j] |= 1; //If the msb was 1 previously, now is the time to add it
				}
				if(msb == 0){
					c = false;
				}	
				else{ 
					c = true;
				}
			}
		}

		return new LargeInteger(shiftCopy);	// returns a new large integer that has applied the specified shift
	}

	//Shift the byte array of a LargeInteger to the right a specified number of bytes
	public LargeInteger shiftRight(int n){
		byte[] shiftCopy = copyVal(val);		// creates a copy of the current number ro return later

		for(int i = 0; i < n; i++){ //We will be shifting n number of specified times
			boolean c = false;
			for(int j = 0; j < shiftCopy.length; j++){ //Ensures that we shift every last bit
				byte lsb = (byte) (shiftCopy[j] & 1); //Get the LSB of this byte so if we shift it right by 1, make sure to transfer it to the next byte of val
				shiftCopy[j] = (byte) ((shiftCopy[j] & 0xFF) >> 1); //Shift right
				
				if(c) {
					shiftCopy[j] |= 128; //If the lsb was previously 1, now is the time to add it back
				}
				if(lsb == 0){
					c = false;
				}	
				else{
					c = true;
				}	
			}
		}

		return new LargeInteger(shiftCopy);		// returns a new large integer that has applied the specified shift
	}

	//Erase bytes containing all zeros
	public LargeInteger trimLeadingZeros(){
		int leadingZeroBytes = 0;
		for(int i = 0; i < val.length; i++){ 
			if(val[i] == 0){
				leadingZeroBytes++;
			}
			else{
				break;
			}	
		}

		byte[] newval = new byte[val.length-leadingZeroBytes]; 
		for(int i = 0; i < newval.length; i++){ 
			newval[i] = val[i+leadingZeroBytes];
		}

		return new LargeInteger(newval);
	}
	
	
	
	private static byte[] copyVal(byte[] arr){
		byte[] output = new byte[arr.length];

		for(int i = 0; i < arr.length; i++){
			output[i] = arr[i];
		}

		return output;
	}

	public static byte[] increaseByOneByte(byte[] curr){
		byte[] output = new byte[curr.length+1];
		for(int i = 0; i < curr.length; i++){
			output[i+1] = curr[i];
		}

		return output;
	}
	
	public void extend(byte x) {
		byte[] newArray = new byte[val.length + 1];
		newArray[0] = x;
		for (int i = 0; i < val.length; i++) {
			newArray[i + 1] = val[i];
		}
		val = newArray;
	}
	
	public boolean isNegative() {
		return (val[0] < 0);
	}
	
	public int length() {
		return val.length;
	}
	
	public byte[] getVal() {
		return val;
	}
}