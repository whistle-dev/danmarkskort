package bfst22.vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.controlsfx.control.Notifications;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapCanvas extends Canvas {
    private Model model;
    private Affine trans = new Affine();

    private int maxZoom = 10;
    private int minZoom = 0;
    private int zoomedIn = 0;

    private Range range = new Range(new Point2D(0, 0), new Point2D(0, 0));
    private Range buffer = new Range(new Point2D(0, 0), new Point2D(0, 0));

    private Point2D mousePos = new Point2D(0, 0);

    private int origin, dest, oldOrigin;

    private boolean streetDebug = false;
    private boolean darkTheme = false;

    private List<Pin> pointsOfInterest = new ArrayList<>();
    private Pin originPin, destPin, currentPin;

    private PolyLine drawable;
    private Dijkstra path;

    public void init(Model model) {
        this.model = model;
        pan(-model.getMinlon(), -model.getMinlat());
        zoom(640 / (model.getMaxlon() - model.getMinlon()), 0, 0);
        moveRange();
        model.addObserver(this::repaint);
        repaint();
    }

    public void repaint() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setTransform(new Affine());

        drawBackground(gc);

        gc.setTransform(trans);

        drawConstants(gc);

        drawDrawables(gc);

        drawRoads(gc);

        drawCurrentRoute(gc);

        drawNearestRoad(gc);

        drawPins(gc);

        drawRanges(gc);
    }

    private void drawBackground(GraphicsContext gc) {
        gc.setFill(calcColor(WayType.LAKE));
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawConstants(GraphicsContext gc) {
        for (Drawable d : model.iterable(WayType.LAND)) {
            gc.setLineWidth(calcWidth(d.getType().getWidth()));
            if (d.getType().fillTrue()) {
                gc.setFill(calcColor(d.getType()));
                d.fill(gc);
            }
        }
        for (Drawable d : model.iterable(WayType.CITY)) {
            gc.setLineWidth(calcWidth(d.getType().getWidth()));
            if (d.getType().fillTrue()) {
                gc.setFill(calcColor(d.getType()));
                d.fill(gc);
            }
        }
    }

    private void drawDrawables(GraphicsContext gc) {
        List<Drawable> queryResult = query();
        Collections.sort(queryResult);

        for (Drawable d : queryResult) {
            if (d.getType() != WayType.LAND && d.getType() != WayType.UNKNOWN && d.getType() != WayType.CITY) {
                if (d.getType().getRequiredZoom() <= zoomedIn) {
                    gc.setLineWidth(calcWidth(d.getType().getWidth()));
                    if (d.getType().fillTrue()) {
                        gc.setFill(calcColor(d.getType()));
                        d.fill(gc);
                    } else {
                        gc.setStroke(calcColor(d.getType()));
                        d.draw(gc);
                    }
                }
            }
        }
    }

    private void drawRoads(GraphicsContext gc) {
        for (Drawable d : model.getRoadTree().query(model.getRoadTree().getRoot(), buffer, 0)) {
            if (d.getType().getRequiredZoom() <= zoomedIn) {
                gc.setLineWidth(calcWidth(d.getType().getWidth()));
                if (d.getType().fillTrue()) {
                    gc.setFill(calcColor(d.getType()));
                    d.fill(gc);
                } else {
                    gc.setStroke(calcColor(d.getType()));
                    d.draw(gc);
                }
            }
        }
    }

    private void drawCurrentRoute(GraphicsContext gc) {
        if (drawable != null) {
            gc.setLineWidth(calcWidth(drawable.getType().getWidth()));
            gc.setStroke(calcColor(drawable.getType()));
            drawable.draw(gc);
        }
    }

    public void clearRoute() {
        drawable = null;
    }

    private void drawNearestRoad(GraphicsContext gc) {
        PolyLine n = (PolyLine) model.getRoadTree().getNearestNeighbor(mousePos);
        if (streetDebug) {
            gc.setLineWidth(calcWidth(n.getType().getWidth()));
            gc.setStroke(Color.RED);
            n.draw(gc);
        }
    }

    private void drawPins(GraphicsContext gc) {
        double calcSize = screenToModel(new Point2D(20, 0)).getX() - screenToModel(new Point2D(0, 0)).getX();

        if (pointsOfInterest.size() > 0) {
            for (Pin p : pointsOfInterest) {
                gc.setFill(p.getColor());
                gc.fillOval(p.getX() - calcSize / 2, p.getY() - calcSize / 2, calcSize, calcSize);
            }
        }

        if (originPin != null) {
            gc.setFill(originPin.getColor());
            gc.fillOval(originPin.getX() - calcSize / 2, originPin.getY() - calcSize / 2, calcSize, calcSize);
        }
        if (destPin != null) {
            gc.setFill(destPin.getColor());
            gc.fillOval(destPin.getX() - calcSize / 2, destPin.getY() - calcSize / 2, calcSize, calcSize);
        }
        if (currentPin != null) {
            gc.setFill(currentPin.getColor());
            gc.fillOval(currentPin.getX() - calcSize / 2, currentPin.getY() - calcSize / 2, calcSize, calcSize);
        }
    }

    private void drawRanges(GraphicsContext gc) {
        if (range.getDebug()) {
            gc.setLineWidth(5 / Math.sqrt(trans.determinant()));
            drawRange(range, Color.BLACK);
            drawRange(buffer, Color.RED);
        }
    }

    private Color calcColor(WayType w) {
        if (darkTheme) {
            return w.getSecondColor();
        } else {
            return w.getMainColor();
        }
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    private double calcWidth(float f) {
        return f / Math.sqrt(trans.determinant());
    }

    public void setStreetDebug(boolean streetDebug) {
        this.streetDebug = streetDebug;
    }

    private ArrayList<Drawable> query() {
        return model.getKdTree().query(model.getKdTree().getRoot(), buffer, 0);
    }

    private void moveRange() {
        GraphicsContext gc = getGraphicsContext2D();
        int bufferSize = 3;

        Point2D topLeft = new Point2D(0, 0);
        Point2D bottomRight = new Point2D(gc.getCanvas().getWidth() - 218, gc.getCanvas().getHeight());
        range.update(screenToModel(topLeft), screenToModel(bottomRight));

        Point2D topLeftBuffer = new Point2D(0 - ((bottomRight.getX() - topLeft.getX()) / bufferSize),
                0 - ((bottomRight.getY() - topLeft.getX()) / bufferSize));
        Point2D bottomRightBuffer = new Point2D(
                gc.getCanvas().getWidth() - 218 + ((bottomRight.getX() - topLeft.getX()) / bufferSize),
                gc.getCanvas().getHeight() + ((bottomRight.getY() - topLeft.getX()) / bufferSize));
        buffer.update(screenToModel(topLeftBuffer), screenToModel(bottomRightBuffer));
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        moveRange();
        repaint();
    }

    public void zoom(double factor, double x, double y) {
        trans.prependTranslation(-x, -y);
        trans.prependScale(factor, factor);
        trans.prependTranslation(x, y);
        moveRange();
        repaint();
    }

    public Point2D screenToModel(Point2D point) {
        try {
            return trans.inverseTransform(point);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawRange(Range r, Color c) {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(c);
        gc.beginPath();
        gc.moveTo(r.getLeft(), r.getTop());
        gc.lineTo(r.getRight(), r.getTop());
        gc.lineTo(r.getRight(), r.getBottom());
        gc.lineTo(r.getLeft(), r.getBottom());
        gc.lineTo(r.getLeft(), r.getTop());
        gc.stroke();
    }

    public void setRangeDebug(boolean debug) {
        range.updateDebug(debug);
        buffer.updateDebug(debug);
    }

    public void updateMousePos(Point2D m) {
        mousePos = screenToModel(m);
    }

    public void drawRoute(int v, int w, EdgeWeightedDigraph G) {
        if (v == oldOrigin) {
            drawable = path.drawablePath(w);
        } else {
            path = new Dijkstra(G, v, w);
            drawable = path.drawablePath(w);
        }
    }

    public void drawEdge(Edge e, GraphicsContext gc) {
        Point2D from = new Point2D(e.getFromC()[0], e.getFromC()[1]);
        Point2D to = new Point2D(e.getToC()[0], e.getToC()[1]);
        Line l = new Line(from, to);
        l.draw(gc);

    }

    public void setOrigin(Point2D pos, int id2) {
        oldOrigin = origin;
        origin = id2;
        originPin = new Pin(pos, Color.RED);
    }

    public void setDest(Point2D pos, int id2) {
        dest = id2;
        destPin = new Pin(pos, Color.RED);
    }

    public void setRoute(Point2D origin, Point2D dest) {
        this.origin = ((PolyLine) model.getRoadTree().getNearestNeighbor(origin)).getFrom().getID2();
        this.dest = ((PolyLine) model.getRoadTree().getNearestNeighbor(dest)).getFrom().getID2();
        this.originPin = new Pin(origin, Color.RED);
        this.destPin = new Pin(dest, Color.PINK);
        repaint();
    }

    public void setZoom(double factor) {
        if (factor > 0) {
            factor = 1;
        } else if (factor < 0) {
            factor = -1;
        }
        zoomedIn += factor;
    }

    public double getMaxZoom() {
        return maxZoom;
    }

    public double getMinZoom() {
        return minZoom;
    }

    public int getZoomedIn() {
        return zoomedIn;
    }

    public Point2D getMousePos() {
        return mousePos;
    }

    public int getOrigin() {
        return origin;
    }

    public int getDest() {
        return dest;
    }

    public String getClosestStreet(Point2D target) {
        PolyLine n = (PolyLine) model.getRoadTree().getNearestNeighbor(target);
        return n.getName();
    }

    public void checkPointOfInterest() {
        double calcSize = screenToModel(new Point2D(20, 0)).getX() - screenToModel(new Point2D(0, 0)).getX();
        System.out.println("checked");
        boolean found = false;

        if (pointsOfInterest.size() > 0) {
            for (int i = 0; i < pointsOfInterest.size(); i++) {
                Pin p = pointsOfInterest.get(i);
                if (Math.hypot(p.getX() - mousePos.getX(), p.getY() - mousePos.getY()) < calcSize / 2) {
                    found = true;
                    deletePointOfInterest(p);
                }
            }
        }
        if (!found) {
            addPointOfInterest();
        }
        repaint();
    }

    private void deletePointOfInterest(Pin p) {
        pointsOfInterest.remove(p);
        Notifications.create().title("Success").text("Removed a Point of Interest at: " + getClosestStreet(new Point2D(p.getX(), p.getY()))).showInformation();
    }

    private void addPointOfInterest() {
        Pin p = new Pin(mousePos, Color.BLUE);
        pointsOfInterest.add(p);
        Notifications.create().title("Success").text("Added a new Point of Interest at: " + getClosestStreet(new Point2D(p.getX(), p.getY()))).showInformation();
    }
}