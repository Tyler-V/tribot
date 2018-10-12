package scripts.starfox.api2007;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Game;
import org.tribot.api2007.Login;
import org.tribot.api2007.Screen;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.ArrayUtil;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.login.Login07;

import java.awt.*;

/**
 * The WorldHopping class is a utility class that can be used to hop worlds.
 *
 * @author Nolan
 */
public final class WorldHopping {

    /**
     * An array of worlds that are generally considered to be "bad worlds". Bad worlds can be over-populated, full, dangerous, or any other undesirable trait.
     */
    public static final int[] BAD_WORLDS = {301, 381, 382, 383, 384, 393, 394, 308, 316, 326, 335, 325, 337, 329, 302, 330, 351};

    /**
     * An array of the free to play worlds.
     */
    public static final int[] F2P_WORLDS = {301, 308, 316, 326, 335, 381, 382, 383, 384, 393, 394};

    /**
     * An array of the PVP worlds.
     */
    public static final int[] PVP_WORLDS = {325, 337};

    /**
     * An array of the worlds between existing worlds that do not exist.
     */
    private static final int[] NON_EXISTENT_WORLDS = {307, 315, 323, 324, 331, 332, 339, 340, 347, 348, 355, 356, 363, 364, 371, 372, 379, 380, 385, 386, 387,
        388, 389, 390, 391, 392};

    /**
     * The minimum world.
     */
    private static final int MIN_WORLD = 301;

    /**
     * The maximum world.
     */
    private static final int MAX_WORLD = 394;

    /**
     * The width in pixels of a world button.
     */
    private final static int WORLD_PIXEL_SIZE_X = 86;

    /**
     * The height in pixels of a world button.
     */
    private final static int WORLD_PIXEL_SIZE_Y = 17;

    /**
     * The amount of columns in the world grid.
     */
    private final static int COLUMNS = 4;

    /**
     * The amount of rows in the world grid.
     */
    private final static int ROWS = 17;

    /**
     * A rectangle representing the area where the switch world button is located.
     */
    private final static Rectangle SWITCH_WORLD_AREA = new Rectangle(5, 463, 100, 35);

    /**
     * A rectangle representing the area where the configure settings button is located.
     */
    private final static Rectangle CONFIGURE_SETTINGS_AREA = new Rectangle(295, 4, 65, 14);

    /**
     * The color of the green arrow.
     */
    private final static Color WORLD_GREEN_ARROW_COLOUR = new Color(47, 130, 43);

    /**
     * The color of the red arrow.
     */
    private final static Color WORLD_RED_ARROW_COLOUR = new Color(172, 12, 4);

    /**
     * Prevent instantiation of this class.
     */
    private WorldHopping() {
    }

    /**
     * Gets the area of the specified world.
     *
     * @param world The world.
     * @return The area. Null if the world does not exist.
     */
    private static Rectangle getWorldArea(int world) {
        if (!doesWorldExist(world)) {
            return null;
        }

        int diff = 0;
        for (int i : NON_EXISTENT_WORLDS) {
            if (world > i) {
                diff++; //Number of worlds below the specified world that don't exist
            }
        }

        world -= 300;

        int column = (world - (diff + 1)) / ROWS;
        int row = (world - (diff + 1)) % ROWS;
        int x = 199 + (column * 93);
        int y = 61 + (row * 24);
        return new Rectangle(x, y, WORLD_PIXEL_SIZE_X, WORLD_PIXEL_SIZE_Y);
    }

    /**
     * Checks to see if the world settings are misconfigured.
     *
     * World settings are misconfigured if the worlds are not listed in numerical order regardless of type.
     *
     * @return True if the world settings are misconfigured, false otherwise.
     */
    private static boolean hasMisconfiguredSettings() {
        return Login07.isLoggedOut() && Screen.getColorAt(301, 9).equals(WORLD_RED_ARROW_COLOUR);
    }

    /**
     * Checks to see if the game is at the login screen.
     *
     * @return True if it is at the login screen, false otherwise.
     */
    private static boolean isAtLoginScreen() {
        return Login07.isLoggedOut() && !isAtWorldHopScreen();
    }

