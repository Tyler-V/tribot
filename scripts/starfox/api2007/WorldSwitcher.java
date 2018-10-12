package scripts.starfox.api2007;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.types.RSInterface;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.login.Login07;

import java.awt.*;
import java.util.Arrays;

/**
 * @author erickho123
 */

public class WorldSwitcher {

    /**
     * A boolean to keep track of whether or not we are switching worlds.
     */
    public static boolean isSwitchingWorlds = false;

    /**
     * The world switching interface master index.
     */
    private static final int WORLD_SWITCHING_MASTER_INDEX = 69;

    /**
     * The world switching close button child index.
     */
    private static final int WORLD_SWITCHING_CLOSE_BUTTON_INDEX = 3;

    /**
     * The world list child index on the world switching interface.
     */
    private static final int WORLD_SWITCHING_WORLD_LIST_INDEX = 7;

    /**
     * The logout interface master index.
     */
    private static final int LOGOUT_INTERFACE_MASTER_INDEX = 182;

    /**
     * The world switching button child index.
     */
    private static final int WORLD_SWITCHING_BUTTON_INDEX = 1;

    /**
     * The NPC chat master index for the confirmation interface (only appears if you haven't disabled it).
     */
    private static final int NPC_CHAT_MASTER_INDEX = 219;

    /**
     * The NPC chat child index for the title.
     */
    private static final int NPC_CHAT_TITLE_CHILD_ID = 0;

    /**
     * The maximum amount of time before the switchWorld() method times out.
     */
    private static final long SWITCH_TIMEOUT = 30000;

    /**
     * The child index of the logout button on the world switching interface.
     */
    private static final int LOGOUT_BUTTON_INDEX = 19;

    /**
     * The number of components between each world interface.
     */
    private static final int WORLD_COMPONENT_SKIP_AMOUNT = 6;

    /**
     * The rectangle containing the view of the currently visible worlds.
     */
    private static final Rectangle WORLD_LIST_RECTANGLE = new Rectangle(547, 229, 174, 204);

    /**
     * An array containing all of the worlds that do not exist between the first and last worlds.
     */
    private static final int[] NON_EXISTENT_WORLDS = new int[]{307, 315, 323,
            324, 331, 332, 339, 340, 347, 348, 352, 355, 356, 363, 364, 371,
            372, 379, 380, 387, 388, 389, 390, 391, 392};

    /**
     * An array containing all of the P2P worlds.
     */
    private static final int[] P2P_WORLDS = {2, 3, 4, 5, 6, 9, 10, 11, 12, 13,
            14, 17, 18, 19, 20, 21, 22, 27, 28, 29, 30, 33, 34, 36, 38, 41, 42,
            43, 44, 45, 46, 49, 50, 51, 52, 53, 54, 57, 58, 59, 60, 61, 62, 65,
            66, 67, 68, 69, 70, 73, 74, 75, 76, 77, 78, 86};

    /**
     * An array containing all of the F2P worlds.
     */
    private static final int[] F2P_WORLDS = {1, 8, 16, 26, 35, 81, 82, 83, 84,
            93, 94};

    /**
     * An array containing all of the PVP worlds.
     */
    private static final int[] PVP_WORLDS = {25, 37};

    /**
     * Gets the world switching button interface.
     *
     * @return The world switching button interface.
     */
    private static RSInterface getWorldSwitchingButtonInterface() {
        return Interfaces07.get(LOGOUT_INTERFACE_MASTER_INDEX, WORLD_SWITCHING_BUTTON_INDEX);
    }

    /**
     * Gets the interface for the specified world.
     *
     * @param world The world to get the interface of.
     * @return The interface for the specifled world.
     * Null if no interface could be found.
     */
    private static RSInterface getWorldInterface(int world) {
        return Interfaces07.get(WORLD_SWITCHING_MASTER_INDEX, WORLD_SWITCHING_WORLD_LIST_INDEX,
                getWorldInterfaceIndex(world));
    }

    /**
     * Gets the index of the specified world in the world switching list.
     *
     * @param world The world to get the index of.
     * @return The index of the specified world.
     */
    private static int getWorldInterfaceIndex(int world) {
        return ((world - getWorldSkipDifference(world)) * WORLD_COMPONENT_SKIP_AMOUNT) - WORLD_COMPONENT_SKIP_AMOUNT;
    }

