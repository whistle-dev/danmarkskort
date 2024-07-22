package bfst22.vector;

import javafx.scene.paint.Color;

public enum WayType {
    LAND(Color.rgb(187, 226, 198), Color.rgb(13, 13, 13), 0, true, 1f),

    FOREST(Color.rgb(0, 204, 102), Color.rgb(40, 68, 53), 1, true, 1f),

    LAKE(Color.rgb(51, 153, 255), Color.rgb(0, 0, 30), 0, true, 1f),

    CITY(Color.rgb(192, 192, 192), Color.rgb(33, 33, 33), 0, true, 1f),

    UNKNOWN(Color.rgb(255, 180, 180), Color.rgb(155, 80, 80), 0, false, 1f),

    BUILDING(Color.rgb(100, 100, 100), Color.rgb(100, 100, 100), 8, false, 1f),

    WATERWAY(Color.rgb(51, 153, 255), Color.rgb(0, 0, 30), 0, false, 1f),

    HIGHWAY(Color.rgb(233, 132, 31), Color.rgb(233, 132, 31), 1, false, 2f),

    SUBWAY(Color.rgb(255, 180, 50), Color.rgb(255, 180, 50), 6, false, 1f),

    PATH(Color.rgb(60, 60, 60), Color.rgb(120, 120, 120), 4, false, 1f),

    CITYWAY(Color.rgb(0, 0, 0), Color.rgb(120, 120, 120), 6, false, 1f),

    ROAD(Color.rgb(0, 0, 0), Color.rgb(200, 200, 200), 3, false, 1f),

    MOTORWAY(Color.rgb(255, 0, 0), Color.rgb(255, 0, 0), 0, false, 2f),

    COASTLINE(Color.rgb(0, 0, 0), Color.rgb(50, 50, 50), 0, false, 2f),

    STONE(Color.rgb(192, 192, 192), Color.rgb(22, 22, 22), 1, true, 1f),

    DIJKSTRA(Color.rgb(255, 255, 100), Color.rgb(255, 255, 100), 0, false, 2f);

    private final Color mainColor;
    private final Color secondColor;
    private final int requiredZoom;
    private final boolean fill;
    private final float width;

    WayType(Color mainColor, Color secondColor, int requiredZoom, boolean fill, float width) {
        this.mainColor = mainColor;
        this.secondColor = secondColor;
        this.requiredZoom = requiredZoom;
        this.fill = fill;
        this.width = width;
    }

    public Color getMainColor() {
        return mainColor;
    }

    public Color getSecondColor() {
        return secondColor;
    }

    public boolean fillTrue() {
        return fill;
    }

    public float getRequiredZoom() {
        return requiredZoom;
    }

    public float getWidth() {
        return width;
    }
}
