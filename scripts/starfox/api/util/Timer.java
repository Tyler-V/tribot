package scripts.starfox.api.util;

/**
 * The Timer class is a way to measure time passed in milliseconds safely.
 *
 *
 * @author Nolan
 */
public class Timer {

    /**
     * How long in milliseconds the timer has before it times out.
     */
    private long timeout;

    /**
     * The time at which the timer was started.
     */
    private long start_time;

    /**
     * A boolean that is true when the timer is stopped or false when the timer is not stopped.
     */
    private boolean stopped;

    /**
     * Constructs a new Timer.
     *
     * @param timeout The amount of time (in milliseconds) before the timer times out.
     */
    public Timer(long timeout) {
        this.timeout = timeout;
        this.start_time = 0;
        this.stopped = false;
    }

    /**
     * Gets the timeout of the timer.
     *
     * @return The timeout.
     */
    public final long getTimeOut() {
        return this.timeout;
    }

    /**
     * Gets the start time of the timer.
     *
     * @return The start time.
     * 0 if the timer has not yet been started.
     */
    public final long getStartTime() {
        return this.start_time;
    }

    /**
     * Sets the start time of the timer to be equal to the specified time.
     *
     * @param start_time The start time.
     */
    public final void setStartTime(long start_time) {
        this.start_time = start_time;
    }

    /**
     * Sets the timeout of the timer to be equal to the specified timeout.
     *
     * @param timeout The timeout.
     */
    public final void setTimeOut(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the time left before the timer times out.
     *
     * @return The amount of milliseconds before the timer times out.
     */
    public final long timeLeft() {
        return (getStartTime() + getTimeOut()) - System.currentTimeMillis();
    }

    /**
     * Checks to see whether or not the timer has timed out.
     *
     * @return True if the timer has timed out, false otherwise.
     */
    public final boolean timedOut() {
        return stopped || timeLeft() <= 0;
    }

    /**
     * Starts the timer.
     */
    public final void start() {
        this.stopped = false;
        this.start_time = System.currentTimeMillis();
    }

    /**
     * Resets the timer.
     */
    public final void reset() {
        start();
    }

    /**
     * Stops the timer.
     */
    public final void stop() {
        this.stopped = true;
    }
}
