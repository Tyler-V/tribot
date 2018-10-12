package scripts.clay_miner;

import org.tribot.api2007.types.RSItem;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.clay_miner.data.Vars;
import scripts.clay_miner.paint.ClayMinerPaint;
import scripts.clay_miner.tasks.Bank;
import scripts.clay_miner.tasks.MineClay;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api2007.observers.inventory.InventoryChange;
import scripts.usa.api2007.observers.inventory.InventoryListener;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

@ScriptManifest(authors = { "Usa" }, category = "Mining", name = "USA Clay Miner", version = 1.0)
public class UsaClayMiner extends TaskScript implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	@Override
	public void init() {
		setVars(new Vars(this));
		setPaint(new ClayMinerPaint());
		setTasks(new MineClay(), new Bank());
	}

	@Override
	public void onScriptStart() {
		super.setAIAntibanState(false);
		ABC.setAntiban(false);
		ABC.setSleepReaction(false);
		ABC.setAlwaysHover();
		ABC.setAlwaysOpenMenu();
	}

	@Override
	public void onScriptEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInventoryChange(InventoryChange change, RSItem item, int count) {
		if (RSItemUtils.nameContains(item, "Clay")) {
			if (change == InventoryChange.DECREASE)
				return;
			Vars.get().clay += count;
		}
		Vars.get().profit += OSBuddy.get(item).getAveragePrice() * count * (change == InventoryChange.INCREASE ? 1 : -1);
	}
}
