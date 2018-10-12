package scripts.usa.api2007;

import java.util.ArrayList;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;

public class GeneralStore {

	private final static int GENERAL_STORE_MASTER = 300;
	private final static int GENERAL_STORE_CHILD_ITEMS = 2;
	private final static int GENERAL_STORE_OPTIONS_CHILD = 1;
	private final static int GENERAL_STORE_CLOSE_COMPONENT = 11;
	private final static int GENERAL_STORE_NAME_COMPONENT = 1;

	private final static int INVENTORY_MASTER = 301;
	private final static int INVENTORY_CHILD_ITEMS = 0;

	/**
	 * Option to be performed on the item in the General Store
	 */
	public static enum OPTION {

		BUY_1("Buy 1"),
		BUY_5("Buy 5"),
		BUY_10("Buy 10"),
		SELL_1("Sell 1"),
		SELL_5("Sell 5"),
		SELL_10("Sell 10"),
		SELL_50("Sell 50"),
		EXAMINE("Examine"),
		VALUE("Value"),
		CANCEL("Cancel");

		private String text;

		OPTION(String text) {
			this.text = text;
		}

		private String getText() {
			return text;
		}

	}

	/**
	 * Menu to be selected to perform actions on
	 */
	public static enum MENU {

		GENERAL_STORE,
		INVENTORY;

	}

	/**
	 * Checks if the General Store is open
	 * 
	 * @return true if open
	 */
	public static boolean isOpen() {
		return Interfaces.isInterfaceValid(GENERAL_STORE_MASTER);
	}

