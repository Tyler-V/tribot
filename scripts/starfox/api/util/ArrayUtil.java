package scripts.starfox.api.util;

import java.lang.reflect.Array;
import java.util.*;

/**
 * The ArrayUtil class contains helper methods for arrays.
 *
 *
 * @author Nolan
 */
public class ArrayUtil {

    /**
     * Checks to see if the specified integer array contains the specified value.
     *
     *
     * @param value The value.
     * @param array The array.
     * @return True if the array contains the value, false otherwise.
     */
    public static boolean contains(int value, int... array) {
        for (int i : array) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified double array contains the specified value.
     *
     *
     * @param value The value.
     * @param array The array.
     * @return True if the array contains the value, false otherwise.
     */
    public static boolean contains(double value, double... array) {
        for (double d : array) {
            if (d == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified long array contains the specified value.
     *
     *
     * @param value The value.
     * @param array The array.
     * @return True if the array contains the value, false otherwise.
     */
    public static boolean contains(long value, long... array) {
        for (long l : array) {
            if (l == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified short array contains the specified value.
     *
     *
     * @param value The key.
     * @param array The array.
     * @return True if the array contains the key, false otherwise.
     */
    public static boolean contains(short value, short... array) {
        for (short s : array) {
            if (s == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified float array contains the specified value.
     *
     *
     * @param value The key.
     * @param array The array.
     * @return True if the array contains the key, false otherwise.
     */
    public static boolean contains(float value, float... array) {
        for (float f : array) {
            if (f == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if the specified object array contains the specified object.
     *
     *
     * @param <T>    The type of array.
     * @param object The object.
     * @param array  The array.
     * @return True if the array contains the object, false otherwise.
     */
    @SafeVarargs
    public static <T> boolean contains(T object, T... array) {
        return Arrays.asList(array).contains(object);
    }

    /**
     * Concatenates the two specified int arrays.
     * Note that this method is expensive, do not call in a loop.
     *
     *
     * @param array1 The first array.
     * @param array2 The second array.
     * @return The concatenated array.
     */
    public static int[] concat(int[] array1, int[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        int[] newArray = new int[length1 + length2];
        System.arraycopy(array1, 0, newArray, 0, length1);
        System.arraycopy(array2, 0, newArray, length1, length2);

        return newArray;
    }

    /**
     * Concatenates the two specified double arrays.
     * Note that this method is expensive, do not call in a loop.
     *
     *
     * @param array1 The first array.
     * @param array2 The second array.
     * @return The concatenated array.
     */
    public static double[] concat(double[] array1, double[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        double[] newArray = new double[length1 + length2];
        System.arraycopy(array1, 0, newArray, 0, length1);
        System.arraycopy(array2, 0, newArray, length1, length2);

        return newArray;
    }

    /**
     * Concatenates the two specified long arrays.
     * Note that this method is expensive, do not call in a loop.
     *
     *
     * @param array1 The first array.
     * @param array2 The second array.
     * @return The concatenated array.
     */
    public static long[] concat(long[] array1, long[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        long[] newArray = new long[length1 + length2];
        System.arraycopy(array1, 0, newArray, 0, length1);
        System.arraycopy(array2, 0, newArray, length1, length2);

        return newArray;
    }

    /**
     * Concatenates the two specified short arrays.
     * Note that this method is expensive, do not call in a loop.
     *
     *
     * @param array1 The first array.
     * @param array2 The second array.
     * @return The concatenated array.
     */
    public static short[] concat(short[] array1, short[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        short[] newArray = new short[length1 + length2];
        System.arraycopy(array1, 0, newArray, 0, length1);
        System.arraycopy(array2, 0, newArray, length1, length2);

        return newArray;
    }

    /**
     * Concatenates the two specified float arrays.
     * Note that this method is expensive, do not call in a loop.
     *
     *
     * @param array1 The first array.
     * @param array2 The second array.
     * @return The concatenated array.
     */
    public static float[] concat(float[] array1, float[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;

        float[] newArray = new float[length1 + length2];
        System.arraycopy(array1, 0, newArray, 0, length1);
        System.arraycopy(array2, 0, newArray, length1, length2);

        return newArray;
    }

    /**
     * Concatenates the two specified arrays.
     * Note that this method is expensive, do not call in a loop.
     *
     *
     * @param <T>    The type of the arrays.
     * @param array1 The first array.
     * @param array2 The second array.
     * @return The concatenated array.
     */
    public static <T> T[] concat(T[] array1, T[] array2) {
        int length1 = array1.length;
        int length2 = array2.length;
        //noinspection unchecked
        T[] newArray = (T[]) Array.newInstance(array1.getClass().getComponentType(), length1 + length2);
        System.arraycopy(array1, 0, newArray, 0, length1);
        System.arraycopy(array2, 0, newArray, length1, length2);

        return newArray;
    }

    /**
     * Adds the specified value to the specified array.
     *
     *
     * @param array The array being added to.
     * @param value The value being added.
     * @return A new array containing the specified value at the end of the array.
     */
    public static int[] add(int[] array, int value) {
        int[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = value;
        return newArray;
    }

    /**
     * Adds the specified value to the specified array.
     *
     *
     * @param array The array being added to.
     * @param value The value being added.
     * @return A new array containing the specified value at the end of the array.
     */
    public static double[] add(double[] array, double value) {
        double[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = value;
        return newArray;
    }

    /**
     * Adds the specified value to the specified array.
     *
     *
     * @param array The array being added to.
     * @param value The value being added.
     * @return A new array containing the specified value at the end of the array.
     */
    public static short[] add(short[] array, short value) {
        short[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = value;
        return newArray;
    }

    /**
     * Adds the specified value to the specified array.
     *
     *
     * @param array The array being added to.
     * @param value The value being added.
     * @return A new array containing the specified value at the end of the array.
     */
    public static long[] add(long[] array, long value) {
        long[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = value;
        return newArray;
    }

    /**
     * Adds the specified value to the specified array.
     *
     *
     * @param array The array being added to.
     * @param value The value being added.
     * @return A new array containing the specified value at the end of the array.
     */
    public static float[] add(float[] array, float value) {
        float[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = value;
        return newArray;
    }

    /**
     * Adds the specified object to the specified array.
     *
     *
     * @param <T>    The type of object.
     * @param array  The array being added to.
     * @param object The object being added.
     * @return A new array containing the specified object at the end of the array..
     */
    public static <T> T[] add(T[] array, T object) {
        T[] newArray = Arrays.copyOf(array, array.length + 1);
        newArray[array.length] = object;
        return newArray;
    }

    /**
     * Filters any duplicates in the specified array and returns a new array with no duplicates.
     *
     * Note that this method <i>does not</i> alter the original array in any way.
     *
     *
     * @param <T>   The object type of the array.
     * @param array The array to filter.
     * @return An array with the duplicates filtered out.
     */
    public static <T extends Object> T[] filterDuplicates(T[] array) {
        if (array.length < 1) {
            return array;
        }
        HashSet<T> set = new LinkedHashSet<>(Arrays.asList(array));
        return set.toArray((T[]) Array.newInstance(array.getClass(), set.size()));
    }

    /**
     * Converts the specified list of strings into an array.
     *
     * If the list is null, this method returns an empty array of strings.
     *
     *
     * @param list The list to convert.
     * @return An array representing the list.
     */
    public static String[] toArrayString(List<String> list) {
        if (list == null) {
            return new String[0];
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * Converts the specified list of ints into an array.
     *
     * If the list is null, this method returns an empty array of ints.
     *
     *
     * @param list The list to convert.
     * @return An array representing the list.
     */
    public static int[] toArrayInt(List<Integer> list) {
        if (list == null) {
            return new int[0];
        }
        int[] arr = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    /**
     * Converts the specified list of doubles into an array.
     *
     * If the list is null, this method returns an empty array of doubles.
     *
     *
     * @param list The list to convert.
     * @return An array representing the list.
     */
    public static double[] toArrayDouble(List<Double> list) {
        if (list == null) {
            return new double[0];
        }
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    /**
     * Converts the specified list of longs into an array.
     *
     * If the list is null, this method returns an empty array of longs.
     *
     *
     * @param list The list to convert.
     * @return An array representing the list.
     */
    public static long[] toArrayLong(List<Long> list) {
        if (list == null) {
            return new long[0];
        }
        long[] arr = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;

    }

    /**
     * Converts the specified list of booleans into an array.
     *
     * If the list is null, this method returns an empty array of booleans.
     *
     *
     * @param list The list to convert.
     * @return An array representing the list.
     */
    public static boolean[] toArrayBoolean(List<Boolean> list) {
        if (list == null) {
            return new boolean[0];
        }
        boolean[] arr = new boolean[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i);
        }
        return arr;

    }

    /**
     * Checks whether or not the specified values can be retrieved from the specified array.
     *
     *
     * @param array The array to check.
     * @param i     The x value to be retrieved from the 2D array.
     * @param j     The y value to be retrieved from the 2D array.
     * @return True if the specified values can be retrieved from the specified array, false otherwise.
     */
    public static boolean isValidArray(Object[][] array, int i, int j) {
        return i >= 0 && j >= 0 && i < array.length && j < array[i].length;
    }

    /**
     * Returns whether the specified String is contained within any String that is contained within the specified ArrayList of Strings.
     *
     * @param string The String that is being checked.
     * @param array  The ArrayList that is being checked.
     * @return Whether the specified String is contained within any String that is contained within the specified ArrayList of Strings.
     */
    public static boolean containsPartOf(String string, String... array) {
        if (string == null || array == null) {
            return false;
        }
        for (String s : array) {
            if (s != null) {
                if (string.toLowerCase().contains(s.toLowerCase()) || s.toLowerCase().contains(string.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any of the String elements contained in the specified ArrayList of Strings contains any of the Strings in the specified varargs of
     * Strings, false otherwise.
     *
     *
     * @param array       The ArrayList being tested.
     * @param stringArray The ArrayList of Strings to be searched for.
     * @return True if any of the String elements contained in the specified ArrayList of Strings contains any of the Strings in the specified varargs of
     * Strings, false otherwise.
     */
    public static boolean containsPartOf(ArrayList<String> array, String... stringArray) {
        for (String tempS : array) {
            for (String s : stringArray) {
                if (tempS.toLowerCase().contains(s.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the array represented by the specified varargs.
     *
     *
     * @param <T>   The type of array.
     * @param array The array.
     * @return The array represented by the specified varargs.
     */
    public static <T extends Object> T[] getAsArray(T... array) {
        return array;
    }

    /**
     * Checks if the specified name is contained in the specified array of Enums.
     *
     *
     * @param value The value that is being tested.
     * @param array The array that is being searched through.
     * @return true if the specified name is contained in the specified array of Enums.
     */
    public static boolean enumArrayContains(String value, Enum[] array) {
        for (Enum e : array) {
            if (value.equals(e.name())) {
                return true;
            }
        }
        return false;
    }
}
