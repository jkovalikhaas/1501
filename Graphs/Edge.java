/**
 * represents edge as a node with all of its information
 */
class Edge implements Comparable<Edge> {

    int start;      // first edge endpoint
    int end;        // last edge endpoint
    String type;
    int bandwidth;
    int length;
    double weight;  // weight of edge

    private final double copperSpeed = 230000000;
    private final double opticalSpeed = 200000000;

    Edge next;

    Edge() {
        this(-1, -1, null, -1, -1);
    }

    private Edge(int start, int end, String type, int bandwidth, int length) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.bandwidth = bandwidth;
        this.length = length;
        this.next = null;
    }

    public void setWeight() {
        if(type.charAt(0) == 'c')
            weight = (double) this.length / copperSpeed;
        else
            weight = (double) this.length / opticalSpeed;
    }

    public int compareTo(Edge o) {
        if(this.weight - o.weight < 0)
            return 0;
        else
            return 1;
    }
}