    /**
     * Checks to see if the game is at the "Select a world" screen.
     *
     * @return True if the game is at the select a world screen, false otherwise.
     */
    public static boolean isAtWorldHopScreen() {
        return Login07.isLoggedOut() && Screen.getColorAt(93, 278).equals(new Color(0, 0, 0));
    }

    /**
     * Checks to see if the specified world is a F2P world.
     *
     * @param world The world.
     * @return True if it is a F2P world, false otherwise.
     */
    public static boolean isF2PWorld(int world) {
        return ArrayUtil.contains(world, F2P_WORLDS);
    }

    /**
     * Checks to see if the specified world is a PVP world.
     *
     * @param world The world.
     * @return True if it is a PVP world, false otherwise.
     */
    public static boolean isPVPWorld(int world) {
        return ArrayUtil.contains(world, PVP_WORLDS);
    }

    /**
     * Checks to see if the specified world exists.
     *
     * @param world The world.
     * @return True if it exists, false otherwise.
     */
    public static boolean doesWorldExist(int world) {
        return world >= MIN_WORLD && world <= MAX_WORLD && !ArrayUtil.contains(world, NON_EXISTENT_WORLDS);
    }

    /**
     * Gets the next existing world above the current world.
     *
     * @param excludedWorlds Worlds to exclude. The world returned will not be any of the specified worlds.
     * @return The next world.
     */
    public static int getNextWorld(int... excludedWorlds) {
        int world = Game.getCurrentWorld();
        do {
            world++;
            if (world > MAX_WORLD) {
                world = MIN_WORLD;
            }
        } while (ArrayUtil.contains(world, excludedWorlds) || !doesWorldExist(world));
        return world;
    }

    /**
     * Gets a random world.
     *
     * @param excludedWorlds Worlds to exclude. The random world returned will not be any of the specified worlds.
     * @return A random world.
     */
    public static int getRandomWorld(int... excludedWorlds) {
        int randomWorld;
        do {
            randomWorld = General.random(MIN_WORLD, MAX_WORLD);
        } while (randomWorld == Game.getCurrentWorld() || ArrayUtil.contains(randomWorld, excludedWorlds) || !doesWorldExist(randomWorld));
        return randomWorld;
    }

    /**
     * Gets a random f2p world.
     *
     * @return A random f2p world.
     */
    public static int getF2PWorld() {
        return F2P_WORLDS[General.random(0, F2P_WORLDS.length - 1)];
    }

    /**
     * Gets a random PVP world.
     *
     * @return A random PVP world.
     */
    public static int getPVPWorld() {
        return PVP_WORLDS[General.random(0, PVP_WORLDS.length - 1)];
    }

    /**
     * Hops to a random world without logging in.
     *
     * @param quick Whether or not to use mouse hopping.
     * @return True if the hop was successful, false otherwise.
     */
    public static boolean changeRandomWorld(boolean quick) {
        return changeWorld(getRandomWorld(), quick);
    }

