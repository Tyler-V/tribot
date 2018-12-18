package scripts.usa.api2007.grand_exchange.slot;

import java.util.Arrays;

import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.types.RSInterface;

import scripts.usa.api.condition.Condition;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.grand_exchange.Constants;
import scripts.usa.api2007.grand_exchange.GrandExchange;
import scripts.usa.api2007.grand_exchange.windows.setup.SetupWindow;
import scripts.usa.api2007.grand_exchange.windows.status.StatusWindow;

/**
 * SLOT represents the (8) loaded Grand Exchange slots with the respective
 * methods for pulling the status, name, coins, amount, if the offer is
 * aborted/complete all from the Grand Exchange interface.
 */
public enum Slot {

	ONE(Constants.SLOT_ONE_CHILD_ID),
	TWO(Constants.SLOT_TWO_CHILD_ID),
	THREE(Constants.SLOT_THREE_CHILD_ID),
	FOUR(Constants.SLOT_FOUR_CHILD_ID),
	FIVE(Constants.SLOT_FIVE_CHILD_ID),
	SIX(Constants.SLOT_SIX_CHILD_ID),
	SEVEN(Constants.SLOT_SEVEN_CHILD_ID),
	EIGHT(Constants.SLOT_EIGHT_CHILD_ID);

	private int childId;

	Slot(int childId) {
		this.childId = childId;
	}

	/**
	 * Gets the interface child id of the Slot.
	 * 
	 * @return int
	 */
	public int getChildID() {
		return this.childId;
	}

	/**
	 * Gets the interface entity for a Slot.
	 * 
	 * @param slot
	 * @return InterfaceEntity
	 */
	private static InterfaceEntity getInterfaceEntity(Slot slot) {
		return Entities.find(InterfaceEntity::new)
				.inMasterAndChild(Constants.MASTER_ID, slot.getChildID());
	}

	/**
	 * Gets the WindowType of the Slot.
	 * 
	 * @return GrandExchange.WindowType.
	 */
	public static SlotType getType(Slot slot) {
		if (!GrandExchange.isOpen())
			return null;

		RSInterface inter = getInterfaceEntity(slot).textEquals(SlotType.BUY.getText(), SlotType.SELL.getText(), SlotType.EMPTY.getText())
				.getFirstResult();
		if (inter == null)
			return null;

		String text = inter.getText();
		if (text == null)
			return null;

		return SlotType.valueOf(text.toUpperCase());
	}

	/**
	 * Check if the slot is disabled.
	 * 
	 * @param slot
	 * @return true if disabled.
	 */
	private static boolean isDisabled(Slot slot) {
		RSInterface inter = getInterfaceEntity(slot).actionContains("Create")
				.getFirstResult();
		return inter == null;
	}

	/**
	 * Check if the slot status is complete.
	 * 
	 * @param slot
	 * @return true if complete.
	 */
	private static boolean isComplete(Slot slot) {
		RSInterface inter = getInterfaceEntity(slot).textColourEquals(Constants.TEXT_COLOUR_COMPLETE)
				.getFirstResult();
		return inter != null;
	}

	/**
	 * Check if the slot status is aborted.
	 * 
	 * @param slot
	 * @return true if aborted.
	 */
	private static boolean isAborted(Slot slot) {
		RSInterface inter = getInterfaceEntity(slot).textColourEquals(Constants.TEXT_COLOUR_ABORTED)
				.getFirstResult();
		return inter != null;
	}

	/**
	 * Gets the SlotStatus of the Slot.
	 * 
	 * @return GrandExchange.slot.SlotStatus
	 */
	public static SlotStatus getStatus(Slot slot) {
		if (!GrandExchange.isOpen())
			return null;

		SlotType type = getType(slot);
		if (type == SlotType.EMPTY) {
			if (isDisabled(slot)) {
				return SlotStatus.DISABLED;
			}
			return SlotStatus.EMPTY;
		}
		else {
			if (isComplete(slot)) {
				return SlotStatus.COMPLETE;
			}
			else if (isAborted(slot)) {
				return SlotStatus.ABORTED;
			}
			else {
				return SlotStatus.IN_PROGRESS;
			}
		}
	}

	/**
	 * Gets the item name of the Slot.
	 * 
	 * @param slot
	 * @return string
	 */
	public static String getItemName(Slot slot) {
		RSInterface inter = getInterfaceEntity(slot).getFirstResult();
		if (inter == null)
			return null;

		return inter.getChild(Constants.SLOT_ITEM_NAME_COMPONENT_ID)
				.getText();
	}

	/**
	 * Gets the item price of the Slot.
	 * 
	 * @param slot
	 * @return int
	 */
	public static int getItemPrice(Slot slot) {
		RSInterface inter = getInterfaceEntity(slot).getFirstResult();
		if (inter == null)
			return 0;

		String text = inter.getChild(Constants.SLOT_ITEM_PRICE_COMPONENT_ID)
				.getText();
		if (text == null)
			return 0;

		return Integer.parseInt(text.replaceAll("[^0-9]", ""));
	}

	/**
	 * Clicks the Slot window View Offer or Abort Offer.
	 * 
	 * @param slot
	 * @param action
	 * @return true if the action was performed successfully.
	 */
	public static boolean click(Slot slot, SlotAction action) {
		RSInterface inter = getInterfaceEntity(slot).actionContains(action.getText())
				.getFirstResult();
		if (inter == null)
			return false;

		if (inter.click()) {
			if (action == SlotAction.VIEW_OFFER) {
				return Condition.wait(() -> StatusWindow.isOpen());
			}
			else if (action == SlotAction.ABORT_OFFER) {
				return Condition.wait(() -> getStatus(slot) == SlotStatus.ABORTED);
			}
			else {
				return Condition.wait(() -> SetupWindow.isOpen());
			}
		}

		return false;
	}

	/**
	 * Checks if the slot you are viewing the status of has the same item name.
	 * 
	 * @param slot
	 * @return true if viewing the slot.
	 */
	public static boolean isViewing(Slot slot) {
		if (StatusWindow.isOpen())
			return false;

		return Slot.getItemName(slot) == StatusWindow.getItemName();
	}

}
