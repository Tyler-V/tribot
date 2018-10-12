package scripts.magic.data;

import java.io.Serializable;

/**
 * The Plank enum holds constants representing planks that can be made with the plank make spell or at the sawmill.
 *
 * @author Nolan
 */
public enum Plank implements Serializable {

    NORMAL("Logs", "Plank", 70, 100),
    OAK("Oak logs", "Oak plank", 175, 250),
    TEAK("Teak logs", "Teak plank", 350, 500),
    MAHOGANY("Mahogany logs", "Mahogany plank", 1050, 1500);

    private final String logName;
    private final String plankName;
    private final int plankMakeCost;
    private final int sawmillCost;

    /**
     * Constructs a new Plank.
     *
     * @param logName       The name of the log used to make the plank.
     * @param plankName     The name of the plank.
     * @param plankMakeCost The amount of coins it costs to make the plank with the plank make spell.
     * @param sawmillCost   The amount of coins it costs to make the plank at the sawmill.
     */
    private Plank(String logName, String plankName, int plankMakeCost, int sawmillCost) {
        this.logName = logName;
        this.plankName = plankName;
        this.plankMakeCost = plankMakeCost;
        this.sawmillCost = sawmillCost;
    }

    /**
     * Gets the name of the log used to make the plank.
     *
     * @return The name of the log.
     */
    public final String getLogName() {
        return this.logName;
    }

    /**
     * Gets the name of the plank.
     *
     * @return The name of the plank.F
     */
    public final String getPlankName() {
        return this.plankName;
    }

    /**
     * Gets the amount of coins it costs to make the plank with the plank make spell.
     *
     * @return The amount of coins it costs.
     */
    public final int getPlankMakeCost() {
        return this.plankMakeCost;
    }

    /**
     * Gets the amount of coins it costs to make the plank at the sawmill.
     *
     * @return The amount of coins it costs.
     */
    public final int getSawmillCost() {
        return this.sawmillCost;
    }

    public static Plank forName(String name) {
        for (Plank plank : values()) {
            if (plank.getPlankName().equals(name)) {
                return plank;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getPlankName();
    }
}
