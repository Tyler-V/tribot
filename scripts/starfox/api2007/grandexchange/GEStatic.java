package scripts.starfox.api2007.grandexchange;

/**
 * Created by Nolan on 10/1/2015.
 */
public class GEStatic {

    private static GeLookup geLookup = new GeLookup(true);

    /**
     * Looks up the item with the specified ID.
     *
     * @param id The ID of the item to look up.
     * @return The GeLookupItem.
     * A blank GeLookupItem is returned if no item could be found.
     */
    public static GeLookupItem lookup(int id) {
        return geLookup.lookup(id, true);
    }
}
