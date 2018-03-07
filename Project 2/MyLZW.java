
import java.util.*;

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L;       // variable for codword length
    private static int W;       // variable for codeword width
	private static final int FULL = 65536;		// Maximum size of codebook

    public static void compress(String c) { 
		int i;
		W = 9; L = 512;		//	Set width and length to minimum
        String input = BinaryStdIn.readString();		//	Read in everything from input file and store in String
        TST<Integer> st = new TST<Integer>();		//	Codebook
        for (i = 0; i < R; i++)		//	Puts all ascii characters into codebook
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF
		i = i + 1;	// Current position in codebook
		
		if (c.equals("n")){		//	if user selects do nothing mode
			BinaryStdOut.write('n');	//	expansion will know what procedure to follow
		
			while (input.length() > 0) {
				String s = st.longestPrefixOf(input);  // Find max prefix match s.
				BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
				int t = s.length();
				if (t < input.length() && code < L){    // Add s to symbol table.
					st.put(input.substring(0, t + 1), code++);
				}
				else if (t < input.length() && W < 16){		//	If current codebook with a width less than 16 is full
					W++;		// increase width by 1
					L = (int)Math.pow(2.0, (double) W);	//	2^W
					st.put(input.substring(0, t + 1), code++);
				}
				input = input.substring(t);            // Scan past s in input.
			}
		}
		if (c.equals("r")){		//	if user selects reset mode
			BinaryStdOut.write('r');	
			
			while (input.length() > 0) {
				
				if ((i >= L) && (W!=16)){		//	Increases codebook word and width length while not hitting the maximum
					W++;
					L = (int)Math.pow(2.0, (double) W);
				}
				
				if (i == FULL){		//	We have reached the end of the code. Time to throw out the old one and create a new one
					st = new TST<Integer>();	//	New codebook
					for (i = 0; i < R; i++){		//	Inputs all ascii characters
						st.put("" + (char) i, i);
					}
					i = i + 1;		//Current position in codebook
					W = 9;		//	Set width and length to minimum
					L = (int)Math.pow(2.0, (double) W);
					code = R + 1;	// EOF
					
				}
				String s = st.longestPrefixOf(input);	
				BinaryStdOut.write(st.get(s), W);	
				int t = s.length();
				if (t < input.length() && code < L){
					st.put(input.substring(0, t + 1), code++);
					i ++;
				}
				input = input.substring(t);
			}
		}
		if (c.equals("m")){		// if user selects monitor mode
			BinaryStdOut.write('m');
			boolean monitorTime = false;	//	variable that will tell the program when it is time to start monitoring (when codebook is full)
			int uncompressedData = 0;	//	Data not yet processed
			int compressedData = 0;		//	Data that has been processed
			double newRatio = 0.0;
			double oldRatio = 0.0;
			while (input.length() > 0){
				
				if ((i >= L) && (W!=16)){	//	Increases codebook word and width length while not hitting the maximum
					W++;
					L = (int)Math.pow(2.0, (double) W);
				}
				if (compressedData != 0){		//	Creates ratio of uncompressed data over compressed data
					newRatio = ((double)uncompressedData / (double)compressedData);
				}
				if (i == FULL || monitorTime == true){		// for when codebook is filled up
					if (monitorTime == true){				//	code for when the program is monitoring
						if ((oldRatio / newRatio) > 1.1){	//	Creates new codebook if ratio exceeds 1.1
							st = new TST<Integer>();		//	new codebook
							for (i = 0; i < R; i++)
                                st.put("" + (char) i, i);
							W = 9;		// Minimum length and width 
							L = (int)Math.pow(2.0, (double) W);
							code = R + 1;	//EOF 
							i = i + 1;	
							monitorTime = false;	//	Turns off monitor
							uncompressedData = 0;	// Resets uncompressed and compressed data to 0
							compressedData = 0;
						}
					}
					else{		// Turns monitor mode on
						oldRatio = newRatio;
						monitorTime = true;
					}
				}
				String s = st.longestPrefixOf(input);
				uncompressedData = uncompressedData + (8 * s.length()); //Adds number of bits remaining to uncompressed data
				compressedData = compressedData + W;		//	Adds width of codeword read to compressed data
				BinaryStdOut.write(st.get(s), W);	
				int t = s.length();
				if (t < input.length() && code < L){
					st.put(input.substring(0, t + 1), code++);
					i++;
				}
				input = input.substring(t);
			}
			
		}
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
		W = 9; L = 512;
		char c = BinaryStdIn.readChar(); 	// Get mode
        String[] st = new String[FULL]; // 2^16
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
	
		int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];		//	Get value to print from codebook

		if (c == 'n'){	// if mode is do nothing
		
			while (true) {
				BinaryStdOut.write(val);	// write value to output file
				codeword = BinaryStdIn.readInt(W);	//	read in next codeword from input file
				if (codeword == R) break;
				String s = st[codeword];
				if (i == codeword) s = val + val.charAt(0);   // special case hack
				if (i < L - 1) st[i++] = val + s.charAt(0);
				else if(W < 16){		//	while width is under 16, size of codewords can increase
					W++;
					L = (int)Math.pow(2.0, (double) W);
					st[i++] = val + s.charAt(0);
				}
				val = s;
			}
		}
		if(c == 'r'){	//	if mode is reset
			while (true) {
				if ( i == (FULL - 1)){	//	if the codebook is about to reach the maximum amount of codewords we reset
					st = new String[FULL];	//	new codebook
					for (i = 0; i < R; i++)		//	ascii characters
						st[i] = "" + (char) i;
					st[i++] = "";
					W = 9;		// minimum length and width
					L = (int)Math.pow(2.0, (double) W);
					codeword = BinaryStdIn.readInt(W);
					val = st[codeword];
				}
				if((i >= L-1) && (W != 16)){
					W++;
					L = (int)Math.pow(2.0, (double) W);
				}
				BinaryStdOut.write(val);
				codeword = BinaryStdIn.readInt(W);
				if (codeword == R) break;
				String s = st[codeword];
				if (i == codeword) s = val + val.charAt(0);
				if (i < L) st[i++] = val + s.charAt(0);
	
				val = s;
			}	
		}
		if (c == 'm'){
			boolean monitorTime = false;	// variable that determines when to open that gates to monitor mode
			int uncompressedData = 0;		//	data in the output file that has been decompressed
			int compressedData = 0;		//	data in the input file that is still compressed
			double newRatio = 0.0;
			double oldRatio = 0.0;
			
			while (true){
				if (compressedData!=0){		//	Creates ratio of uncompressedData over compressedData 
                
                    newRatio = ((double)uncompressedData / (double)compressedData);    
                }

                if((i >= L-1) && (W != 16)){	//	Resize codebook till width hits 16
                    W++;
                    L = (int)Math.pow(2.0, (double) W);
                }
				BinaryStdOut.write(val);		//	write value to output file
				
				if (i == FULL - 1 || monitorTime == true){	//	open the gate once codebook is full (i like this term open the gate now)
					if(monitorTime == true){	// enter monitor mode
						if ((oldRatio/newRatio) > 1.1){		// if ratio exceeds the thresehold then throw the old codebook out and make a new one
							st = new String[FULL];		//	new codebook
							for (i = 0; i < R; i++) //re-initialize symbol table with all 1-character strings
                                st[i] = "" + (char) i;
                            W = 9;		// minimum length and width
                            L = (int)Math.pow(2.0, (double) W);
                            st[i++] = "";                        // (unused) lookahead for EOF
                            codeword = BinaryStdIn.readInt(W);	
                            val = st[codeword];
                            monitorTime = false;
                            uncompressedData=0;
                            compressedData=0;
						}
					}
					else{
						oldRatio = newRatio;
						monitorTime = true;
					}
				}
				compressedData = compressedData + W;
				codeword = BinaryStdIn.readInt(W);
				if (codeword == R) break;
				String s = st[codeword];
				if (i == codeword) s = val + val.charAt(0); 
                if (i < L) st[i++] = val + s.charAt(0);
                val = s;
                uncompressedData = uncompressedData + (8 * val.length());
			}
		}
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}