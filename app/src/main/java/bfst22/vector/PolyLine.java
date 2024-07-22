package bfst22.vector;

import java.util.List;

import javafx.scene.canvas.GraphicsContext;

public class PolyLine extends Drawable {
    public float[] coords;
    public PolyLine left, right;
    public WayType type;
    public int size = 0;
    public OSMNode from, to;
    public String name;

    public PolyLine(List<OSMNode> nodes, WayType type) {
        coords = new float[nodes.size() * 2];
        int i = 0;
        for (OSMNode node : nodes) {
            coords[i++] = node.getX();
            coords[i++] = node.getY();
        }
        this.type = type;
        this.size = coords.length / 2;
        this.from = nodes.get(0);
        this.to = nodes.get(nodes.size() - 1);
    }

    public PolyLine(List<OSMNode> nodes, WayType type, String name) {
        coords = new float[nodes.size() * 2];
        int i = 0;
        for (OSMNode node : nodes) {
            coords[i++] = node.getX();
            coords[i++] = node.getY();

        }
        this.type = type;
        this.size = coords.length / 2;
        this.from = nodes.get(0);
        this.to = nodes.get(nodes.size() - 1);
        this.name = name;
    }

    @Override
    public void trace(GraphicsContext gc) {
        gc.moveTo(coords[0], coords[1]);
        for (int i = 2; i < coords.length; i += 2) {
            gc.lineTo(coords[i], coords[i + 1]);
        }
    }

    //Idea of relationtrace given by group6
    public void relationTrace(GraphicsContext gc) {
        for (int i = 2; i < coords.length; i += 2) {
            gc.lineTo(coords[i], coords[i + 1]);
        }
    }

    @Override
    public float getAvgX() {
        float tempX = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 0) {
                tempX += coords[i];
            }
        }
        float avgX = tempX / (coords.length / 2);
        return avgX;
    }

    @Override
    public float getAvgY() {
        float tempY = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 1) {
                tempY += coords[i];
            }
        }
        float avgY = tempY / (coords.length / 2);
        return avgY;
    }

    @Override
    public float getMinX() {
        float minX = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 0) {
                if (coords[i] < minX)
                    minX = coords[i];
            }
        }
        return minX;
    }

    @Override
    public float getMinY() {
        float minY = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 1) {
                if (coords[i] < minY)
                    minY = coords[i];
            }
        }
        return minY;
    }

    @Override
    public float getMaxX() {
        float maxX = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 0) {
                if (coords[i] < maxX)
                    maxX = coords[i];
            }
        }
        return maxX;
    }

    @Override
    public float getMaxY() {
        float maxY = 0;

        for (int i = 0; i < coords.length; i++) {
            if (i % 2 == 0) {
                if (coords[i] < maxY)
                    maxY = coords[i];
            }
        }
        return maxY;
    }

    @Override
    public WayType getType() {
        return type;
    }

    public OSMNode getFrom() {
        return from;
    }

    public OSMNode getTo() {
        return to;
    }

    public String getName() {
        return name;
    }
}
