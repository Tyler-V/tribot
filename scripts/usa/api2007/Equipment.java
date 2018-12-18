package scripts.usa.api2007;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tribot.api.General;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSPlayerDefinition;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.util.Strings;
import scripts.usa.api2007.enums.Staffs;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

public class Equipment extends org.tribot.api2007.Equipment {

	public static String[] getEquippableItems() {
		return getEquippableItemsExcept(new String[0]);
	}

	public static String[] getEquippableItemsExcept(String... names) {
		final String[] ACTIONS = { "Wear", "Wield", "Equip" };
		List<String> list = new ArrayList<String>();
		for (RSItem item : Inventory.getAll()) {
			if (item == null)
				continue;
			RSItemDefinition definition = item.getDefinition();
			if (definition == null)
				continue;
			String name = definition.getName();
			if (name == null)
				continue;
			if (names.length > 0 && Arrays.stream(names)
					.parallel()
					.anyMatch(name::contains))
				continue;
			String[] actions = definition.getActions();
			if (actions.length == 0)
				continue;
			for (String action : actions) {
				if (Arrays.stream(ACTIONS)
						.parallel()
						.anyMatch(action::contains)) {
					list.add(name);
					break;
				}
			}
		}
		return list.toArray(new String[0]);
	}

	public static boolean equipAll() {
		String[] names = getEquippableItems();
		if (names.length == 0)
			return true;

		for (String name : names)
			equip(name);

		return getEquippableItems().length == 0;
	}

	public static boolean equip(String... names) {
		RSItem[] items = Inventory.find(names);
		if (items.length == 0)
			return false;

		for (RSItem item : items) {
			if (!equip(item))
				return false;
		}

		return true;
	}

	public static boolean equip(Predicate<RSItem> filter) {
		RSItem[] items = Inventory.find(filter);
		if (items.length == 0)
			return false;

		for (RSItem item : items) {
			if (!equip(item))
				return false;
		}

		return true;
	}

	public static boolean isPlayerWearing(RSPlayer player, String... equipment) {
		if (player == null || equipment.length == 0)
			return false;

		RSPlayerDefinition playerDefinition = player.getDefinition();
		if (playerDefinition == null)
			return false;

		RSItem[] playerEquipment = playerDefinition.getEquipment();
		if (playerEquipment.length == 0)
			return false;

		for (RSItem item : playerEquipment) {
			RSItemDefinition itemDefinition = item.getDefinition();
			if (itemDefinition == null)
				continue;

			String name = itemDefinition.getName();
			if (name == null)
				continue;

			if (Arrays.stream(equipment)
					.anyMatch(e -> name.toLowerCase()
							.contains(e.toLowerCase())))
				return true;
		}

		return false;
	}

	public static String getEquipment(RSPlayer player) {
		if (player == null)
			return null;

		RSPlayerDefinition playerDefinition = player.getDefinition();
		if (playerDefinition == null)
			return null;

		RSItem[] playerEquipment = playerDefinition.getEquipment();
		if (playerEquipment.length == 0)
			return null;

		List<String> list = new ArrayList<String>();

		for (RSItem item : playerEquipment) {
			RSItemDefinition itemDefinition = item.getDefinition();
			if (itemDefinition == null)
				continue;

			String name = itemDefinition.getName();
			if (name == null)
				continue;

			list.add(name);
		}

		return list.stream()
				.collect(Collectors.joining(", "));
	}

	public static boolean equip(RSItem item) {
		if (item == null)
			return false;

		Interfaces.closeAll();

		ScriptVars.get().status = "Equipping " + RSItemUtils.getName(item);

		final int count = Equipment.getCount(item.getID());
		if (item.click())
			return Condition.wait(() -> count > 0 ? Equipment.getCount(item.getID()) > count : isEquipped(item.getID()));

		return isEquipped(item);
	}

	public static boolean isEquipped(RSItem item) {
		if (item == null)
			return false;

		return Equipment.isEquipped(item.getID());
	}

	public static boolean equipExcept(String... names) {
		for (String item : getEquippableItemsExcept(names)) {
			equip(item);
		}
		return getEquippableItemsExcept(names).length == 0;
	}

	public static boolean hasMatchingEquipment(RSPlayer player, String... names) {
		return getMatchingEquipment(player, names) != null;
	}

	public static String getMatchingEquipment(RSPlayer player, String... names) {
		List<String> items = new ArrayList<String>();
		if (player == null)
			return null;
		RSPlayerDefinition playerDefinition = player.getDefinition();
		if (playerDefinition == null)
			return null;
		RSItem[] equipment = playerDefinition.getEquipment();
		for (RSItem item : equipment) {
			RSItemDefinition itemDefinition = item.getDefinition();
			if (itemDefinition == null)
				continue;
			String name = itemDefinition.getName();
			if (name == null)
				continue;
			if (Arrays.stream(names)
					.map(String::toLowerCase)
					.parallel()
					.anyMatch(name.toLowerCase()::contains))
				items.add(name);
		}
		return items.isEmpty() ? null : String.join(",", items);
	}

	public static Staffs getStaff() {
		RSItem weapon = Equipment.getItem(SLOTS.WEAPON);
		if (weapon == null)
			return null;

		RSItemDefinition definition = weapon.getDefinition();
		if (definition == null)
			return null;

		String name = definition.getName();
		if (name == null)
			return null;

		try {
			return Staffs.valueOf(Strings.toEnumCase(name));
		}
		catch (Exception e) {
			return null;
		}
	}

	public static List<Integer> getEquipment(SLOTS... slots) {
		List<Integer> ids = new ArrayList<Integer>();
		for (SLOTS slot : slots) {
			int id = getID(slot);
			if (id != -1)
				ids.add(id);
		}
		return ids;
	}

	public static String getName(SLOTS slot) {
		RSItem item = Equipment.getItem(slot);
		if (item == null)
			return null;

		RSItemDefinition definition = item.getDefinition();
		if (definition == null)
			return null;

		return definition.getName();
	}

	public static int getID(SLOTS slot) {
		RSItem item = Equipment.getItem(slot);
		if (item == null)
			return -1;

		return item.getID();
	}

	public static boolean isWearing(List<Predicate<RSItem>> predicates) {
		return predicates.stream()
				.allMatch(predicate -> Equipment.isEquipped(predicate));
	}

	public static List<Predicate<RSItem>> getPredicate(SLOTS... slots) {
		List<Predicate<RSItem>> predicates = new ArrayList<Predicate<RSItem>>();

		for (SLOTS slot : slots) {
			Predicate<RSItem> predicate = getPredicate(slot);
			if (predicate != null)
				predicates.add(predicate);
		}

		return predicates;
	}

	public static Predicate<RSItem> getPredicate(SLOTS slot) {
		RSItem item = Equipment.getItem(slot);
		if (item == null)
			return null;

		RSItemDefinition definition = item.getDefinition();
		if (definition == null)
			return null;

		String name = definition.getName();
		if (name == null)
			return null;

		if (name.contains("(")) {
			General.println(name.replaceAll("\\(.?\\)", ""));
			return Filters.Items.nameContains(name)
					.and(Filters.Items.nameContains("("));
		}
		else {
			return Filters.Items.idEquals(item.getID());
		}
	}
}
