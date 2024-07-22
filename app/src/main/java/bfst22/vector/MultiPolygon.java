package bfst22.vector;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class MultiPolygon extends Drawable {
    private List<Drawable> parts = new ArrayList<>();
    private WayType type;
    public MultiPolygon left, right;

    public MultiPolygon(List<OSMWay> rel, WayType type) {
        for (OSMWay way : rel) {
            parts.add(new PolyLine(way.getNodes(), type));
        }
        this.type = type;
    }

    @Override
    public void trace(GraphicsContext gc) {
        OSMNode prevLastNode = null;

        
        for (Drawable d : parts) {
            if (prevLastNode != null && ((PolyLine) d).getFrom() == prevLastNode) {
                ((PolyLine) d).relationTrace(gc);
            } else {
                d.trace(gc);
            }
            prevLastNode = ((PolyLine) d).getTo();
        }
    }

    @Override
    public float getAvgX() {
        float tempX = 0;

        for (int i = 0; i < parts.size(); i++) {
            PolyLine l = (PolyLine) parts.get(i);
            tempX += l.getAvgX();
        }

        float avgX = tempX / parts.size();

        return avgX;
    }

    @Override
    public float getAvgY() {
        float tempY = 0;

        for (int i = 0; i < parts.size(); i++) {
            PolyLine l = (PolyLine) parts.get(i);
            tempY += l.getAvgY();
        }

        float avgY = tempY / parts.size();

        return avgY;
    }

    @Override
    public float getMinX() {
        float minX = 0;

        for (int i = 0; i < parts.size(); i++) {
            PolyLine l = (PolyLine) parts.get(i);
            if (l.getMinX() < minX)
                minX = l.getMinX();
        }

        return minX;
    }

    @Override
    public float getMinY() {
        float minY = 0;

        for (int i = 0; i < parts.size(); i++) {
            PolyLine l = (PolyLine) parts.get(i);
            if (l.getMinY() < minY)
                minY = l.getMinY();
        }

        return minY;
    }

    @Override
    public float getMaxX() {
        float maxX = 0;

        for (int i = 0; i < parts.size(); i++) {
            PolyLine l = (PolyLine) parts.get(i);
            if (l.getMaxX() < maxX)
                maxX = l.getMaxX();
        }
        return maxX;
    }

    @Override
    public float getMaxY() {
        float maxY = 0;

        for (int i = 0; i < parts.size(); i++) {
            PolyLine l = (PolyLine) parts.get(i);
            if (l.getMaxY() < maxY)
                maxY = l.getMaxY();
        }
        return maxY;
    }

    @Override
    public WayType getType() {
        return type;
    }

}
