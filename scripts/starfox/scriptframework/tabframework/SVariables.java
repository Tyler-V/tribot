package scripts.starfox.scriptframework.tabframework;

import java.util.HashMap;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;

/**
 * @author Spencer
 */
public abstract class SVariables {

    public Script script;
    public ThreadGroup group;
    public boolean debug;
    public HashMap<Integer, Boolean> itemData;
    public RSTile[] currentPath;

    private boolean isInRandom;
    private boolean isPaused;

    public SVariables() {
        script = null;
        group = null;
        debug = false;
        itemData = new HashMap<>();
        currentPath = null;
    }

    /**
     * Does nothing yet.
     *
     * @return Nothing yet.
     */
    public final boolean isInRandom() {
        return isInRandom;
    }

    /**
     * Does nothing yet.
     *
     * @return Nothing yet.
     */
    public final boolean isPaused() {
        return isPaused;
    }
}
