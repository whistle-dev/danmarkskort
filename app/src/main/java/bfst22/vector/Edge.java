package bfst22.vector;

public class Edge {
    private final long from, to;
    private final int from2, to2;
    private final double weight, distance;
    private final float[] fromC, toC;

    public Edge(long from, long to, int from2, int to2, double weight, double distance) {
        this.from = from;
        this.to = to;
        this.from2 = from2;
        this.to2 = to2;
        this.fromC = new float[2];
        this.toC = new float[2];
        this.weight = weight;
        this.distance = distance;

    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public double getDistance() {
        return distance;
    }

    public int getTo2() {
        return to2;
    }

    public int getFrom2() {
        return from2;
    }

    public float[] getFromC() {
        return fromC;
    }

    public float[] getToC() {
        return toC;
    }

    public void addFromC(float lat, float lon) {
        this.fromC[0] = lat;
        this.fromC[1] = lon;
    }

    public void addToC(float lat, float lon) {
        this.toC[0] = lat;
        this.toC[1] = lon;
    }

}
