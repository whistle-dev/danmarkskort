package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Pin {
    private float x, y;
    private Color color;

    public Pin(Point2D target, Color color) {
        this.x = (float) target.getX();
        this.y = (float) target.getY();
        this.color = color;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }
}
