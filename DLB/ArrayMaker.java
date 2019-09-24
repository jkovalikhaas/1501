import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/*
 * reads the characters from a text file and stores them into an array
 */
public class ArrayMaker {
	private char[] array;		// sets array to store characters from text file
	private int charAmount = 0; // keeps track of how many characters are in array

	private String fileName;    // name of file

	// constructor to run file
	public ArrayMaker(String fileName) {
		this.fileName = fileName;

		readFile();
	}

	// reads from file and then stores contents into an array
	public void readFile() {
		File file = new File(fileName);     // file that will be read

		FileReader fileReader = null; // file for reading words
		Scanner fileCount = null; // file for getting array size
		// try/catch to make sure file exists
		try {
			fileReader = new FileReader(fileName);  // reads file for characters
			fileCount = new Scanner(file);			// scans file for getting amount of chars

			// get the number of characters in the file
			while(fileCount.hasNextLine()) { // while there are
				String s = fileCount.nextLine(); // stores current line in a string
				// increments char amount by characters in each string/line of the file
				// and then adds one to include the new line character not read by "nextLine()"
				charAmount += s.length() + 1;
			}
			fileCount.close(); // closes fileCount
			// instantiates array with length of how many characters are in the file
			array = new char[charAmount];
			// c is to store each value of the current char
			// i is to iterate through the array to place each char
			int c, i = 0;
			// updates c for each char in array while there are still chars in the file
			while ((c = fileReader.read()) != -1) {
				array[i] = (char) c; // adds current char value to the array
				i++;  				 // increments array
			}
			fileReader.close();      // closes fileReader
		// catch cases for the possible exceptions thrown
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// getter to return array
	public char[] getArray() {
		return array;
	}

	public String[] getStrings() {
		String[] strings = null;
		int lineNum = 0;

		File file = new File(fileName);     // file that will be read

		Scanner fileReader = null; // file for reading words
		Scanner fileCount = null; // file for getting array size
		// try/catch to make sure file exists
		try {
			fileReader = new Scanner(file); // reads file for strings
			fileCount = new Scanner(file);	// scans file for getting amount of chars

			// get the number of characters in the file
			while(fileCount.hasNextLine()) { // while there are
				fileCount.nextLine(); // stores current line in a string
				lineNum++;
			}
			fileCount.close(); // closes fileCount
			// instantiates array with length of how many characters are in the file
			strings = new String[lineNum];
			// i is to iterate through the array to place each char
			// updates string array for each string file
			int i = 0;
			while (fileReader.hasNextLine()) {
				strings[i] = fileReader.nextLine();
				i++;
			}
			fileReader.close();      // closes fileReader
		// catch cases for the possible exceptions thrown
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return strings;
	}
}
