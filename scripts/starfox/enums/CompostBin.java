package scripts.starfox.enums;

import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSTile;

/**
 * @author Nolan
 */
public enum CompostBin {

    CATHERBY(511, 8, new RSTile(2804, 3464, 0)),
    ARDOUGNE(511, 24, new RSTile(2661, 3375, 0)),
    FALADOR(511, 0, new RSTile(3056, 3312, 0)),
    CANIFIS(511, 16, new RSTile(3610, 3522, 0));

    private final int settingIndex;
    private final int rightShift;
    private final RSTile position;

    /**
     * Constructs a new CompostBin.
     *
     * @param settingIndex The setting index for the compost bin.
     * @param rightShift   The amount to bit-shift the setting value for the compost bin to get the amount.
     * @param position     The position of the compost bin.
     */
    private CompostBin(int settingIndex, int rightShift, RSTile position) {
        this.settingIndex = settingIndex;
        this.rightShift = rightShift;
        this.position = position;
    }

    /**
     * Gets the setting index of the compost bin.
     *
     * @return The setting index.
     */
    public final int getSettingIndex() {
        return this.settingIndex;
    }

    /**
     * Gets the amount of the compost bin.
     *
     * @return The amount.
     */
    public final int getAmount() {
        return (Game.getSetting(settingIndex) >>> rightShift) & 0xff;
    }

    /**
     * Gets the position of the compost bin.
     *
     * @return The position.
     */
    public final RSTile getPosition() {
        return position;
    }

    /**
     * Gets the state of the compost bin.
     *
     * @return The state of the compost bin.
     */
    public final CompostState getState() {
        int amount = getAmount();
        if (amount <= 30 && amount > 15 || amount >= 48 && amount <= 62) {
            return CompostState.GATHER;
        } else if (amount >= 0 && amount <= 14 || amount >= 33 && amount <= 46) {
            return CompostState.FILL;
        } else if (amount == 15 || amount == 47) {
            return CompostState.CLOSE;
        } else if (amount == 94 || amount == 126) {
            return CompostState.OPEN;
        } else {
            return CompostState.ROTTING;
        }
    }

    /**
     * The CompostState enum holds constants that represent different states that a compost bin can be in.
     */
    public enum CompostState {

        GATHER, FILL, CLOSE, OPEN, ROTTING
    }
}
