package bfst22.vector;

import javafx.geometry.Point2D;

public class Address {
    private String city, postcode, houseNumber, street;
    private final float x, y;
    private final long id;

    public Address(OSMNode node) {
        this.x = node.getX();
        this.y = node.getY();
        this.id = node.getID();
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public void setHousenumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean isFull() {
        if (city == null) {
            return false;
        }
        if (postcode == null) {
            return false;
        }
        if (houseNumber == null) {
            return false;
        }
        if (street == null) {
            return false;
        } else {
            return true;
        }
    }

    public String getStreet() {
        return street.toLowerCase().replace(" ", "");
    }

    public String toString() {
        return street.toLowerCase() + " " + houseNumber.toLowerCase() + " " + postcode + " " + city.toLowerCase();
    }

    public Point2D getCords() {
        Point2D cords = new Point2D(x, y);
        return cords;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCity() {
        return city;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public long getId() {
        return id;
    }
}
