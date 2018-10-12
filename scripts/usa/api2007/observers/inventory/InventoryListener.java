package scripts.usa.api2007.observers.inventory;

import org.tribot.api2007.types.RSItem;

public interface InventoryListener {

	public void onInventoryChange(InventoryChange change, RSItem item, int count);

}
