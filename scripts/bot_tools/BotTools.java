package scripts.bot_tools;

import org.tribot.api2007.types.RSItem;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.bot_tools.data.Vars;
import scripts.bot_tools.gui.FxController;
import scripts.bot_tools.gui.FxGUI;
import scripts.bot_tools.tasks.Sell;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api2007.observers.inventory.InventoryChange;
import scripts.usa.api2007.observers.inventory.InventoryListener;

@ScriptManifest(authors = { "Usa" }, category = "Tools", name = "USA Bot Tools", version = 1.0)
public class BotTools extends TaskScript implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	@Override
	public void init() {
		setVars(new Vars(this));
		setJFX(new FxGUI(), new FxController());
		setTasks(new Sell());
	}

	@Override
	public void onScriptStart() {
	}

	@Override
	public void onScriptLoop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScriptEnd() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInventoryChange(InventoryChange change, RSItem item, int count) {
		// TODO Auto-generated method stub
	}
}
