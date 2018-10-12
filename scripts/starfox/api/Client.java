package scripts.starfox.api;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

/**
 * The Client class keeps track of information about the client and contains
 * useful methods related to the client.
 *
 * @author Nolan
 */
public class Client {

    /**
     * Checks to see if the Old-School TRiBot client is loaded.
     *
     * @return True if it is loaded, false otherwise.
     */
    public static boolean isLoaded() {
	try {
	    Player.getRSPlayer();
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    /**
     * Gets the username that is running the client.
     *
     * @return The username.
     */
    public static String getUsername() {
	return General.getTRiBotUsername();
    }

    /**
     * Puts the current thread to sleep for the specified amount of
     * milliseconds.
     *
     * @param milliseconds
     *            The amount of milliseconds.
     */
    public static void sleep(long milliseconds) {
	General.sleep(milliseconds);
    }

    /**
     * Puts the current thread to sleep for a random amount of milliseconds
     * between the specified minimum and maximum (inclusive).
     *
     * @param min
     *            The minimum amount of time.
     * @param max
     *            The maximum amount of time.
     */
    public static void sleep(int min, int max) {
	General.sleep(min, max);
    }

    /**
     * Prints the specified object to the client debug.
     *
     * @param object
     *            The object to print.
     */
    public static void println(Object object) {
	General.println(object);
    }

    /**
     * Sets default client settings. These settings are, in general, appropriate
     * for any script. The settings that are set are as follows:
     * <ol>
     * <li>Clicking API use dynamic (true)</li>
     * <li>Model clicking method (human)</li>
     * <li>Mouse speed (105)</li>
     * <li>Walking timeout (1000 milliseconds)</li>
     * </ol>
     */
    public static void setSettings() {
	ThreadSettings.get().setClickingAPIUseDynamic(true);
	ThreadSettings.get().setObjectCModelMethod(ThreadSettings.MODEL_CLICKING_METHOD.HUMAN);
	Mouse.setSpeed(105);
	Walking.setWalkingTimeout(1000);
	println("Dynamic clicking: true, Object model clicking: human, Mouse speed: 105, Walking timeout: 1000");
    }

    /**
     * Gets the script manifest of the specified class.
     *
     * @param c
     *            The class.
     * @return The script manifest. Null if no script manifest is present.
     */
    public static ScriptManifest getManifest(Class<? extends Script> c) {
	if (c == null || !c.isAnnotationPresent(ScriptManifest.class)) {
	    return null;
	}
	return c.getAnnotation(ScriptManifest.class);
    }

}
