import java.util.*;
/*
 * Contains main method
 * Runs program to store Apartments in PQs based on min price and max footage
 */
public class AptTracker {

  private static ArrayList<PQ<Apartment>> minArr;   // ArrayList to store PQs of Apartments by price
  private static ArrayList<PQ<Apartment>> maxArr;   // ArrayList to store PQs of Apartments by footage
  // HashMap to find and store Apartments through adress, apt num, zip
  private static HashMap<Integer, IndexObj> map;

	private static boolean done = false;    // boolean to check when to exit program

	public static void main(String[] args) {
	    Scanner kb = new Scanner(System.in);
      System.out.println("Welcome to Apartment Tracker!\n");

      minArr = new ArrayList<>();
      maxArr = new ArrayList<>();
      map = new HashMap<>();
      // runs while correct input is inserted (1-7)
	    while(!done) {
	        askQuestions();  // asks options

	        System.out.print("Option: ");
	        char option = kb.next().charAt(0); // takes first char of input
          // runs through the options
	        switch (option) {
	             // add
                case('1'):
                    Apartment a1 = newApartment();
                    insert(a1, minArr, 0);
                    addToHash(a1);
                    insert(a1, maxArr, 1);
                    break;
                // update
                case('2'):
                    update();
                    break;
                // delete
                case('3'):
                    // deletes Apartment from both ArrayLists
                    Apartment a2 = askHash();
                    delete(minArr, a2, 0);
                    delete(maxArr, a2, 1);
                    break;
                // min price
                case('4'):
                    if(!minArr.isEmpty())
                        aptInfo(minArr.get(0).getRoot());
                    break;
                // max footage
                case('5'):
                    if(!maxArr.isEmpty())
                        aptInfo(maxArr.get(0).getRoot());
                    break;
                // min price by city
                case('6'):
                    String c1 = askCity();
                    if(findCity(minArr, c1) >= 0)
                        aptInfo(minArr.get(findCity(minArr, c1)).getRoot());
                    break;
                // max footage by city
                case('7'):
                    String c2 = askCity();
                    if(findCity(maxArr, c2) >= 0)
                        aptInfo(maxArr.get(findCity(maxArr, c2)).getRoot());
                    break;
                default:
                    done = true;
            }
            Collections.sort(minArr, new PQapt());  // sorts ArrayList
            Collections.sort(maxArr, new PQapt());  // sorts ArrayList
        }
	}
  // questions being asked
	public static void askQuestions() {
        System.out.println();
        System.out.println("1) Add an apartment");
        System.out.println("2) Update an apartment");
        System.out.println("3) Remove an apartment");
        System.out.println("4) Lowest price apartment");
        System.out.println("5) Highest footage apartment");
        System.out.println("6) Lowest price in a city");
        System.out.println("7) Highest footage in a city");
    }
  // asks for information on an Apartment and then creates an Apartment
  // to insert in both the price and footage PQ
	public static Apartment newApartment() {
  		Scanner kb = new Scanner(System.in);
  		Apartment apt = new Apartment();

  		// set apartments street address
  		System.out.print("Street Address: ");
  		apt.address = kb.nextLine().replaceAll("\\s","");
  		// set apartments apartment number
  		System.out.print("Apartment Number: ");
  		apt.aptNum = kb.nextInt();
  		// set apartments city
  		System.out.print("City: ");
  		apt.city = kb.next().replaceAll("\\s+","");
  		// set apartments zip code
  		System.out.print("Zip Code: ");
  		apt.zip = kb.nextInt();
  		// set apartments rent
  		System.out.print("Rent: ");
  		apt.price = kb.nextInt();
  		// set apartments square footage
  		System.out.print("Square Footage: ");
  		apt.footage = kb.nextInt();

      return apt;
	}
	// inserts apartment into both the price and footage PQ's
	public static void insert(Apartment apt, ArrayList<PQ<Apartment>> pq, int m) {
	    int s = pq.size();     // current amount of keys in PQ
      boolean found = false;  // state if city is found
      apt.m = m;
      // if no keys in PQ, create a new one
      if(s == 0) {
          newCity(apt, pq);
      } else {
        // check if city is already taken
          for(int i = 0; i < s; i++) {
              // if so, insert apartment into that city's PQ
              if(apt.city.equals(pq.get(i).getRoot().city)) {
                  pq.get(i).insert(apt);
                  found = true;
              }
          }
          // if not, create new city PQ for price and footage
          if(!found)
            newCity(apt, pq);
      }
    }
    // create a new PQ to store the apartment
    public static void newCity(Apartment apt, ArrayList<PQ<Apartment>> pq) {
  	    // insert new PQ<Apartment>
        PQ<Apartment> m = new PQ<>(apt.m);
        m.insert(apt);
        pq.add(m);
    }
    // adds Apartment values to HashMap
    public static void addToHash(Apartment apt) {
        int h = stringToInt(apt.address) + apt.aptNum + apt.zip;
        map.put(h, new IndexObj(apt));
    }
    // asks user for address/apt num/zip and then returns hashable value
    public static Apartment askHash() {
  	    Scanner kb = new Scanner(System.in);
  	    int n = 0;

  	    System.out.print("Address: ");
  	    n += stringToInt(kb.next().replaceAll("\\s",""));
  	    System.out.print("Apartment Number: ");
  	    n += kb.nextInt();
  	    System.out.print("Zip: ");
  	    n += kb.nextInt();

  	    return map.get(n).getApt();
    }
    // converts string to int
    public static int stringToInt(String s) {
        int n = 0;
        for(int i = 0; i < s.length(); i++) {
            n += s.charAt(i);
        }
        return n;
    }
    // update an apartments price
    public static void update() {
	      Scanner kb = new Scanner(System.in);
        Apartment temp = askHash();
        delete(minArr, temp, 0);

        System.out.print("New Price: ");
        int newPrice = kb.nextInt();
        temp.price = newPrice;

        insert(temp, minArr, 0);
    }
    // remove apt from pq's
    public static void delete(ArrayList<PQ<Apartment>> pq, Apartment apt, int m) {
	    // remove Apartment from PQ
        Apartment temp = apt;
        temp.m = m;
        int n = findCity(pq, temp.city);
        int s = pq.get(n).size();

        for(int i = 0; i < s; i++) {
            if(pq.get(n).getKey(i).equals(temp)) {
                // if city will be empty, remove city
                if (s == 1) {
                    pq.remove(n);
                    break;
                } else {
                    pq.get(n).delete(i);
                    break;
                }
            }
        }
    }
    // asks user for city
    public static String askCity() {
        System.out.print("City: ");
        Scanner kb = new Scanner(System.in);
        String c = kb.next().replaceAll("\\s","");

        return c;
    }
    // find min/max of city
    // @param min   if true search for min price; if false search for max footage
    // @return      returns the reference to the min/max of a city
    public static int findCity(ArrayList<PQ<Apartment>> apt, String c) {
        for(int i = 0; i < apt.size(); i++) {
            if(apt.get(i).getRoot().city.equals(c)) {
                return i;
            }
        }
        // returns negative if city not found (so program doesn't crash over typos)
        return -1;
    }
    // prints out info about specified Apartment
	public static void aptInfo(Apartment apt) {
	    if(apt == null)
	        return;
	    System.out.println("Street Address: " + apt.address);
	    System.out.println("Apartment Number: " + apt.aptNum);
	    System.out.println("City: " + apt.city);
	    System.out.println("Zip Code: " + apt.zip);
	    System.out.println("Rent: " + apt.price);
	    System.out.println("Square Footage: " + apt.footage);
    }
}
// compares PQs of Apartments (price vs footage PQs)
class PQapt implements Comparator<PQ<Apartment>> {
    // returns compator based on Apartment.m, else returns throws an IllegalArgumentException

    public int compare(PQ<Apartment> i, PQ<Apartment> j) {
        int m = i.getM();

        if(m == 0) {
          return i.getRoot().price - j.getRoot().price;
        }
        else if(m == 1) {
          return j.getRoot().footage - i.getRoot().footage;
        }
        else
            throw new IllegalArgumentException("invalid m value");
    }
}
