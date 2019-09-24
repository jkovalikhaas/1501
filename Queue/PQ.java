/*
 * A generic Priority Queue
 */
public class PQ<Key extends Comparable<Key>> {

    private int size;       // current number of Keys
    private int maxSize;    // max number of Keys

    private Key[] keys;     // PQ as an array

    private int m;          // min = 0; max = 1;

    // default MaxPQ with size 8
    // @param m must be 1 or 0
    public PQ(int m) { this(8, m); }

    // instantiates PQ
    @SuppressWarnings("unchecked")  // get rid of "unchecked" error for array
    public PQ(int maxSize, int m) {
        this.m = m;
        this.maxSize = maxSize;
        size = 0;
        keys = (Key[]) new Comparable[maxSize];
    }
    // inserts a key into the PQ
    // @param key   key to insert
    public void insert(Key key) {
        // resizes PQ incase of overflow
        if (size >= maxSize) {
            keys = resize();
        }
        keys[size] = key;  // adds key to PQ
        swim(size);        // swim to maintain PQ order
        size++;            // increase size
    }
    // deletes a key for the PQ
    // @param i   reference to key to delete
    public void delete(int i) {
        exchange(i, --size);  // exchanges specified key with last key
        swim(i);              // swim to maintain PQ order
        sink(i);              // sink to maintain PQ order
        keys[size] = null;    // removes last key
    }
    // returns top of PQ
    public Key getRoot() {
        if(isEmpty()) throw new IllegalArgumentException();
        return keys[0];
    }
    // checks if PQ is empty
    public boolean isEmpty() {
        return size == 0;
    }
    // returns size of PQ
    public int size() {
        return size;
    }
    // compares two keys in the PQ
    // @param i, j  the two keys to compare
    public int compare(int i, int j) {
        return keys[j].compareTo(keys[i]);
    }
    // exchanges the place of two keys
    // @param i, j  the two keys to exchange
    public void exchange(int i, int j) {
        Key temp = keys[i];
        keys[i] = keys[j];
        keys[j] = temp;
    }
    // exchanges up the PQ to maintain its order
    // @param n   position to begin swimming
    public void swim(int n) {
        // makes sure comparing either min or max baesed on m
        while(n > 0 && compare((n-1)/2, n) == m) {
            exchange(n, (n-1)/2);
            n = (n-1)/2;
        }
    }
    // exchanges down the PQ to maintain its order
    // @param i   position to begin sinking
    public void sink(int i) {
        while((i * 2) + 1 <= size) {
            int j = 2 * i + 1;
            if(i < size && compare(i, j) == m) i++;
            if(compare(i, j) != m) break;
            exchange(i, j);
            i = j;
        }
    }
    // returns key based on position
    public Key getKey(int i) {
        return keys[i];
    }
    //returns m
    public int getM() {
      return m;
    }
    // resize heap if array would exceed maxSize
    @SuppressWarnings("unchecked")  // get rid of "unchecked" error for array
    public Key[] resize() {
        maxSize *= 2;
        Key[] temp = (Key[]) new Comparable[maxSize];
        for(int i = 0; i < maxSize / 2; i++) {
            temp[i] = keys[i];
        }
        return temp;
    }
}
