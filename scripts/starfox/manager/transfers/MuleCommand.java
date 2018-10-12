package scripts.starfox.manager.transfers;

import java.io.Serializable;

/**
 * @author Starfox
 */
public enum MuleCommand
        implements Serializable {

    GET_MULE_DATA, PUBLISH_DATA, PUBLISH_RSN, SEND_SCREEN_IMAGE;
}
