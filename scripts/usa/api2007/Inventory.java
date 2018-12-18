package scripts.usa.api2007;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tribot.api2007.GameTab;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.framework.task.ScriptVars;

public class Inventory extends org.tribot.api2007.Inventory {

	public static boolean open() {
		if (GameTab.getOpen() == GameTab.TABS.INVENTORY)
			return true;
		GameTab.open(GameTab.TABS.INVENTORY);
		return Condition.wait(() -> GameTab.getOpen() == GameTab.TABS.INVENTORY);
	}

	public static int getSpace() {
		return 28 - Inventory.getAll().length;
	}

	public static int getCount(List<String> names) {
		return Inventory.getCount(names.toArray(new String[names.size()]));
	}

	public static int getCount(RSItem item) {
		return item == null ? 0 : Inventory.getCount(item.getID());
	}

	public static int getCount(RSItem... items) {
		int count = 0;
		List<RSItem> list = Arrays.stream(items)
				.distinct()
				.collect(Collectors.toList());
		for (RSItem item : list)
			count += getCount(item);
		return count;
	}

	public static int getCount(Predicate<RSItem> predicate) {
		RSItem[] items = Inventory.find(predicate);
		if (items.length == 0)
			return 0;

		return getCount(items[0]);
	}

	public static boolean has(String name) {
		return Inventory.find(name).length > 0;
	}

	public static boolean has(Predicate<RSItem> predicate) {
		return Inventory.find(predicate).length > 0;
	}

	public static boolean drop(RSItem item) {
		if (item == null)
			return false;

		RSItemDefinition definition = item.getDefinition();
		if (definition == null)
			return false;

		String name = definition.getName();
		if (name == null)
			return false;

		ScriptVars.get().status = "Dropping " + name;

		final int count = Inventory.getCount(item);
		if (item.click("Drop"))
			Condition.wait(() -> count != Inventory.getCount(item));

		return count != Inventory.getCount(item);
	}

	public static boolean drop(int amount, String... names) {
		RSItem[] items = Inventory.find(names);
		if (items.length == 0)
			return false;

		for (RSItem item : items)
			drop(item);

		return !Inventory.has(Filters.Items.nameEquals(names));
	}

	public static boolean drop(Predicate<RSItem> predicate) {
		RSItem[] items = Inventory.find(predicate);
		if (items.length == 0)
			return false;

		for (RSItem item : items)
			drop(item);

		return !Inventory.has(predicate);
	}
}
