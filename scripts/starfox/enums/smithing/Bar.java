package scripts.starfox.enums.smithing;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import scripts.starfox.api2007.Interfaces07;

/**
 * The Bar enum holds constants representing bars that can be made by smelting ores in a furnace.
 *
 * @author Nolan
 */
public enum Bar {

    BRONZE(2349, "Bronze bar", 4, new Ore[]{new Ore(RockOre.TIN, 1), new Ore(RockOre.COPPER, 1)}),
    BLURITE(9467, "Blurite bar", 5, new Ore[]{new Ore(RockOre.BLURITE, 1)}),
    IRON(2351, "Iron bar", 6, new Ore[]{new Ore(RockOre.IRON, 1)}),
    SILVER(2355, "Silver bar", 7, new Ore[]{new Ore(RockOre.SILVER, 1)}),
    STEEL(2353, "Steel bar", 8, new Ore[]{new Ore(RockOre.IRON, 1), new Ore(RockOre.COAL, 2)}),
    GOLD(2357, "Gold bar", 9, new Ore[]{new Ore(RockOre.GOLD, 1)}),
    MITHRIL(2359, "Mithril bar", 10, new Ore[]{new Ore(RockOre.MITHRIL, 1), new Ore(RockOre.COAL, 4)}),
    ADAMANT(2361, "Adamant bar", 11, new Ore[]{new Ore(RockOre.ADAMANTITE, 1), new Ore(RockOre.COAL, 6)}),
    RUNE(2363, "Rune bar", 12, new Ore[]{new Ore(RockOre.RUNITE, 1), new Ore(RockOre.COAL, 8)});

    public final int SMELTING_MASTER_INDEX = 311;
    public final int CANNONBALL_MASTER_INDEX = 309;

    private final int id;
    private final String name;
    private final int childIndex;
    private final Ore[] requiredOres;

    /**
     * Constructs a new Bar.
     *
     * @param id           The id.
     * @param name         The name.
     * @param childIndex   The child index.
     * @param requiredOres The required ores.
     */
    private Bar(int id, String name, int childIndex, Ore[] requiredOres) {
        this.id = id;
        this.name = name;
        this.childIndex = childIndex;
        this.requiredOres = requiredOres;
    }

    /**
     * Gets the ID of the bar.
     *
     * @return The ID.
     */
    public final int getId() {
        return this.id;
    }

    /**
     * Gets the name of the bar.
     *
     * @return The name.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Gets the child index of the interface of the bar.
     *
     * @return The child index.
     */
    public final int getChildIndex() {
        return this.childIndex;
    }

    /**
     * Gets the ores required to smelt the bar.
     *
     * @return The required ores.
     */
    public final Ore[] getRequiredOres() {
        return this.requiredOres;
    }

    /**
     * Gets the interface of the bar on the smelting interface.
     *
     * @param cannonball Whether or not to use the cannonball interface.
     * @return The interface for this bar.
     */
    public final RSInterface getInterface(boolean cannonball) {
        return cannonball ? Interfaces.get(CANNONBALL_MASTER_INDEX, 3) : Interfaces.get(SMELTING_MASTER_INDEX, getChildIndex());
    }

    /**
     * Clicks the bars interface. If the number of possible bars is greater than 10, this method will handle the enter amount menu.
     *
     * @param cannonball Whether or not to use the cannonball interface.
     * @return True if the interface was clicked successfully, false otherwise.
     */
    public final boolean clickInterface(final boolean cannonball) {
        RSInterface widget = getInterface(cannonball);
        if (widget != null) {
            String op;
            if (cannonball) {
                op = "Make All";
            } else {
                op = getNumberOfPossibleBars() > 10 ? "Smelt X" : "Smelt 10";
            }
            final String option = op;
            final boolean containsX = option.contains("X");
            if (Clicking.click(option, widget)) {
                if (containsX) {
                    if (Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return Interfaces07.isEnterAmountMenuUp();
                        }
                    }, 3000)) {
                        Keyboard.typeSend("" + General.random(28, 99));
                    }
                }
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return cannonball ? Interfaces07.isUp(CANNONBALL_MASTER_INDEX)
                                : containsX ? !Interfaces07.isEnterAmountMenuUp() : !Interfaces07.isUp(SMELTING_MASTER_INDEX);
                    }
                }, 2000);
            }
        }
        return false;
    }

    /**
     * Gets the number of possible bars that can be made in the current inventory.
     *
     * @return The number of possible bars that can be made.
     */
    public final int getNumberOfPossibleBars() {
        int freespace = 28;
        int bars = 0;
        while (freespace > 0) {
            for (Ore ore : getRequiredOres()) {
                freespace -= ore.getAmount();
            }
            if (freespace <= 0) {
                break;
            }
            bars++;
        }
        return bars;
    }

    @Override
    public String toString() {
        return getName().replace(" bar", "");
    }

    public enum RockOre {

        TIN(438, "Tin ore"),
        COPPER(436, "Copper ore"),
        BLURITE(668, "Blurite ore"),
        IRON(440, "Iron ore"),
        SILVER(442, "Silver ore"),
        COAL(453, "Coal"),
        GOLD(444, "Gold ore"),
        MITHRIL(447, "Mithril ore"),
        ADAMANTITE(449, "Adamantite ore"),
        RUNITE(451, "Runite ore");

        private final int id;
        private final String name;

        /**
         * Constructs a new ore.
         *
         * @param id   The id.
         * @param name The name.
         */
        private RockOre(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * Gets the id of the ore.
         *
         * @return The id of the ore.
         */
        public int getId() {
            return this.id;
        }

        /**
         * Gets the name of the ore.
         *
         * @return The name of the ore.
         */
        public String getName() {
            return this.name;
        }
    }

    public static class Ore {

        private final RockOre ore;
        private final int amount;

        public Ore(RockOre ore, int amount) {
            this.ore = ore;
            this.amount = amount;
        }

        /**
         * Gets the ore.
         *
         * @return The ore.
         */
        private RockOre getOre() {
            return ore;
        }

        /**
         * Gets the name of the ore.
         *
         * @return The name of the ore.
         */
        public String getName() {
            return getOre().getName();
        }

        /**
         * Gets the id of the ore.
         *
         * @return The id of the ore.
         */
        public int getId() {
            return getOre().getId();
        }

        /**
         * The amount of ores.
         *
         * @return The amount of ores.
         */
        public int getAmount() {
            return amount;
        }
    }
}
