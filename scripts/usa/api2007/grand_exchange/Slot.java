package scripts.usa.api2007.grand_exchange;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;

/**
 * SLOT represents the (8) loaded Grand Exchange slots with the respective
 * methods for pulling the status, name, coins, amount, if the offer is
 * aborted/complete all from the Grand Exchange interface.
 */
public enum Slot {

	ONE(new int[] { Constants.MASTER, Constants.SLOT_ONE_CHILD }),

	TWO(new int[] { Constants.MASTER, Constants.SLOT_TWO_CHILD }),

	THREE(new int[] { Constants.MASTER, Constants.SLOT_THREE_CHILD }),

	FOUR(new int[] { Constants.MASTER, Constants.SLOT_FOUR_CHILD }),

	FIVE(new int[] { Constants.MASTER, Constants.SLOT_FIVE_CHILD }),

	SIX(new int[] { Constants.MASTER, Constants.SLOT_SIX_CHILD }),

	SEVEN(new int[] { Constants.MASTER, Constants.SLOT_SEVEN_CHILD }),

	EIGHT(new int[] { Constants.MASTER, Constants.SLOT_EIGHT_CHILD });

	public final int[] index;

	Slot(int[] index) {
		this.index = index;
	}

	private int getMaster() {
		return index[0];
	}

	private int getChild() {
		return index[1];
	}

	/**
	 * Gets the TYPE of the SLOT (Buy, Sell, Empty)
	 * 
	 * @return TYPE
	 */
	public static WindowType getType(Slot slot) {

		if (!GrandExchange.isOpen())
			return null;

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return null;

		RSInterfaceComponent component = child.getChild(Constants.SLOT_TYPE);
		if (component == null)
			return null;

		String text = component.getText();
		if (text == null)
			return null;

		return WindowType.valueOf(text.toUpperCase());

	}

	/**
	 * Gets the STATUS of the SLOT (COMPLETE, IN_PROGRESS, ABORTED)
	 * 
	 * @return STATUS
	 */
	public static OfferStatus getStatus(Slot slot) {

		if (!GrandExchange.isOpen())
			return null;

		if (isDisabled(slot))
			return OfferStatus.DISABLED;

		if (getType(slot) == WindowType.BUY || getType(slot) == WindowType.SELL) {

			if (isComplete(slot)) {

				return OfferStatus.COMPLETE;

			}
			else if (isAborted(slot)) {

				return OfferStatus.ABORTED;

			}
			else {

				return OfferStatus.IN_PROGRESS;

			}

		}
		else {

			return OfferStatus.EMPTY;

		}

	}

	/**
	 * Gets the name of the Item in the SLOT
	 * 
	 * @return String
	 */
	public static String getName(Slot slot) {

		if (!GrandExchange.isOpen())
			return null;

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return null;

		RSInterfaceComponent component = child.getChild(Constants.SLOT_ITEM_NAME);
		if (component == null)
			return null;

		String text = component.getText();
		if (text == null || text.length() == 0)
			return null;

		return text;

	}

	/**
	 * Gets the quantity of the Item in the SLOT
	 * 
	 * @return int
	 */
	public static int getQuantity(Slot slot) {

		if (!GrandExchange.isOpen())
			return 0;

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return 0;

		RSInterfaceComponent component = child.getChild(Constants.SLOT_ITEM_QUANTITY);
		if (component == null)
			return 0;

		return component.getComponentStack();

	}

	/**
	 * Gets the price per Item in the SLOT
	 * 
	 * @return int
	 */
	public static int getPrice(Slot slot) {

		if (!GrandExchange.isOpen())
			return 0;

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return 0;

		RSInterfaceComponent component = child.getChild(Constants.SLOT_ITEM_PRICE);
		if (component == null)
			return 0;

		String text = component.getText();
		if (text == null || text.isEmpty())
			return 0;

		text = text.replaceAll(",", "");
		text = text.replaceAll("coins", "");
		text = text.replaceAll("coin", "");
		text = text.trim();

		return Integer.parseInt(text);

	}

