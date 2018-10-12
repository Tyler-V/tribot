package scripts.starfox.scriptframework.loopframework;

/**
 * Created by Nolan on 10/21/2015.
 */
public class LoopManager {

    private static Loop loop;

    static {
        loop = null;
    }

    /**
     * Gets the {@link Loop}.
     *
     * @return The {@link Loop}.
     */
    public static Loop getLoop() {
        return loop;
    }

    /**
     * Sets the {@link Loop} to be the specified {@link Loop}.
     *
     * @param l The {@link Loop} to set.
     */
    public static void setLoop(Loop l) {
        loop = l;
    }

    /**
     * Destroys the {@link Loop} so that the variable can be garbage collected at the end of the script.
     */
    public static void destroy() {
        loop = null;
    }

    /**
     * Calls the {@link Loop#loop()} method.
     * This method will return if the {@link Loop} is null.
     */
    public static void runLoop() {
        if (getLoop() == null) {
            return;
        }
        getLoop().loop();
    }

    /**
     * Checks to see if the {@link Loop} should terminate.
     * The {@link Loop} should terminate if any of the {@link scripts.starfox.scriptframework.TerminateCondition}'s have been met.
     *
     * @return True if it should terminate, false otherwise.
     */
    public static boolean shouldTerminate() {
        return getLoop() != null && getLoop().shouldTerminate();
    }
}
