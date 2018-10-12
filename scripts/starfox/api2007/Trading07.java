package scripts.starfox.api2007;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Players;
import org.tribot.api2007.Trading;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.util.ThreadSettings;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Strings;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.chatting.Chat;

/**
 *
 * @author Spencer
 */
public class Trading07 {

    /**
     * Checks to see if a trade window is open (either the initial or final screen).
     *
     * @return True if a trade window is open, false otherwise.
     */
    public static boolean isOpen() {
        return Trading.getWindowState() != null;
    }

    /**
     * Checks to see if the first trade screen is open.
     *
     * @return True if the first trade screen is open, false otherwise.
     */
    public static boolean isFirst() {
        return Trading.getWindowState() == Trading.WINDOW_STATE.FIRST_WINDOW;
    }

    /**
     * Checks to see if the second trade screen is open.
     *
     * @return True if the second trade screen is open, false otherwise.
     */
    public static boolean isSecond() {
        return Trading.getWindowState() == Trading.WINDOW_STATE.SECOND_WINDOW;
    }

    /**
     * Gets the name of the player that you are currently trading with (using regular spaces).
     *
     * @return The name of the player that you are currently trading with (using regular spaces), or null if you are not trading with anyone.
     */
    public static String getName() {
        return Strings.fixRSN(Trading.getOpponentName());
    }

    /**
     * Checks to see if there are items offered in the trade screen.
     *
     * @param other True if the other player's trade screen is being checked, false if your own trade screen is being checked.
     * @return True if there are offered items in the specified trade screen, false otherwise.
     */
    public static boolean hasOfferedItems(boolean other) {
        return Trading.getOfferedItems(other).length > 0;
    }

