package scripts.starfox.api.listeners;

import org.tribot.api2007.types.RSGroundItem;
import scripts.starfox.api2007.entities.GroundItems07;
import scripts.starfox.api2007.grandexchange.GEStatic;

/**
 * Created by nolan on 9/21/2016.
 */
public class GELookupListener
        extends ListenerThread {

    /**
     * Constructs a new PriceCacheListener.
     */
    public GELookupListener(boolean start) {
        super("PriceCacheListenerThread", 1000, start);
    }

    @Override
    public void listen() {
        for (RSGroundItem groundItem : GroundItems07.getAll()) {
            GEStatic.lookup(GroundItems07.getId(groundItem));
        }
    }
}
