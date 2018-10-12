package scripts.starfox.scriptframework;

import org.tribot.api.input.Mouse;
import org.tribot.api.util.CPUOptimization;
import org.tribot.script.Script;
import org.tribot.script.interfaces.*;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Argument;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Strings;
import scripts.starfox.api2007.Clicking07;
import scripts.starfox.api2007.Mouse07;
import scripts.starfox.api2007.Settings;
import scripts.starfox.graphics.GraphicsUtil;
import scripts.starfox.graphics.Painter;
import scripts.starfox.interfaces.Endable;
import scripts.starfox.scriptframework.eventframework.EventManager;
import scripts.starfox.scriptframework.jobframework.JobManager;
import scripts.starfox.scriptframework.loopframework.LoopManager;
import scripts.starfox.scriptframework.taskframework.TaskManager;
import scripts.starfox.swing.SwingUtil;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * @param <T> The generic type for the GUI.
 * @author Nolan
 */
public abstract class ScriptKit<T extends ScriptFrame>
        extends Script
        implements Arguments, Breaking, Pausing, Painting, MousePainting, MouseSplinePainting, EventBlockingOverride, Ending, Endable {

    private final Painter painter;
    private T gui;
    private boolean skipGui;

    /**
     * Constructs a new {@link ScriptKit}.
     *
     * The {@link ScriptFrame} for the script will be set to null by default.
     *
     * @param painter The {@link Painter} being used for the script.
     */
    public ScriptKit(Painter painter) {
        this(painter, null);
    }

    /**
     * Constructs a new {@link ScriptKit}.
     *
     * @param painter The {@link Painter} being used for the script.
     * @param gui     The {@link ScriptFrame} for the script.
     */
    public ScriptKit(Painter painter, T gui) {
        Vars.load(loadVars());
        this.painter = painter;
        this.gui = gui;
        this.skipGui = false;
    }

    /**
     * Gets the {@link Painter}.
     *
     * @return The {@link Painter}.
     */
    public Painter getPainter() {
        return this.painter;
    }

    /**
     * Gets the GUI for the script.
     *
     * @return The GUI.
     * Null if no GUI was provided upon creation.
     */
    public T getGUI() {
        return this.gui;
    }

    /**
     * This method is used to set the type of {@link Vars} in the script.
     *
     * @return The {@link Vars} for the script.
     */
    protected abstract Vars loadVars();

    /**
     * This method is ran after {@link #onScriptStart()} inside {@link #run()}.
     */
    public abstract void runScript();

    /**
     * This method is ran before the script starts.
     */
    public abstract void onScriptStart();

    /**
     * This method is ran directly after the script starts to process any arguments that may have been passed into the
     * script.
     *
     * @param arguments The arguments passed into the script.
     *                  Null if no arguments were found.
     */
    public abstract void processArguments(String arguments);

    @Override
    public void passArguments(HashMap<String, String> map) {
        String args = Argument.getArguments(map);
        if (args != null) {
            skipGui = true;
            processArguments(Argument.getArguments(map));
        }
    }

    @Override
    public void run() {
        //If the gui is being skipped, set it to be null.
        if (skipGui) {
            gui = null;
        }

        //Set the default client settings
        Client.setSettings();
        //Run the script start method
        onScriptStart();
        println("Your unique anti-ban seed: " + AntiBan.getSleepSeed() + "[" + Client.getUsername() + "]");

        //If a GUI is being used, add the version to the title
        if (getGUI() != null)
            getGUI().setTitle(getGUI().getTitle() + " v" + Vars.get().version());
        //If a paint is being used, set the version for the paint
        if (getPainter() != null)
            getPainter().setVersion(Vars.get().version());

        //Set the painter, gui, status, and JobManager for the Vars
        Vars.get().setPainter(painter);
        Vars.get().setGui(gui);
        Vars.get().setStatus("");
        Vars.get().setJobManager(new JobManager());

        //If a GUI is being used, make this thread wait until it is notified
        if (getGUI() != null) {
            SwingUtil.bringToFront(getGUI());
            SwingUtil.makeVisible(getGUI());

            //Wait until the script is notified by the GUI to continue.
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //Refresh the TimeTracker and SkillTracker
        Vars.get().getTimeTracker().refresh();
        Vars.get().getSkillTracker().refresh();

        //Run the script
        runScript();
    }

    @Override
    public void onBreakStart(long break_time) {
        Vars.get().setStatus("Taking a break");
        Vars.get().getTimeTracker().subtract(break_time);
        Mouse07.fixSelected();
    }

    @Override
    public void onBreakEnd() {
        Vars.get().setStatus("Logging back in from break");
    }

    @Override
    public void onPause() {
        Vars.get().setStatus("Performing client action");
        Mouse07.fixSelected();
        Client.println("Script paused.");
    }

    @Override
    public void onResume() {
        Client.println("Script resumed.");
    }

    @Override
    public void onPaint(Graphics g) {
        Painter painter = getPainter();
        if (painter != null) {
            if (Vars.get() != null) {
                Graphics2D g2 = GraphicsUtil.create2D(g);
                painter.onPaint(g2);
                g2.dispose();
            }
        }
    }

    @Override
    public void paintMouse(Graphics g, Point loc, Point dragLoc) {
        Painter painter = getPainter();
        if (painter != null) {
            Graphics2D g2 = GraphicsUtil.create2D(g);
            painter.getMousePaint().paint(g2);
            g2.dispose();
        }
    }

    @Override
    public void paintMouseSpline(Graphics var1, ArrayList<Point> var2) {

    }

    @Override
    public OVERRIDE_RETURN overrideMouseEvent(MouseEvent event) {
        Painter painter = getPainter();
        if (painter != null) {
            return painter.notifyMouseEvent(event);
        }
        return OVERRIDE_RETURN.PROCESS;
    }

    @Override
    public OVERRIDE_RETURN overrideKeyEvent(KeyEvent event) {
        if (event.getID() == KeyEvent.KEY_PRESSED) {
            if (event.getKeyCode() == KeyEvent.VK_EQUALS && event.isControlDown()) {
                if (Mouse.getSpeed() < 200) {
                    Mouse07.incrementSpeed();
                    Client.println("Set mouse speed to: " + Mouse.getSpeed());
                } else {
                    Client.println("Mouse speed cannot exceed 200.");
                }
                return OVERRIDE_RETURN.DISMISS;
            } else if (event.getKeyCode() == KeyEvent.VK_MINUS && event.isControlDown()) {
                if (Mouse.getSpeed() > 100) {
                    Mouse07.decrementSpeed();
                    Client.println("Set mouse speed to: " + Mouse.getSpeed());
                } else {
                    Client.println("Mouse speed cannot be lower than 100");
                }
                return OVERRIDE_RETURN.DISMISS;
            }
        }
        Painter painter = getPainter();
        if (painter != null) {
            return painter.notifyKeyEvent(event);
        }
        return OVERRIDE_RETURN.PROCESS;
    }

    @Override
    public void onEnd() {
        println("Reason for stopping: " + Vars.get().getStoppingDiagnosis());
        onScriptEnd();
        long cleanup_stamp = System.currentTimeMillis();
        if (getPainter() != null) {
            getPainter().getLootTracker().stop();
        }
        Clicking07.destroy();
        LoopManager.destroy();
        TaskManager.destroy();
        EventManager.destroy();
        Vars.destroy();
        AntiBan.destroy();
        Client.println("Script clean-up completed in " + (System.currentTimeMillis() - cleanup_stamp) + "ms.");
    }
}
