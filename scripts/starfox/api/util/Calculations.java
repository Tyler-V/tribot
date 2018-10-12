package scripts.starfox.api.util;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import java.awt.*;

/**
 * The Calculations class contains helper methods for doing simple arithmetic calculations.
 *
 * @author Nolan
 */
public class Calculations {

    private static final double HOUR = 3600000D;

    /**
     * Gets the absolute value difference between the two specified integers.
     * This method will evaluate the two values as |a - b|.
     *
     * @param a The first integer.
     * @param b The second integer.
     * @return The absolute value.
     */
    public static int getAbsDifference(int a, int b) {
        return (Math.abs(a - b));
    }

    /**
     * Gets the per hour value for the specified value.
     *
     * @param value     The value to evaluate.
     * @param startTime The time in milliseconds when the tracking of the value started.
     * @return The per hour value.
     */
    public static long getPerHour(int value, long startTime) {
        if (Timing.currentTimeMillis() == startTime) {
            return 0;
        }
        return (long) (value * HOUR / (Timing.currentTimeMillis() - startTime));
    }

    /**
     * Gets the amount of time (in milliseconds) it will take to reach the next level in the specified skill.
     *
     * @param skill     The skill.
     * @param expGained How much exp you've gained.
     * @param startTime The time at which you started gaining exp.
     * @return The amount of time (in milliseconds) until the next level.
     */
    public static long getTimeToLevel(SKILLS skill, int expGained, long startTime) {
        return (long) (((double) Skills.getXPToNextLevel(skill) / (double) getPerHour(expGained, startTime)) * HOUR);
    }

    /**
     * Rounds the specified number using the specified place.
     *
     * @param i     The number.
     * @param place The place being rounded to.
     * @return The formatted number.
     */
    public static int round(int i, int place) {
        place = (int) Math.pow(10, place);
        return i < place ? i : (i / place) * place;
    }

    /**
     * Rounds the specified int ({@code i}) to a value that is a multiple of the second specified int ({@code number}). If i < number, then 0 is returned.
     *
     * @param i      The number being rounded.
     * @param number The returned value must be a multiple of this number.
     * @return The a value that is a multiple of the second specified int, or 0 if i < number.
     */
    public static int multipleOf(int i, int number) {
        return i / number * number;
    }

    /**
     * Returns the distance between the points defined by Point(x1, y1) and Point(x2, y2).
     *
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The distance between the points defined by Point(x1, y1) and Point(x2, y2).
     */
    public static double distanceTo(int x1, int y1, int x2, int y2) {
        return General.distanceTo(new Point(x1, y1), new Point(x2, y2));
    }

    /**
     * Returns the distance between the points defined by p1 and p2.
     *
     * @param p1 The first Point.
     * @param p2 the second Point.
     * @return The distance between the points defined by p1 and p2.
     */
    public static double distanceTo(Point p1, Point p2) {
        return distanceTo(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Checks if the two specified points are within the specified range of each other using their x and y coordinates.
     * This method does NOT use the distance formula.
     *
     * @param p1    The first point.
     * @param p2    The second point.
     * @param range The range.
     * @return True if the two specified points are within the specified range of each other, false otherwise.
     */
    public static boolean isPointWithinRange(Point p1, Point p2, int range) {
        return p1 != null && p2 != null
                && Math.abs(p1.x - p2.x) <= range && Math.abs(p1.y - p2.y) <= range;
    }
}
