package bfst22.vector;

import java.io.Serializable;

public class OSMNode implements Serializable {
    public static final long serialVersionUID = 9082413;
    private long id;
    private int id2;
    private float lat, lon;
    public OSMNode right, left;
    private String streetName;

    public OSMNode(long id, int id2, float lat, float lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.id2 = id2;
        this.right = null;
        this.left = null;
    }

    public OSMNode(long id, int id2, float lat, float lon, String streetName) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.id2 = id2;
        this.right = null;
        this.left = null;
        this.streetName = streetName;
    }

    public float getX() {
        return lat;
    }

    public float getY() {
        return lon;
    }

    public long getID() {
        return id;
    }

    public int getID2() {
        return id2;
    }

    public String getStreetName() {
        return streetName;
    }
}
