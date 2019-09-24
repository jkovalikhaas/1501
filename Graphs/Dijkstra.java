import java.util.LinkedList;

/**
 * uses Dijkstra's algorithm to find shortest path between 2 vertices
 */
public class Dijkstra {
    private double[] distTo;    // distance of current path
    private Edge[] edgeTo;      // last edge in shortest path

    private PQ<Double> pq;

    private double arr[];

    private int V;              // vertices

    /**
     *
     * @param G graph
     * @param v starting vertex
     */
    public Dijkstra(Edge[] G, int v) {
        V = G.length;

        distTo = new double[V];
        edgeTo = new Edge[V];

        for(int i = 0 ; i < V; i++)
            distTo[i] = Double.POSITIVE_INFINITY;
        distTo[v] = 0.0;

        pq = new PQ<>(V);


        arr = new double[V];

        for(int i = 0; i < V; i++)
            arr[i] = -1;

        pq.insert(distTo[v]);
        arr[v] = distTo[v];

        while(!pq.isEmpty()) {
            int i = getMin();
            if(i < 0) break;
            Edge current = G[i];
            while(current != null) {
                relax(current);
                current = current.next;
            }
        }
    }

    private void relax(Edge e) {
        int v = e.start; int w = e.end;
        if (distTo[w] > distTo[v] + e.weight) {
            distTo[w] = distTo[v] + e.weight;
            edgeTo[w] = e;
            if (pq.contains(w)) {
                pq.decreaseKey(w, distTo[w]);
                arr[w] = distTo[w];
            }
            else {
                pq.insert(distTo[w]);
                arr[w] = distTo[w];
            }
        }
    }

    public int getMin() {
        double d = pq.deleteRoot();
        for(int i = 0; i < V; i++)
            if(arr[i] == d) return i;
        return -1;
    }

    public Edge[] getEdgeTo() {
        return edgeTo;
    }
}
