package scripts.starfox.scriptframework.eventframework;

import scripts.starfox.api.Client;
import scripts.starfox.api2007.login.Login07;
import scripts.starfox.scriptframework.TerminateCondition;
import scripts.starfox.scriptframework.Vars;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nolan
 */
public final class EventManager extends EventContainer {

    private static List<TerminateCondition> terminateConditions;
    
    static {
        terminateConditions = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Adds a terminate condition to the manager.
     * 
     * @param condition The condition.
     * @return True if the condition was added, false otherwise.
     */
    public static boolean addTerminateCondition(TerminateCondition condition) {
        return terminateConditions.add(condition);
    }

    public static void destroy() {
        terminateConditions = null;
        kill();
    }

    /**
     * Polls the event manager.
     *
     * This method will run an event if it should be ran, and then return.
     */
    public static void poll() {
        Event event = null;
        synchronized (lock) {
            Event[] events = getEvents().toArray(new Event[getEvents().size()]);
            for (Event e : events) {
                if (e.shouldRun()) {
                    event = e;
                    break;
                }
            }
        }
        if (event == null) {
            return;
        }
        event.run();
    }
    
    /**
     * Checks to see whether or not the script should terminate.
     * 
     * @return True if the any of the terminate conditions are met, false otherwise.
     */
    public static boolean terminate() {
        if (!Login07.isLoggedIn()) {
            return false;
        }
        for (TerminateCondition condition : terminateConditions) {
            if (condition.isMet()) {
                Vars.get().setStoppingDiagnosis(condition.diagnosis());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Stops the event manager.
     */
    public static void stop() {
        addTerminateCondition(new TerminateCondition() {
            @Override
            public boolean isMet() {
                return true;
            }

            @Override
            public String diagnosis() {
                return "Manually stopped.";
            }
        });
    }
}
