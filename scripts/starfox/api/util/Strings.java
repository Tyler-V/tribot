package scripts.starfox.api.util;

import java.awt.Graphics;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * The Strings class is an extension of utility methods contained within the String class.
 *
 *
 * @author Nolan
 */
public class Strings {

    /**
     * A constant representing one thousand (1,000).
     */
    private static final int K = 1000;

    /**
     * A constant representing one million (1,000,000).
     */
    private static final int M = 1000000;

    /**
     * A constant representing one billion (1,000,000,000).
     */
    private static final int B = 1000000000;

    /**
     * Capitalizes the first letter of the specified string.
     *
     *
     * @param string The string.
     * @return The string with the capital first letter.
     * Returns the original string if it has a length of 0 or less.
     */
    public static String capitalizeFirst(String string) {
        return capitalize(string, 0, 1);
    }

    /**
     * Capitalizes the specified substring within the string provided.
     *
     * @param string The string that will have its chars capitalized.
     * @param start  The starting index of the substring to capitalize.
     * @param end    The ending index of the substring to capitalize.
     * @return The string with the specified substring capitalized.
     * Returns the original string if it has a length of 0 or less.
     */
    public static String capitalize(String string, int start, int end) {
        if (string.length() < 1) {
            return string;
        }
        return string.substring(start, end).toUpperCase() + string.substring(end);
    }

    /**
     * Gets the sum of the char codes in the specified String.
     *
     * @param string The String to get the sum of char codes from.
     * @return The sum of char codes.
     */
    public static int charSum(String string) {
        int chars = 0;
        for (char c : string.toCharArray()) {
            chars += c;
        }
        return chars;
    }

    /**
     * Replaces any underscores in the specified string with spaces.
     *
     *
     * @param string The string.
     * @return A string that has its underscores replaced with spaces.
     */
    public static String replaceUnderscores(String string) {
        return string.replaceAll("_", " ");
    }

    /**
     * Formats the specified string for an enum toString.
     *
     * @param enumName The name of the enum.
     * @return The formatted string.
     */
    public static String enumToString(String enumName) {
        return capitalizeFirst(replaceUnderscores(enumName.toLowerCase()));
    }

    /**
     * Gets the length of the specified string in pixels.
     *
     * @param g The graphics used to draw the string.
     * @param s The string.
     * @return The length.
     */
    public static int getStringPixelLength(Graphics g, String s) {
        return g.getFontMetrics().stringWidth(s);
    }

    /**
     * Inserts commas (US LOCALE) into the specified integer.
     *
     * @param number The integer to insert commas into.
     * @return A string representing the specified integer with commas inserted.
     */
    public static String commas(int number) {
        return commas((long) number);
    }

    /**
     * Inserts commas (US LOCALE) into the specified double.
     *
     * @param number The double to insert commas into.
     * @return A string representing the specified double with commas inserted.
     */
    public static String commas(double number) {
        return commas((long) number);
    }

    /**
     * Inserts commas (US LOCALE) into the specified long.
     *
     * @param number The long to insert commas into.
     * @return A string representing the specified long with commas inserted.
     */
    public static String commas(long number) {
        return NumberFormat.getNumberInstance(Locale.US).format(number);
    }

    /**
     * Formats a number to be in KMB format.
     *
     * @param number    The number.
     * @param upperCase True to display the string in upper case, false for lower case.
     * @return A KMB formatted string representing the number.
     * @see #kmb(long, boolean)
     */
    public static String kmb(int number, boolean upperCase) {
        return kmb((long) number, upperCase);
    }

    /**
     * Formats a number to be in KMB format.
     *
     * @param number    The number.
     * @param upperCase True to display the string in upper case, false for lower case.
     * @return A KMB formatted string representing the number.
     * @see #kmb(long, boolean)
     */
    public static String kmb(double number, boolean upperCase) {
        return kmb((long) number, upperCase);
    }

