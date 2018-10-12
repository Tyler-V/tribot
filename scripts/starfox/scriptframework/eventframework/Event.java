package scripts.starfox.scriptframework.eventframework;

/**
 * @author Nolan
 */
public abstract class Event {
    
    /**
     * This method is used to determine whether or not the event should run.
     * @return True if the even should run, false otherwise.
     */
    public abstract boolean shouldRun();
    
    /**
     * This method should be used to run any code that needs to be executed when the event runs.
     */
    public abstract void run();
}
