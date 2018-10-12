package scripts.starfox.graphics.trails;

import java.awt.Point;

/**
 * @author Nolan
 */
public class TrailPoint
        extends Point {

    private final double lastingTime;
    private final long finishTime;

    /**
     * Constructs a new TrailPoint.
     *
     * @param x           The x coordinate.
     * @param y           The y coordinate.
     * @param lastingTime The lasting time (in milliseconds) of the trail point.
     */
    public TrailPoint(int x, int y, long lastingTime) {
        super(x, y);
        this.lastingTime = (double) lastingTime;
        this.finishTime = System.currentTimeMillis() + lastingTime;
    }

    /**
     * Constructs a new TrailPoint.
     *
     * @param location    The location of the trail point.
     * @param lastingTime The lasting time (in milliseconds) of the trail point.
     */
    public TrailPoint(Point location, long lastingTime) {
        this(location.x, location.y, lastingTime);
    }

    /**
     * Gets the lasting time of the trail point.
     *
     * @return The lasting time.
     */
    public double getLastingTime() {
        return this.lastingTime;
    }

    /**
     * Gets the time at which the trail point is finished.
     *
     * @return The finish time.
     */
    public long getFinishTime() {
        return this.finishTime;
    }

    /**
     * Checks to see if the trail point is still up.
     *
     * @return True if the trail point is up, false otherwise.
     */
    public boolean isUp() {
        return System.currentTimeMillis() < getFinishTime();
    }

    /**
     * Gets the amount of time in milliseconds until the trail points finish time.
     *
     * @return The time left.
     */
    public long getTimeLeft() {
        long timeLeft = getFinishTime() - System.currentTimeMillis();
        return timeLeft > 0 ? timeLeft : 0;
    }
}
