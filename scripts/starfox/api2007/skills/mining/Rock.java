package scripts.starfox.api2007.skills.mining;

/**
 * The Rock enum is used to differentiate between the different types of rocks.
 *
 * @author Nolan
 */
public enum Rock {

    CLAY((short) 6705, (short) 6589),
    COPPER((short) 4645, (short) 3776, (short) 4510),
    TIN((short) 53, (short) 57),
    IRON((short) 2576),
    SILVER((short) 73663),
    COAL((short) 10508),
    GOLD((short) 8885, (short) 8128),
    MITHRIL((short) -22239),
    ADAMANTITE((short) 21662),
    RUNITE((short) -31437),
    UNKNOWN((short) -1);

    private final short[] modifiedColours;

    /**
     * Constructs a new Rock.
     *
     * @param modifiedColours The modified colors of the rock.
     */
    private Rock(final short... modifiedColours) {
        this.modifiedColours = modifiedColours;
    }

    /**
     * Gets the modified colors of the rock.
     *
     * @return The modified colors.
     */
    public final short[] getModifiedColours() {
        return modifiedColours;
    }
}