    /**
     * Checks to see if the worlds need to be sorted.
     * The worlds need to be sorted if they are not sorted by lowest to highest.
     *
     * @return True if the worlds need to be sorted, false otherwise.
     */
    private static boolean needToSortWorlds() {
        RSInterface child = Interfaces07.get(WORLD_SWITCHING_MASTER_INDEX, WORLD_SWITCHING_WORLD_LIST_INDEX);
        if (child == null)
            return false;

        RSInterface worldOne = child.getChild(getWorldInterfaceIndex(1) + 2);
        if (worldOne == null)
            return false;

        String worldOneText = worldOne.getText();

        RSInterface worldTwo = child.getChild(getWorldInterfaceIndex(2) + 2);
        if (worldTwo == null)
            return false;

        String worldTwoText = worldTwo.getText();

        return worldOneText != null && !worldOneText.equals("1")
                || worldTwoText != null && !worldTwoText.equals("2");
    }

    /**
     * Gets the number of worlds that do not exist between the first world and the specified world.
     *
     * @param world The world to calculate from.
     * @return The number of worlds that do not exist between the first world and the specified world.
     */
    private static int getWorldSkipDifference(int world) {
        int diff = 0;
        for (int i : NON_EXISTENT_WORLDS) {
            if (world > i - 300)
                diff++;
        }
        return diff;
    }

    /**
     * Checks to see if the confirmation chat world is the same as the specified world.
     *
     * @param world The world to check for.
     * @return True if the confirmation chat world is that same as the specified world, false otherwise.
     */
    private static boolean isChatWorld(int world) {
        RSInterface title = Interfaces07.get(NPC_CHAT_MASTER_INDEX, NPC_CHAT_TITLE_CHILD_ID);
        if (title == null)
            return false;

        RSInterface child = title.getChild(0);
        if (child == null)
            return false;

        String text = child.getText();
        if (text == null)
            return false;
        try {
            String number = text.substring(16, world > 9 ? 18 : 17);
            return Integer.parseInt(number) == world;
        } catch (Exception e) {
            Client.println("Failed to parse world number from chat interface.");
        }
        return false;
    }

