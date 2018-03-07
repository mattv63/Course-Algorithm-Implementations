import java.util.*;
import java.io.*;

/* 
 * ac_test is a program that simulates an auto complete feature commonly found on smart phones. The program uses several other classes including
 * DLB, Rway and Queue to store and retrieve data accordingly. The program uses a DLB trie to store every word contained in dictionary.txt and 
 * a Rway trie to store all words that user has previously entered. A queue is used to retrieve all keys within both previously mentioned data
 * structures. 
 */



public class ac_test{
	
	public static void main(String[] args){
		
		DLB<Integer> dict = new DLB<Integer>();			// Creates DLB trie dict
		Rway<Integer> history = new Rway<Integer>();	// Creates Rway trie history
		
		Scanner scan = new Scanner(System.in);			// Creates scanner to read character input from user
		int value = 0;									// Value for keys in DLB
		int value2 = 0;									// Value for keys in Rway
		String user = "";								// User input
		boolean loop = true;						
		long startTime;									// Time before predictions
		long endTime;									// Time after predictions
		float time;										
		float totalTime = 0;
		float total = 0;
		char c;											// char user enters
		int i, j;
		
		/* The following code reads in every string from dictionary.txt and stores it
		 * in the DLB, dict. The program uses a BufferedReader and stores each line in
		 * strLine. The function put() from DLB is continously called using strLine and 
		 * the current value count as parameters. Value is raised by 1 after each string
		 * is added.
		 */
		
		BufferedReader br = null;
        String strLine = "";
        try {
            br = new BufferedReader( new FileReader("dictionary.txt"));
            while( (strLine = br.readLine()) != null){
                dict.put(strLine, value);
				value ++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the file: fileName");
        } catch (IOException e) {
            System.err.println("Unable to read the file: fileName");
        }
		
		/* Now we are looking at some more fun file stuff. This time we check to see
		 * if a text file called history.txt has been created yet. This file contains 
		 * previous entries that the user has made. If it exists, the entries are stored
		 * in the Rway trie, history. If it does not exist, the program will create a new
		 * history.txt file
		 */
		
		try {
            br = new BufferedReader( new FileReader("user_history.txt"));
            while( (strLine = br.readLine()) != null){
                history.put(strLine, value2);
				value2 ++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the file: user_history.txt. Creating now");
			try{
				PrintWriter writer = new PrintWriter("user_history.txt", "UTF-8");
			} catch(IOException a) {
					   
					}
        } catch (IOException e) {
            System.err.println("Unable to find the file: user_history.txt. Will be created at end of program.");
        }
		
		//Program begins for the user. Loop will end when user types '!'
		
		while(loop){
			user = "";		//resets current user input to nothing
			String guess, guess1 = "", guess2 = "", guess3 = "", guess4 = "", guess5 = "";
			i = 1;
			System.out.print("Enter your first character: ");
			Queue<String> prefixWords = dict.keysWithPrefix(user);				// Calls keysWithPrefix method from DLB which returns a queue with predictions from dictionary
			Queue<String> userPrefixWords = history.keysWithPrefix(user);		// Calls keysWithPrefix method from Rway which returns a queue with predictions from user's history
	
			while(loop){
				c = scan.next().charAt(0);		// c stores the current char that the user has entered
				
				/* 33 corresponds to the '!' symbol. Entering this system will end the program.
				 * But before ending, this if statement will print the average time the program
				 * took to find predictions and displays a 'Bye' message.
				 */
				
				if (c == 33){
					System.out.println("Average Time: " + (totalTime/total) + " s");
					System.out.print("Bye!");
					System.exit(0);
				}
				
				/* The next five if statements account for when the user has found the word that
				 * they are trying to enter. The program will put that word into the Rway trie and
				 * write it into the history.txt file. Program will continue and ask user for a new
				 * word. 
				 */
				
				if (c == 49){ 
					System.out.println("Word Completed! " + guess1); 
					history.put(guess1, value2); 
					try(FileWriter fw = new FileWriter("user_history.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw))
						{
							out.println(guess1);
					} catch (IOException e) {
							
						}
					value2++; 
					break;
				}
				if (c == 50){ 
					System.out.println("Word Completed! " + guess2); 
					history.put(guess2, value2); 
					try(FileWriter fw = new FileWriter("user_history.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw))
						{
							out.println(guess2);
						
					} catch (IOException e) {
							
						}
					value2++; 
					break;
					}
				if (c == 51){ 
					System.out.println("Word Completed! " + guess3); 
					history.put(guess3, value2);
					try(FileWriter fw = new FileWriter("user_history.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw))
						{
							out.println(guess3);
						
					} catch (IOException e) {
							
						}
					value2++; 
					break;
				}
				if (c == 52){ 
					System.out.println("Word Completed! " + guess4); 
					history.put(guess4, value2);
					try(FileWriter fw = new FileWriter("user_history.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw))
						{
							out.println(guess4);
						
					} catch (IOException e) {
							
						}
					value2++; 
					break;
				}
				if (c == 53){ 
					System.out.println("Word Completed! " + guess5); 
					history.put(guess5, value2); 
					try(FileWriter fw = new FileWriter("user_history.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw))
						{
							out.println(guess5);
						
					} catch (IOException e) {
							
						}
					value2++; 
					break;
				}
				
				/* 36 corresponds to the '$' symbol. If this is entered by the user then the user has 
				 * finished typing their input. This string will be added into the history Rway trie
				 * and written onto history.txt. The Rway trie value will be increased by 1 to account
				 * for the new entry and the loop will be broken.
				 */
				
				if (c == 36){
					System.out.println(user + " added to user's history!");
					history.put(user, value2);
					try(FileWriter fw = new FileWriter("user_history.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter out = new PrintWriter(bw))
						{
							out.println(user);
						
					} catch (IOException e) {
							
						}
						
					value2++;
					break;
				}
				
				// If the program has made it this far, then the user is continuing to type a word
				
				user += c;		// Adds current character onto user
				
				
				startTime = System.nanoTime();						// Determines time before finding predictions
				prefixWords = dict.keysWithPrefix(user);			// Finds predictions from dict
				userPrefixWords = history.keysWithPrefix(user);		// Finds prediction from history
				
				endTime = System.nanoTime();		// Determines time immediatly after finding predictions
				time = endTime - startTime;			// Determines time in nano seconds
				time = time/1000000000;				// Determines time in seconds
				totalTime += time;					// Adds time it took to find previous prediction to total time
				total ++;							// Add 1 to count to find average later
				
				System.out.println("\n(" + time + " s)");
				System.out.println("Predictions:");
				
				/* Displays predictions from history first. Uses for loop with a maximum of 5 loops.
				 * First the program will ensure that there is data in the user history queue and then
				 * call the dequeue method. Strings will be displayed to the screen. Breaks as soon as
				 * no more predictions can be made.
				 */
				
				for (i = 1; i < 6; i++){
					if (userPrefixWords.size() > 0){
						guess = userPrefixWords.dequeue();
						System.out.print("(" + i + ") " + guess + "\t");
						if(i == 1) guess1 = guess;
						if(i == 2) guess2 = guess;
						if(i == 3) guess3 = guess;
						if(i == 4) guess4 = guess;
						if(i == 5) guess5 = guess;
					}
					else{
						break;
					}
				}
				
				/* After displaying history predictions, the program will display the dictionary predictions. 
				 * The for loop will start with whatever number that the history loop left off on. If a 
				 * prediction was already made in the history loop, then that string will not be displayed here.
				 * If there are no predictions to be made than a message will be displayed informing the user and
				 * telling them to continue typing.
				 */
				
				for (i = i; i < 6; i++){
					if (prefixWords.size() > 0){
						guess = prefixWords.dequeue();
						if (guess.equals(guess1) || guess.equals(guess2) || guess.equals(guess3) || guess.equals(guess4) || guess.equals(guess5)){
							i = i - 1;
						}
						else{
							System.out.print("(" + i + ") " + guess + "\t");
							if(i == 1) guess1 = guess;
							if(i == 2) guess2 = guess;
							if(i == 3) guess3 = guess;
							if(i == 4) guess4 = guess;
							if(i == 5) guess5 = guess;
						}
					}
					else{
						if(i == 1){
							System.out.println("Current word does not appear in dictionary or user history. Finish and word and press '$' to add to user history.");
						}
					}
				}
				System.out.println("\nEnter the next character: ");
			}
		}
	}	
}