	/**
	 * Creates a new offer in the SLOT specified with OFFER type, String name,
	 * int quantity, int price
	 * 
	 * @param Slot
	 * @param OfferOption
	 * @param String
	 * @param int
	 * @param int
	 * @return true if an new offer was created
	 */
	public static boolean offer(Slot slot, OfferOption offerType, String name, int quantity, Offer offer) {

		if (!GrandExchange.isOpen())
			return false;

		if (!GrandExchange.isOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
			if (child == null)
				return false;

			RSInterfaceComponent component = null;

			if (offerType == OfferOption.BUY) {
				component = child.getChild(Constants.SLOT_BUY_COMPONENT);
			}
			else if (offerType == OfferOption.SELL) {
				component = child.getChild(Constants.SLOT_SELL_COMPONENT);
			}

			if (component == null)
				return false;

			if (component.click()) {
				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(100);
						return GrandExchange.isOfferWindowOpen();
					}
				}, 3000);
			}

		}

		if (GrandExchange.isOfferWindowOpen())
			return GrandExchange.setUpOffer(offerType, name, quantity, offer);

		return false;

	}

	/**
	 * Creates a new offer in the SLOT specified with OFFER type, String name,
	 * int quantity, int price
	 * 
	 * @param Slot
	 * @param OfferOption
	 * @param String
	 * @param int
	 * @param int
	 * @return true if an new offer was created
	 */
	public static boolean offer(Slot slot, OfferOption offer, String name, int quantity, int price) {

		if (!GrandExchange.isOpen())
			return false;

		if (!GrandExchange.isOfferWindowOpen()) {

			RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
			if (child == null)
				return false;

			RSInterfaceComponent component = null;

			if (offer == OfferOption.BUY) {
				component = child.getChild(Constants.SLOT_BUY_COMPONENT);
			}
			else if (offer == OfferOption.SELL) {
				component = child.getChild(Constants.SLOT_SELL_COMPONENT);
			}

			if (component == null)
				return false;

			if (component.click()) {
				Timing.waitCondition(new Condition() {
					public boolean active() {
						General.sleep(100);
						return GrandExchange.isOfferWindowOpen();
					}
				}, 3000);
			}

		}

		if (GrandExchange.isOfferWindowOpen())

			return GrandExchange.setUpOffer(offer, name, quantity, price);

		return false;

	}

	/**
	 * Checks if you are viewing the specified SLOT
	 * 
	 * @param Slot
	 * @return true if viewing the specified SLOT
	 */
	public static boolean isViewing(Slot slot) {

		if (!GrandExchange.isViewOfferWindowOpen())
			return false;

		String name = getName(slot);
		if (name == null || !name.equals(GrandExchange.getWindowName()))
			return false;

		if (getPrice(slot) != GrandExchange.getWindowPrice())
			return false;

		if (getQuantity(slot) != GrandExchange.getWindowQuantity())
			return false;

		return true;

	}

	/**
	 * Views the current offer in the specified SLOT
	 * 
	 * @param Slot
	 * @return true if viewing the SLOT offer
	 */
	public static boolean view(Slot slot) {

		if (isViewing(slot))
			return true;

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(Constants.SLOT_WINDOW);
		if (component == null)
			return false;

		if (component.click()) {
			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return isViewing(slot);
				}
			}, 3000);
		}

		return isViewing(slot);

	}

	/**
	 * Aborts the current offer in the specified SLOT
	 * 
	 * @param Slot
	 * @return true if the offer was aborted
	 */
	public static boolean abort(Slot slot) {

		if (isAborted(slot))
			return true;

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return false;

		if (child.click("Abort offer")) {
			Timing.waitCondition(new Condition() {
				public boolean active() {
					General.sleep(100);
					return isAborted(slot);
				}
			}, 3000);
		}

		return isAborted(slot);

	}

	/**
	 * Checks if the offer in the specified SLOT is aborted
	 * 
	 * @param Slot
	 * @return true if it is aborted
	 */
	public static boolean isAborted(Slot slot) {

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(Constants.SLOT_COLOR);
		if (component == null)
			return false;

		return component.getTextColour() == 9371648;

	}

	/**
	 * Checks if the offer in the specified SLOT is complete
	 * 
	 * @param Slot
	 * @return true if it is complete
	 */
	public static boolean isComplete(Slot slot) {

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(Constants.SLOT_COLOR);
		if (component == null)
			return false;

		return component.getTextColour() == 24320;
	}

	/**
	 * Checks if the slot is disabled out meaning it is members only
	 * 
	 * @param slot
	 * @return
	 */
	public static boolean isDisabled(Slot slot) {

		RSInterfaceChild child = Interfaces.get(slot.getMaster(), slot.getChild());
		if (child == null)
			return false;

		RSInterfaceComponent component = child.getChild(Constants.SLOT_BUY_CHILD_TEXTURE);
		if (component == null)
			return false;

		return component.getTextureID() == 1109;

	}

}
