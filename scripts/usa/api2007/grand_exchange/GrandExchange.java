package scripts.usa.api2007.grand_exchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;

public class GrandExchange {

	/**
	 * Gets a list of all Slots
	 * 
	 * @return List of type <Slot>
	 */
	public static List<Slot> getSlots() {
		return new ArrayList<Slot>(Arrays.asList(Slot.values()));
	}

	/**
	 * Gets a list of Slots with the specified status
	 * 
	 * @return List of type <Slot>
	 */
	public static List<Slot> getSlots(OfferStatus status) {

		List<Slot> slots = new ArrayList<Slot>();

		if (!isOpen())
			return slots;

		for (Slot e : Slot.values()) {

			if (status == OfferStatus.AVAILABLE_FOR_COLLECTION) {

				if (Slot.getStatus(e) == OfferStatus.ABORTED || Slot.getStatus(e) == OfferStatus.COMPLETE)
					slots.add(e);

			}
			else if (Slot.getStatus(e) == status) {

				slots.add(e);

			}

		}

		return slots;

	}

	/**
	 * Gets a list of Slots with the specified status
	 * 
	 * @return List of type <Slot>
	 */
	public static List<Slot> getSlotsExcept(OfferStatus status) {

		List<Slot> slots = new ArrayList<Slot>();

		if (!isOpen())
			return slots;

		for (Slot e : Slot.values()) {

			if (status == OfferStatus.AVAILABLE_FOR_COLLECTION) {

				if (Slot.getStatus(e) == OfferStatus.EMPTY || Slot.getStatus(e) == OfferStatus.IN_PROGRESS)
					slots.add(e);

			}
			else if (Slot.getStatus(e) != status) {

				slots.add(e);

			}

		}

		return slots;

	}

