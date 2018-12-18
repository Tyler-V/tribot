package scripts.usa.api.framework.task;

import org.tribot.script.ScriptManifest;

import scripts.usa.api.util.Timer;
import scripts.usa.api2007.Client;
import scripts.usa.api2007.Wilderness;
import scripts.usa.api2007.tracking.SkillsTracker;

public abstract class ScriptVars {

	private static ScriptVars scriptVars;
	private final TaskScript taskScript;
	private final ScriptManifest scriptManifest;
	private SkillsTracker skillsTracker;
	private boolean running = true;

	public String status = "Starting...";
	public int fail;
	public int deaths;
	public Timer teleblockTimer;

	protected ScriptVars(TaskScript taskScript) {
		this.taskScript = taskScript;
		this.scriptManifest = Client.getManifest(taskScript.getClass());
		this.skillsTracker = new SkillsTracker();
	}

	public static ScriptVars get() {
		return scriptVars;
	}

	public static void set(ScriptVars vars) {
		scriptVars = vars;
	}

	public static void destroy() {
		set(null);
	}

	public void stopScript() {
		running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public TaskScript getTaskScript() {
		return this.taskScript;
	}

	public ScriptManifest getScriptManifest() {
		return this.scriptManifest;
	}

	public SkillsTracker getSkillsTracker() {
		return this.skillsTracker;
	}

	public boolean isTeleblocked() {
		return this.teleblockTimer != null ? this.teleblockTimer.isRunning() : false;
	}
}
