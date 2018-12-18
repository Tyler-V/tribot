package scripts.crafter;

import org.tribot.api.General;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.EventBlockingOverride;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.crafter.data.Crafting;
import scripts.crafter.data.Crafting.Products;
import scripts.crafter.data.Vars;
import scripts.crafter.gui.FxController;
import scripts.crafter.gui.FxGUI;
import scripts.crafter.paint.UsaCrafterPaint;
import scripts.crafter.tasks.Bank;
import scripts.crafter.tasks.Craft;
import scripts.crafter.tasks.Furnace;
import scripts.crafter.tasks.SelectProduct;
import scripts.crafter.tasks.Spin;
import scripts.usa.api.framework.task.TaskScript;
import scripts.usa.api2007.observers.inventory.InventoryChange;
import scripts.usa.api2007.observers.inventory.InventoryListener;

@ScriptManifest(authors = { "Usa" }, category = "Crafting", name = "USA Crafter", version = 6.1)
public class UsaCrafter extends TaskScript implements Painting, Ending, EventBlockingOverride, InventoryListener, MessageListening07 {

	@Override
	public void init() {
		setVars(new Vars(this));
		setJFX(new FxGUI(), new FxController());
		setPaint(new UsaCrafterPaint());
		setTasks(new Bank(), new SelectProduct(), new Craft(), new Furnace(), new Spin());
	}

	@Override
	public void onScriptStart() {
		if (Vars.get().autoProgression) {
			Vars.get().product = Crafting.getHighestProductFor(Vars.get().material);
			if (Vars.get().product == null) {
				General.println("We do not high enough Crafting level to make any items for " + Vars.get().material);
				Vars.get().stopScript();
			}
		}
		General.println("We are making " + Vars.get().product.getName() + (Vars.get().location != null ? " at " + Vars.get().location.getName() : " in bank"));
	}

	@Override
	public void onScriptLoop() {
		if (Vars.get().autoProgression) {
			Products product = Crafting.getHighestProductFor(Vars.get().material);
			if (product != Vars.get().product && product.getLevel() > Vars.get().product.getLevel()) {
				General.println("Switching from " + Vars.get().product.getName() + " to " + product.getName());
				Vars.get().product = product;
			}
		}
	}

	@Override
	public void onScriptEnd() {
	}

	@Override
	public void onInventoryChange(InventoryChange change, RSItem item, int count) {
		if (change == InventoryChange.INCREASE && Vars.get().product.getID() == item.getID()) {
			Vars.get().profit += count * Vars.get().product.getProfit();
			Vars.get().items++;
		}
	}
}
