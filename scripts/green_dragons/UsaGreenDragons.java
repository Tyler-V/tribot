package scripts.green_dragons;

import org.tribot.api2007.Player;
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
import scripts.green_dragons.tasks.ChangeWorlds;
import scripts.green_dragons.tasks.Dragons;
import scripts.green_dragons.tasks.Drop;
import scripts.green_dragons.tasks.Eat;
import scripts.green_dragons.tasks.Equip;
import scripts.green_dragons.tasks.Evade;
import scripts.green_dragons.tasks.Loot;
import scripts.green_dragons.tasks.Potions;
import scripts.green_dragons.tasks.ResetPlayer;
import scripts.green_dragons.threads.ThreatSearch;
import scripts.green_dragons.threads.WorldHop;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api.threads.SafeThread;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyItem;
import scripts.usa.api2007.looting.LootingBag;
import scripts.usa.api2007.observers.inventory.InventoryChange;
import scripts.usa.api2007.observers.inventory.InventoryListener;

@ScriptManifest(authors = { "Usa" }, category = "Money Making", name = "USA Green Dragons", version = 14.7)
public class UsaGreenDragons extends TaskScript implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	private SafeThread threatSearchThread;
	private SafeThread worldHopThread;

	@Override
	public void init() {
		setVars(new Vars(this));
		setJFX(new FxGUI(), new FxController());
		setPaint(new GreenDragonsPaint());
		setTasks(new Equip(), new Eat(), new Evade(), new ResetPlayer(), new Bank(), new ChangeWorlds(), new Drop(), new Loot(), new Potions(), new Dragons());
	}

	@Override
	public void onScriptStart() {
		threatSearchThread = new SafeThread(new ThreatSearch());
		worldHopThread = new SafeThread(new WorldHop());
		ABC.setAntiban(Vars.get().useAntiban);
		ABC.setReactionSleeping(Vars.get().useReactionTiming);
		ABC.setHover(false);
		ABC.setOpenMenu(false);
		Vars.get().looter.add("Bass", () -> !LootingBag.hasLootingBag());
		Vars.get().looter.add("Clue scroll (hard)");
	}

	@Override
	public void onScriptLoop() {
	}

	@Override
	public void onScriptEnd() {
		if (threatSearchThread != null)
			threatSearchThread.stop();
		if (worldHopThread != null)
			worldHopThread.stop();
	}

	@Override
	public void onInventoryChange(InventoryChange change, RSItem item, int count) {
		if (!Vars.get().location.getArea()
				.contains(Player.getPosition()))
			return;
		OSBuddyItem osbuddyItem = OSBuddy.get(item);
		if (osbuddyItem != null)
			Vars.get().profit += OSBuddy.get(item)
					.getAveragePrice() * count *
					(change == InventoryChange.INCREASE ? 1 : -1);
	}
}
