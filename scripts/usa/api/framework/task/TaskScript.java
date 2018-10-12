package scripts.usa.api.framework.task;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.tribot.api.General;
import org.tribot.api2007.Camera;
import org.tribot.script.Script;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.usa.api.antiban.ABC;
import scripts.usa.api.gui.AbstractFxController;
import scripts.usa.api.gui.AbstractFxGUI;
import scripts.usa.api.gui.FxApplication;
import scripts.usa.api.painting.PaintUtils;
import scripts.usa.api.painting.Painter;
import scripts.usa.api.threads.SafeThread;
import scripts.usa.api2007.observers.inventory.InventoryListener;
import scripts.usa.api2007.observers.inventory.InventoryObserver;

public abstract class TaskScript extends Script implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	private Painter painter;
	private FxApplication jfx;
	private Tasks tasks = new Tasks();
	private SafeThread inventoryObserverThread;

	public abstract void init();

	public abstract void onScriptStart();

	public abstract void onScriptLoop();

	public abstract void onScriptEnd();

	@Override
	public void run() {
		init();

		if (jfx != null) {
			jfx.show();
			while (jfx.isShowing()) {
				ScriptVars.get().status = "GUI";
				sleep(100);
			}
		}

		ScriptVars.get().status = "Starting...";

		inventoryObserverThread = new SafeThread(new InventoryObserver(this));

		onScriptStart();

		Camera.setCameraAngle(100);

		// General.println(tasks);

		Task task = getTasks().first();

		while (ScriptVars.get().isRunning()) {
			onScriptLoop();

			if (!ScriptVars.get().getSkillsTracker().isSet())
				ScriptVars.get().getSkillsTracker().setSkills();

			if (task.validate()) {
				task.execute();
				if (tasks.hasPriorityTask()) {
					task = getTasks().first();
				}
				else {
					task = getTasks().higher(task);
				}
			}
			else {
				task = getTasks().higher(task);
			}

			if (task == null)
				task = getTasks().first();

			if (ABC.performAntiban())
				ScriptVars.get().status = "Performing Antiban";

			sleep(General.randomSD(100, 50));
		}

		onEnd();
	}

	public void setVars(ScriptVars scriptVars) {
		ScriptVars.set(scriptVars);
	}

	public void setJFX(AbstractFxGUI fxGUI, AbstractFxController controller) {
		this.jfx = new FxApplication(fxGUI, controller);
	}

	public void setPaint(Painter painter) {
		this.painter = painter;
	}

	public Tasks getTasks() {
		return this.tasks;
	}

	public void setTasks(Task... tasks) {
		this.tasks = new Tasks(tasks);
	}

	public void onEnd() {
		if (inventoryObserverThread != null)
			inventoryObserverThread.stop();
		if (jfx != null)
			jfx.close();
		onScriptEnd();
		ABC.destroy();
		ScriptVars.destroy();
	}

	@Override
	public void onPaint(Graphics g) {
		if (painter == null || ScriptVars.get() == null)
			return;
		Graphics2D g2 = PaintUtils.create2D(g);
		painter.onPaint(g2);
		g2.dispose();
	}

	@Override
	public OVERRIDE_RETURN overrideMouseEvent(MouseEvent event) {
		if (painter != null)
			return painter.notifyMouseEvent(event);
		return OVERRIDE_RETURN.PROCESS;
	}

	@Override
	public OVERRIDE_RETURN overrideKeyEvent(KeyEvent event) {
		if (painter != null)
			return painter.notifyKeyEvent(event);
		return OVERRIDE_RETURN.PROCESS;
	}

	@Override
	public void clanMessageReceived(String name, String message) {
	}

	@Override
	public void duelRequestReceived(String name, String message) {
	}

	@Override
	public void personalMessageReceived(String name, String message) {
	}

	@Override
	public void playerMessageReceived(String name, String message) {
	}

	@Override
	public void serverMessageReceived(String message) {
	}

	@Override
	public void tradeRequestReceived(String name) {
	}
}
