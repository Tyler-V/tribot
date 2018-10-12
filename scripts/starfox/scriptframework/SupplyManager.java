package scripts.starfox.scriptframework;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nolan on 10/24/2016.
 */
public class SupplyManager {

    private final ArrayList<Supply> supplies;

    /**
     * Constructs a new SupplyManager.
     * SupplyManager contains a list of supplies that can be retrieved and manipulated.
     *
     * @param supplies The supplies to add upon construction (if any).
     */
    public SupplyManager(Supply... supplies) {
        this.supplies = new ArrayList<>();
        Collections.addAll(getSupplies(), supplies);
    }

    /**
     * Gets the list of supplies.
     *
     * @return The supplies.
     */
    public ArrayList<Supply> getSupplies() {
        return this.supplies;
    }

    /**
     * Checks to see if the player has the supply with the specified id.
     *
     * @param id The id of the supply.
     * @return True if the player has the supply, false otherwise.
     */
    public boolean hasSupply(int id) {
        for (Supply supply : getSupplies()) {
            if (supply.getId() == id && supply.hasSupply()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the player has the specified supply.
     *
     * @param supply The supply.
     * @return True if the player has the supply, false otherwise.
     */
    public boolean hasSupply(Supply supply) {
        for (Supply sup : getSupplies()) {
            if (sup.getId() == supply.getId() && sup.hasSupply()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the player has all of the supplies contained within the manager.
     *
     * @return True if the play has all of the supplies, false otherwise.
     */
    public boolean hasSupplies() {
        for (Supply supply : getSupplies()) {
            if (!supply.hasSupply()) {
                return false;
            }
        }
        return true;
    }
}
