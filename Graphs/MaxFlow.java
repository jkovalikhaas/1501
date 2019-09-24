/**
 * gets max bandwidth between two vertices
 */
public class MaxFlow {

    private MaxPQ<Edge> pq;
    private boolean[] marked;  // keeps track of vertices seen
    private int totalBand = 0; // keeps track of total bandwidth

    private Edge[] G;

    public MaxFlow(Edge[] G, int v, int w) {
        pq = new MaxPQ<>();
        marked = new boolean[G.length];
        this.G = G;

        loadEdges(v);

        while(!pq.isEmpty()) {
            Edge e = pq.deleteRoot();
            int i = e.end;
            if(marked[i]) continue;
            totalBand += e.bandwidth;
            if(i == w) break;
            if(!marked[i]) loadEdges(i);
        }

        System.out.println("Total Bandwidth: " + totalBand);
    }

    /**
     * add connecting edges to PQ, if not marked
     * @param v vertex to add edges from
     */
    public void loadEdges(int v) {
        Edge current = G[v];
        marked[v] = true;

        while(current != null) {
            if(!marked[current.end]) pq.insert(current);
            current = current.next;
        }
    }
}
