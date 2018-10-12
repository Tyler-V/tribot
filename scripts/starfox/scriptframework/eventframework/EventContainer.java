package scripts.starfox.scriptframework.eventframework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nolan
 */
public class EventContainer {
    
    protected static Object lock;
    private static List<Event> events;

    static {
        lock = new Object();
        events = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Creates an empty event container.
     */
    protected EventContainer() {
        
    }

    /**
     * Gets the list of events.
     *
     * @return The events.
     */
    public static List<Event> getEvents() {
        return events;
    }
    
    /**
     * Adds the specified event to the container.
     * 
     * @param event The event to add.
     * @return True if the event was added, false otherwise.
     */
    public static boolean add(Event event) {
        synchronized (lock) {
            return getEvents().add(event);
        }
    }
    
    /**
     * Removes the specified event from the container.
     * 
     * @param event The event to remove.
     * @return True if the event was removed, false otherwise.
     */
    public static boolean remove(Event event) {
        synchronized (lock) {
            return getEvents().remove(event);
        }
    }

    protected static void kill() {
        if (lock != null) {
            synchronized (lock) {
                events = null;
            }
            lock = null;
        }
    }
}
