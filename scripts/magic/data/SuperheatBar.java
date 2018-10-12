package scripts.magic.data;

import java.io.Serializable;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Skills.SKILLS;
import scripts.magic.tasks.Superheat;
import scripts.starfox.api2007.Inventory07;

/**
 * The SuperheatBar enum holds constants representing bars that can be made with the superheat item spell.F
 *
 * @author Starfox
 */
public enum SuperheatBar implements Serializable {

    BRONZE("Bronze bar", 1, new String[]{"Tin ore", "Copper ore"}, new int[]{1, 1}),
    IRON("Iron bar", 15, new String[]{"Iron ore"}, new int[]{1}),
    SILVER("Silver bar", 20, new String[]{"Silver ore"}, new int[]{1}),
    STEEL("Steel bar", 30, new String[]{"Iron ore", "Coal"}, new int[]{1, 2}),
    GOLD("Gold bar", 40, new String[]{"Gold ore"}, new int[]{1}),
    MITHRIL("Mithril bar", 50, new String[]{"Mithril ore", "Coal"}, new int[]{1, 4}),
    ADAMANT("Adamant bar", 70, new String[]{"Adamantite ore", "Coal"}, new int[]{1, 6}),
    RUNE("Rune bar", 85, new String[]{"Runite ore", "Coal"}, new int[]{1, 8});

    private final String name;
    private final int requiredLevel;
    private final String[] requiredOres;
    private final int[] numberOres;

    /**
     * Constructs a new SuperheatBar.
     *
     * @param name          The name of the bar.
     * @param requiredLevel The smithing level required to make the bar.
     * @param requiredOres  The required ores to make the bar.
     * @param numberOres    The number of required ores.
     */
    SuperheatBar(String name, int requiredLevel, String[] requiredOres, int[] numberOres) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.requiredOres = requiredOres;
        this.numberOres = numberOres;
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
     * Gets the required smithing level to make the bar.
     *
     * @return The required smithing level.
     */
    public final int getRequiredLevel() {
        return this.requiredLevel;
    }

    /**
     * Gets the names of the ores required to make the bar.
     *
     * @return The names of the ores required to make the bar.
     */
    public final String[] getRequiredOres() {
        return this.requiredOres;
    }

    /**
     * Gets the number of ores required to make the bar.
     *
     * This method returns an array in which each element represents how many of each ore is required to make the bar.F
     *
     * @return The number of ores required to make the bar.
     */
    public final int[] getNumberOres() {
        return this.numberOres;
    }

    /**
     * Checks to see if the inventory contains the ores required to make the bar.
     *
     * @return True if the inventory contains the ores required to make the bar, false otherwise.
     */
    public final boolean hasRequiredOres() {
        for (int i = 0; i < getRequiredOres().length; i++) {
            if (Inventory07.getCount(getRequiredOres()[i]) < getNumberOres()[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks to see if the player has the required smithing level to make the bar.
     *
     * @return True if the player has the required smithing level to make the bar, false otherwise.
     */
    public final boolean hasRequiredLevel() {
        return SKILLS.SMITHING.getActualLevel() >= getRequiredLevel();
    }

    /**
     * Gets the number of possible bars that can be made in the current inventory.
     *
     * @return The number of possible bars that can be made.F
     */
    public final int numberOfPossibleBars() {
        int total = 0;
        for (int i : getNumberOres()) {
            total += i;
        }
        int free = 28 - (Inventory.getAll().length - Inventory.find(getRequiredOres()).length);
        return free / total;
    }

    /**
     * Gets the required amount of the specified ore to make the bar.
     *
     * @param oreName The ore name.
     * @return The amount of the ore required to make the bar.
     */
    public final int getRequiredOreAmount(String oreName) {
        for (int i = 0; i < getRequiredOres().length; i++) {
            if (getRequiredOres()[i].equals(oreName)) {
                return getNumberOres()[i];
            }
        }
        return 0;
    }

    public static SuperheatBar forName(String name) {
        for (SuperheatBar bar : values()) {
            if (name.equals(bar.getName())) {
                return bar;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
