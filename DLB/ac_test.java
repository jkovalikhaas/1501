import java.util.ArrayList;
import java.util.Scanner;

/*
 * contains main method. Creates a dlb trie with the contents of the dictionary
 * then runs a program that searches the dictionary for a word based on the guessed characters
 */

public class ac_test {
	private static ArrayMaker arrayMaker;	// for reading from file
	private static PreviousWords pw;		// gets previous guesses
	private static DLB dlb;					// creates dlb

	private static Node current;										// stores current node
	private static ArrayList<Node> guessList = new ArrayList<Node>();	// list of current guesses
	private static ArrayList<Node> nodes = new ArrayList<Node>();		// list of nodes for current prediction

	private static char[] array;			// array to store dictionary

	public static void main(String[] args) {
		arrayMaker = new ArrayMaker("dictionary.txt");	// reads from dictionary.txt
		array = arrayMaker.getArray();					// stores array contents in array

		dlb = new DLB(array);			// creates dlb
		Node root = dlb.getRoot();		// sets root node based on dlb

		pw = new PreviousWords();		// sets up previous answers

		program(root);					// runs program
	}
	// runs program which makes predictions based on current guesses
	public static void program(Node root) {
		String guessNum = "your first";		// for changing question based on if first char
		char guess = ' ';					// stores current guess

		int guesses = 0;					// number current of guesses
		int totalGuesses = 0;				// number of total guesses
		int numPredictions = 5;				// total amount of predictions
		int predictionsPlace = 0;			// dictates place of prediction based on previous guesses

		long startTime = 0;					// stores current time per guess
		long endTime = 0;					// stores end time of guess

		double seconds = 0;					// to convert time nano time to seconds
		double totalTime = 0;				// stores total time of guesses

		ArrayList<String> predictions = new ArrayList<String>();	// stores current predictions
		String[] strings;											// stores previous answers

		current = root;		// sets current to root

		Scanner kb = new Scanner(System.in);	// scanner to read guesses
		// loop to run program until user quits with '!' as guess
		do {
			nodes.clear(); // clears current nodes
			// changes question if not the first guess
			if(guesses > 0)
				guessNum = "the next";
			System.out.print("Enter " + guessNum + " character: "); // asks for guess
			// reads current guess, exits program if guess is invalid
			try {
				guess = kb.nextLine().charAt(0);
			} catch(IndexOutOfBoundsException e) {
				System.out.println("Invalid Guess.");
				guess = '!';
			}

			guesses++;		// increments number of guesses
			totalGuesses++; // increments total guesses
			// if guess is 1-5 or $, shows answer based on guess value and
			// exports the prediction to "user_history.txt"
			 if(guess >= '1' && guess <= '5' || guess == '$') {
				guesses = 0; 				// resets current number of guesses
				int guessInt = guess - 49;	// converts char to int
				String g = "";				// stores chosen prediction

				if(guess == '$') // sets answer to current guesses
					g = arrListToString(guessList);
				else {
					// checks if predictions have been made, if so sets current predciton as anser
					if(predictions.size() != 0 && guessInt < predictions.size())
						g = predictions.get(guessInt);
				}
				// prints out selected word and then exports it through the PreviousWords class
				if(predictions.size() != 0) {
					System.out.println("\n\tWORD COMPLETED: " + g + "\n");
					// does not add single letters to file, since they will be first prediction anyway
					if(g.length() > 1)
						pw.writeToFile(g); 	// writes completed word to file
					guessList.clear();		// clears current guesses
					current = root;			// resets current to root
				} // states that no predictions were made and then returns to beginning of program
				else
					System.out.println("No predictions made.");
			// runs if the user didn't chose to exit program
			} else if(guess != '!') {
				predictions.clear();	// clears current predictions

				startTime = System.nanoTime();	// sets current start time

				nextGuess(guess);	// gets the next guess

				strings = pw.getArray(arrListToString(guessList)); // gets previous answers if they exist
				if(strings != null) {
					// copies previous answers to predictions list
					for(int i = 0; i < strings.length; i++) {
						predictions.add(strings[i]);
						predictionsPlace++; // increments to not overwrite previous answers
					}
				}
				// copies guessList to nodes
				for(int i = 0; i < guessList.size(); i++) {
					nodes.add(i, guessList.get(i));
				}
				// if current isn't null
				if(current != null) {
					// gets the predictions for current guesses
					for(int i = predictionsPlace; i < numPredictions; i++) {
						predictions.add(i, getPrediction());
						if(strings != null) {
							for(int j = 0; j < strings.length; j++) {
								if(predictions.get(i).equals(predictions.get(j))) {
									predictions.add(i, getPrediction());
								}
							}
						}
					}

					endTime = System.nanoTime() - startTime;	// calculates total time of getting predictions
					seconds = (double) endTime / 1000000000;	// converts endTime to seconds
					totalTime += seconds;						// increments total time

					System.out.printf("\n(%f)", seconds);		// prints seconds as a decimal
					System.out.println("s\n" + "Predictions:");
					// prints out each prediction
					for(int i = 0; i < numPredictions; i++) {
						System.out.print("(" + (i + 1) + ") " + predictions.get(i) + " \t");
					}
					predictionsPlace = 0;	// resets predictionPlace
				}
				System.out.println("\n");
			}
		} while(guess != '!'); // ends program

		totalTime = totalTime / totalGuesses;	// calculates average time
		System.out.printf("\n\nAverage Time: %f s\n", totalTime);

		System.out.println("Bye!");
	}
	// gets next guess
	public static void nextGuess(char g) {
		boolean done = false; // boolean to dictate when current is found
		// makes sure there are guesses
		if(guessList.size() > 0) {
			// sets current to to of guessList
			current = guessList.get(guessList.size() - 1);
			if(current.childNode != null)
				current = current.childNode;
			else
				done = true;
		}

		while(!done) {
			if(current.value == g) {
				guessList.add(current);
				done = true;
			} else if(current.nextNode == null) {
				current = current.nextNode;
				done = true;
			} else {
				current = current.nextNode;
			}
		}

		if(current == null) {
			System.out.println("No predictions were found.");
		}
	}
	// gets current prediction
	public static String getPrediction() {
		boolean done = false;
		int place = nodes.size() - 1;

		while(!done) {
			if(current.childNode != null) {
				if(current.childNode.value == '\n') {
					current = current.childNode;
					done = true;
				} else {
					current = current.childNode;
					nodes.add(current);
				}
			} else {
				if(current.nextNode == null) {
					// goes back through nodes to find new predictions
					while(current.nextNode == null) {
						nodes.remove(place);
						place--;
						current = nodes.get(place);
					}
					current = current.nextNode;
					nodes.remove(place);
					nodes.add(current);
				} else {
					current = current.nextNode;
					nodes.add(current);
				}
			}
		}

		return arrListToString(nodes);
	}
	// converts array list of nodes into a readable string
	public static String arrListToString(ArrayList<Node> arr) {
		ArrayList<String> list = new ArrayList<String>(); // list to copy contents of ArrayList<Node> into
		// copies contents into string array list
		for(int i = 0; i < arr.size(); i++) {
			list.add(Character.toString(arr.get(i).value));
		}

		// set string array list to a string
		String s = list.toString();
		// replaces unwanted chars with empty strings
		s = s.replace(",", "");
		s = s.replace("[", "");
		s = s.replace("]", "");
		s = s.replaceAll("\\s+",""); // removes white spaces

		return s;
	}
}
