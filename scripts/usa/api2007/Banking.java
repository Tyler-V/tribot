package scripts.usa.api2007;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Player;
import org.tribot.api2007.Screen;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.entity.selector.prefabs.NpcEntity;
import scripts.usa.api2007.entity.selector.prefabs.ObjectEntity;
import scripts.usa.api2007.looting.LootingBag;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

public class Banking extends org.tribot.api2007.Banking {

	private final static int BANK_MASTER = 12;
	private final static int BANK_ITEMS_COUNT_CHILD = 5;

	private final static int COLLECTION_BOX_MASTER = 402;

	public static boolean open() {
		if (isInBank() || isAtBank()) {
			if (!isOpen()) {
				ScriptVars.get().status = "Opening Bank";
				Interfaces.closeAll();
				openBank();
			}
		}
		else {
			ScriptVars.get().status = "Traveling to Bank";
			if (!Player.isMoving())
				Walking.travelToBank();
		}
		return isOpen();
	}

	public static boolean isAtBank() {
		RSObject[] objects = Entities.find(ObjectEntity::new)
				.nameContains("Bank")
				.getResults();
		RSNPC[] npcs = Entities.find(NpcEntity::new)
				.nameEquals("Banker")
				.getResults();
		return npcs.length > 0 || objects.length > 0;
	}

	public static boolean isCollectionBoxOpen() {
		return Interfaces.isInterfaceValid(COLLECTION_BOX_MASTER);
	}

	public static boolean closeCollectionBox() {
		if (!isCollectionBoxOpen())
			return true;

		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(COLLECTION_BOX_MASTER)
				.actionEquals("Close")
				.getFirstResult();
		if (inter != null && inter.click())
			return Condition.wait(() -> !isCollectionBoxOpen());

		return !isCollectionBoxOpen();
	}

	public static boolean isOpen() {
		return isBankScreenOpen() && isLoaded();
	}

	public static boolean close() {
		if (!isOpen())
			return true;

		try {
			if (VarBits.isEscKeybinded()) {
				Keyboard.pressKeys(KeyEvent.VK_ESCAPE);
				return Condition.wait(() -> !isOpen());
			}
		}
		finally {
			if (isOpen()) {
				RSInterface inter = Entities.find(InterfaceEntity::new)
						.inMaster(BANK_MASTER)
						.actionEquals("Close")
						.getFirstResult();
				if (inter != null && inter.click())
					return Condition.wait(() -> !isOpen());
			}
		}

		return !isOpen();
	}

	public static boolean isLoaded() {
		RSInterfaceChild child = Interfaces.get(BANK_MASTER, BANK_ITEMS_COUNT_CHILD);
		if (child == null)
			return false;

		String text = child.getText();
		if (text == null || text.isEmpty())
			return false;

		int count = Integer.parseInt(text);
		if (count == 0)
			return true;

		return Condition.wait(() -> getAll().length > 0);
	}

	public static boolean has(String... names) {
		if (!isOpen() || names.length == 0)
			return false;

		return find(names).length > 0;
	}

	public static boolean withdraw(int quantity, Predicate<RSItem> predicate) {
		if (!isOpen())
			return false;

		int count = Inventory.getCount(predicate);
		int amount = quantity - count;
		if (amount < 0) {
			return deposit(Math.abs(amount), predicate);
		}
		else if (amount == 0) {
			return true;
		}
		else {
			return withdraw(quantity, find(predicate));
		}
	}

	public static boolean withdraw(int quantity, String... names) {
		if (!isOpen())
			return false;

		if (quantity == 0)
			return withdraw(quantity, find(names));

		int count = Inventory.getCount(names);
		int amount = quantity - count;
		if (amount < 0) {
			return deposit(Math.abs(amount), names);
		}
		else if (amount > 0) {
			return withdraw(quantity, find(names));
		}
		else {
			return true;
		}
	}

	private static boolean withdraw(int quantity, RSItem... items) {
		if (ScriptVars.get().fail >= 3) {
			General.println("Failed withdrawing items!");
			ScriptVars.get()
					.stopScript();
			return true;
		}

		if (_withdraw(quantity, items)) {
			ScriptVars.get().fail = 0;
			return true;
		}
		else {
			ScriptVars.get().fail++;
			General.sleep(1000);
			return false;
		}
	}

	private static boolean _withdraw(int quantity, RSItem... items) {
		if (ScriptVars.get().fail >= 3) {
			General.println("Failed withdrawing items!");
			ScriptVars.get()
					.stopScript();
			return true;
		}

		if (items.length == 0) {
			ScriptVars.get().fail++;
			General.sleep(1000);
			return false;
		}

		RSItem item = items[0];
		String name = RSItemUtils.getName(item);
		int count = Inventory.getCount(item);
		int amount = quantity - count;

		if (quantity == Integer.MAX_VALUE || quantity == 0) {
			amount = 0;
			ScriptVars.get().status = "Withdrawing all " + name;
		}
		else {
			amount = Math.abs(quantity - count);
			ScriptVars.get().status = "Withdrawing " + amount + " " + name;
		}

		withdraw(amount, item.getID());

		return Condition.wait(() -> {
			if (quantity == Integer.MAX_VALUE || quantity == 0) {
				return Banking.find(item.getID()).length == 0;
			}
			else {
				return Inventory.getCount(item.getID()) >= (quantity - count);
			}
		});
	}

