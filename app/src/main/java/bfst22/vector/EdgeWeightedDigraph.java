package bfst22.vector;

import java.util.ArrayList;
import java.util.HashMap;

//Initial code from Sedgewick and Wayne
public class EdgeWeightedDigraph {
    private final int V; // number of vertices in this digraph
    private int E; // number of edges in this digraph

    private HashMap<Integer, ArrayList<Edge>> adjacencyMap;
    private int[] indegree;

    public EdgeWeightedDigraph(int V) {
        adjacencyMap = new HashMap<Integer, ArrayList<Edge>>();
        if (V < 0)
            throw new IllegalArgumentException("Number of vertices in a Digraph must be non-negative");
        this.V = V;
        this.E = 0;
        this.indegree = new int[V];
        for (int v = 0; v < V; v++) {
            adjacencyMap.put(v, new ArrayList<Edge>());
        }
    }

    public int V() {
        return V;
    }

    public int E() {
        return E;
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    /**
     * Adds the directed edge {@code e} to this edge-weighted digraph.
     *
     * @param e the edge
     * @throws IllegalArgumentException unless endpoints of edge are between
     *                                  {@code 0}
     *                                  and {@code V-1}
     */
    public void addEdge(Edge e) {

        int v = e.getFrom2();
        int w = e.getTo2();
        validateVertex(v);
        validateVertex(w);
        adjacencyMap.get(v).add(e);
        adjacencyMap.get(w).add(e);

        indegree[w]++;
        E++;
    }

    /**
     * Returns the directed edges incident from vertex {@code v}.
     *
     * @param v the vertex
     * @return the directed edges incident from vertex {@code v} as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public ArrayList<Edge> adj(int v) {
        validateVertex(v);
        return adjacencyMap.get(v);
    }

    public int outdegree(int v) {
        validateVertex(v);
        return adjacencyMap.get(v).size();
    }

    public int indegree(int v) {
        validateVertex(v);
        return indegree[v];
    }

    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for (int v = 0; v < V; v++) {
            for (Edge e : adj(v)) {
                list.add(e);
            }
        }
        return list;
    }

    public HashMap<Integer, ArrayList<Edge>> getAdjacencyMap() {
        return adjacencyMap;
    }
}
