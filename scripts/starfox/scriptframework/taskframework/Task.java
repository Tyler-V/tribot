package scripts.starfox.scriptframework.taskframework;

import scripts.starfox.scriptframework.TerminateCondition;
import scripts.starfox.scriptframework.Vars;

import java.util.ArrayList;

/**
 * @author Nolan
 */
public abstract class Task {

    private final ArrayList<TerminateCondition> terminateConditions;

    /**
     * Constructs a new Task.
     */
    public Task() {
        this.terminateConditions = new ArrayList<>();
    }

    /**
     * The task loop.
     */
    public abstract void loop();

    /**
     * Used to load terminate conditions for the task.
     * This method is automatically called in the task manager when the task is set and should not be called anywhere else.
     */
    public abstract void loadTerminateConditions();

    /**
     * Gets the terminate conditions for the task.
     *
     * @return The terminate conditions.
     */
    public final ArrayList<TerminateCondition> getTerminateConditions() {
        return this.terminateConditions;
    }

    /**
     * Adds a terminate condition to the task.
     *
     * @param tc The condition to add.
     * @return True if it was added, false otherwise.
     */
    public final boolean addTerminateCondition(TerminateCondition tc) {
        return this.terminateConditions.add(tc);
    }

    /**
     * Checks to see if any of the terminate conditions for the task are met.
     *
     * @return True if any are met, false otherwise.
     */
    public final boolean terminate() {
        for (TerminateCondition tc : getTerminateConditions()) {
            if (tc.isMet()) {
                Vars.get().setStoppingDiagnosis(tc.diagnosis());
                return true;
            }
        }
        return false;
    }
}
