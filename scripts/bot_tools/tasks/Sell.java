package scripts.bot_tools.tasks;

import java.util.Iterator;

import org.tribot.api.General;

import scripts.bot_tools.data.Vars;
import scripts.bot_tools.models.GEItem;
import scripts.usa.api.framework.task.Task;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Inventory;
import scripts.usa.api2007.grand_exchange.GrandExchange;
import scripts.usa.api2007.grand_exchange.enums.CollectionMethod;

public class Sell implements Task {

	@Override
	public boolean validate() {
		return !Vars.get().sellingItems.isEmpty();
	}

	@Override
	public void execute() {
		if (getItem() != null) {
			if (Banking.close() && GrandExchange.open()) {
				if (GrandExchange.hasOpenSlot()) {
					GEItem item = getItem();
					if (GrandExchange.sell(item.getName(), item.getPricePercentage()))
						Vars.get().sellingItems.remove(item);
				}
				else {
					GrandExchange.collect(CollectionMethod.INVENTORY);
				}
			}
		}
		else {
			if (Banking.open()) {
				if (Banking.setNoteSelected(true)) {
					for (Iterator<GEItem> iter = Vars.get().sellingItems.iterator(); iter.hasNext();) {
						if (Inventory.isFull()) {
							Vars.get().status = "Closing Bank";
							Banking.close();
						}
						GEItem item = iter.next();
						if (!Banking.has(item.getName())) {
							General.println("Removing " + item.getName());
							iter.remove();
							continue;
						}
						if (!Banking.withdraw(Integer.MAX_VALUE, item.getName())) {
							General.println("Removing " + item.getName());
							iter.remove();
						}
					}
				}
			}
		}
	}

	private GEItem getItem() {
		for (GEItem item : Vars.get().sellingItems) {
			if (Inventory.has(item.getName()))
				return item;
		}
		return null;
	}
}
