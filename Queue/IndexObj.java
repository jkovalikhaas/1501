/*
 * An object to store in HashMap to return necessary values
 * stores an Apartment and the city it belongs to
 */
public class IndexObj {

    private String city;
    private Apartment apt;
    // creates an IndexObj based on an apt
    public IndexObj(Apartment apt) {
        this.city = apt.city;
        this.apt = apt;
    }
    // returns Apartment
    public Apartment getApt() {
        return apt;
    }
}
