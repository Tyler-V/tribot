package scripts.starfox.enums.smithing;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Strings;
import scripts.starfox.api2007.Interfaces07;

/**
 * The Smithable enum holds constants representing items that can be made by smithing bars at an anvil.
 *
 * @author Nolan
 */
public enum Smithable {

    DAGGER(1, "dagger"),
    AXE(1, "axe"),
    MACE(1, "mace"),
    BOLTS(1, "bolts"),
    MEDIUM_HELM(1, "med helm"),
    SWORD(1, "sword"),
    BRONZE_WIRE(1, "wire"),
    DART_TIPS(1, "dart tips"),
    NAILS(1, "nails"),
    ARROWTIPS(1, "arrowtips"),
    SCIMITAR(2, "scimitar"),
    LIMBS(1, "limbs"),
    LONG_SWORD(2, "longsword"),
    FULL_HELM(2, "full helm"),
    THROWING_KNIVES(1, "knife"),
    SQUARE_SHIELD(2, "sq shield"),
    WARHAMMER(3, "warhammer"),
    BATTLE_AXE(3, "battleaxe"),
    CHAIN_BODY(3, "chainbody"),
    KITE_SHIELD(3, "kite shield"),
    CLAWS(2, "claws"),
    TWO_HAND_SWORD(3, "2h sword"),
    PLATE_LEGS(3, "platelegs"),
    PLATE_SKIRT(3, "plateskirt"),
    IRON_SPIT(1, "spit"),
    OIL_LANTERN_FRAME(1, "Oil lantern frame"),
    STUDS(1, "studs"),
    BULLSEYE_LANTERN(1, "Bullseye lantern"),
    GRAPPLE_TIP(1, "grapple"),
    PLATE_BODY(5, "platebody");

    public static final int SMITHING_MASTER_INDEX = 312;

    private final int numberOfBars;
    private final String itemName;

    /**
     * Creates a new Smithable.
     *
     * @param numberOfBars The number of bars required to make the smithable.
     */
    private Smithable(final int numberOfBars, String itemName) {
        this.numberOfBars = numberOfBars;
        this.itemName = itemName;
    }

    /**
     * Gets the number of bars required to make the smithable.
     *
     * @return The number of bars required to make the smithable.
     */
    public final int getNumberOfBars() {
        return this.numberOfBars;
    }

    /**
     * Gets the item name.
     *
     * @return The item name.
     */
    public final String getItemName() {
        return this.itemName;
    }

    /**
     * Gets the special bar, if any, of the smithable.
     *
     * This method will return null if no special bar is required to make the smithable.
     *
     * @return The special bar required to smith the smithable. Null if no special bar is required.
     */
    public final Bar getSpecialBar() {
        switch (this) {
            case BRONZE_WIRE:
                return Bar.BRONZE;
            case IRON_SPIT:
                return Bar.IRON;
            case OIL_LANTERN_FRAME:
                return Bar.IRON;
            case STUDS:
                return Bar.STEEL;
            case BULLSEYE_LANTERN:
                return Bar.STEEL;
            case GRAPPLE_TIP:
                return Bar.MITHRIL;
        }
        return null;
    }

    /**
     * Gets the interface of the smithable.
     *
     * @return The interface.
     */
    public final RSInterface getInterface() {
        RSInterface smithing_screen = Interfaces.get(SMITHING_MASTER_INDEX);
        if (smithing_screen != null) {
            RSInterface[] children = smithing_screen.getChildren();
            if (children != null && children.length > 0) {
                for (RSInterface child : children) {
                    if (child != null) {
                        RSInterface[] components = child.getChildren();
                        if (components != null && components.length > 0) {
                            for (RSInterface component : components) {
                                if (component != null) {
                                    String text = component.getText();
                                    if (text != null && text.equalsIgnoreCase(getInterfaceText())) {
                                        return component;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Clicks the interface for this Smithable.
     *
     * If the "Smith X" option is required this method will handle the enter amount menu.
     *
     * @return True if the click was successful, false otherwise.
     */
    public final boolean clickInterface() {
        final RSInterface component = getInterface();
        if (component == null) {
            return false;
        }
        final String option = getOption();
        if (Clicking.click(option, component)) {
            if (Timing.waitCondition(new Condition() {
                //<editor-fold defaultstate="collapsed">
                @Override
                public boolean active() {
                    Client.sleep(50);
                    return !Interfaces07.isUp(SMITHING_MASTER_INDEX) || Interfaces07.isEnterAmountMenuUp();
                }//</editor-fold>
            }, 2500)) {
                if (Interfaces07.isEnterAmountMenuUp()) {
                    Keyboard.typeSend("" + General.random(28, 99));
                    return Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            Client.sleep(50);
                            return !Interfaces07.isEnterAmountMenuUp();
                        }
                    }, 2500);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the smithing option that should be used when clicking the interface of the smithable.
     *
     * @return The smithing option.
     */
    public final String getOption() {
        int inventoryBarCount = Inventory.find(Filters.Items.nameContains("bar")).length;
        int smithableBarCount = getNumberOfBars();
        int numItems = inventoryBarCount / smithableBarCount;
        if (numItems > 10) {
            return "Smith X";
        } else if (numItems > 5) {
            return "Smith 10";
        } else if (numItems > 1) {
            return "Smith 5";
        }
        return "Smith 1";
    }

    /**
     * Gets the text of the interface of the smithable.
     *
     * @return The text.
     */
    public final String getInterfaceText() {
        if (this == TWO_HAND_SWORD) {
            return "2-handed sword";
        }
        return toString();
    }

    /**
     * Checks to see if a smithable can be parsed from the specified name.
     *
     * A smithable can be parsed if the name specified is equal to any smithables name.
     *
     * @param name The name.
     * @return True if a smithable can be parsed from the name, false otherwise.
     */
    public static final boolean isSmithable(String name) {
        for (Smithable smithable : Smithable.values()) {
            if (name.toLowerCase().contains(Strings.replaceUnderscores(smithable.name().toLowerCase()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String formatted_string = Strings.capitalizeFirst(Strings.replaceUnderscores(name().toLowerCase()));
        return formatted_string;
    }
}
