package scripts.starfox.api.util;

import java.util.ArrayList;
import java.util.Set;
import org.tribot.api.General;

/**
 * @author Nolan
 */
public class Threads {

    /**
     * Resumes all threads in the specified ThreadGroup.
     *
     * @param group The ThreadGroup that is being resumed.
     * @param nots  Any thread that contains any of these names will not be resumed.
     */
    public static void resumeGroup(ThreadGroup group, String... nots) {
        for (Thread t : getThreadsByParent(group, nots)) {
            t.resume();
            General.println("Thread Resumed: " + t.getName());
        }
    }

    /**
     * Suspends all threads in the specified ThreadGroup.
     *
     * @param group The ThreadGroup that is being suspended.
     * @param nots  Any thread that contains any of these names will not be paused.
     */
    public static void pauseGroup(ThreadGroup group, String... nots) {
        for (Thread t : getThreadsByParent(group, nots)) {
            t.suspend();
            General.println("Thread Suspended: " + t.getName());
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Specific Thread Control">
    /**
     * Pauses the thread with the specified name for the current script. If the current thread is not static safe, this method returns false.
     *
     * @param name The name of the thread that is being paused.
     * @return True if the thread was correctly paused, false otherwise.
     */
    public static boolean pause(String name) {
        return prThread(name, true);
    }

    /**
     * Resumes the thread with the specified name for the current script. If the current thread is not static safe, this method returns false.
     *
     * @param name The name of the thread that is being resumed.
     * @return True if the thread was correctly resumed, false otherwise.
     */
    public static boolean resume(String name) {
        return prThread(name, false);
    }

    /**
     * Pauses or resumes the thread with the specified name for the current script. If the current thread is not static safe, this method returns false.
     *
     * @param name  The name of the thread that is being paused or resumed.
     * @param pause True if the thread is being paused, false if the thread is being resumed.
     * @return True if the thread was correctly paused or resumed, false otherwise.
     */
    private static boolean prThread(String name, boolean pause) {
        if (Threads.isStaticSafe()) {
            final ArrayList<Thread> threads = Threads.getThreadsByParent(Thread.currentThread().getThreadGroup(), (String) null);
            for (Thread thread : threads) {
                if (thread.getName().toLowerCase().contains(name)) {
                    if (pause) {
                        thread.suspend();
                    } else {
                        thread.resume();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Pauses the TRiBot login bot thread (which is also the randoms thread).
     *
     * @see #pause(String)
     * @return True if the thread was paused, false otherwise.
     */
    public static boolean pauseLoginBot() {
        return Threads.pause("random");
    }

    /**
     * Resumes the TRiBot login bot thread (which is also the randoms thread.
     *
     * @see #resume(String)
     * @return True if the thread was resumes, false otherwise.
     */
    public static boolean resumeLoginBot() {
        return resume("random");
    }

    /**
     * Pauses the TRiBot anti-ban thread.
     *
     * @see #pause(String)
     * @return True if the thread was paused, false otherwise.
     */
    public static final boolean pauseAntiBan() {
        return Threads.pause("antiban");
    }

    /**
     * Resumes the TRiBot anti-ban thread.
     *
     * @see #resume(String)
     * @return True if the thread was paused, false otherwise.
     */
    public static final boolean resumeAntiBan() {
        return resume("antiban");
    }
//</editor-fold>

    /**
     * Returns an ArrayList of threads that match the specified name (by contains) and are in the same script group as the current thread.
     *
     * @param name The name to search for.
     * @return An ArrayList of threads that match the specified name (by contains) and are in the same script group as the current thread.
     */
    public static ArrayList<Thread> getThreads(String name) {
        ArrayList<Thread> all = getAllThreads();
        ArrayList<Thread> threads = new ArrayList<>();
        for (Thread thread : all) {
            if (getScriptGroup().equals(getScriptGroup(thread.getThreadGroup())) && thread.getName().toLowerCase().contains(name.toLowerCase())) {
                threads.add(thread);
            }
        }
        return threads;
    }

    /**
     * Gets all of the currently running threads.
     *
     * @return The currently running threads.
     */
    public static ArrayList<Thread> getAllThreads() {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();
        return new ArrayList<>(threads);
    }

    /**
     * Returns an ArrayList of threads containing all threads that have the same "grandparent" group. (IE, parent of parent group).
     *
     * @param group The original thread group that is being checked (IE, the parent).
     * @param nots  An array representing any Strings that cannot be contained in the name of any returned thread.
     * @return An ArrayList of threads containing all threads that have the same "grandparent" group. (IE, parent of parent group).
     */
    public static ArrayList<Thread> getThreadsByParent(ThreadGroup group, String... nots) {
        ArrayList<Thread> returnThreads = new ArrayList<>();
        ArrayList<Thread> tempThreads = getAllThreads();
        if (tempThreads != null) {
            ThreadGroup g1 = group.getParent();
            for (Thread tempThread : tempThreads) {
                ThreadGroup g2 = tempThread.getThreadGroup();
                g2 = g2 != null ? g2.getParent() : g2;
                if (g2 == null || (nots != null && ArrayUtil.containsPartOf(tempThread.getName().toLowerCase(), nots))) {
                    continue;
                }
                if (g2.hashCode() == g1.hashCode()) {
                    returnThreads.add(tempThread);
                }
            }
        }
        return returnThreads;
    }

    /**
     * Returns the script group that the current thread is part of, or null if the current thread is not part of a script group.
     *
     * @return the script group that the current thread is part of, or null if the current thread is not part of a script group.
     */
    public static ThreadGroup getScriptGroup() {
        return getScriptGroup(Thread.currentThread().getThreadGroup());
    }

    /**
     * Returns the script group that the specified thread is part of, or null if the specified thread is not part of a script group.
     *
     * @param group The thread group.
     * @return the script group that the specified thread is part of, or null if the specified thread is not part of a script group.
     */
    public static ThreadGroup getScriptGroup(ThreadGroup group) {
        if (group != null) {
            ArrayList<Thread> tempThreads = getAllThreads();
            if (tempThreads != null) {
                ThreadGroup g1 = group.getParent();
                if (g1 != null) {
                    for (Thread tempThread : tempThreads) {
                        ThreadGroup g2 = tempThread.getThreadGroup();
                        g2 = g2 != null ? g2.getParent() : g2;
                        if (g2 == null) {
                            continue;
                        }
                        if (g2.hashCode() == g1.hashCode()/* && tempThread.getName().toLowerCase().contains("script thread")*/) {
                            return g1;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns true if the specified thread is in the same script group as the current thread.
     *
     * @param thread The thread.
     * @return True if the specified thread is in the same script group as the current thread.
     */
    public static boolean matches(Thread thread) {
        ThreadGroup group1 = getScriptGroup();
        ThreadGroup group2 = getScriptGroup(thread.getThreadGroup());
        return group1 != null && group2 != null && group1.equals(group2);
    }

    /**
     * Returns true if the specified ThreadGroup contains a script thread, false otherwise.
     *
     * IE, returns true if the current thread group is not null.
     *
     * @param group The group that is being checked for a script thread.
     * @see #getScriptGroup(ThreadGroup)
     * @return True if the specified ThreadGroup contains a script thread, false otherwise.
     */
    public static boolean containsScriptThread(ThreadGroup group) {
        return getScriptGroup(group) != null;
    }

    /**
     * Checks to see if the current thread is a script thread.
     *
     * @see #getScriptGroup()
     * @return True if the parent of the group that the current thread belongs to is a script thread group. Returns false if the current thread does not belong to a group.
     */
    public static boolean isScriptThread() {
        return getScriptGroup() != null;
    }

    /**
     * Returns true if a static call on the current thread is tab-safe, false otherwise. If anything is null, returns false.
     *
     * A static safe thread is defined as a method that will function correctly when a static method is called on it. This allows for tab support.
     *
     * @see #isStaticSafe(Thread)
     * @return True if a static call on the current thread is tab-safe, false otherwise.
     */
    public static boolean isStaticSafe() {
        return isStaticSafe(Thread.currentThread());
    }

    /**
     * Returns true if a static call on the specified thread is tab-safe, false otherwise. If anything is null, returns false.
     *
     * A static safe thread is defined as a method that will function correctly when a static method is called on it. This allows for tab support.
     *
     * @param thread The thread that is being tested.
     * @see #isStaticSafe()
     * @return True if a static call on the specified thread is tab-safe, false otherwise.
     */
    public static boolean isStaticSafe(Thread thread) {
        try {
            return thread.getThreadGroup().getParent().getName().toLowerCase().contains("thread group");
        } catch (NullPointerException e) {
            return false;
        }
    }
}
