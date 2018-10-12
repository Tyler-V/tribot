package scripts.starfox.scriptframework.loopframework;

import scripts.starfox.scriptframework.TerminateCondition;
import scripts.starfox.scriptframework.Vars;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Nolan on 10/19/2015.
 */
public abstract class Loop {

    private final ArrayList<TerminateCondition> terminateConditions;

    /**
     * Constructs a new {@link Loop}.
     *
     * @param terminateConditions The {@link TerminateCondition}'s (if any).
     */
    public Loop(TerminateCondition... terminateConditions) {
        this.terminateConditions = new ArrayList<>();
        Collections.addAll(this.terminateConditions, terminateConditions);
    }

    /**
     * Gets the {@link TerminateCondition}'s of the {@link Loop}.
     *
     * @return The {@link TerminateCondition}'s.
     */
    public ArrayList<TerminateCondition> getTerminateConditions() {
        return this.terminateConditions;
    }

    /**
     * Checks to see whether or not the {@link Loop} should terminate.
     *
     * @return True if it should terminate, false otherwise.
     */
    public boolean shouldTerminate() {
        for (TerminateCondition terminateCondition : getTerminateConditions()) {
            if (terminateCondition.isMet()) {
                Vars.get().setStoppingDiagnosis(terminateCondition.diagnosis());
                return true;
            }
        }
        return false;
    }

    /**
     * This method will be looped.
     */
    public abstract void loop();
}
