package scripts.starfox.scriptframework;

import org.tribot.api2007.types.RSItemDefinition;
import scripts.starfox.api2007.Inventory07;

/**
 * Created by nolan on 10/21/2016.
 */
public class Supply {

    private int amount;
    private int id;

    /**
     * Constructs a new Supply.
     * A supply is an object that wraps information about an item that is required for a script.
     *
     * @param amount The amount of the supply needed.
     * @param id     The id of the supply.
     */
    public Supply(int amount, int id) {
        this.amount = amount;
        this.id = id;
    }

    /**
     * Gets the amount required for the supply.
     *
     * @return The amount.
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Gets the id of the supply.
     *
     * @return The id.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Gets the name of the supply based on the id of the supply.
     *
     * @return The name of the supply.
     * Returns an empty string if the supply does not have a valid id or the definition could not be found.
     */
    public String getName() {
        RSItemDefinition definition = RSItemDefinition.get(getId());
        if (definition != null) {
            String name = definition.getName();
            if (name != null) {
                return name;
            }
        }
        return "";
    }

    /**
     * Gets the amount required to have the supply.
     *
     * @return The amount required to have the supply.
     * If there is a surplus of the supply, returns 0.
     */
    public int getRequiredAmount() {
        int required = getAmount() - Inventory07.getCount(getId());
        return required < 0 ? 0 : required;
    }

    /**
     * Sets the required amount for the supply to be equal to the specified amount.
     *
     * @param amount The amount to set.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Sets the id of the supply to be equal to the specified id.
     *
     * @param id The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Checks the inventory to see if we have the supply.
     *
     * @return True if we have the supply, false otherwise
     * We have the supply if there is an item in the inventory matching the id of the supply AND the stack of the item is greater than or equal to the amount required.
     */
    public boolean hasSupply() {
        return Inventory07.getCount(getId()) >= getAmount();
    }

    /**
     * Checks to inventory to see if we have a surplus of the supply.
     *
     * @return True if we have a surplus of the supply, false otherwise.
     */
    public boolean hasSurplus() {
        return Inventory07.getCount(getId()) > getAmount();
    }

    /**
     * Gets the surplus amount for the supply.
     *
     * @return The surplus amount.
     * 0 If there is no surplus.
     */
    public int getSurplus() {
        return getAmount() % Inventory07.getCount(getId());
    }
}
