package scripts.starfox.scriptframework.stateframework;

/**
 * @author Nolan
 * @param <T> The state enum.
 */
public abstract class StateManager<T extends Enum<?>> {

    private static volatile StateManager state_manager;

    /**
     * Gets the state.
     *
     * @return The state.
     */
    public abstract T getState(); //help me what is this class

    /**
     * Sets the state manager.
     *
     * @param s The class.
     */
    public static void setStateManager(Class<? extends StateManager> s) {
        try {
            state_manager = s.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets the state manager.
     *
     * @param stateManager The state manager to set.
     */
    public static void setStateManager(StateManager stateManager) {
        state_manager = stateManager;
    }

    /**
     * Gets the state of the state manager.
     *
     * @param <T> The state enum.
     * @return The state.
     */
    public static <T extends Enum> T state() {
        return (T) state_manager.getState();
    }
}
