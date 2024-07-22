package bfst22.vector;

import java.util.*;


//Initial code from Sedgewick and Wayne
public class Dijkstra {

    private Edge[] edgeTo;
    private IndexMinPQ<Double> pq;
    private double[] dist;

    public Dijkstra(EdgeWeightedDigraph G, int s, int t) {
        // set array of distances to hold infinity, and visited vertex boolean to false
        Boolean visited[] = new Boolean[G.V()];
        dist = new double[G.V()];
        edgeTo = new Edge[G.V()];
        for (int i = 0; i < G.V(); i++) {
            dist[i] = Double.POSITIVE_INFINITY;
            visited[i] = false;
        }
        // Distance to source is always 0
        dist[s] = 0.0;

        pq = new IndexMinPQ<Double>(G.V());
        pq.insert(s, dist[s]);

        while (!pq.isEmpty()) {
            int v = pq.delMin();
            for (Edge e : G.adj(v)) {
                relax(e, t);
            }
        }

    }

    double h(Edge e, Edge t) {
        double R = 6371 * 1000;
        double lat1 = e.getFromC()[0] * Math.PI / 180;
        double lat2 = t.getFromC()[0] * Math.PI / 180;
        double deltaLat = (lat2 - lat1) * Math.PI / 180;
        double lon1 = e.getFromC()[1];
        double lon2 = t.getFromC()[1];
        double deltaLon = (lon2 - lon1) * Math.PI / 180;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1) *
                Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = R * c;

        return d;
    }

    private void relax(Edge e, int t) {
        int v = e.getFrom2(), w = e.getTo2();
        if (dist[w] > dist[v] + e.getWeight()) {
            dist[w] = dist[v] + e.getWeight();
            edgeTo[w] = e;
            double priority = dist[w] + h(e, e);
            if (pq.contains(w))
                pq.decreaseKey(w, priority);
            else {
                pq.insert(w, priority);
            }
        }
    }

    public double dist(int v) {
        return dist[v];
    }

    public boolean hasPath(int v) {
        return dist[v] < Double.POSITIVE_INFINITY;
    }

    public Iterable<Edge> pathTo(int v) {
        if (!hasPath(v))
            return null;
        Stack<Edge> path = new Stack<Edge>();
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFrom2()]) {
            path.push(e);
        }
        return path;
    }

    public PolyLine drawablePath(int v) {
        if (!hasPath(v)) {
            return null;
        }
        ArrayList<OSMNode> nodes = new ArrayList<>();
        for (Edge e = edgeTo[v]; e != null; e = edgeTo[e.getFrom2()]) {
            OSMNode o = new OSMNode(e.getFrom(), e.getFrom2(), e.getFromC()[0], e.getFromC()[1]);
            nodes.add(o);
        }
        PolyLine path = new PolyLine(nodes, WayType.DIJKSTRA);

        return path;

    }

}
