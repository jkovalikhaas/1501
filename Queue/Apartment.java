/*
 * Apartment class to store information about an Apartment
 */
class Apartment implements Comparable<Apartment> {

	String address;
	int aptNum;
	String city;
	int zip;
	int price;
	int footage;

	int m;  // sets 'mode' of Apartment; min = 0, max = 1

	// sets default ("empty") Apartment with 'min' mode
	public Apartment() {
		this(null, -1, null, -1, -1, -1, 0);
	}

	public Apartment(String address, int aptNum, String city, int zip, int price, int footage, int m) {
		this.address = address;
		this.aptNum = aptNum;
		this.city = city;
		this.zip = zip;
		this.price = price;
		this.footage = footage;
		this.m = m;
	}
    /*
     * Compares two Apartments
     *
     * @param apt   Apartment to compare
     * @return      returns 0 if looking for price difference
     *              returns 1 if looking for footage difference
     *              otherwise returns -1
     */
	public int compareTo(Apartment apt) {
	    if(this.price < apt.price && m == 0)
	        return 0;
	    else if(this.footage > apt.footage && m == 1)
	        return 1;
	    else
	        return -1;
	}

}
