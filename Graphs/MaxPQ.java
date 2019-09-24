/**
 * A generic Priority Queue
 */
public class MaxPQ<Key extends Comparable<Key>> {

    private int size;       // current number of Keys
    private int maxSize;    // max number of Keys

    private Key[] keys;     // PQ as an array

    /**
     * default MinPQ with size 8
     */
    public MaxPQ() { this(8); }

    /**
     * instantiates PQ
     */
    @SuppressWarnings("unchecked")  // get rid of "unchecked" error for array
    public MaxPQ(int maxSize) {
        this.maxSize = maxSize;
        size = 0;
        keys = (Key[]) new Comparable[maxSize];
    }

    /** inserts a key into the PQ
     * @param key   key to insert
     */
    public void insert(Key key) {
        // resize PQ in case of overflow
        if (size >= maxSize) {
            keys = resize();
        }
        keys[size] = key;  // adds key to PQ
        swim(size);        // swim to maintain PQ order
        size++;            // increase size
    }

    /**
     * deletes root of PQ
     * @return root
     */
    public Key deleteRoot() {
        Key key = getRoot();
        delete(0);

        return key;
    }

    /**
     * deletes a key for the PQ
     * @param i   reference to key to delete
     */
    public void delete(int i) {
        exchange(i, --size);  // exchanges specified key with last key
        swim(i);              // swim to maintain PQ order
        sink(i);              // sink to maintain PQ order
        keys[size] = null;    // removes last key
    }

    /**
     * changes a key and then updates PQ
     * @param i     index in pq
     * @param key   key to decrease
     */
    public void decreaseKey(int i, Key key) {
        keys[i] = key;
        swim(i);
    }

    /**
     * @return top of PQ
     */
    public Key getRoot() {
        if(isEmpty()) throw new IllegalArgumentException();
        return keys[0];
    }

    /**
     * @return if PQ is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return size of PQ
     */
    public int size() {
        return size;
    }

    /**
     * compares two keys in the PQ
     * @param   i  the first key to compare
     * @param   j  the second key to compare
     * @return  value which the Keys are compared
     */
    public int compare(int i, int j) {
        return keys[j].compareTo(keys[i]);
    }

    /**
     * exchanges two keys in the PQ
     * @param i     first key to exchange
     * @param j     second key to exchange
     */
    public void exchange(int i, int j) {
        Key temp = keys[i];
        keys[i] = keys[j];
        keys[j] = temp;
    }

    /**
     * exchanges up the PQ to maintain its order
     * @param n   position to begin swimming
     */
    public void swim(int n) {
        while(n > 0 && compare((n-1)/2, n) > 0) {
            exchange(n, (n-1)/2);
            n = (n-1)/2;
        }
    }

    /**
     * exchanges down the PQ to maintain its order
     * @param i   position to begin sinking
     */
    public void sink(int i) {
        while((i * 2) + 1 <= size) {
            int j = 2 * i + 1;
            if(i < size && compare(i, j) <= 0) i++;
            if(compare(i, j) != 0) break;
            exchange(i, j);
            i = j;
        }
    }

    /**
     * checks if a key exists in the PQ
     * @return true if key is found
     */
    public boolean contains(int n) {
        for(int i = 0; i < size; i++)
            if(keys[n] != null) return true;
        return false;
    }

    /**
     * @param i reference of Key in PQ
     * @return  key based on position
     */
    public Key getKey(int i) {
        return keys[i];
    }

    /**
     * resize heap if array would exceed maxSize
     * @return  copied array with double maxSize
     */
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
