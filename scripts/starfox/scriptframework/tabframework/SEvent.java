package scripts.starfox.scriptframework.tabframework;

/**
 * @author Spencer
 * @param <T> The SVariables object that is being used for this event (the one set by the script).
 */
public abstract class SEvent<T extends SVariables> {

    public abstract boolean validate(T vars, Object lock);

    public abstract void execute(T vars, Object lock);
}
