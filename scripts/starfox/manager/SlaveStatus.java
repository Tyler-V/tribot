package scripts.starfox.manager;

import java.io.Serializable;

/**
 * @author Starfox
 */
public enum SlaveStatus
        implements Serializable {
    SCRIPTING, PREPARING, READY, FAILED;
}
