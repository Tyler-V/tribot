package scripts.starfox.api2007;

import org.tribot.api.Clicking;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.entities.Objects07;

/**
 * @author Nolan
 */
public class Wilderness {

    /**
     * Gets the current wilderness level.
     *
     * @return The current wilderness level.
     *         Returns 0 If you are not in the wilderness.
     */
    public static int getLevel() {
        RSInterface levelInterface = Interfaces07.get(90, 24);
        if (levelInterface != null) {
            String text = levelInterface.getText();
            if (text != null && text.contains(":")) {
                return Integer.parseInt(text.split(":")[1].trim());
            }
        }
        return 0;
    }

    /**
     * Checks to see whether or not you are in the wilderness.
     *
     * @return True if you are in the wilderness, false otherwise.
     */
    public static boolean isInWilderness() {
        return getLevel() > 0;
    }

    /**
     * Checks to see if the player is north of the wilderness ditch.
     *
     * @return True if the player is north of the wilderness ditch, false otherwise.
     */
    public static boolean isNorthOfDitch() {
        return Player.getPosition().getY() >= 3523;
    }

    /**
     * Checks to see if the specified player is attackable by your player.
     *
     * @param player The player to test.
     * @return True if the player is attackable, false otherwise.
     */
    public static boolean isPlayerAttackable(RSPlayer player) {
        if (player == null) {
            return false;
        }
        RSPlayer myPlayer = Player07.getPlayer();
        return myPlayer != null && Math.abs(myPlayer.getCombatLevel() - player.getCombatLevel()) <= getLevel();
    }

    /**
     * Checks if your local player can be attacked by any players that are loaded.
     *
     * @return True if your player can be attacked, false otherwise.
     */
    public static boolean canBeAttacked() {
        for (RSPlayer player : Players.getAll()) {
            if (player != null) {
                String name = player.getName();
                if (name != null && !name.equals(Player07.getName()) && isPlayerAttackable(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Crosses the wilderness ditch.
     *
     * This method will only cross the ditch if it is on the screen.
     *
     * @return True if the ditch was crossed successfully, false otherwise.
     */
    public static boolean crossDitch() {
        RSObject ditch = Objects07.getObject("Wilderness Ditch", 10);
        if (ditch != null && ditch.isOnScreen()) {
            final boolean north = isNorthOfDitch();
            if (Clicking.click("Cross", ditch) && Timing.waitCrosshair(75) == 2) {
                return Waiting.waitMoveCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return (north != isNorthOfDitch());
                    }
                }, 2000);
            }
        }
        return false;
    }
}
