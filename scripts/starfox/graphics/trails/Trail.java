package scripts.starfox.graphics.trails;

import scripts.starfox.api2007.Mouse07;
import scripts.starfox.interfaces.graphics.Paintable;

import java.awt.*;
import java.util.LinkedList;

/**
 * @author Nolan
 */
public abstract class Trail
        implements Paintable, Runnable {

    protected final Object lock;

    private final LinkedList<TrailPoint> trailPoints;
    private long lastingTime;
    private Thread trailThread;

    /**
     * Constructs a new Trail.
     *
     * @param lastingTime The lasting time of each trail point.
     */
    public Trail(long lastingTime) {
        this.lock = new Object();
        this.trailPoints = new LinkedList<>();
        this.lastingTime = lastingTime;
        this.trailThread = new Thread(this, "Trail Thread-" + lastingTime);
        this.trailThread.start(); //I am lazy, don't yell at me.
    }

    /**
     * Gets the list of trail points currently on the trail.
     *
     * @return The trail points.
     */
    public LinkedList<TrailPoint> getTrailPoints() {
        return this.trailPoints;
    }

    /**
     * Gets the lasting time of each trail point on the trail.
     *
     * @return The lasting time.
     */
    public long getLastingTime() {
        return this.lastingTime;
    }

    /**
     * Sets the lasting time of the trail points.
     *
     * @param lastingTime The lasting time to set.
     */
    public void setLastingTime(long lastingTime) {
        this.lastingTime = lastingTime;
    }

    /**
     * Starts the trail thread if it is not already started.
     */
    public void start() {
        if (trailThread == null) {
            trailThread = new Thread(this, "Trail Thread-" + lastingTime);
        }
        if (!trailThread.isAlive()) {
            trailThread.start();
        }
    }

    /**
     * Stops the trail thread.
     */
    public void stop() {
        this.trailThread = null;
    }

    @Override
    public void run() {
        Thread thisThread = Thread.currentThread();
        while (thisThread == trailThread) {
            synchronized (lock) {
                while (!trailPoints.isEmpty() && !trailPoints.peek().isUp()) {
                    trailPoints.remove();
                }
                Point mouse = Mouse07.getLocation();
                TrailPoint tp = new TrailPoint(mouse.x, mouse.y, getLastingTime());
                if (trailPoints.isEmpty() || (!trailPoints.getLast().equals(tp) && trailPoints.peek().distance(tp.x, tp.y) > 3)) {
                    trailPoints.add(tp);
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public abstract void paint(Graphics g);
}