	/**
	 * Opens the General Store by selecting option Trade on the closest NPC with
	 * that option
	 * 
	 * @return true if successfully opened
	 */
	public static boolean open() {

		if (isOpen())
			return true;

		RSNPC[] npc = NPCs.findNearest(Filters.NPCs.actionsContains("Trade"));

		if (npc.length == 0)
			return false;

		if (npc[0].isOnScreen()) {

			String name = npc[0].getName();

			if (name != null) {

				if (npc[0].click("Trade " + name)) {

					Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(200);
							return isOpen();
						}
					}, 5000);

				}

			}

		}

		return isOpen();

	}

	/**
	 * Closes the General Store interface
	 * 
	 * @return true if successfully closed
	 */
	public static boolean close() {

		if (!isOpen())
			return true;

		RSInterfaceChild child = Interfaces.get(GENERAL_STORE_MASTER, GENERAL_STORE_OPTIONS_CHILD);

		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(GENERAL_STORE_CLOSE_COMPONENT);

		if (component == null)
			return false;

		if (component.click()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(200);
					return !isOpen();
				}
			}, 3000);

		}

		return isOpen();

	}

	/**
	 * Gets the name of the Store currently open
	 * 
	 * @return String
	 */
	public static String getName() {

		if (!isOpen())
			return null;

		RSInterfaceChild child = Interfaces.get(GENERAL_STORE_MASTER, GENERAL_STORE_OPTIONS_CHILD);

		if (child == null)
			return null;

		RSInterfaceComponent component = child.getChild(GENERAL_STORE_NAME_COMPONENT);

		if (component == null)
			return null;

		return component.getText();
	}

	/**
	 * Clicks an item in the chosen MENU interface by RSItem item and selects
	 * the OPTION chosen.
	 * 
	 * @param MENU
	 * @param OPTION
	 * @param String
	 * @return true if the item was clicked with the specified option
	 */
	public static boolean clickItem(MENU menu, OPTION option, String name) {

		if (!isOpen())
			return false;

		RSInterfaceComponent r = getItem(menu, name);

		if (r == null)
			return false;

		return r.click(option.getText());
	}

	/**
	 * Clicks an item in the chosen MENU interface by RSItem item and selects
	 * the OPTION chosen.
	 * 
	 * @param MENU
	 * @param OPTION
	 * @param RSItem
	 * @return true if the item was clicked with the specified option
	 */
	public static boolean clickItem(MENU menu, OPTION option, RSItem item) {

		if (!isOpen())
			return false;

		RSInterfaceComponent r = getItem(menu, item);

		if (r == null)
			return false;

		return r.click(option.getText());
	}

	/**
	 * Clicks an item in the chosen MENU interface by index location of the item
	 * and selects the OPTION chosen.
	 * 
	 * @param MENU
	 * @param OPTION
	 * @param int
	 * @return true if the item was clicked with the specified option
	 */
	public static boolean clickItem(MENU menu, OPTION option, int index) {

		if (!isOpen())
			return false;

		RSInterfaceComponent r = getItem(menu, index);

		if (r == null)
			return false;

		return r.click(option.getText());
	}

	/**
	 * Gets all the items from the selected MENU.
	 * 
	 * @param MENU
	 * @return List<RSItem>
	 */
	public static List<RSItem> getAllItems(MENU menu) {

		List<RSItem> items = new ArrayList<RSItem>();

		if (!isOpen())
			return items;

		if (menu == MENU.GENERAL_STORE) {

			RSInterfaceChild child = Interfaces.get(GENERAL_STORE_MASTER, GENERAL_STORE_CHILD_ITEMS);

			if (child == null)
				return items;

			RSInterfaceComponent[] components = child.getChildren();

			if (components.length == 0)
				return items;

			for (RSInterfaceComponent r : components) {

				if (r != null) {

					String name = r.getComponentName();

					if (name != null && !name.isEmpty()) {

						String[] actions = r.getActions();

						if (actions.length > 0) {

							items.add(new RSItem(name.replaceAll("<col=ff9040>", "").replaceAll("</col>", ""), actions, r.getComponentIndex(),
									r.getComponentItem(), r.getComponentStack(), RSItem.TYPE.OTHER));

						}

					}

				}

			}

		}
		else if (menu == MENU.INVENTORY) {

			RSInterfaceChild child = Interfaces.get(INVENTORY_MASTER, INVENTORY_CHILD_ITEMS);

			if (child == null)
				return items;

			RSInterfaceComponent[] components = child.getChildren();

			if (components.length == 0)
				return items;

			for (RSInterfaceComponent r : components) {

				if (r != null) {

					String name = r.getComponentName();

					if (name != null && !name.isEmpty()) {

						String[] actions = r.getActions();

						if (actions.length > 0) {

							items.add(new RSItem(name.substring(12), actions, r.getComponentIndex(), r.getComponentItem(), r.getComponentStack(),
									RSItem.TYPE.OTHER));

						}

					}

				}

			}

		}

		return items;

	}

	/**
	 * Gets an item by String name from the specified MENU.
	 * 
	 * @param MENU
	 * @param String
	 * @return RSInterfaceComponent
	 */
	public static RSInterfaceComponent getItem(MENU menu, String name) {

		if (!isOpen())
			return null;

		if (menu == MENU.GENERAL_STORE) {

			RSInterfaceChild child = Interfaces.get(GENERAL_STORE_MASTER, GENERAL_STORE_CHILD_ITEMS);

			if (child == null)
				return null;

			RSInterfaceComponent[] components = child.getChildren();

			if (components.length == 0)
				return null;

			for (RSInterfaceComponent r : components) {

				if (r != null) {

					String n = r.getComponentName();

					if (n != null && !n.isEmpty()) {

						n = n.replaceAll("<col=ff9040>", "").replaceAll("</col>", "");

						if (name.equalsIgnoreCase(n))

							return r;

					}

				}

			}

		}
		else if (menu == MENU.INVENTORY) {

			RSInterfaceChild child = Interfaces.get(INVENTORY_MASTER, INVENTORY_CHILD_ITEMS);

			if (child == null)
				return null;

			RSInterfaceComponent[] components = child.getChildren();

			if (components.length == 0)
				return null;

			for (RSInterfaceComponent r : components) {

				if (r != null) {

					String n = r.getComponentName();

					if (n != null && !n.isEmpty()) {

						n = n.substring(12);

						if (name.equalsIgnoreCase(n))

							return r;

					}

				}

			}

		}

		return null;
	}

	/**
	 * Gets an RSItem item from the specified MENU.
	 * 
	 * @param MENU
	 * @param RSItem
	 * @return RSInterfaceComponent
	 */
	public static RSInterfaceComponent getItem(MENU menu, RSItem item) {

		if (!isOpen())
			return null;

		RSItemDefinition d = item.getDefinition();

		if (d == null)
			return null;

		String name = d.getName();

		if (name == null)
			return null;

		if (menu == MENU.GENERAL_STORE) {

			RSInterfaceChild child = Interfaces.get(GENERAL_STORE_MASTER, GENERAL_STORE_CHILD_ITEMS);

			if (child == null)
				return null;

			RSInterfaceComponent[] components = child.getChildren();

			if (components.length == 0)
				return null;

			for (RSInterfaceComponent r : components) {

				if (r != null) {

					String n = r.getComponentName();

					if (n != null && !n.isEmpty()) {

						n = n.replaceAll("<col=ff9040>", "").replaceAll("</col>", "");

						if (name != null && name.equalsIgnoreCase(n))

							return r;

					}

				}

			}

		}
		else if (menu == MENU.INVENTORY) {

			RSInterfaceChild child = Interfaces.get(INVENTORY_MASTER, INVENTORY_CHILD_ITEMS);

			if (child == null)
				return null;

			RSInterfaceComponent[] components = child.getChildren();

			if (components.length == 0)
				return null;

			for (RSInterfaceComponent r : components) {

				if (r != null) {

					String n = r.getComponentName();

					if (n != null && !n.isEmpty()) {

						n = n.substring(12);

						if (name.equalsIgnoreCase(n))

							return r;

					}

				}

			}

		}

		return null;
	}

	/**
	 * Gets an item from the specified MENU at a certain index.
	 * 
	 * @param MENU
	 * @param int
	 * @return RSInterfaceComponent
	 */
	public static RSInterfaceComponent getItem(MENU menu, int index) {

		if (!isOpen())
			return null;

		if (menu == MENU.GENERAL_STORE) {

			RSInterfaceChild child = Interfaces.get(GENERAL_STORE_MASTER, GENERAL_STORE_CHILD_ITEMS);

			if (child == null)
				return null;

			RSInterfaceComponent[] components = child.getChildren();

			if (components.length == 0)
				return null;

			if (index > components.length)
				return null;

			return components[index];

		}
		else if (menu == MENU.INVENTORY) {

			RSInterfaceChild child = Interfaces.get(INVENTORY_MASTER, INVENTORY_CHILD_ITEMS);

			if (child == null)
				return null;

			RSInterfaceComponent[] components = child.getChildren();

			if (components.length == 0)
				return null;

			if (index > components.length)
				return null;

			return components[index];

		}

		return null;
	}

}
