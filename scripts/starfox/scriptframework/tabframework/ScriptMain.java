package scripts.starfox.scriptframework.tabframework;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import scripts.starfox.api.Client;
import scripts.starfox.api.Printing;

/**
 * @author Spencer
 * @param <T> The SVariables object that is being used for this manager (the one set by the script).
 * @param <E> The GUI of the script.
 */
public abstract class ScriptMain<T extends SVariables, E extends SGUI> {

    //Concurrency Lock
    private final Object lock;

    //Instance Variables
    private final SEventManager mainEventManager;
    private final T vars;
    private final E gui;
    private final ArrayList<SEventManager<T>> eventManagers;
    private final ArrayList<Thread> threads;
    private boolean running;
    
    public ScriptMain(final Script script, final T vars) {
        this(script, vars, 100);
    }

    public ScriptMain(final Script script, final T vars, final int mainUpdateTime) {
        vars.group = null;
        this.lock = new Object();
        setup(vars);
        this.mainEventManager = new SEventManager("Main Manager", mainUpdateTime, false);
        this.vars = vars;
        vars.script = script;
        GUIRunnable<E> r = new GUIRunnable() {
            @Override
            public void run() {
                E gui = createGUI();
                setGUI(gui);
            }
        };
        try {
            EventQueue.invokeAndWait(r);
        } catch (InterruptedException e) {
            Printing.warn("GUI creation interrupted.");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        this.gui = r.getGUI();
        this.eventManagers = new ArrayList<>();
        this.threads = new ArrayList<>();
        running = true;
    }

    public abstract E createGUI();

    public final void showGUI() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                gui.setVisible(true);
            }
        });
    }

    public abstract void setup(T vars);

    public final void start() {
        vars.group = Thread.currentThread().getThreadGroup();
        try {
            EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    threads.clear();
                    for (final SEventManager manager : eventManagers) {
                        Thread t = new Thread(manager.getName()) {
                            @Override
                            public void run() {
                                ThreadSettings.get().setClickingAPIUseDynamic(true);
                                while (running) {
                                    manager.runEvents(vars, lock, vars.group);
                                    try {
                                        Thread.sleep(manager.getRecheckTime());
                                    } catch (InterruptedException ex) {
                                        Printing.dev("Thread Interrupted. Thread \"" + this.getName() + "\" has been successfully terminated.");
                                    }
                                }
                                Printing.dev("Thread \"" + this.getName() + "\" has ended.");
                            }
                        };
                        threads.add(t);
                        t.start();
                    }
                }
            });
        } catch (InterruptedException ex) {
            Printing.warn("Initial thread creation was interrupted.");
        } catch (InvocationTargetException ex) {
            Printing.warn("InvocationTargetException in inital thread creation.");
            ex.printStackTrace();
        }
        loop();
    }
    
    public final void waitForGUI() {
        try {
            gui.getLock().wait();
        } catch (InterruptedException ex) {
            Printing.status("Thread interrupted while waiting for GUI.");
        }
    }

    public final void stop() {
        running = false;
        for (SEventManager manager : eventManagers) {
            manager.stop();
        }
        for (Thread t : threads) {
            if (t.isAlive()) {
                t.interrupt();
            }
        }
    }

    private void loop() {
        if (mainEventManager.hasEvents()) {
            while (running) {
                mainEventManager.runEvents(vars, lock, vars.group);
                try {
                    Thread.sleep(mainEventManager.getRecheckTime());
                } catch (InterruptedException ex) {
                    Client.println("Main Thread Interrupted. Terminated Successfully.");
                    stopGUI();
                }
            }
        }
        Client.println("Main thread has ended.");
        stopGUI();
    }

    public void stopGUI() {
        if (gui != null) {
            gui.dispose();
        }
    }

    public final Script getScript() {
        return vars.script;
    }

    public final T getVars() {
        return vars;
    }

    public final E getGUI() {
        return gui;
    }

    public final ArrayList<Thread> getThreads() {
        return threads;
    }

    public final void addEventManager(SEventManager manager) {
        eventManagers.add(manager);
    }

    public final void addMainEvent(SEvent event) {
        mainEventManager.addEvent(event);
    }
}
