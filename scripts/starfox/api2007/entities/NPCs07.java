package scripts.starfox.api2007.entities;

import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;

/**
 * The NPCs07 class is a utility class that can get RSNPC's without the need for array checking. Note that null checking is still required.
 *
 * @author Nolan
 */
public class NPCs07 {

    /**
     * Gets the nearest RSNPC with the specified id.
     *
     * @param id The ID of the RSNPC.
     * @return The nearest RSNPC. Null if no RSNPC's were found.
     */
    public static RSNPC getNPC(int id) {
        RSNPC[] npcs = NPCs.find(id);
        Sorting.sortByDistance(npcs, Player.getPosition(), true);
        return npcs.length > 0 ? npcs[0] : null;
    }

    /**
     * Gets the nearest RSNPC with the specified name.
     *
     * @param name The name of the RSNPC.
     * @return The nearest RSNPC. Null if no RSNPC's were found.
     */
    public static RSNPC getNPC(String name) {
        RSNPC[] npcs = NPCs.sortByDistance(Player.getPosition(), NPCs.findNearest(name));
        return npcs.length > 0 ? npcs[0] : null;
    }

    /**
     * Gets the nearest RSNPC that is accepted by the specified filter.
     *
     * @param filter The filter.
     * @return The nearest RSNPC. Null if no RSNPC's were found.
     */
    public static RSNPC getNPC(Filter<RSNPC> filter) {
        RSNPC[] npcs = NPCs.findNearest(filter);
        return npcs.length > 0 ? npcs[0] : null;
    }

    /**
     * Gets the nearest NPC whose name matches the specified name that is inside the specified area.
     *
     * @param name The name of the NPC.
     * @param area The area.
     * @return The NPC closest to you inside the specified area.
     */
    public static RSNPC getIn(final String name, final RSArea area) {
        RSNPC[] npcs = NPCs.sortByDistance(Player.getPosition(), NPCs.find(new Filter<RSNPC>() {
            @Override
            public boolean accept(RSNPC n) {
                if (n != null) {
                    String nName = n.getName();
                    return nName != null && nName.equals(name) && area.contains(n);
                }
                return false;
            }
        }));
        return npcs.length > 0 ? npcs[0] : null;
    }
}
