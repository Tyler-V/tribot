package scripts.usa.api2007.grand_exchange;

import java.util.Arrays;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

import scripts.usa.api.condition.Condition;
import scripts.usa.api2007.entity.Entity;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.entity.selector.prefabs.NpcEntity;
import scripts.usa.api2007.grand_exchange.enums.CollectionMethod;
import scripts.usa.api2007.grand_exchange.enums.OfferType;
import scripts.usa.api2007.grand_exchange.slot.Slot;
import scripts.usa.api2007.grand_exchange.slot.SlotStatus;
import scripts.usa.api2007.grand_exchange.windows.setup.SetupWindow;
import scripts.usa.api2007.grand_exchange.windows.status.StatusWindow;

public class GrandExchange {

	/**
	 * Checks if the Grand Exchange interface is open.
	 * 
	 * @return true if open.
	 */
	public static boolean isOpen() {
		return Interfaces.isInterfaceSubstantiated(Constants.MASTER_ID);
	}

	/**
	 * Opens the Grand Exchange
	 * 
	 * @return true if opened.
	 */
	public static boolean open() {
		if (isOpen())
			return true;

		return Entity.interact("Exchange",
				Entities.find(NpcEntity::new)
						.actionsEquals("Exchange"),
				() -> isOpen());
	}

	/**
	 * Gets a Slot by SlotStatus
	 * 
	 * @param status
	 * @return Slot
	 */
	public static Slot getSlot(SlotStatus status) {
		return Arrays.stream(Slot.values())
				.filter(slot -> Slot.getStatus(slot) == status)
				.findFirst()
				.orElse(null);
	}

	public static boolean hasOpenSlot() {
		return getSlot(SlotStatus.EMPTY) != null;
	}

	/**
	 * Sets up an offer.
	 * 
	 * @param offer
	 * @param name
	 * @param quantity
	 * @param price
	 * @return true if the offer was successfully confirmed.
	 */
	public static boolean offer(OfferType offer, String name, int quantity, int price) {
		SetupWindow.setItem(offer, name);
		if (quantity > 0)
			SetupWindow.setQuantity(quantity);
		SetupWindow.setPrice(price);
		return SetupWindow.confirm();
	}

	/**
	 * Sets up an offer.
	 * 
	 * @param offer
	 * @param name
	 * @param quantity
	 * @param price
	 * @return true if the offer was successfully confirmed.
	 */
	public static boolean offer(OfferType offer, String name, int quantity, double percent) {
		SetupWindow.setItem(offer, name);
		if (quantity > 0)
			SetupWindow.setQuantity(quantity);
		SetupWindow.setPricePercent(percent);
		return SetupWindow.confirm();
	}

	public static boolean sell(String name, double price) {
		return offer(OfferType.SELL, name, 0, price);
	}

	/**
	 * Gets the collection interface entity.
	 * 
	 * @return InterfaceEntity
	 */
	private static InterfaceEntity getCollectionEntity() {
		return Entities.find(InterfaceEntity::new)
				.inMasterAndChild(Constants.MASTER_ID, Constants.COLLECTION_CHILD_ID);
	}

	/**
	 * Checks if there is a collection option available.
	 * 
	 * @param collection
	 * @return true if you can collect.
	 */
	public static boolean canCollect() {
		if (StatusWindow.isOpen()) {
			return StatusWindow.canCollect();
		}
		else {
			return getCollectionEntity().actionContains("Collect")
					.isNotHidden()
					.getFirstResult() != null;
		}
	}

	/**
	 * Collects items from the window with the desired WindowCollection method,
	 * defaults to "Collect" if action not found.
	 * 
	 * @param collection
	 * @return true if all items were collected
	 */
	public static boolean collect(CollectionMethod collectionMethod) {
		if (StatusWindow.isOpen()) {
			return StatusWindow.collect(collectionMethod);
		}
		else {
			RSInterface inter = getCollectionEntity().isNotHidden()
					.actionEquals(collectionMethod.getText())
					.getFirstResult();
			if (inter == null)
				return false;

			if (inter.click(collectionMethod.getText()))
				return Condition.wait(() -> !canCollect());

			return !canCollect();
		}
	}
}
