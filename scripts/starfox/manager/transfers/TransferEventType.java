package scripts.starfox.manager.transfers;

import java.io.Serializable;

/**
 * @author Starfox
 */
public enum TransferEventType
        implements Serializable {

    PRESS, RELEASE, MOVE, CLICK;
}
