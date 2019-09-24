/*
 * creates a node to be added to the DLB trie
 * each node contains a char value and Node pointers for the childNode and the nextNode
 */
class Node {

	char value; // value of node
	Node childNode; // next node in column
	Node nextNode; // node that current node will be connected to

	// creates parent node
	public Node(char value) {
		this(value, null, null);
	}

	// creates node with connecting nodes
	public Node(char value, Node childNode, Node previousNode) {
		this.value = value;
		this.childNode = childNode;
		this.nextNode = previousNode;
	}
}

/*
 * creates a DLB trie from a char array passed through constructor
 */
public class DLB {
	private Node root, current; // root and current nodes
	private char[] arr;		   // array of characters from dictionary
	private int place = 0;	   // character place in array

	public DLB(char[] array) {
		this.arr = array;

		dlbMaker();
	}

	// creates the dlb trie
	public void dlbMaker() {
		boolean start; // if true looks to place a next node, if false looks to place a child node
		char value = arr[place]; // sets value of current char

		root = new Node(value); // creates root
		current = root; // updates current node
		place++; // increments array
		start = false; // sets start to false
		// creates dlb try while there are still chars in the array
		while(place < arr.length) {
			value = arr[place]; // updates value of current char
			// checks if current value is an end of line char
			// if so creates a new node and sets the current node to root to start a new word
			if(value == '\n') {
				current.childNode = new Node(value); // creates new node
				current = root; // updates current node to root
				place++;        // increments array
				start = true;   // sets start equal to true
			} else {
				// checks if start is true, checks to add a next node
				if(start) {
					// if the current value is equal to current node
					// increment array and set start to false to check for child node
					if(current.value == value) {
						place++; // increment array
						start = false; // set start to false
					// if the next node is null, create a new nextNode with the current value
					// then update current node, iterate array and set start to false to check for child node
					} else if(current.nextNode == null) {
						current.nextNode = new Node(value); // create new nextNode
						current  = current.nextNode;        // update current node
						place++;		// increment array
						start = false;  // set start to false
					// if there is a next node that does not equal current node
					// set current node to next node and set start to true to check the nextNode
					} else {
						current = current.nextNode; // update current node
						start = true; 				// set start to true
					}
				// if start is false, checks to add a child node
				} else {
					// if there is no child node, create a new childNode with current value
					// then update current node, increment array, and set start to false to check for childNode
					if(current.childNode == null) {
						current.childNode = new Node(value); // create new childNode
						current = current.childNode;		 // update current node
						place++;		// increment array
						start = false;  // set start to false
					// if current value equals the value of childNode
					// update current to that childNode, increment array and set start to false to check next child
					} else if(current.childNode.value == value) {
						current = current.childNode;	// update current node
						place++;		// increment array
						start = false;	// set start to false
					// if a child node exists but doesn't equal current value
					// update current node to that child node, set start to true to check that nodes nextNode
					} else {
						current = current.childNode;	// update current node to childNode
						start = true;					// set start to true
					}
				}
			}
		}
	}
	// returns root of DLB trie
	public Node getRoot() {
		return root;
	}
}