    /**
     * Changes the world to be the specified world.
     *
     * @param world The world.
     * @param quick Whether or not to use mouse hopping.
     * @return True if the world was changed successfully, false otherwise.
     */
    public static boolean changeWorld(final int world, boolean quick) {
        if (!doesWorldExist(world)) {
            Client.println("World " + world + " does not exist.");
            return false;
        }
        if (Login07.isLoggedIn()) {
            Client.println("Cannot change worlds when game is logged in.");
            return false;
        }
        Timer timer = new Timer(10000);
        timer.start();
        while (!timer.timedOut()) {
            if (Game.getCurrentWorld() == world) {
                return true;
            } else {
                if (isAtWorldHopScreen()) {
                    if (hasMisconfiguredSettings()) {
                        Client.println("Changing world settings.");
                        if (quick) {
                            Mouse07.hopClick(Screen07.getRandomPoint(CONFIGURE_SETTINGS_AREA), Mouse07.LEFT_BUTTON, 75);
                        } else {
                            Mouse07.moveClick(Screen07.getRandomPoint(CONFIGURE_SETTINGS_AREA), Mouse07.LEFT_BUTTON, 75);
                        }
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                Client.sleep(100);
                                return !hasMisconfiguredSettings();
                            }
                        }, 1500);
                    } else {
                        Client.println("Clicking world " + world + ".");
                        if (quick) {
                            Mouse07.hopClick(Screen07.getRandomPoint(getWorldArea(world)), Mouse07.LEFT_BUTTON, 75);
                        } else {
                            Mouse07.moveClick(Screen07.getRandomPoint(getWorldArea(world)), Mouse07.LEFT_BUTTON, 75);
                        }
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                Client.sleep(100);
                                return isAtLoginScreen() && Game.getCurrentWorld() == world;
                            }
                        }, 1500);
                    }
                } else {
                    Client.println("Clicking world switch button.");
                    if (quick) {
                        Mouse07.hopClick(Screen07.getRandomPoint(SWITCH_WORLD_AREA), Mouse07.LEFT_BUTTON, 75);
                    } else {
                        Mouse07.moveClick(Screen07.getRandomPoint(SWITCH_WORLD_AREA), Mouse07.LEFT_BUTTON, 75);
                    }
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            Client.sleep(100);
                            return isAtWorldHopScreen();
                        }
                    }, 1500);
                }
            }
            Client.sleep(50);
        }
        return false;
    }

    /**
     * Hops to the specified world.
     *
     * This method will log out if the game is logged in and the player is not already in the specified world. This method will also log in once the world has
     * been changed successfully if the player is not logged in already.
     *
     * @param world The world to hop to.
     * @param quick Whether or not to use mouse hopping.
     * @return True if hopping was successful, false otherwise.
     */
    public static boolean hop(int world, boolean quick) {
        if (!doesWorldExist(world)) {
            Client.println("World " + world + " does not exist.");
            return false;
        }
        Login07.pause();
        Timer timer = new Timer(30000);
        timer.start();
        while (!timer.timedOut()) {
            if (Login07.isLoggedIn()) {
                if (Game.getCurrentWorld() == world) {
                    Client.println("World hop successful.");
                    Login07.resume();
                    return true;
                } else {
                    Client.println("Logging out.");
                    Login.logout();
                }
            } else {
                if (isAtLoginScreen() && Game.getCurrentWorld() == world) {
                    Client.println("Logging in.");
                    Login.login();
                } else {
                    changeWorld(world, quick);
                }
            }
            Client.sleep(25);
        }
        return false;
    }

    /**
     * Hops to the next world.
     *
     * @param quick          Whether or not to use mouse hopping.
     * @param excludedWorlds Worlds to exclude. This method will skip these worlds.
     * @return True if hopping was successful, false otherwise.
     */
    public static boolean hopNext(boolean quick, int... excludedWorlds) {
        return hop(getNextWorld(excludedWorlds), quick);
    }

    /**
     * Hops to a random world.
     *
     * @param quick          Whether or not to use mouse hopping.
     * @param excludedWorlds Worlds to exclude. This method will skip these worlds.
     * @return True if hopping was successful, false otherwise.
     */
    public static boolean hopRandom(boolean quick, int... excludedWorlds) {
        return hop(getRandomWorld(excludedWorlds), quick);
    }

    /**
     * Switches to a random f2p world. If the account that is hopping is already in a f2p world, this method does nothing.
     *
     * @param quick Whether or not to use mouse hopping.
     * @return True if the account is in a f2p world at the end of executing, false otherwise.
     */
    public static boolean switchF2P(boolean quick) {
        return isF2PWorld(Game.getCurrentWorld()) || changeWorld(getF2PWorld(), quick);
    }

    /**
     * Hops to a random f2p world. If the account that is hopping is already in a f2p world, this method does nothing.
     *
     * @param quick Whether or not to use mouse hopping.
     * @return True if the account is in a f2p world at the end of executing, false otherwise.
     */
    public static boolean hopF2P(boolean quick) {
        return isF2PWorld(Game.getCurrentWorld()) || hop(getF2PWorld(), quick);
    }
}
