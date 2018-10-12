package scripts.starfox.scriptframework;

/**
 * @author Nolan
 */
public interface TerminateCondition {

    /**
     * Whether or not the condition has been met.
     *
     * @return True if the condition is met, false otherwise.
     */
    boolean isMet();

    /**
     * A string that represents the diagnosis for the condition being met.
     *
     * @return The diagnosis.
     */
    String diagnosis();
}
