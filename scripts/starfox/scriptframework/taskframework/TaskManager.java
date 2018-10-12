package scripts.starfox.scriptframework.taskframework;

import scripts.starfox.api2007.login.Login07;

/**
 * Manages a task.
 *
 * @author Nolan
 */
public final class TaskManager {

    private static Task task;

    static {
        task = null;
    }

    /**
     * Gets the task that is being managed.
     *
     * @return The task.
     */
    public static Task getTask() {
        return task;
    }

    /**
     * Sets the task to be the specified task.
     *
     * @param t The task to be set.
     */
    public static void setTask(Task t) {
        task = t;
        task.loadTerminateConditions();
    }

    /**
     * Runs the tasks loop.
     */
    public static void loop() {
        if (task == null) {
            return;
        }
        task.loop();
    }

    /**
     * Destroys the current {@link Task} held by the {@link TaskManager} if there is one.
     */
    public static void destroy() {
        task = null;
    }

    /**
     * Checks to see if the task should terminate.
     *
     * @return True if the task should terminate, false otherwise.
     */
    public static boolean shouldTerminate() {
        return task != null && Login07.isLoggedIn() && task.terminate();
    }
}
