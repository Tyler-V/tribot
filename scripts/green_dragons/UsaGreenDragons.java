package scripts.green_dragons;

import org.tribot.api2007.types.RSItem;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.green_dragons.data.Vars;
import scripts.green_dragons.gui.FxController;
import scripts.green_dragons.gui.FxGUI;
import scripts.green_dragons.paint.GreenDragonsPaint;
import scripts.green_dragons.tasks.Bank;
import scripts.green_dragons.tasks.Dragons;
import scripts.green_dragons.tasks.Drop;
import scripts.green_dragons.tasks.Eat;
import scripts.green_dragons.tasks.Evade;
import scripts.green_dragons.tasks.Loot;
import scripts.green_dragons.tasks.Potions;
import scripts.green_dragons.threads.ThreatSearch;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api.threads.SafeThread;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyItem;
import scripts.usa.api2007.observers.inventory.InventoryChange;
import scripts.usa.api2007.observers.inventory.InventoryListener;

@ScriptManifest(authors = { "Usa" }, category = "Money Making", name = "USA Green Dragons", version = 14.0)
public class UsaGreenDragons extends TaskScript implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	private SafeThread threatSearchThread;

	@Override
	public void init() {
		setVars(new Vars(this));
		setJFX(new FxGUI(), new FxController());
		setPaint(new GreenDragonsPaint());
		setTasks(new Evade(), new Eat(), new Bank(), new Drop(), new Loot(), new Potions(), new Dragons());
	}

	@Override
	public void onScriptStart() {
		threatSearchThread = new SafeThread(new ThreatSearch());
		// super.setAIAntibanState(false);
		ABC.setAntiban(false);
		ABC.setReactionSleeping(false);
		ABC.setHover(false);
		ABC.setOpenMenu(false);
	}

	@Override
	public void onScriptLoop() {
	}

	@Override
	public void onScriptEnd() {
		if (threatSearchThread != null)
			threatSearchThread.stop();
	}

	@Override
	public void onInventoryChange(InventoryChange change, RSItem item, int count) {
		OSBuddyItem osbuddyItem = OSBuddy.get(item);
		if (osbuddyItem != null)
			Vars.get().profit += OSBuddy.get(item).getAveragePrice() * count * (change == InventoryChange.INCREASE ? 1 : -1);
	}
}
