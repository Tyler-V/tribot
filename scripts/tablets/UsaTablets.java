package scripts.tablets;

import org.tribot.api.General;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.tablets.data.Vars;
import scripts.tablets.gui.TabletsFxController;
import scripts.tablets.gui.TabletsFxGUI;
import scripts.tablets.paint.TabletsPaint;
import scripts.tablets.tasks.EnterHouse;
import scripts.tablets.tasks.MakeTablets;
import scripts.tablets.tasks.Phials;
import scripts.tablets.tasks.ServantRequest;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api2007.observers.inventory.InventoryChange;
import scripts.usa.api2007.observers.inventory.InventoryListener;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

@ScriptManifest(authors = { "Usa" }, category = "Money Making", name = "USA Tablets", version = 11.4)
public class UsaTablets extends TaskScript implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	@Override
	public void init() {
		setVars(new Vars(this));
		setPaint(new TabletsPaint());
		setJFX(new TabletsFxGUI(), new TabletsFxController());
		setTasks(new MakeTablets(), new Phials(), new ServantRequest(), new EnterHouse());
	}

	@Override
	public void onScriptStart() {
		ABC.setAntiban(Vars.get().antiban);
		ABC.setReactionSleeping(Vars.get().reactionSleep);
	}

	@Override
	public void onScriptLoop() {
	}

	@Override
	public void onScriptEnd() {
	}

	@Override
	public void onInventoryChange(InventoryChange change, RSItem item, int count) {
		if (change == InventoryChange.INCREASE) {
			if (Vars.get().servant && RSItemUtils.nameEquals(item, "Soft clay"))
				return;
			if (Vars.get().tablet.getOSBuddyItem().getId() == item.getID())
				Vars.get().tablets += count;
		}
		Vars.get().profit += OSBuddy.get(item).getAveragePrice() * count * (change == InventoryChange.INCREASE ? 1 : -1);
	}

	@Override
	public void serverMessageReceived(String message) {
		if (message.contains("you need")) {
			General.println(message);
			Vars.get().stopScript();
		}
		if (message.contains("That player is offline")) {
			General.println(message);
			Vars.get().stopScript();
		}
	}
}
