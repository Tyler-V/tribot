package scripts.usa.api2007.grand_exchange.windows.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tribot.api2007.types.RSInterface;

import scripts.usa.api.condition.Condition;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.grand_exchange.Constants;
import scripts.usa.api2007.grand_exchange.enums.CollectionMethod;
import scripts.usa.api2007.grand_exchange.enums.OfferType;

public class StatusWindow {

	/**
	 * Gets the interface entity for the status window
	 * 
	 * @return InterfaceEntity
	 */
	private static InterfaceEntity statusWindowEntity() {
		return Entities.find(InterfaceEntity::new)
				.inMasterAndChild(Constants.MASTER_ID, Constants.OFFER_WINDOW_CHILD_ID);
	}

	/**
	 * Checks if the OfferWindow is open.
	 * 
	 * @return true if viewing the OfferWindow.
	 */
	public static boolean isOpen() {
		return Interfaces.isInterfaceSubstantiated(statusWindowEntity().getFirstResult());
	}

	/**
	 * Gets the OfferType of the OfferWindow.
	 * 
	 * @return Grand_Exchange.OfferType
	 */
	public static OfferType getOfferType() {
		if (!isOpen())
			return null;

		RSInterface inter = statusWindowEntity().textContains(OfferType.BUY.getText(), OfferType.SELL.getText())
				.getFirstResult();
		if (inter == null)
			return null;

		String text = inter.getText();
		if (text == null)
			return null;

		text = text.replaceAll(" offer", "")
				.toUpperCase();
		return OfferType.valueOf(text);
	}

	/**
	 * Gets the id of the item.
	 * 
	 * @return int
	 */
	public static int getItemID() {
		if (!isOpen())
			return 0;

		RSInterface inter = statusWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_STATUS_ITEM_ID_COMPONENT_ID);
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

		RSInterface inter = statusWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_STATUS_ITEM_NAME_COMPONENT_ID);
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

		RSInterface inter = statusWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_STATUS_ITEM_QUANTITY_COMPONENT_ID);
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

		RSInterface inter = statusWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_STATUS_PRICE_PER_ITEM_COMPONENT_ID);
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

		RSInterface inter = statusWindowEntity().getFirstResult()
				.getChild(Constants.OFFER_WINDOW_STATUS_OFFER_TOTAL_VALUE_COMPONENT_ID);
		if (inter == null)
			return 0;

		String text = inter.getText();
		if (text == null)
			return 0;

		return Integer.parseInt(text.replaceAll("[^0-9]", ""));
	}

	/**
	 * Gets the description of the item.
	 * 
	 * @return string
	 */
	public static String getItemDescription() {
		if (!isOpen())
			return null;

		RSInterface inter = Interfaces.get(Constants.MASTER_ID, Constants.OFFER_WINDOW_STATUS_DESCRIPTION_CHILD_ID);
		if (inter == null)
			return null;

		return inter.getText();
	}

	/**
	 * Gets the guide price of the item.
	 * 
	 * @return int
	 */
	public static int getGuidePrice() {
		if (!isOpen())
			return 0;

		RSInterface inter = Interfaces.get(Constants.MASTER_ID, Constants.OFFER_WINDOW_STATUS_GUIDE_PRICE_CHILD_ID);
		if (inter == null)
			return 0;

		String text = inter.getText();
		if (text == null)
			return 0;

		return Integer.parseInt(text.replaceAll("[^0-9]", ""));
	}

	/**
	 * Gets the current amount of items bought or sold.
	 * 
	 * @return int
	 */
	public static int getProgressAmount() {
		if (!isOpen())
			return 0;

		RSInterface inter = Interfaces.get(Constants.MASTER_ID, Constants.OFFER_WINDOW_STATUS_PROGRESS_CHILD_ID, Constants.OFFER_WINDOW_STATUS_PROGRESS_TEXT_COMPONENT_ID);
		if (inter == null)
			return 0;

		String text = inter.getText();
		if (text == null)
			return 0;

		String regex = "<col=ffb83f>(.*)</col>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);

		if (!matcher.find())
			return 0;

		return Integer.parseInt(matcher.group(1));
	}

	/**
	 * Checks if the progress amount of items being sold or bought equals the item
	 * quantity.
	 * 
	 * @return true if complete.
	 */
	public static boolean isOfferComplete() {
		if (!isOpen())
			return false;

		return getProgressAmount() == getItemQuantity();
	}

	/**
	 * Gets the collection interface entity.
	 * 
	 * @return InterfaceEntity
	 */
	private static InterfaceEntity getCollectionEntity() {
		return Entities.find(InterfaceEntity::new)
				.inMasterAndChild(Constants.MASTER_ID, Constants.OFFER_WINDOW_STATUS_COLLECT_CHILD_ID);
	}

	/**
	 * Checks if there is a collection option available.
	 * 
	 * @param collection
	 * @return true if you can collect.
	 */
	public static boolean canCollect() {
		return getCollectionEntity().actionContains("Collect")
				.isNotHidden()
				.getFirstResult() != null;
	}

	/**
	 * Collects items from the window with the desired WindowCollection method,
	 * defaults to "Collect" if action not found.
	 * 
	 * @param collection
	 * @return true if all items were collected
	 */
	public static boolean collect(CollectionMethod collection) {
		if (!isOpen())
			return false;

		RSInterface[] interfaces = getCollectionEntity().isNotHidden()
				.actionEquals(collection.getText())
				.getResults();

		for (RSInterface inter : interfaces) {
			if (inter.click(collection.getText()))
				Condition.wait(() -> !isOpen() || inter.getActions() == null);
		}

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