    /**
     * Switches to the specified world using the world switching interface.
     *
     * @param world The world to switch to.
     * @return True if the player successfully switched to the specified world, false otherwise.
     */
    public static boolean switchWorld(final int world) {
        Timer timer = new Timer(15000);
        timer.start();
        isSwitchingWorlds = true;
        Login07.pause();

        while (!timer.timedOut() && Game.getGameState() != 10
                && (Game.getCurrentWorld() != world + 300 || Game.getGameState() != 30) && !Combat.isUnderAttack()) {
            String[] options = NPCChat.getOptions();
            if (options != null && Arrays.asList(options).contains("Yes.") && isChatWorld(world)) {
                Keyboard.typeString("2");
                Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        int state = Game.getGameState();
                        return state == 45 || state == 10;
                    }
                }, General.random(1000, 2000));
            } else if (Game.getGameState() == 45 || Game.getGameState() == 25) {
                if (Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        int state = Game.getGameState();
                        return Game.getCurrentWorld() == world + 300
                                && (state == 30 || state == 10);
                    }
                }, 7000)) {
                    isSwitchingWorlds = false;
                    Login07.resume();
                    return true;
                }
            } else if (!isOpen()) {
                open();
            } else if (needToSortWorlds()) {
                sortWorlds();
            } else if (needToScroll(world)) {
                scroll(world);
            } else {
                clickWorld(world);
            }
            General.sleep(50, 100);
        }
        isSwitchingWorlds = false;
        Login07.resume();
        return Game.getCurrentWorld() == world + 300
                && (Game.getGameState() == 30 || Game.getGameState() == 10)
                && !isOpen();
    }

    /**
     * Switches to a random world.
     * The world that is switched to will be the same world type of the world that your player is currently in.
     * In example: If your player is in a P2P world, the world that we will switch to will also be P2P.
     *
     * @return True if the player switched worlds successfully, false otherwise.
     */
    public static boolean switchToRandomWorld() {
        if (ArrayUtil.contains(Game.getCurrentWorld() - 300, F2P_WORLDS))
            return switchWorld(F2P_WORLDS[General.random(0, F2P_WORLDS.length - 1)]);
        return switchWorld(P2P_WORLDS[General.random(0, P2P_WORLDS.length - 1)]);
    }

    /**
     * Sorts the worlds in order from lowest to highest.
     *
     * @return True if the worlds were sorted successfully, false otherwise.
     */
    public static boolean sortWorlds() {
        if (!needToSortWorlds()) {
            return true;
        }
        if (Clicking.click(Interfaces07.get(WORLD_SWITCHING_MASTER_INDEX, 10))) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return !needToSortWorlds();
                }
            }, 2000);
        }
        return false;
    }

    private static boolean needToScrollUp(int world) {
        RSInterface child = getWorldInterface(world);
        if (child == null)
            return false;

        Rectangle r = child.getAbsoluteBounds();
        if (r == null)
            return false;
        int y = (int) r.getY();
        return WORLD_LIST_RECTANGLE.getY() - y > 1;
    }

    private static boolean scrollUp(int world) {
        int ticks = 0;

        RSInterface child = getWorldInterface(world);
        if (child == null)
            return false;

        Rectangle r = child.getAbsoluteBounds();
        if (r == null)
            return false;
        int y = (int) r.getY();

        double diff1 = WORLD_LIST_RECTANGLE.getY() - y;

        while (diff1 > 1) {
            diff1 -= 45;
            ticks++;
        }

        Mouse.scroll(true, ticks + General.random(1, 3));
        r = child.getAbsoluteBounds();
        if (r == null)
            return false;
        y = (int) r.getY();
        return (int) WORLD_LIST_RECTANGLE.getY() - y > 1;
    }

    private static boolean needToScrollDown(int world) {
        RSInterface child = getWorldInterface(world);
        if (child == null)
            return false;

        Rectangle r = child.getAbsoluteBounds();
        if (r == null)
            return false;
        int y = (int) r.getY();
        return WORLD_LIST_RECTANGLE.getY() - y < -195;
    }

    private static boolean scrollDown(int world) {
        int ticks = 0;

        RSInterface child = getWorldInterface(world);
        if (child == null)
            return false;

        Rectangle r = child.getAbsoluteBounds();
        if (r == null)
            return false;
        int y = (int) r.getY();
        double diff1 = WORLD_LIST_RECTANGLE.getY() - y;

        while (diff1 < -195) {
            diff1 += 45;
            ticks++;
        }

        Mouse.scroll(false, ticks + General.random(1, 3));
        r = child.getAbsoluteBounds();
        if (r == null)
            return false;
        y = (int) r.getY();
        return (int) WORLD_LIST_RECTANGLE.getY() - y < -195;
    }

    public static boolean needToScroll(int world) {
        return needToScrollUp(world) || needToScrollDown(world);
    }

    public static boolean scroll(int world) {
        if (!WORLD_LIST_RECTANGLE.contains(Mouse.getPos())) {
            Mouse.moveBox(WORLD_LIST_RECTANGLE);
        } else if (needToScrollDown(world)) {
            return scrollDown(world);
        } else if (needToScrollUp(world)) {
            return scrollUp(world);
        }
        return false;
    }

    private static boolean clickWorld(final int world) {
        if (Clicking.click(getWorldInterface(world))) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return isChatWorld(world) || Game.getGameState() == 45;
                }
            }, 2000);
        }
        return false;
    }

    /**
     * Checks to see if the world switching interface is selected.
     *
     * @return True if it is selected, false otherwise.
     */
    public static boolean isSelected() {
        return Interfaces.isInterfaceValid(WORLD_SWITCHING_MASTER_INDEX);
    }

    /**
     * Checks to see if the world switching interface is open.
     *
     * @return True if it is open, false otherwise.
     */
    public static boolean isOpen() {
        return isSelected() && TABS.LOGOUT.isOpen();
    }

    /**
     * Opens the world switching interface if it is not already open.
     *
     * @return True if the world switching interface was opened or was already open at the time of this method call,
     * false otherwise.
     */
    public static boolean open() {
        if (TABS.LOGOUT.open()) {
            if (!isSelected()) {
                if (Clicking.click(getWorldSwitchingButtonInterface()))
                    return Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return isOpen();
                        }
                    }, 2000);
            }
        }

        return isOpen();
    }

    /**
     * Closes the world switching interface if it is not already closed.
     *
     * @return True if the world switching interface was closed or was already closed at the time of this method call,
     * false otherwise.
     */
    public static boolean close() {
        if (TABS.LOGOUT.open()) {
            if (Clicking.click(Interfaces07.get(WORLD_SWITCHING_MASTER_INDEX, WORLD_SWITCHING_CLOSE_BUTTON_INDEX))) {
                return Timing.waitCondition(new Condition() {
                    @Override
                    public boolean active() {
                        return !isOpen();
                    }
                }, 2000);
            }
        }
        return false;
    }

    /**
     * Logs out from the world switching interface.
     *
     * @return True if the play logged out successfully, false otherwise.
     */
    public static boolean logout() {
        if (Clicking.click(Interfaces.get(WORLD_SWITCHING_MASTER_INDEX, LOGOUT_BUTTON_INDEX))) {
            return Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    return Game.getGameState() == 10;
                }
            }, 2000);
        }
        return false;
    }

}
