package scripts.starfox.scriptframework.tabframework;

import java.util.ArrayList;
import scripts.starfox.api.Client;

/**
 * @author Spencer
 * @param <T> The SVariables object that is being used for this manager (the one set by the script).
 */
public class SEventManager<T extends SVariables> {

    private final String name;
    private final ArrayList<SEvent> events;
    private final long recheckTime;
    private final boolean isBackground;
    private Thread tabSupportThread;
    private final Object THREAD_LOCK;
    private boolean stopped;

    public SEventManager(String name, long recheckTime, boolean isBackground) {
        THREAD_LOCK = new Object();
        this.name = name;
        this.isBackground = isBackground;
        this.events = new ArrayList<>();
        this.recheckTime = recheckTime;
        tabSupportThread = null;
        stopped = false;
    }

    public final void addEvent(SEvent event) {
        this.events.add(event);
    }

    public final void runEvents(final T vars, final Object lock, final ThreadGroup group) {
        if (isBackground) {
            if (tabSupportThread == null || group == null || !tabSupportThread.isAlive()) {
                System.out.println("Thread not running. Starting thread for " + getName() + ".");
                tabSupportThread = new Thread(group, name + " Tab Thread") {
                    @Override
                    public void run() {
                        while (!stopped) {
                            synchronized (THREAD_LOCK) {
                                try {
                                    THREAD_LOCK.wait();
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                for (SEvent event : events) {
                                    if (event.validate(vars, lock)) {
                                        event.execute(vars, lock);
                                    }
                                }
                            }
                        }
                    }
                };
                tabSupportThread.start();
            }
        }
        for (SEvent event : events) {
            if (isBackground) {
                synchronized (THREAD_LOCK) {
                    THREAD_LOCK.notifyAll();
                }
            } else {
                if (event.validate(vars, lock)) {
                    event.execute(vars, lock);
                }
            }
        }
    }

    public final String getName() {
        return this.name;
    }

    public final long getRecheckTime() {
        return this.recheckTime;
    }

    public final boolean hasEvents() {
        return !events.isEmpty();
    }

    public final void stop() {
        stopped = true;
    }
}