	public static boolean deposit(int count, Predicate<RSItem> predicate) {
		RSItem[] items = Inventory.find(predicate);
		if (items.length == 0)
			return true;

		return deposit(count, items[0]);
	}

	public static boolean deposit(RSItem... items) {
		if (!isOpen())
			return false;

		if (Inventory.getCount(items) == 0)
			return true;

		List<RSItem> list = Arrays.stream(items)
				.distinct()
				.collect(Collectors.toList());
		for (RSItem item : list) {
			if (deposit(0, item))
				Condition.wait(Conditions.inventoryDoesNotContain(item));
		}

		return Inventory.getCount(items) == 0;
	}

	public static boolean deposit(int count, RSItem item) {
		if (item == null)
			return false;

		if (LootingBag.Banking.isViewing())
			LootingBag.Banking.dismiss();

		String name = RSItemUtils.getName(item);
		ScriptVars.get().status = "Depositing " + (count > 0 ? (count + " ") : "") + name;

		return deposit(count, item.getID());
	}

	@SuppressWarnings("unchecked")
	public static boolean depositExcept(List<Predicate<RSItem>> predicates) {
		return depositExcept((Predicate<RSItem>[]) predicates.toArray(new Predicate[predicates.size()]));
	}

	@SuppressWarnings("unchecked")
	public static boolean depositOneExcept(List<Predicate<RSItem>> predicates) {
		return depositOneExcept((Predicate<RSItem>[]) predicates.toArray(new Predicate[predicates.size()]));
	}

	@SafeVarargs
	public static boolean depositOneExcept(Predicate<RSItem>... predicates) {
		List<RSItem> list = Arrays.stream(predicates)
				.map(predicate -> Inventory.find(predicate))
				.flatMap(Arrays::stream)
				.distinct()
				.collect(Collectors.toList());
		return deposit(Arrays.stream(Inventory.getAll())
				.filter(item -> !list.contains(item))
				.findFirst()
				.get());
	}

	@SafeVarargs
	public static boolean depositExcept(Predicate<RSItem>... predicates) {
		List<RSItem> list = Arrays.stream(predicates)
				.map(predicate -> Inventory.find(predicate))
				.flatMap(Arrays::stream)
				.distinct()
				.collect(Collectors.toList());
		if (list.size() == 0) {
			if (Inventory.getAll().length == 0) {
				return true;
			}
			else {
				Banking.depositAll();
				return Condition.wait(() -> Inventory.getAll().length == 0);
			}
		}

		RSItem[] items = Arrays.stream(Inventory.getAll())
				.filter(item -> !list.contains(item))
				.toArray(RSItem[]::new);
		return deposit(items);
	}

	public static boolean depositExcept(String... names) {
		RSItem[] items = Inventory.find(Filters.Items.nameEquals(names));
		if (items.length == 0) {
			if (Inventory.getAll().length == 0) {
				return true;
			}
			else {
				Banking.depositAll();
				return Condition.wait(() -> Inventory.getAll().length == 0);
			}
		}

		return deposit(Inventory.find(Filters.Items.nameNotEquals(names)));
	}

	public static boolean depositExcept(int... ids) {
		RSItem[] items = Inventory.find(Filters.Items.idEquals(ids));
		if (items.length == 0) {
			if (Inventory.getAll().length == 0) {
				return true;
			}
			else {
				Banking.depositAll();
				return Condition.wait(() -> Inventory.getAll().length == 0);
			}
		}

		return deposit(Inventory.find(Filters.Items.idNotEquals(ids)));
	}

	public static boolean isNoteSelected() {
		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(BANK_MASTER)
				.actionEquals("Note")
				.isNotHidden()
				.getFirstResult();
		if (inter == null)
			return false;
		Rectangle bounds = inter.getAbsoluteBounds();
		if (bounds == null)
			return false;
		return Screen.getColorAt(new Point(bounds.x + 10, bounds.y + 10))
				.getRed() > 100;
	}

	public static boolean setNoteSelected(boolean selected) {
		if (isNoteSelected() == selected)
			return true;
		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(BANK_MASTER)
				.actionEquals("Note")
				.isNotHidden()
				.getFirstResult();
		if (inter == null)
			return false;
		if (inter.click())
			return Condition.wait(() -> isNoteSelected() == selected);
		return isNoteSelected() == selected;
	}
}