	/**
	 * Opens the Grand Exchange using the clerk.
	 * 
	 * @return true if the Grand Exchange interface was successfully opened.
	 */
	public static boolean open() {

		if (isOpen())
			return true;

		RSNPC[] npc = NPCs.findNearest(Filters.NPCs.actionsEquals("Exchange"));
		if (npc.length == 0)
			return false;

		String name = npc[0].getName();
		if (name == null)
			return false;

		if (DynamicClicking.clickRSNPC(npc[0], "Exchange " + name)) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return isOpen();
				}
			}, 5000);

		}

		return isOpen();

	}

	/**
	 * Closes the Grand Exchange interface.
	 * 
	 * @return true if it successfully closed Grand Exchange.
	 */
	public static boolean close() {

		if (!isOpen())
			return true;

		RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.CLOSE_CHILD);
		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(Constants.CLOSE_COMPONENT);
		if (component == null)
			return false;

		if (component.click()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return !isOpen();
				}
			}, 5000);

		}

		return !isOpen();

	}

	/**
	 * Checks if the Grand Exchange interface is open.
	 * 
	 * @return true if the Grand Exchange interface is open.
	 * 
	 */
	public static boolean isOpen() {
		return Interfaces.isInterfaceValid(Constants.MASTER);
	}

	/**
	 * Selects the back menu inside the offer window.
	 * 
	 * @return true if we went back to the main Grand Exchange interface.
	 */
	public static boolean back() {

		if (!isOfferWindowOpen() && !isViewOfferWindowOpen())
			return true;

		RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.BACK_CHILD);
		if (child == null)
			return false;

		if (child.click()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return !isOfferWindowOpen() && !isViewOfferWindowOpen();
				}
			}, 5000);

		}

		return !isOfferWindowOpen() && !isViewOfferWindowOpen();

	}

	/**
	 * Sleeps until the items are collected.
	 * 
	 * @param component
	 * @return true if all items are collected
	 */
	private static boolean sleepUntilItemCollected(RSInterfaceComponent component) {

		long timer = System.currentTimeMillis() + 3000;

		while (timer > System.currentTimeMillis()) {

			if (component == null)
				return false;

			String text = component.getText();
			if (text == null || text.isEmpty())
				return true;

			General.sleep(100);

		}

		return false;

	}

	/**
	 * Sleeps until the items are collected
	 * 
	 * @return true if all items are collected
	 */
	private static boolean sleepUntilItemsCollected() {

		long timer = System.currentTimeMillis() + 3000;

		while (timer > System.currentTimeMillis()) {

			if (!canCollectItems())
				return true;

			General.sleep(100);

		}

		return !canCollectItems();

	}

	public static boolean canCollectItems() {

		if (!isOpen())
			return false;

		if (isOfferWindowOpen() || isViewOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.COLLECT_ITEM_CHILD);
			if (child == null)
				return false;

			RSInterfaceComponent[] components = child.getChildren();
			if (components == null || components.length == 0)
				return false;

			for (RSInterfaceComponent component : components) {

				String[] actions = component.getActions();

				if (actions != null)
					return true;

			}

			return false;

		}

		RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.COLLECT_CHILD);
		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(Constants.COLLECT_COMPONENT);

		return component != null && !component.isHidden();

	}

	/**
	 * Collects items through the Grand Exchange interface with the specified
	 * CollectionMethod, will collect items from the main interface or the offer
	 * window depending on which is open when this method is called.
	 * 
	 * @return true if the items were collected.
	 */
	public static boolean collect(CollectionMethod method) {

		if (!isOpen())
			return false;

		if (!canCollectItems())
			return false;

		if (isOfferWindowOpen() || isViewOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.COLLECT_ITEM_CHILD);
			if (child == null)
				return false;

			RSInterfaceComponent[] components = child.getChildren();
			if (components == null || components.length == 0)
				return false;

			for (RSInterfaceComponent component : components) {

				if (method == CollectionMethod.DEFAULT) {

					if (component.click())
						return sleepUntilItemCollected(component);

				}
				else {

					String[] component_actions = component.getActions();

					if (component_actions != null) {

						for (String component_action : component_actions) {

							for (String method_action : method.getActions()) {

								if (component_action.equals(method_action)) {

									if (component.click(method_action))
										return sleepUntilItemCollected(component);

								}

							}

						}

					}

				}

			}

		}
		else {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.COLLECT_CHILD);
			if (child == null)
				return false;

			RSInterfaceComponent component = child.getChild(Constants.COLLECT_COMPONENT);
			if (component == null || component.isHidden())
				return false;

			if (method == CollectionMethod.DEFAULT) {

				if (component.click())
					return sleepUntilItemsCollected();

			}
			else {

				String[] component_actions = component.getActions();

				for (String component_action : component_actions) {

					for (String method_action : method.getActions()) {

						if (component_action.equals(method_action)) {

							if (component.click(method_action))
								return sleepUntilItemCollected(component);

						}

					}

				}

			}

		}

		return !canCollectItems();

	}

	/**
	 * Returns whether the offer window is open
	 * 
	 * @return true if it is open
	 */
	public static boolean isOfferWindowOpen() {

		if (!isOpen())
			return false;

		RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);

		return child != null && !child.isHidden();

	}

	/**
	 * Returns whether the view offer window is open
	 * 
	 * @return true if it is open
	 */
	public static boolean isViewOfferWindowOpen() {

		if (!isOpen())
			return false;

		RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);

		return child != null && !child.isHidden();

	}

	/**
	 * Gets the item name in the offer window
	 * 
	 * @return String
	 */
	public static String getWindowName() {

		if (isOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
			if (child == null)
				return null;

			RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_ITEM_NAME);
			if (component == null)
				return null;

			String text = component.getText();
			if (text == null || text.length() == 0)
				return null;

			return text;

		}
		else if (isViewOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);
			if (child == null)
				return null;

			RSInterfaceComponent component = child.getChild(Constants.VIEW_OFFER_WINDOW_ITEM_NAME);
			if (component == null)
				return null;

			String text = component.getText();
			if (text == null || text.length() == 0)
				return null;

			return text;

		}

		return null;

	}

	/**
	 * Gets the item quantity in the offer window
	 * 
	 * @return int
	 */
	public static int getWindowQuantity() {

		if (isOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
			if (child == null)
				return 0;

			RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_ITEM_QUANTITY);
			if (component == null)
				return 0;

			String text = component.getText();
			if (text == null || text.length() == 0)
				return 0;

			text = text.replaceAll(",", "");

			return Integer.parseInt(text);

		}
		else if (isViewOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);
			if (child == null)
				return 0;

			RSInterfaceComponent component = child.getChild(Constants.VIEW_OFFER_WINDOW_ITEM_QUANTITY);
			if (component == null)
				return 0;

			String text = component.getText();
			if (text == null || text.length() == 0)
				return 0;

			text = text.replaceAll(",", "");

			return Integer.parseInt(text);

		}

		return 0;

	}

	/**
	 * Gets the price per item in the offer window
	 * 
	 * @return int
	 */
	public static int getWindowPrice() {

		if (isOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
			if (child == null)
				return 0;

			RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_ITEM_PRICE);
			if (component == null)
				return 0;

			String text = component.getText();
			if (text == null || text.length() == 0)
				return 0;

			text = text.replaceAll(",", "");
			text = text.replaceAll("coins", "");
			text = text.replaceAll("coin", "");
			text = text.trim();

			return Integer.parseInt(text);

		}
		else if (isViewOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);
			if (child == null)
				return 0;

			RSInterfaceComponent component = child.getChild(Constants.VIEW_OFFER_WINDOW_ITEM_PRICE);
			if (component == null)
				return 0;

			String text = component.getText();
			if (text == null || text.length() == 0)
				return 0;

			text = text.replaceAll(",", "");
			text = text.replaceAll("coins", "");
			text = text.replaceAll("coin", "");
			text = text.trim();

			return Integer.parseInt(text);

		}

		return 0;

	}

	/**
	 * Gets the total value in the offer window
	 * 
	 * @return int
	 */
	public static int getWindowValue() {

		if (isOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
			if (child == null)
				return 0;

			RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_VALUE);
			if (component == null)
				return 0;

			String text = component.getText();
			if (text == null || text.length() == 0)
				return 0;

			text = text.replaceAll(",", "");
			text = text.replaceAll("coins", "");
			text = text.replaceAll("coin", "");
			text = text.trim();

			return Integer.parseInt(text);

		}
		else if (isViewOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);
			if (child == null)
				return 0;

			RSInterfaceComponent component = child.getChild(Constants.VIEW_OFFER_WINDOW_VALUE);
			if (component == null)
				return 0;

			String text = component.getText();
			if (text == null || text.length() == 0)
				return 0;

			text = text.replaceAll(",", "");
			text = text.replaceAll("coins", "");
			text = text.replaceAll("coin", "");
			text = text.trim();

			return Integer.parseInt(text);

		}

		return 0;

	}

	/**
	 * Sets up a new offer in the offer window
	 * 
	 * @param Offer
	 *            offer
	 * @param String
	 *            name
	 * @param int
	 *            quantity
	 * @param int
	 *            price
	 * @return true if an offer was setup
	 */
	public static boolean setUpOffer(OfferOption offerType, String name, int quantity, int price) {

		if (!isOfferWindowOpen())
			return false;

		if (!setItem(offerType, name))
			return false;

		if (!setQuantity(name, quantity))
			return false;

		if (!setPrice(price))
			return false;

		return confirm();
	}

	/**
	 * Sets up a new offer in the offer window
	 * 
	 * @param Offer
	 *            offer
	 * @param String
	 *            name
	 * @param int
	 *            quantity
	 * @param int
	 *            price
	 * @return true if an offer was setup
	 */
	public static boolean setUpOffer(OfferOption offerType, String name, int quantity, Offer offer) {

		if (!isOfferWindowOpen())
			return false;

		if (!setItem(offerType, name))
			return false;

		if (!setQuantity(name, quantity))
			return false;

		if (!setPrice(offer))
			return false;

		return confirm();
	}

	/**
	 * Checks if the window offer item is set
	 * 
	 * @param name
	 *            of the item
	 * @return true if item is set
	 */
	public static boolean isWindowItemSet(String name) {

		String item = getWindowName();
		if (item == null)
			return false;

		return item.equalsIgnoreCase(name);

	}

	/**
	 * Sets the item in the window offer
	 * 
	 * @param Offer
	 *            offer
	 * @param String
	 *            name
	 * @return true if the item was set
	 */
	public static boolean setItem(OfferOption offerType, String name) {

		if (isWindowItemSet(name))
			return true;

		if (offerType == OfferOption.BUY) {
			if (!enterNameMenuOpen()) {
				RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
				if (child == null)
					return false;

				RSInterfaceComponent component = child.getChild(Constants.CHOOSE_ITEM_COMPONENT);
				if (component == null)
					return false;

				if (component.click()) {
					Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(100);
							return enterNameMenuOpen();
						}
					}, 5000);
					General.sleep(General.randomSD(1000, 200));
				}
			}

			if (enterNameMenuOpen()) {
				Keyboard.typeString(name);
				General.sleep(General.randomSD(1000, 200));
				return selectItem(name);
			}
		}
		else if (offerType == OfferOption.SELL) {

			RSItem[] item = Inventory.find(name);
			if (item.length == 0)
				return false;

			if (item[0].click()) {
				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(100);
						return isWindowItemSet(name);
					}
				}, 5000);
			}
		}

		return isWindowItemSet(name);
	}

	public static boolean selectItem(String name) {

		RSInterfaceChild child = Interfaces.get(Constants.ITEM_SELECTION_MASTER, Constants.ITEM_SELECTION_CHILD);
		if (child == null)
			return false;

		RSInterfaceComponent[] components = child.getChildren();
		if (components == null || components.length == 0)
			return false;

		for (int i = 0; i < components.length; i++) {

			String component_name = components[i].getText();

			if (component_name != null && component_name.equals(name)) {

				RSInterfaceComponent component_item = components[i - 1];
				if (component_item == null)
					return false;

				if (component_item.click()) {

					Timing.waitCondition(new Condition() {
						public boolean active() {
							General.sleep(100);
							return isWindowItemSet(name);
						}
					}, 5000);

					return isWindowItemSet(name);

				}

			}

		}

		return isWindowItemSet(name);

	}

	/**
	 * Checks if the enter item name menu is open
	 * 
	 * @return true if the menu is open
	 */
	public static boolean enterNameMenuOpen() {

		RSInterfaceChild child = Interfaces.get(Constants.OFFER_WINDOW_ENTER_MENU_MASTER, Constants.OFFER_WINDOW_ENTER_ITEM_NAME_CHILD);
		return child != null && !child.isHidden();

	}

	/**
	 * Sets the window item quantity, a quantity of less than or equal to 0 will
	 * set the quantity to all.
	 * 
	 * 
	 * @param String
	 *            name
	 * @param int
	 *            quantity
	 * @return true if the quantity was set
	 */
	public static boolean setQuantity(String name, int quantity) {

		if (isWindowQuantitySet(name, quantity))
			return true;

		if (!enterAmountMenuUp()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
			if (child == null)
				return false;

			RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_QUANTITY_ENTER);
			if (component == null)
				return false;

			if (component.click()) {

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(100);
						return enterAmountMenuUp();
					}
				}, 5000);

			}

		}

		if (enterAmountMenuUp()) {

			Keyboard.typeSend(Integer.toString(quantity));

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return isWindowQuantitySet(name, quantity);
				}
			}, 5000);

		}

		return isWindowQuantitySet(name, quantity);

	}

	/**
	 * Checks if the window quantity is set
	 * 
	 * @param String
	 *            name
	 * @param int
	 *            quantity
	 * @return true if the quantity is set
	 */
	public static boolean isWindowQuantitySet(String name, int quantity) {

		return (getWindowQuantity() == quantity || (quantity <= 0 && Inventory.getCount(name) == getWindowQuantity()));

	}

	/**
	 * Sets the specified price of the item, a price less than or equal to 0
	 * will set it as market price.
	 * 
	 * @param int
	 *            price
	 * @return true if the price was set
	 */
	public static boolean setPrice(int price) {

		if (isWindowPriceSet(price))
			return true;

		if (!enterAmountMenuUp()) {

			RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
			if (child == null)
				return false;

			RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_PRICE_ENTER);
			if (component == null)
				return false;

			if (component.click()) {

				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(100);
						return enterAmountMenuUp();
					}
				}, 5000);

			}

		}

		if (enterAmountMenuUp()) {

			Keyboard.typeSend(Integer.toString(price));

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return isWindowPriceSet(price);
				}
			}, 5000);

		}

		return isWindowPriceSet(price);

	}

	/**
	 * Sets the specified price of the item, a price less than or equal to 0
	 * will set it as market price.
	 * 
	 * @param int
	 *            price
	 * @return true if the price was set
	 */
	public static boolean setPrice(Offer offer) {
		RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(offer.getChild());
		if (component == null)
			return false;

		int loop = 1;

		if (offer == Offer.ADD_TEN_PERCENT || offer == Offer.SUBTRACT_TEN_PERCENT)
			loop++;

		if (offer == Offer.GUIDE_PRICE)
			loop = 0;

		for (int i = 0; i < loop; i++) {
			final int previous_price = getWindowPrice();
			if (component.click()) {
				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(100);
						return previous_price != getWindowPrice();
					}
				}, 5000);
			}
		}

		return true;
	}

	/**
	 * Checks if the window quantity is set
	 * 
	 * @param String
	 *            name
	 * @param int
	 *            quantity
	 * @return true if the quantity is set
	 */
	public static boolean isWindowPriceSet(int price) {

		return getWindowPrice() == price || price <= 0;

	}

	/**
	 * Checks if the enter item price menu is up
	 * 
	 * @return true if the menu is open
	 */
	public static boolean enterAmountMenuUp() {

		RSInterfaceChild child = Interfaces.get(Constants.OFFER_WINDOW_ENTER_MENU_MASTER, Constants.OFFER_WINDOW_ENTER_AMOUNT_CHILD);
		return child != null && !child.isHidden();

	}

	/**
	 * Clicks the confirm button
	 * 
	 * @return true if the offer was confirmed
	 */
	public static boolean confirm() {

		RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_CONFIRM);
		if (component == null || component.isHidden())
			return false;

		if (component.click()) {

			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return !isOfferWindowOpen() && !isViewOfferWindowOpen();
				}
			}, 5000);

		}

		return !isOfferWindowOpen() && !isViewOfferWindowOpen();

	}

}
