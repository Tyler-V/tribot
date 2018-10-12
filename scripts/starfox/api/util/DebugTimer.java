package scripts.starfox.api.util;

import java.util.HashMap;
import scripts.starfox.api.Client;

/**
 * User for debug timing.
 *
 * @author Spencer
 */
public class DebugTimer {

    private static final HashMap<String, DebugTimer> timers;
    
    static {
        timers = new HashMap<>();
    }
    
    private long startTime;
    private double limit;
    private final String name;

    public DebugTimer() {
        this(null, -1);
    }

    public DebugTimer(double limit) {
        this(null, limit);
    }
    
    public DebugTimer(String name) {
        this(name, -1);
    }
    
    public DebugTimer(String name, double limit) {
        this.name = name;
        this.limit = limit;
        reset();
    }

    public final void reset() {
        startTime = System.nanoTime();
    }

    public final double getElapsed() {
        return (System.nanoTime() - startTime) / 1000000d;
    }

    public final void setLimit(double limit) {
        this.limit = limit;
    }

    public final boolean meetsLimit() {
        return limit == -1 || getElapsed() >= limit;
    }

    public final void print() {
        print(null);
    }

    public final void print(String message) {
        if (meetsLimit()) {
            Client.println("[TIMER] " + (name == null ? "NONE" : name) + ":" + (message != null ? " " + message : "") + " (" + getElapsed() + ")");
        }
    }
}