    /**
     * Checks to see if you are trading with a player whose RSN matches one of the specified RSNs.
     *
     * The matching is done using a standard equalsIgnoreCase check.
     *
     * @param rsns The RSNs being checked.
     * @return True if you are trading with a player whose RSN matches one of the specified RSNs, false otherwise.
     */
    public static boolean isTradingWith(String... rsns) {
        if (rsns != null) {
            for (String rsn : rsns) {
                if (rsn != null && rsn.equalsIgnoreCase(getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Accepts a trade request from any of the specified RSNs, or the most recent trade if no RSNs are specified.
     *
     * This method accepts a trade REQUEST, not the trade itself. The difference between this method and {@link #initiatePlayer(boolean, String)} is that this
     * method will first try
     * to click the trade message that was received, THEN click the player.
     *
     * If a trade window is already open, this method returns the same as {@link #isTradingWith(String...) isTradingWith(rsns)}.
     *
     * If the player has not previously received a trade message from any of the specified RSNs, this method will simply right-click trade the first player in
     * the array of RSNs. If
     * no players are in range, this method does nothing and returns false.
     *
     * If the array of RSNs is empty and no trade requests have been received by players who are within range, this method does nothing and returns false.
     *
     * NOTE: The specified RSNs MUST be in fixed format in order for this method to work properly.
     *
     * @param closeOpen True if the trade should be closed if your player is trading with the wrong person, false otherwise.
     * @param rsns      An array of RSNs that must be matched in order for the trade request to be accepted.
     * @see #initiatePlayer(boolean, String)
     * @return True if the trade request was successfully accepted, false otherwise.
     */
    public static boolean initiate(boolean closeOpen, String... rsns) {
        if (!initiateMessage(rsns)) {
            for (String rsn : rsns) {
                if (initiatePlayer(closeOpen, rsn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the trade screen of the specified player is empty or not.
     *
     * @param other True if the other player's trade screen is to be checked, false if your own is to be checked.
     * @return True if the trade screen of the specified player is empty, false otherwise.
     */
    public static boolean isEmpty(boolean other) {
        return Trading.getOfferedItems(other).length == 0;
    }

    /**
     * Removes all of your currently offered items from the trade screen. Will return false if the first trade screen is not open.
     *
     * @return True if your trade screen is empty at the end of execution, false otherwise.
     */
    public static boolean removeAll() {
        if (!isFirst()) {
            return false;
        }
        for (RSItem item : Trading.getOfferedItems(false)) {
            if (!Trading.remove(item, 0)) {
                return false;
            }
        }
        Client.sleep(100);
        return isEmpty(false);
    }

    /**
     * Initiates a trade with another player by right-clicking the trade option on the player.
     *
     * If a trade window is already open with another player, it might be closed depending on the specified closeOpen boolean. If the value is set to true, this
     * method will close
     * the open window, and attempt to trade the appropriate player. If the value is set to false, the method will simply return false.
     *
     * If a trade window is already open with the specified player, this method instantly returns true without taking any action.
     *
     * NOTE: This method should only be used when the trade request is being sent. If a trade request is being accepted, use
     * {@link #initiate(boolean, String...)} instead.
     *
     * @param closeOpen Whether or not the currently open trade window (if there is one) should be closed or not.
     * @param rsn       The rsn of the player that is to be traded.
     * @see #initiate(boolean, String...)
     * @return true if a trade window is open with the specified player at the end of the method, false otherwise.
     */
    public static boolean initiatePlayer(boolean closeOpen, String rsn) {
        if (isOpen()) {
            if (isTradingWith(rsn)) {
                return true;
            } else {
                if (closeOpen) {
                    Trading.close();
                    Client.sleep(200);
                } else {
                    return false;
                }
            }
        }
        final String tradeOption = "Trade with " + rsn;
        RSPlayer[] players = Players.find(rsn);
        if (players.length == 0) {
            Client.println("No Players with rsn: " + rsn);
            return false;
        }
        final RSPlayer player = players[0];
        if (!player.isOnScreen()) {
            Camera.turnToTile(player);
        }
        if (!initTrade(player, tradeOption)) {
            Camera.setCameraAngle(General.random(50, 100));
            int cameraRotation = Camera.getCameraRotation();
            do {
                cameraRotation += General.random(-45, 45);
            } while (cameraRotation > 0 && cameraRotation < 360);
            Camera.setCameraRotation(Camera.getCameraRotation() + General.random(-30, 30));
            return initTrade(player, tradeOption);
        } else {
            return true;
        }
    }

    private static boolean initiateMessage(String... rsns) {
        if (isOpen()) {
            return isTradingWith(rsns);
        }
        RSInterfaceChild chat = Chat.getChatBox();
        RSInterfaceComponent component = null;
        ThreadSettings.get().setClickingAPIUseDynamic(true);
        Timer timeout = new Timer(3500);
        timeout.start();
        boolean clicked;
        do {
            for (int i = chat.getChildren().length - 1; i >= 0 && component == null; i--) {
                for (String rsn : rsns) {
                    final String tradeMessage = rsn + " wishes to trade with you.";
                    if (chat.getChildren()[i].getText().toLowerCase().contains(tradeMessage)) {
                        component = chat.getChildren()[i];
                    }
                }
            }
            clicked = false;
            if (component != null && !Trading07.isOpen()) {
                clicked = Clicking.click(component);
                General.sleep(500, 1000);
            }
            General.sleep(25, 150);
        } while (!clicked && !timeout.timedOut() && !isOpen());
        if (timeout.timedOut()) {
            return false;
        } else {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return isOpen();
                }
            }, 2500);
        }
    }

    private static boolean initTrade(final RSPlayer player, final String option) {
        final Condition condition = new Condition() {
            @Override
            public boolean active() {
                Client.sleep(200);
                return isOpen();
            }
        };
        return clickPlayer(player, option, condition, 3500);
    }

    //Should probably move this to a "Clicking07" class, or something of the like.
    private static boolean clickPlayer(final RSPlayer player, final String option, final Condition condition, final long timeout) {
        if (player != null) {
            boolean clicked = false;
            //Attempts to click 4 times
            for (int i = 0; i < 4 && clicked == false; i++) {
                clicked = Clicking.click(option, player);
                if (!clicked) {
                    Client.sleep(2000);
                }
            }
            if (!clicked) {
                Client.println("Could not click player.");
            }
            return Timing.waitCondition(condition, timeout);
        } else {
            return false;
        }
    }
}
