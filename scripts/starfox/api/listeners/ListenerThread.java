package scripts.starfox.api.listeners;

/**
 * The ListenerThread class allows for a safe implementation of stopping and starting a thread.
 *
 * @author Nolan
 */
public abstract class ListenerThread
        implements Runnable {

    /**
     * The default sleep time after every listen.
     * This value is roughly equivalent to 16 listens per 1,000 milliseconds.
     */
    public static final long DEFAULT_SLEEP_TIME = 600 / 10;

    /**
     * The thread that will be running.
     */
    private volatile Thread listener;

    /**
     * A lock for the listener thread.
     */
    protected final Object lock;

    /**
     * The sleep time after every listen.
     */
    private final long sleepTime;

    /**
     * Constructs a new ListenerThread.
     */
    public ListenerThread() {
        this(null, DEFAULT_SLEEP_TIME, false);
    }

    /**
     * Constructs a new ListenerThread.
     *
     * @param start True to start the thread upon construction, false otherwise.
     */
    public ListenerThread(boolean start) {
        this(null, DEFAULT_SLEEP_TIME, start);
    }

    /**
     * Constructs a new ListenerThread.
     *
     * @param sleepTime The amount of time (in milliseconds) to sleep after each listen.
     * @param start     True to start the thread upon construction, false otherwise.
     */
    public ListenerThread(long sleepTime, boolean start) {
        this(null, sleepTime, start);
    }

    /**
     * Constructs a new ListenerThread.
     *
     * @param threadName The name of the thread.
     * @param sleepTime  The amount of time (in milliseconds) to sleep for after each listen.
     * @param start      True to start the thread upon construction, false otherwise.
     */
    public ListenerThread(String threadName, long sleepTime, boolean start) {
        this.lock = new Object();
        this.sleepTime = sleepTime;
        if (start) {
            if (threadName == null) {
                start();
            } else {
                start(threadName);
            }
        }
    }

    /**
     * Gets the listener thread.
     *
     * @return The listener thread.
     */
    public final Thread getListenerThread() {
        return this.listener;
    }

    /**
     * Gets the amount of time (in milliseconds) that the thread sleeps for after every listen.
     *
     * @return The sleep time.
     */
    public final long getSleepTime() {
        return this.sleepTime;
    }

    /**
     * Starts the listener thread with no name.
     */
    public final void start() {
        start("ListenerThread - " + getClass().getSimpleName() + "," + getClass().hashCode());
    }

    /**
     * Starts the listener thread and gives it the specified name.
     *
     * @param threadName The name.
     */
    public final void start(String threadName) {
        if (getListenerThread() == null) {
            this.listener = new Thread(this, threadName);
        }
        if (!getListenerThread().isAlive()) {
            getListenerThread().start();
        }
    }

    /**
     * Stops the listener thread.
     */
    public final void stop() {
        this.listener = null;
    }

    @Override
    public void run() {
        while (getListenerThread() != null) {
            listen();
            try {
                Thread.sleep(getSleepTime());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method will run on the listener thread between each sleep time.
     */
    public abstract void listen();
}
