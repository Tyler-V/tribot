package scripts.starfox.api;

import org.tribot.api.General;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The Trial class is used to manage trail versions of applications.
 *
 * @author Nolan
 */
public class Trial {

    /**
     * A list of names that can by-pass the trial time.
     */
    private static List<String> whiteList;

    /**
     * The amount of time the trial allows the application to run for in minutes.
     */
    private static int trialTime;

    /**
     * Initializes the trial.
     */
    static {
        Trial.whiteList = new ArrayList<>();
        Trial.trialTime = 0;
    }

    /**
     * Sets the trial white list.
     *
     * @param list The list of names.
     */
    public static void setWhiteList(String... list) {
        Trial.whiteList = Arrays.asList(list);
    }

    /**
     * Sets the trial time.
     *
     * @param trialTime The time (in minutes).
     */
    public static void setTrialTime(int trialTime) {
        Trial.trialTime = trialTime;
    }

    /**
     * Sets up the trial with the specified trial time and white-list.
     *
     * @param trialTime The trial time (in minutes).
     * @param whiteList The list of names on the white-list.
     */
    public static void setup(int trialTime, String... whiteList) {
        setTrialTime(trialTime);
        setWhiteList(whiteList);
    }

    /**
     * Checks to see if the user is white listed.
     *
     * @return True if the user is white listed, false otherwise.
     */
    public static boolean isWhiteListed() {
        return Trial.whiteList.contains(General.getTRiBotUsername());
    }

    /**
     * Checks to see if the trial time is up.
     *
     * @param startTime The time the trial was started.
     * @return True if the trial time is up, false otherwise.
     */
    public static boolean isTimeUp(long startTime) {
        if (Trial.trialTime == 0 || startTime == 0 || isWhiteListed()) {
            return false;
        }
        return TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime) >= Trial.trialTime;
    }
}
