package scripts.usa.api.framework.task;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.tribot.api.General;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.script.Script;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.usa.api.antiban.ABC;
import scripts.usa.api.condition.Condition;
import scripts.usa.api.gui.AbstractFxController;
import scripts.usa.api.gui.AbstractFxGUI;
import scripts.usa.api.gui.FxApplication;
import scripts.usa.api.painting.PaintUtils;
import scripts.usa.api.painting.Painter;
import scripts.usa.api.threads.SafeThread;
import scripts.usa.api.util.Timer;
import scripts.usa.api2007.observers.inventory.InventoryListener;
import scripts.usa.api2007.observers.inventory.InventoryObserver;
import scripts.usa.api2007.observers.teleblock.Teleblock;

public abstract class TaskScript extends Script implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	private Painter painter;
	private FxApplication jfx;
	private Tasks tasks = new Tasks();
	private SafeThread inventoryObserverThread;
	private SafeThread teleblockThread;

	public abstract void init();

	public abstract void onScriptStart();

	public abstract void onScriptLoop();

	public abstract void onScriptEnd();

	@Override
	public void run() {
		init();

		while (Login.getLoginState() != Login.STATE.INGAME || Player.getRSPlayer() == null) {
			ScriptVars.get().status = "Logging in...";
			sleep(500);
		}
		ScriptVars.get().status = "Logged in!";
		sleep(1000);

		if (jfx != null) {
			jfx.show();
			while (jfx.isShowing()) {
				ScriptVars.get().status = "GUI";
				sleep(100);
			}
		}

		ScriptVars.get().status = "Starting...";

		inventoryObserverThread = new SafeThread(new InventoryObserver(this));
		teleblockThread = new SafeThread(new Teleblock());

		onScriptStart();

		while (ScriptVars.get()
				.isRunning()) {
			onScriptLoop();

			if (!ScriptVars.get()
					.getSkillsTracker()
					.isSet())
				ScriptVars.get()
						.getSkillsTracker()
						.setSkills();

			if (tasks.current()
					.validate()) {
				tasks.current()
						.execute();
				if (tasks.current() instanceof PriorityTask) {
					tasks.reset();
				}
			}
			else {
				tasks.next();
			}

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

	public void setTasks(Task... tasks) {
		this.tasks = new Tasks(tasks);
	}

	public void onEnd() {
		if (inventoryObserverThread != null)
			inventoryObserverThread.stop();
		if (teleblockThread != null)
			teleblockThread.stop();
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
		if (message.equals("Oh dear, you are dead!"))
			ScriptVars.get().deaths++;
		if (message.contains("teleport block has been cast on you")) {
			if (ScriptVars.get().teleblockTimer == null) {
				General.println(message);
				ScriptVars.get().teleblockTimer = new Timer(300000);
			}
		}
	}

	@Override
	public void tradeRequestReceived(String name) {
	}
}
