package scripts.starfox.manager.transfers;

import java.io.Serializable;

/**
 * @author Starfox
 */
public enum SlaveCommand
        implements Serializable {

    GET_SLAVE_DATA, PUBLISH_DATA, PUBLISH_RSN, GET_SLAVES, SEND_SCREEN_IMAGE;
}
