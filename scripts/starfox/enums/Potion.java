package scripts.starfox.enums;

/**
 * Created by nolan on 9/21/2016.
 *
 * Potion enum contains constants for potions in Old School Runescape.
 */
public enum Potion {

    SUPER_COMBAT(new int[]{12701, 12699, 12697, 12695});

    private final int[] ids;

    /**
     * Constructs a new Potion.
     *
     * @param ids The ID's of the potion from dose 1-4 in that order.
     */
    Potion(int[] ids) {
        this.ids = ids;
    }

    /**
     * Gets the ID's of the potion.
     *
     * @return The ID's of the potion.
     */
    public int[] getIds() {
        return this.ids;
    }
}
