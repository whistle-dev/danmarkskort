package bfst22.vector;

import javafx.geometry.Point2D;

//Range class is for defining the users screen as a range in the program
public class Range {
  private Point2D topLeft, bottomRight;
  private boolean debug;

  public Range(Point2D topLeft, Point2D bottomRight) {
    this.topLeft = topLeft;
    this.bottomRight = bottomRight;
    this.debug = false;
  }

  public void update(Point2D tL, Point2D bR) {
    topLeft = tL;
    bottomRight = bR;
  }

  public void updateDebug(boolean b) {
    debug = b;
  }

  public double getWidth() {
    return Math.abs(topLeft.getX() - bottomRight.getX());
  }

  public double getHeight() {
    return Math.abs(topLeft.getY() - bottomRight.getY());
  }

  public double getLeft() {
    if (debug)
      return topLeft.getX() + (getWidth() / 4);
    return topLeft.getX();
  }

  public double getTop() {
    if (debug)
      return topLeft.getY() + (getHeight() / 4);
    return topLeft.getY();
  }

  public double getRight() {
    if (debug)
      return bottomRight.getX() - (getWidth() / 4);
    return bottomRight.getX();
  }

  public double getBottom() {
    if (debug)
      return bottomRight.getY() - (getHeight() / 4);
    return bottomRight.getY();
  }

  public boolean getDebug() {
    return debug;
  }
}
