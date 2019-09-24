import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PreviousWords {
	private String fileName = "user_history.txt";
	private File file = new File(fileName);

	private DLB dlb;
	private ArrayMaker am;

	private Node root;
	private Node current;

	private boolean found;   // if a word/letter was found or not
	private boolean exists;  // if file exists

	private ArrayList<Node> nodes = new ArrayList<Node>();
	private ArrayList<Node> guessList = new ArrayList<Node>();

	private ArrayList<String> strings = new ArrayList<String>();

	private char[] array;

	public PreviousWords() {
		addToDlb();
	}
	// returns array of previous answers
	public String[] getArray(String word) {
		// if root is null doesn't return array
		if(root == null)
			return null;
		// resets all values
		current = root;
		nodes.clear();
		guessList.clear();
		strings.clear();
		found = true;

		ArrayList<String> list = new ArrayList<String>();
		list.clear();
		String[] arr = null;
		char c = ' ';
    // searches for current guess
		for(int i = 0; i < word.length(); i++) {
			c = word.charAt(i);
			getCurrent(c);
			if(!found && guessList.size() <= 0)
				return null;
		}
    // adds guesses to nodes
		for(int i = 0; i < guessList.size(); i++) {
			nodes.add(guessList.get(i));
		}
    // adds found words to strings
		for(int i = 0; i < 5; i++) {
			strings.add(i, findWord());
		}
    // copies words to list
		int j = 0;
		while(strings.get(j) != "*") {
			list.add(strings.get(j));
			j++;
		}

		arr = new String[list.size()];
    // copies string array list to string array
		for(int i = 0; i < list.size(); i++) {
			if(!list.get(i).equals("*"))
				arr[i] = list.get(i);
		}

		return arr;
	}
	// gets current node
	public void getCurrent(char g) {
		boolean done = false;

		if(guessList.size() > 0) {
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
			found = false;
			current = root;
		}
	}
  // finds words in dlb based on guesses
	public String findWord() {
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
          // checks if this backtracks to far
					if(place < guessList.size()) {
						return "*";
					}
					while(current.nextNode == null) {
						nodes.remove(place);
						place--;
            // checks if this backtracks to far
						if(place < guessList.size()) {
							return "*";
						}
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

		String[] arr = null;
    // checks if words found isn't in the file
		if(exists) {
			arr = am.getStrings();

			for(int i = 0; i < arr.length; i++) {
				if(arrListToString(nodes).equals(arr[i]))
					return arrListToString(nodes);
			}
		}
		return "*";
	}
	// adds contents of file to dlb
	public void addToDlb() {
		readFromFile(); // reads from file
		// if file exits create a new dlb based on files contents
		if(exists && array.length != 0) {
			dlb = new DLB(array);
			root = dlb.getRoot();
			current = root;
		} else
			root = null;
	}
	// reads from file and stores contents into array
	public void readFromFile() {
		exists = false;
		try {
			// if file doesn't exist create one
			if(!file.exists()) {
			    file.createNewFile();
			}
			else {
				// stores file contents into array
				am = new ArrayMaker(fileName);
				array = am.getArray();
				exists = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// writes word to "user_history.txt"
	public void writeToFile(String s) {
		PrintWriter out;
		String[] arr = null;

		// checks if word isnt already in file
		if(exists) {
			arr = am.getStrings();

			for(int i = 0; i < arr.length; i++) {
				if(s.equals(arr[i]))
					return;
			}
		}
		// exports word to file
		try {
		    out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
		    out.print(s + "\n");
		    out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		addToDlb();
	}

	// converts array list of nodes into a readable string
	public String arrListToString(ArrayList<Node> arr) {
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
