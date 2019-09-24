
import java.util.*;
import java.io.*;

public class NetworkAnalysis {

    private static Edge[] adjacencyList; // represents graph as adjacency list
    private static int V = 0;   // number of vertices
    private static int E = 0;   // number of edges

    public static void main(String[] args) {
        File file = new File(args[0]);
        Scanner scanner;
        boolean done = false;       // lets program know when to exit
        // reads graph values into adjacency list
        try {
            scanner = new Scanner(file);
            V = scanner.nextInt();          // gets number of vertices
            scanner.nextLine();             // goes to next line
            adjacencyList = new Edge[V];

            System.out.println("Loading Graph...");
            while(scanner.hasNext()) {
                String s = scanner.nextLine();
                E++;
                Edge e = createEdge(s);
                addEdge(e);
                addEdge(flipV(e));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        do {
            int option = askQuestions();

            switch (option) {
                // lowest latency path
                case 1:
                    lowestPath(true);
                    break;
                // copper only
                case 2:
                    copperOnly();
                    break;
                // max data
                case 3:
                    lowestPath(false);
                    break;
                // lowest avg spanning tree
                case 4:
                    lowestAvgTree();
                    break;
                // test failed vertices
                case 5:
                    failedV();
                    break;
                // quit for all inputs other than 1-5
                default:
                    done = true;
            }
        } while(!done);
    }

    /**
     * test lowest latency path or max bandwidth
     * @param m  if true, looks for lowest latency between vertices
                 if false, looks for max bandwidth between vertices
     */
    public static void lowestPath(boolean m) {
        Scanner kb = new Scanner(System.in);

        int v, w;   // holds vertices entered by user
        // if input is not an integer, returns user to menu
        try {
            System.out.print("First Vertex: ");
            v = kb.nextInt();
            System.out.print("Second Vertex: ");
            w = kb.nextInt();
        } catch(InputMismatchException e) { return; }
        // makes sure vertices are valid, if not returns user to menu
        if(v < 0 || v > V || w < 0 || v > V || v == w) return;

        if(m) {
            System.out.println("\nShortest Path between " + v + " and " + w + ":");
            Dijkstra dijkstra = new Dijkstra(adjacencyList, v);
            distTo(w, dijkstra.getEdgeTo());
        }
        else{
            System.out.println("\nMax Bandwidth between " + v + " and " + w + ":");
            MaxFlow maxFlow = new MaxFlow(adjacencyList, v, w);
        }
    }
    // gets distance between to the second vertex from edge array
    public static void distTo(int i, Edge[] edgeTo) {
        LinkedList<Edge> list = new LinkedList<>();
        int band = 0;   // total bandwidth
        Edge current = edgeTo[i];
        // adds up total bandwidth
        while(current != null) {
            list.add(current);
            band += current.bandwidth;
            current = edgeTo[current.start];
        }
        // prints out edges starting from first vertex
        int s = list.size() - 1;
        while(s >= 0) {
            current = list.get(s);
            System.out.println("(" + current.start + ", " + current.end + ")");
            s--;
        }

        System.out.println("Total Bandwidth: " + band);
    }

    /**
     * checks if graph can be connected copper only
     */
    public static void copperOnly() {
        LazyPrims lazyPrims = new LazyPrims(adjacencyList);
        lazyPrims.getLatency(true);
        if(lazyPrims.checkFull())
            System.out.println("\nThe Graph is Copper Only Connected.");
        else
            System.out.println("\nThe Graph is NOT Copper Only Connected.");
    }

    /**
     *  get shortest latency path of graph
     */
    public static void lowestAvgTree() {
        LazyPrims lazyPrims = new LazyPrims(adjacencyList);

        System.out.println("\nLowest Average Latency Tree: ");
        lazyPrims.getLatency(false);
        lazyPrims.printQueue();
    }

    /**
     * check if graph will remain connected if any two vertices fail
     */
    public static void failedV() {
        LazyPrims lazyPrims = new LazyPrims(adjacencyList);

        for(int i = 0; i < V; i++) {
            for(int j = i + 1; j < V; j++) {
                if(!lazyPrims.checkFail(i, j)) {
                    System.out.println("\nThe Graph is NOT Connected.");
                    return;
                }
            }
        }
        System.out.println("\nThe Graph is Connected.");
    }

    /**
     * prompts questions to user
     * @return  option selected by user
     */
    public static int askQuestions() {
        Scanner kb = new Scanner(System.in);

        System.out.println("\nMenu:");
        System.out.println("1) Find Lowest Latency Path: ");
        System.out.println("2) Copper Only: ");
        System.out.println("3) Maximum Data: ");
        System.out.println("4) Lowest Average Latency Spanning Tree: ");
        System.out.println("5) Test For Failed Vertices: ");
        System.out.println("6) Quit: (or any other input)");
        System.out.print("   Option: ");

        int o = -1;
        try {
            o = kb.nextInt();
        } catch(InputMismatchException e) {}

        return o;
    }

    /**
     * creates edge from info on line
     * @param line  from file to create Edge from
     * @return      created edge
     */
    public static Edge createEdge(String line) {
        Scanner s = new Scanner(line);
        Edge e = new Edge();

        e.start = s.nextInt();
        e.end = s.nextInt();
        e.type = s.next();
        e.bandwidth = s.nextInt();
        e.length = s.nextInt();
        e.setWeight();

        return e;
    }

    /**
     * adds edge to adjacency list
     * @param e edge to add to adjacency list
     */
    public static void addEdge(Edge e) {
        Edge current;
        int n = e.start;

        for(int i = 0; i < V; i++) {
            if(i == n) {
                if(adjacencyList[i] == null)
                    adjacencyList[i] = e;
                else {
                    current = adjacencyList[i];
                    while(current.next != null)
                        current = current.next;
                    current.next = e;
                }
            }
        }
    }

    /**
     * flips the start and end vertices of an Edge
     * @param e edge to flip vertices
     * @return  returns edge with swapped vertices
     */
    public static Edge flipV(Edge e) {
        Edge f = new Edge();

        f.start = e.end;
        f.end = e.start;
        f.type = e.type;
        f.bandwidth = e.bandwidth;
        f.length = e.length;
        f.setWeight();

        return f;
    }
}
