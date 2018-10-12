package scripts.starfox.api2007;

import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;

import java.util.ArrayList;

/**
 * The LocalPlayer class provides a way to safely retrieve information about the local player.
 *
 * @author Nolan
 */
public class Player07 {

    /**
     * Gets the player's name.
     *
     * @return The player's name.
     * If no name was found, an empty string is returned.
     */
    public static String getName() {
        RSPlayer player = getPlayer();
        if (player == null) {
            return "";
        }
        String name = player.getName();
        return name == null ? "" : name;
    }

    /**
     * Gets the players current hit points as a percent between 0 and 100.
     *
     * @return The players current hit points as a percent.
     */
    public static int getHPPercent() {
        return (int) (100.0 * ((double) SKILLS.HITPOINTS.getCurrentLevel() / (double) SKILLS.HITPOINTS.getActualLevel()));
    }

    /**
     * Gets the interacting character of the player.
     *
     * @return The interacting character. Null if no such character exists.
     */
    public static RSCharacter getInteractingCharacter() {
        RSPlayer myPlayer = getPlayer();
        if (myPlayer != null) {
            return myPlayer.getInteractingCharacter();
        }
        return null;
    }

    /**
     * Gets all of the loaded players excluding the local player.
     *
     * @return All of the players in an array excluding the local player.
     */
    public static RSPlayer[] getAllButMe() {
        ArrayList<RSPlayer> players = new ArrayList<>();
        for (RSPlayer player : Players.getAll()) {
            if (player != null && !isMe(player)) {
                players.add(player);
            }
        }
        return players.toArray(new RSPlayer[players.size()]);
    }

    /**
     * Gets the player as a RSPlayer object.
     *
     * @return The player. Null if the local player is not loaded.
     */
    public static RSPlayer getPlayer() {
        return Player.getRSPlayer();
    }

    /**
     * Gets the player's position.
     *
     * @return The player's position.
     */
    public static RSTile getPosition() {
        return Player.getPosition();
    }

    /**
     * Checks to see if the player is animating.
     *
     * @return True if the player is animating, false otherwise.
     */
    public static boolean isAnimating() {
        return Player.getAnimation() != -1;
    }

    /**
     * Checks to see if the specified player is your local player.
     *
     * @param player The player to check.
     * @return True if the specified player is your local player, false otherwise.
     */
    public static boolean isMe(RSPlayer player) {
        if (player == null) {
            return false;
        }
        String playerName = player.getName();
        return playerName != null && playerName.equals(getName());
    }

    /**
     * Checks to see if the player is in the specified area.
     *
     * @param area The area.
     * @return True if the player is in the specified area, false otherwise.
     */
    public static boolean isInArea(RSArea area) {
        return area != null && area.contains(Player.getPosition());
    }

    /**
     * Checks to see if the player is in tutorial island.
     *
     * @return True if the player is in tutorial island, false otherwise.
     */
    public static boolean isInTutorialIsland() {
        return Interfaces07.isUp(371) || Skills.getXP(SKILLS.MAGIC) == 0;
    }

    /**
     * Checks to see if the player is poisoned.
     *
     * @return True if the player is poisoned, false otherwise.
     */
    public static boolean isPoisoned() {
        return Settings.get(Settings.Indexes.Player.POISONED_INDEX) > 0;
    }

    /**
     * Checks to see if the player is immune to poison.
     * A player is immune to poison if the effect of an anti-poison potion is still active.
     *
     * @return True if the player is immune to poison, false otherwise.
     */
    public static boolean isImmuneToPoison() {
        return Game.getSetting(Settings.Indexes.Player.POISONED_INDEX) < 0;
    }
}
