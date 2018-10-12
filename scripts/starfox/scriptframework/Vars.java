package scripts.starfox.scriptframework;

import org.tribot.api.input.Mouse;
import scripts.starfox.api.Client;
import scripts.starfox.api.listeners.ListenerThread;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api2007.Player07;
import scripts.starfox.graphics.Painter;
import scripts.starfox.scriptframework.jobframework.JobManager;
import scripts.starfox.tracking.skills.SkillTracker;
import scripts.starfox.tracking.time.TimeTracker;


/**
 * Created by Nolan on 9/30/2015.
 */
public abstract class Vars<T extends ScriptFrame>
        extends ListenerThread {

    private static Vars vars;

    /**
     * Gets the {@link Vars} instance.
     *
     * @return The Vars instance.
     */
    public static Vars get() {
        return vars;
    }

    /**
     * Loads the {@link Vars} specified as the singleton.
     *
     * @param v The {@link Vars} to load.
     */
    public static void load(Vars v) {
        vars = v;
    }

    /**
     * Destroys the current instance of {@link Vars} if there is one.
     */
    public static void destroy() {
        load(null);
    }

    /**
     * Constructs a new {@link Vars}.
     */
    protected Vars(ScriptKit scriptKit) {
        this.scriptKit = scriptKit;
        this.version = Client.getManifest(scriptKit.getClass()).version();
        this.busyTimer = new Timer(5000);
        this.timeTracker = new TimeTracker();
        this.skillTracker = new SkillTracker();
        this.status = "";
        this.stoppingDiagnosis = "n/a";
        this.mouseSpeed = 105;
        getBusyTimer().start();
        start();
    }

    private final ScriptKit scriptKit;
    private final double version;

    private Painter painter;
    private T gui;
    private JobManager jobManager;
    private Timer busyTimer;
    private TimeTracker timeTracker;
    private SkillTracker skillTracker;

    private String status;
    private String stoppingDiagnosis;
    private int mouseSpeed;


    /**
     * Gets the version of the script.
     *
     * @return The version.
     */
    public double version() {
        return this.version;
    }

    /**
     * Gets the {@link ScriptKit}.
     *
     * @return The {@link ScriptKit}.
     */
    public ScriptKit getScriptKit() {
        return this.scriptKit;
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
     * Gets the Gui.
     *
     * @return The Gui.
     */
    public T getGui() {
        return this.gui;
    }

    /**
     * Gets the {@link JobManager} for the script.
     *
     * @return The {@link JobManager}.
     * Null if no {@link JobManager} is being used.
     */
    public JobManager getJobManager() {
        return jobManager;
    }

    /**
     * The busy timer for the script.
     *
     * @return The busy timer.
     */
    public Timer getBusyTimer() {
        return this.busyTimer;
    }

    /**
     * Gets the time tracker.
     *
     * @return The time tracker.
     */
    public TimeTracker getTimeTracker() {
        return this.timeTracker;
    }

    /**
     * Gets the skill tracker.
     *
     * @return The skill tracker.
     */
    public SkillTracker getSkillTracker() {
        return this.skillTracker;
    }

    /**
     * Gets the status of the script.
     *
     * @return The status of the script.
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Gets the stopping diagnosis for the script.
     *
     * @return The stopping diagnosis.
     */
    public String getStoppingDiagnosis() {
        return this.stoppingDiagnosis;
    }

    /**
     * Gets the mouse speed for the script.
     *
     * @return The mouse speed.
     */
    public int getMouseSpeed() {
        return this.mouseSpeed;
    }

    /**
     * Sets the {@link Painter}.
     *
     * @param painter The {@link Painter} to set.
     */
    public void setPainter(Painter painter) {
        this.painter = painter;
    }

    /**
     * Sets the Gui.
     *
     * @param gui The Gui to set.
     */
    public void setGui(T gui) {
        this.gui = gui;
    }

    /**
     * Sets the {@link JobManager} for the script.
     *
     * @param jobManager The {@link JobManager} to set.
     */
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    /**
     * Sets the status of the script.
     *
     * @param status The status of the script.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the stopping diagnosis for the script.
     *
     * @param stoppingDiagnosis The stopping diagnosis.
     */
    public void setStoppingDiagnosis(String stoppingDiagnosis) {
        this.stoppingDiagnosis = stoppingDiagnosis;
    }

    /**
     * Sets the mouse speed for the script.
     *
     * @param mouseSpeed The mouse speed to set.
     */
    public void setMouseSpeed(int mouseSpeed) {
        this.mouseSpeed = mouseSpeed;
        Mouse.setSpeed(getMouseSpeed());
    }

    @Override
    public void listen() {
        if (Player07.isAnimating()) {
            getBusyTimer().reset();
        }
        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
