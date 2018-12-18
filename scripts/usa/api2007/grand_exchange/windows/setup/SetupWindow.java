package scripts.usa.api2007.grand_exchange.windows.setup;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api.condition.Condition;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.Keyboard;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.grand_exchange.Constants;
import scripts.usa.api2007.grand_exchange.GrandExchange;
import scripts.usa.api2007.grand_exchange.enums.OfferType;
import scripts.usa.api2007.grand_exchange.slot.Slot;
import scripts.usa.api2007.grand_exchange.slot.SlotAction;
import scripts.usa.api2007.grand_exchange.slot.SlotStatus;

public class SetupWindow {

	/**
	 * Gets the interface entity for the setup window
	 * 
	 * @return InterfaceEntity
	 */
	private static InterfaceEntity setupWindowEntity() {
		return Entities.find(InterfaceEntity::new)
				.inMasterAndChild(Constants.MASTER_ID, Constants.OFFER_WINDOW_SETUP_CHILD_ID);
	}

	/**
	 * Checks if the OfferWindow is open.
	 * 
	 * @return true if viewing the OfferWindow.
	 */
	public static boolean isOpen() {
		return Interfaces.isInterfaceSubstantiated(setupWindowEntity().getFirstResult());
	}

	/**
	 * Types the item name into the search bar.
	 * 
	 * @param name
	 * @return true if the current item is set.
	 */
	public static boolean setItem(OfferType offerType, String name) {
		Slot slot = GrandExchange.getSlot(SlotStatus.EMPTY);
		if (slot == null)
			return false;

		if (offerType == OfferType.BUY) {
			if (!isOpen())
				Slot.click(slot, SlotAction.BUY);

			if (isOpen())
				Keyboard.typeSend(name);
		}
		else if (offerType == OfferType.SELL) {
			if (isOpen())
				return false;

			RSItem[] item = Inventory.find(name);
			if (item.length == 0)
				return false;

			if (item[0].click())
				Condition.wait(() -> isOpen() && getItemName().equals(name));
		}

		return isOpen() && getItemName().equals(name);
	}

	/**
	 * Sets the offer quantity.
	 * 
	 * @param quantity
	 * @return true if the quantity was successfully set.
	 */
	public static boolean setQuantity(int quantity) {
		if (!isOpen())
			return false;

		RSInterface inter = setupWindowEntity().actionEquals("Enter quantity")
				.getFirstResult();
		if (inter == null)
			return false;

		if (inter.click())
			Condition.wait(() -> Interfaces.isEnterAmountUp());

		if (Interfaces.isEnterAmountUp())
			Keyboard.typeSend(quantity);

		return Condition.wait(() -> getItemQuantity() == quantity);
	}

	/**
	 * Sets the offer price.
	 * 
	 * @param price
	 * @return true if the price was successfully set.
	 */
	public static boolean setPrice(int price) {
		if (!isOpen())
			return false;

		RSInterface inter = setupWindowEntity().actionEquals("Enter price")
				.getFirstResult();
		if (inter == null)
			return false;

		if (inter.click())
			Condition.wait(() -> Interfaces.isEnterAmountUp());

		if (Interfaces.isEnterAmountUp())
			Keyboard.typeSend(price);

		return Condition.wait(() -> getPricePerItem() == price);
	}

	/**
	 * Sets the offer price based on the percentage of the current guide price.
	 * 
	 * @param percent
	 * @return true if the price was successfully set.
	 */
	public static boolean setPricePercent(double percent) {
		if (!isOpen())
			return false;

		int guidePrice = getGuidePrice();
		int price = (int) (((percent / 100) * guidePrice) + guidePrice);

		if (getPricePerItem() == price)
			return true;

		return setPrice(price);
	}

	/**
	 * Gets the id of the item.
	 * 
	 * @return int
	 */
	public static int getItemID() {
		if (!isOpen())
			return 0;

		RSInterface inter = setupWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_SETUP_ITEM_ID_COMPONENT_ID);
		if (inter == null)
			return 0;

		return inter.getComponentItem();
	}

	/**
	 * Gets the name of the item.
	 * 
	 * @return string
	 */
	public static String getItemName() {
		if (!isOpen())
			return null;

		RSInterface inter = setupWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_SETUP_ITEM_NAME_COMPONENT_ID);
		if (inter == null)
			return null;

		return inter.getText();
	}

	/**
	 * Gets the quantity of the item.
	 * 
	 * @return int
	 */
	public static int getItemQuantity() {
		if (!isOpen())
			return 0;

		RSInterface inter = setupWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_SETUP_ITEM_QUANTITY_COMPONENT_ID);
		if (inter == null)
			return 0;

		String text = inter.getText();
		if (text == null)
			return 0;

		return Integer.parseInt(text.replaceAll("[^0-9]", ""));
	}

	/**
	 * Gets the price per item.
	 * 
	 * @return int
	 */
	public static int getPricePerItem() {
		if (!isOpen())
			return 0;

		RSInterface inter = setupWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_SETUP_PRICE_PER_ITEM_COMPONENT_ID);
		if (inter == null)
			return 0;

		String text = inter.getText();
		if (text == null)
			return 0;

		return Integer.parseInt(text.replaceAll("[^0-9]", ""));
	}

	/**
	 * Gets the total coin value of the offer.
	 * 
	 * @return int
	 */
	public static int getOfferValue() {
		if (!isOpen())
			return 0;

		RSInterface inter = setupWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_SETUP_TOTAL_VALUE_COMPONENT_ID);
		if (inter == null)
			return 0;

		String text = inter.getText();
		if (text == null)
			return 0;

		return Integer.parseInt(text.replaceAll("[^0-9]", ""));
	}

	/**
	 * Gets the guide price of the item.
	 * 
	 * @return int
	 */
	public static int getGuidePrice() {
		if (!isOpen())
			return 0;

		RSInterface inter = Interfaces.get(Constants.MASTER_ID, Constants.OFFER_WINDOW_SETUP_GUIDE_PRICE_CHILD_ID);
		if (inter == null)
			return 0;

		String text = inter.getText();
		if (text == null)
			return 0;

		return Integer.parseInt(text.replaceAll("[^0-9]", ""));
	}

	/**
	 * Clicks the confirm button.
	 * 
	 * @return true if the offer was confirmed.
	 */
	public static boolean confirm() {
		if (!isOpen())
			return false;

		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(Constants.MASTER_ID)
				.actionEquals("Confirm")
				.getFirstResult();
		if (inter == null)
			return false;

		if (inter.click("Confirm"))
			return Condition.wait(() -> !isOpen());

		return !isOpen();
	}

	/**
	 * Navigates back from the offer status window.
	 * 
	 * @return true if successfully navigated back.
	 */
	public static boolean back() {
		if (!isOpen())
			return true;

		RSInterface inter = Entities.find(InterfaceEntity::new)
				.inMaster(Constants.MASTER_ID)
				.actionEquals("Back")
				.getFirstResult();
		if (inter == null)
			return false;

		if (inter.click())
			Condition.wait(() -> !isOpen());

		return !isOpen();
	}
}