    /**
     * Formats a number to be in KMB format.
     * KMB format is as follows:
     * <ul>
     * <li>Any number between 0 and 999 is represented as such.</li>
     * <li>Any number between 1000 and 999999 is represented with a 'k' replacing the last trailing digit of the number. e.g. 52250 would be represented as
     * 52.25k.</li>
     * <li>Any number between 1000000 and 999999999 is represented with a 'm' replacing the last four trailing digits of the number. e.g. 1220000 would be
     * represented as 1.22m.</li>
     * <li>Any number above 1000000000 is represented with a 'b' replacing the last seven trailing digits of the number. e.e. 1220000000 would be represented as
     * 1.22b</li>
     * </ul>
     * All negative numbers are treated the same as positive numbers.
     *
     * @param number    The number.
     * @param upperCase True to display the string in upper case, false for lower case.
     * @return A KMB formatted string representing the number.
     */
    public static String kmb(long number, boolean upperCase) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.DOWN);
        df.setMinimumFractionDigits(2);
        if (Math.abs(number) < K) {
            return "" + number;
        } else if (Math.abs(number) < M) {
            double num = (double) number / (double) K;
            return "" + df.format(num) + (upperCase ? "K" : "k");
        } else if (Math.abs(number) < B) {
            double num = (double) number / (double) M;
            return "" + df.format(num) + (upperCase ? "M" : "m");
        }
        double num = (double) number / (double) B;
        return "" + df.format(num) + (upperCase ? "B" : "b");
    }

    /**
     * Formats a number to be in a readable time format. The number inputted should be in milliseconds.
     * The format returned is DD:HH:MM:SS. Milliseconds are truncated and not included in the formatted display.
     * All negative numbers are treated the same as positive numbers.
     *
     * @param milliseconds The time being formatted, in milliseconds.
     * @return A KMB formatted string representing the number.
     */
    public static String timeFormat(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        long hours = minutes / 60;
        minutes %= 60;
        long days = hours / 24;
        hours %= 24;
        String formatted = "";
        String dayString = "" + days;
        if (dayString.length() == 1) {
            dayString = "0" + dayString;
        }
        formatted += dayString + ":";
        String hourString = "" + hours;
        if (hourString.length() == 1) {
            hourString = "0" + hourString;
        }
        formatted += hourString + ":";
        String minuteString = "" + minutes;
        if (minuteString.length() == 1) {
            minuteString = "0" + minuteString;
        }
        formatted += minuteString + ":";
        String secondString = "" + seconds;
        if (secondString.length() == 1) {
            secondString = "0" + secondString;
        }
        formatted += secondString;
        return formatted;
    }

    /**
     * Formats the specified amount of time in milliseconds to a readable format.
     *
     * @param ms        The amount of time in milliseconds.
     * @param upperCase True to display the string in upper case, false for lower case.
     * @return A formatted version of the time specified.
     */
    public static String msToString(long ms, boolean upperCase) {
        String msString = "";
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        long hours = minutes / 60;
        minutes %= 60;
        long days = hours / 24;
        hours %= 24;
        if (days >= 1) {
            msString += days + (days > 1 ? " " + (upperCase ? "D" : "d") + "ays, " : " " + (upperCase ? "D" : "d") + "ay, ");
        }
        if (hours >= 1) {
            msString += hours + (hours > 1 ? " " + (upperCase ? "H" : "h") + "ours, " : " " + (upperCase ? "H" : "h") + "our, ");
        }
        if (minutes >= 1) {
            msString += minutes + (minutes > 1 ? " " + (upperCase ? "M" : "m") + "inutes, " : " " + (upperCase ? "M" : "m") + "inute, ");
        }
        msString += seconds + (seconds > 1 || seconds == 0 ? " " + (upperCase ? "S" : "s") + "econds" : " " + (upperCase ? "S" : "s") + "econd");
        return msString;
    }

    /**
     * Inserts non-breaking spaces in place of breaking spaces in the specified RSN.
     *
     * @param rsn The RSN to fix.
     * @return The fixed RSN.
     */
    public static String fixRSN(String rsn) {
        return rsn != null ? rsn.replaceAll(" ", " ") : null;
    }

    /**
     * Inserts breaking spaces in place of non-breaking spacing in the specified RSN.
     *
     * @param rsn The RSN to reverse.
     * @return The reversed RSN.
     */
    public static String reverseRSN(String rsn) {
        return rsn != null ? rsn.replaceAll(" ", " ") : null;
    }

    /**
     * Gets the enum matching the specified string.
     *
     * @param <T>    Enum type.
     * @param c      The enum class type.
     * @param string The string to match.
     * @return The corresponding enum.
     * Null if no enum is found matching the string.
     */
    public static <T extends Enum<T>> T getEnumFromString(Class<T> c, String string) {
        if (c != null && string != null) {
            try {
                return Enum.valueOf(c, string.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
