package bfst22.vector;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;

public class Framerate {
    private final long[] frameTimes = new long[100];
    private int frameTimeIndex = 0;
    private boolean arrayFilled = false;
    private double frameRate;

    public Framerate() {
        Label label = new Label();
        AnimationTimer frameRateMeter = new AnimationTimer() {

            @Override
            public void handle(long now) {
                long oldFrameTime = frameTimes[frameTimeIndex];
                frameTimes[frameTimeIndex] = now;
                frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
                if (frameTimeIndex == 0) {
                    arrayFilled = true;
                }
                if (arrayFilled) {
                    long elapsedNanos = now - oldFrameTime;
                    long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                    frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                    label.setText(String.format("Current frame rate: %.3f", frameRate));
                }
            }
        };

        frameRateMeter.start();
    }

    public String getFrameRate() {
        int intFramerate = (int) frameRate;
        String stringFramerate = Integer.toString(intFramerate);
        return "FPS: " + stringFramerate;
    }

}
