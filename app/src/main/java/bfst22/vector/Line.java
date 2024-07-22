package bfst22.vector;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public class Line extends Drawable {
    private Point2D from, to;

    Line(String line) {
        String[] parts = line.split(" ");
        float x1 = Float.parseFloat(parts[1]);
        float y1 = Float.parseFloat(parts[2]);
        float x2 = Float.parseFloat(parts[3]);
        float y2 = Float.parseFloat(parts[4]);
        from = new Point2D(x1, y1);
        to = new Point2D(x2, y2);
    }

    public Line(Point2D from, Point2D to) {
        this.from = from;
        this.to = to;
    }

    public void trace(GraphicsContext gc) {
        gc.beginPath();
        gc.moveTo(from.getX(), from.getY());
        gc.lineTo(to.getX(), to.getY());
        gc.stroke();
    }

}
