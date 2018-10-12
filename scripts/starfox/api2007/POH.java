package scripts.starfox.api2007;

import org.tribot.api.Clicking;
import org.tribot.api2007.Game;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import scripts.starfox.api.waiting.Condition07;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.entities.Objects07;

/**
 * The POH class provides methods related to your player owned house (POH).
 *
 * @author Nolan
 */
public class POH {

    /*
        INTERFACE ID'S
     */
    //MASTER
    private static final int HOUSE_LOADING_MASTER_INDEX = 399;
    private static final int HOUSE_OPTIONS_MASTER_INDEX = 370;

    //CHILDREN
    private static final int VIEW_HOUSE_OPTIONS_BUTTON_INDEX = 70;
    private static final int TELEPORT_INSIDE_ON_INDEX = 11;
    private static final int TELEPORT_INSIDE_OFF_INDEX = 12;

    //COMPONENT


    //The index the holds the setting for if the player teleports inside or outside of the house.
    private static final int TELEPORT_INSIDE_SETTING_INDEX = 1047;

    /**
     * Gets the house options button interface.
     *
     * @return The house options button interface.
     * Null if the button was not found or is not loaded.
     */
    public static RSInterfaceChild getHouseOptionsButton() {
        return Interfaces07.get(Options07.TAB_MASTER_INDEX, VIEW_HOUSE_OPTIONS_BUTTON_INDEX);
    }

    /**
     * Checks to see if the house setting for teleporting inside is on.
     *
     * @return True if the player will teleport inside the house, false if the player will teleport outside the house.
     */
    public static boolean isTeleportingInside() {
        return Settings.get(TELEPORT_INSIDE_SETTING_INDEX) >> 23 == 0;
    }

    /**
     * Gets the amount of tasks that your servant will do before asking for payment.
     *
     * @return The amount of tasks.
     */
    public static int getServentTasks() {
        return (Settings.get(Settings.Indexes.POH.SERVANT_TASK_INDEX) >> 21) & 0xF;
    }

    /**
     * Checks to see whether or not you are in building mode.
     *
     * @return True if you are in building mode, false otherwise.
     */
    public static boolean isInBuildingMode() {
        return Game.getSetting(Settings.Indexes.POH.BUILDING_MODE_INDEX) >> 10 == 1;
    }

    /**
     * Checks to see whether or not you are in your POH.
     *
     * @return True if you are in your POH, false otherwise.
     */
    public static boolean isInPOH() {
        if (isHouseLoading()) {
            return false;
        }
        RSObject portal = Objects07.getObject(Filters.Objects.actionsContains("Lock"), 50);
        if (portal != null) {
            RSObjectDefinition def = portal.getDefinition();
            if (def != null) {
                String name = def.getName();
                return name != null && name.equalsIgnoreCase("Portal");
            }
        }
        return false;
    }

    /**
     * Checks to see whether or not your POH is being loaded.
     *
     * @return True if it is being loaded, false otherwise.
     */
    public static boolean isHouseLoading() {
        return Interfaces07.isUp(HOUSE_LOADING_MASTER_INDEX);
    }

    /**
     * Checks to see if the house options interface is open.
     *
     * @return True if the house options interface is open, false otherwise.
     */
    public static boolean isOpen() {
        return Interfaces07.isUp(HOUSE_OPTIONS_MASTER_INDEX);
    }

    /**
     * Toggles the teleporting inside setting to the specified position.
     *
     * @param on True to turn the setting on, false to turn it off.
     * @return True if the setting was toggled successfully, false otherwise.
     */
    public static boolean toggleIsTeleportingInside(boolean on) {
        if (isTeleportingInside() == on) {
            return true;
        }
        if (Options07.open()) {
            if (Clicking.click(getHouseOptionsButton())) {
                if (Waiting.waitUntil(() -> Interfaces07.isUp(HOUSE_OPTIONS_MASTER_INDEX), 5000)) {
                    int interfaceId = on ? TELEPORT_INSIDE_ON_INDEX : TELEPORT_INSIDE_OFF_INDEX;
                    if (Clicking.click(Interfaces07.get(HOUSE_OPTIONS_MASTER_INDEX, interfaceId))) {
                        return Waiting.waitUntil(() -> isTeleportingInside() == on, 5000);
                    }
                }
            }
        }
        return false;
    }
}
