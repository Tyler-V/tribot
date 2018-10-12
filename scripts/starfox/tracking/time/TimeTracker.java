package scripts.starfox.tracking.time;

/**
 * @author Nolan
 */
public class TimeTracker {

    private long startTime;

    /**
     * Constructs a new TimeTracker.
     */
    public TimeTracker() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Gets the time at which the time tracker started tracking time.
     *
     * @return The start time.
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Gets the amount of time that the tracker has tracked since it started.
     *
     * @return The total amount of time.
     */
    public long getRunningTime() {
        return System.currentTimeMillis() - getStartTime();
    }

    /**
     * Subtracts the specified amount of milliseconds from the running time.
     *
     * @param time The amount of milliseconds to subtract.
     */
    public void subtract(long time) {
        this.startTime += time;
    }

    /**
     * Refreshes the time tracker to erase any previous run-time.
     */
    public void refresh() {
        this.startTime = System.currentTimeMillis();
    }
}
