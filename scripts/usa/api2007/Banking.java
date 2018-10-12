package scripts.usa.api2007;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Player;
import org.tribot.api2007.Screen;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
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
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

public class Banking extends org.tribot.api2007.Banking {

	private final static int BANK_MASTER = 12;
	private final static int BANK_ITEMS_COUNT_CHILD = 5;
	private final static int BANK_NOTE_CHILD = 35;
	private final static int BANK_CLOSE_CHILD = 14;
	private final static int BANK_CLOSE_COMPONENT = 11;

	private final static int COLLECTION_BOX_MASTER = 402;
	private final static int COLLECTION_BOX_CLOSE_CHILD = 2;
	private final static int COLLECTION_BOX_CLOSE_COMPONENT = 11;

	public static boolean open() {
		if (isInBank() || isAtBank()) {
			ScriptVars.get().status = "Opening Bank";
			if (!isOpen()) {
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
		RSObject[] objects = Entities.find(ObjectEntity::new).nameContains("Bank").getResults();
		RSNPC[] npcs = Entities.find(NpcEntity::new).nameEquals("Banker").getResults();
		return npcs.length > 0 || objects.length > 0;
	}

	public static boolean isCollectionBoxOpen() {
		return Interfaces.isInterfaceValid(COLLECTION_BOX_MASTER);
	}

	public static boolean closeCollectionBox() {
		if (!isCollectionBoxOpen())
			return true;
		RSInterfaceChild child = Interfaces.get(COLLECTION_BOX_MASTER, COLLECTION_BOX_CLOSE_CHILD);
		if (child == null)
			return false;
		RSInterfaceComponent component = child.getChild(COLLECTION_BOX_CLOSE_COMPONENT);
		if (component == null)
			return false;
		if (component.click()) {
			Condition.wait(() -> isCollectionBoxOpen());
		}
		return !isCollectionBoxOpen();
	}

	public static boolean isOpen() {
		return isBankScreenOpen() && isLoaded();
	}

	public static boolean close() {
		if (!isOpen())
			return true;

		if (VarBits.isEscKeybinded()) {
			Keyboard.pressKeys(KeyEvent.VK_ESCAPE);
			return Condition.wait(() -> !isOpen());
		}
		else {
			RSInterfaceChild child = Interfaces.get(BANK_MASTER, BANK_CLOSE_CHILD);
			if (child == null)
				return false;

			RSInterfaceComponent component = child.getChild(BANK_CLOSE_COMPONENT);
			if (component == null)
				return false;

			if (component.click())
				return Condition.wait(() -> !isOpen());
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
		if (withdraw(quantity, find(predicate)))
			return true;
		Condition.wait(() -> find(predicate).length > 0);
		return false;
	}

	public static boolean withdraw(int quantity, String... names) {
		if (withdraw(quantity, find(names)))
			return true;
		Condition.wait(() -> find(names).length > 0);
		return false;
	}

	public static boolean withdraw(int quantity, Filter<RSItem> filter) {
		if (withdraw(quantity, find(filter)))
			return true;
		Condition.wait(() -> find(filter).length > 0);
		return false;
	}

	private static boolean withdraw(int quantity, RSItem... items) {
		if (ScriptVars.get().fail >= 3) {
			General.println("Failed withdrawing items!");
			ScriptVars.get().stopScript();
			return true;
		}

		if (!open())
			return false;

		ScriptVars.get().status = "Withdrawing...";

		if (items.length == 0) {
			ScriptVars.get().fail++;
			return false;
		}

		RSItem item = items[0];
		String name = RSItemUtils.getName(item);
		int count = Inventory.getCount(item);
		int amount = quantity - count;

		if (amount == 0)
			return true;

		if (amount < 0) {
			deposit(Math.abs(quantity - count), item);
		}
		else if (amount > 0) {
			if (quantity == Integer.MAX_VALUE) {
				amount = item.getStack();
				ScriptVars.get().status = "Withdrawing all " + name;
			}
			else {
				if (28 - Inventory.getAll().length == quantity) {
					amount = 0;
					ScriptVars.get().status = "Withdrawing all " + name;
				}
				else {
					amount = Math.abs(quantity - count);
					ScriptVars.get().status = "Withdrawing " + amount + " " + name;
				}
			}
			if (withdraw(amount, item.getID()))
				Condition.wait(() -> Inventory.getCount(item.getID()) >= quantity);
		}

		if (Inventory.getCount(item.getID()) >= quantity) {
			ScriptVars.get().fail = 0;
			return true;
		}
		else {
			ScriptVars.get().fail++;
			return false;
		}
	}

	public static boolean deposit(int count, RSItem item) {
		if (item == null)
			return false;

		String name = RSItemUtils.getName(item);
		ScriptVars.get().status = "Depositing " + (count > 0 ? (count + " ") : "") + name;

		return deposit(count, item.getID());
	}

	public static boolean deposit(RSItem... items) {
		General.println(Arrays.asList(items));

		if (!isOpen())
			return false;

		if (Inventory.getCount(items) == 0)
			return true;

		List<RSItem> list = Arrays.stream(items).distinct().collect(Collectors.toList());
		for (RSItem item : list) {
			if (deposit(0, item))
				Condition.wait(Conditions.inventoryDoesNotContain(item));
		}

		return Inventory.getCount(items) == 0;
	}

	@SuppressWarnings("unchecked")
	public static boolean depositExcept(List<Predicate<RSItem>> predicates) {
		return depositExcept(predicates.stream().toArray(Predicate[]::new));
	}

	@SafeVarargs
	public static boolean depositExcept(Predicate<RSItem>... predicates) {
		List<RSItem> list = Arrays.stream(predicates)
				.map(predicate -> Inventory.find(predicate))
				.flatMap(Arrays::stream)
				.distinct()
				.collect(Collectors.toList());
		RSItem[] items = Arrays.stream(Inventory.getAll()).filter(item -> !list.contains(item)).toArray(RSItem[]::new);
		return deposit(items);
	}

	public static boolean depositExcept(String... names) {
		return deposit(Inventory.find(Filters.Items.nameNotEquals(names)));
	}

	public static boolean depositExcept(int... ids) {
		return deposit(Inventory.find(Filters.Items.idNotEquals(ids)));
	}

	public static boolean isNoteSelected() {
		RSInterface inter = Entities.find(InterfaceEntity::new).inMaster(BANK_MASTER).actionEquals("Note").isNotHidden().getFirstResult();
		if (inter == null)
			return false;
		Rectangle bounds = inter.getAbsoluteBounds();
		if (bounds == null)
			return false;
		return Screen.getColorAt(new Point(bounds.x + 10, bounds.y + 10)).getRed() > 100;
	}

	public static boolean setNoteSelected(boolean selected) {
		if (isNoteSelected() == selected)
			return true;
		RSInterface inter = Entities.find(InterfaceEntity::new).inMaster(BANK_MASTER).actionEquals("Note").isNotHidden().getFirstResult();
		if (inter == null)
			return false;
		if (inter.click())
			return Condition.wait(() -> isNoteSelected() == selected);
		return isNoteSelected() == selected;
	}
}
