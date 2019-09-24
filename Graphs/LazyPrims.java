import java.util.*;
/**
 * uses lazy prim's to find lowest latency path
 */
public class LazyPrims {
    private PQ<Edge> pq;   // priority queue to find shortest path
    private LinkedList<Edge> lowestPath;   // edges with lowest path
    private boolean[] marked;              // marks visited vertices

    private Edge[] G;
    private int V;

    /**
     *  @param G    graph represented as adjacency list
     */
    public LazyPrims(Edge[] G) {
        pq = new PQ<>();
        lowestPath = new LinkedList<>();
        marked = new boolean[G.length];
        this.G = G;
        this.V = G.length;
    }

    /**
     * checks lowest latency path
     * @param copper if true, only adds copper Edges to queue
     */
    public void getLatency(boolean copper) {
        loadEdges(0);

        while(!pq.isEmpty()) {
            Edge e = pq.deleteRoot();
            // if copper is true, only add copper Edges to queue
            if(copper && e.type.charAt(0) != 'c') continue;
            int i = e.end;
            if(marked[i]) continue;
            lowestPath.add(e);
            if(!marked[i]) loadEdges(i);
        }
    }

    /**
     * checks if a graph is still connected if a pair of vertices fail
     * @param v first vertex
     * @param w second vertex
     * @return true if the graph is connected
     */
    public boolean checkFail(int v, int w) {
        int n = 0;  // first vertex to check
        // makes sure vertex does not equal v or w
        while(v == n || w == n)
            n++;
        loadEdges(n);

        while(!pq.isEmpty()) {
            Edge e = pq.deleteRoot();
            int i = e.end;
            if(i == v || i == w) continue;
            if(marked[i]) continue;
            lowestPath.add(e);
            if(!marked[i]) loadEdges(i);
        }

        for(int i = 0; i < V; i++)
            if(!marked[i] && i != v && i != w) return false;
        return true;
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

    /**
     * @return  true if all vertices have been checked
     */
    public boolean checkFull() {
        for(int i = 0; i < V; i++)
            if(!marked[i]) return false;
        return true;
    }
    /**
     * prints out edges in Queue
     */
    public void printQueue() {
        System.out.println("Edges:");
        for(int i = 0; i < lowestPath.size(); i++) {
            System.out.println("(" + lowestPath.get(i).start + ", " + lowestPath.get(i).end + ")");
        }
    }
}
