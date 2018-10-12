package scripts.usa.api2007.utils.RSItem;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSTile;

import scripts.usa.api.condition.Conditions;
import scripts.usa.api.condition.ResultCondition;
import scripts.usa.api.condition.Status;
import scripts.usa.api2007.NPCChat;

public class RSItemUtils {

	public static boolean nameContains(RSItem item, String name) {
		try {
			return getName(item).toLowerCase().contains(name.toLowerCase());
		}
		catch (Exception e) {
		}
		return false;
	}

	public static boolean nameEquals(RSItem item, String name) {
		try {
			return getName(item).toLowerCase().equalsIgnoreCase(name);
		}
		catch (Exception e) {
		}
		return false;
	}

	public static boolean teleport(Filter<RSItem> filter, String regex) {
		ArrayList<RSItem> items = new ArrayList<>();
		items.addAll(Arrays.asList(ArrayUtils.addAll(Inventory.find(filter), Equipment.find(filter))));
		if (items.size() == 0)
			return false;

		if (!clickRegex(items.get(0), "(Rub|" + regex + ")"))
			return false;

		RSTile startingPosition = Player.getPosition();
		return ResultCondition.Success.wait(() -> {
			if (Conditions.hasPlayerMoved(startingPosition).isTrue())
				return Status.SUCCESS;
			if (Conditions.isPlayerActive().isTrue() || NPCChat.handleConversation(regex))
				return Status.RESET;
			return Status.CONTINUE;
		});
	}

	public static boolean click(String itemNameRegex, String itemAction) {
		return click(new Filter<RSItem>() {
			@Override
			public boolean accept(RSItem item) {
				return getName(item).matches(itemNameRegex) && Arrays.stream(getActions(item)).anyMatch(s -> s.equals(itemAction));
			}
		}, itemAction);
	}

	public static boolean clickRegex(RSItem item, String regex) {
		return item.click(new Filter<RSMenuNode>() {
			@Override
			public boolean accept(RSMenuNode node) {
				String action = node.getAction();
				return action != null && action.matches(regex);
			}
		});
	}

	public static boolean click(int itemID) {
		return click(itemID, null);
	}

	public static boolean click(int itemID, String action) {
		return click(Filters.Items.idEquals(itemID), action, true);
	}

	public static boolean click(Filter<RSItem> filter, String action) {
		return click(filter, action, true);
	}

	public static boolean click(Filter<RSItem> filter, String action, boolean one) {
		if (action == null) {
			action = "";
		}
		List<RSItem> list = Arrays.stream(Inventory.find(filter)).collect(Collectors.toList());
		if (one) {
			RSItem closest = getClosestToMouse(list);
			return closest != null && closest.click(action);
		}
		boolean value = false;
		while (!list.isEmpty()) {
			RSItem item = getClosestToMouse(list);
			if (item != null) {
				list.remove(item);
				if (item.click(action)) {
					value = true;
				}
			}
		}
		return value;
	}

	public static boolean click(RSItem item, String action) {
		if (Banking.isBankScreenOpen()) {
			Banking.close();
		}
		return action != null ? item.click(action) : item.click();
	}

	public static boolean use(int id) {
		String name = Game.getSelectedItemName();
		if (name == null)
			return false;

		RSItemDefinition definition = RSItemDefinition.get(id);
		if (definition == null)
			return false;

		if (Game.getItemSelectionState() == 1 && definition.getName().equals(name)) {
			return true;
		}
		else if (Game.getItemSelectionState() == 1) {
			Mouse.click(3);
			ChooseOption.select("Cancel");
		}

		return click(id, "Use");
	}

	public static RSItem getClosestToMouse(List<RSItem> items) {
		Point mouse = Mouse.getPos();
		items.sort(Comparator.comparingInt(o -> (int) getCenter(o.getArea()).distance(mouse)));
		return items.size() > 0 ? items.get(0) : null;
	}

	private static Point getCenter(Rectangle rectangle) {
		return new Point(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
	}

	public static RSItem get(Filter<RSItem> filter) {
		return getClosestToMouse(Arrays.stream(Inventory.find(filter)).collect(Collectors.toList()));
	}

	public static boolean isNoted(RSItem item) {
		return item != null && isNoted(item.getID());
	}

	public static boolean isNoted(int id) {
		RSItemDefinition definition = RSItemDefinition.get(id);
		return definition != null && definition.isNoted();
	}

	public static String[] getActions(RSGroundItem item) {
		return getActions(item.getDefinition());
	}

	public static String[] getActions(RSItem item) {
		return getActions(item.getDefinition());
	}

	public static String getName(int id) {
		return getName(RSItemDefinition.get(id));
	}

	public static String getName(RSGroundItem item) {
		return getName(item.getDefinition());
	}

	public static String getName(RSItem item) {
		return getName(item.getDefinition());
	}

	public static boolean isStackable(int id) {
		RSItemDefinition definition = RSItemDefinition.get(id);
		return definition != null && definition.isStackable();
	}

	public static boolean isStackable(RSItem item) {
		RSItemDefinition definition = item.getDefinition();
		return definition != null && definition.isStackable();
	}

	public static String[] getActions(RSItemDefinition definition) {
		if (definition == null) {
			return new String[0];
		}
		String[] actions = definition.getActions();
		return actions != null ? actions : new String[0];
	}

	public static String getName(RSItemDefinition definition) {
		if (definition == null)
			return null;

		String name = definition.getName();
		if (name == null)
			return null;

		return name;
	}

	public static int distanceToMouse(RSItem item) {
		Rectangle rectangle = item.getArea();
		if (rectangle == null) {
			return Integer.MAX_VALUE;
		}
		return (int) Mouse.getPos().distance(rectangle.x + rectangle.width, rectangle.y + rectangle.height);
	}
}
