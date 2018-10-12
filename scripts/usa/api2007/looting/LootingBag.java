package scripts.usa.api2007.looting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSItem;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.util.Strings;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api2007.Banking;
import scripts.usa.api2007.Interfaces;
import scripts.usa.api2007.entity.selector.Entities;
import scripts.usa.api2007.entity.selector.prefabs.BankItemEntity;
import scripts.usa.api2007.entity.selector.prefabs.InterfaceEntity;
import scripts.usa.api2007.entity.selector.prefabs.ItemEntity;

public class LootingBag {

	private final static int LOOTING_BAG_MASTER = 81;
	private final static int LOOTING_BAG_ITEMS_CHILD = 5;

	public enum BagOption {
		OPEN,
		CLOSE,
		VIEW,
		CHECK;

		public String getAction() {
			return Strings.toSentenceCase(this.name());
		}
	}

	public static RSItem getLootingBag() {
		return Entities.find(ItemEntity::new).nameEquals("Looting bag").getFirstResult();
	}

	public static boolean hasLootingBag() {
		return getLootingBag() != null;
	}

	private static boolean isCheckLootingBagInterfaceOpen() {
		return Interfaces.isInterfaceSubstantiated(LOOTING_BAG_MASTER);
	}

	public static boolean isState(BagOption bagOption) {
		switch (bagOption) {
			case OPEN:
				return Entities.find(ItemEntity::new).nameEquals("Looting bag").actionsEquals("Close").getFirstResult() != null;
			case CLOSE:
				return Entities.find(ItemEntity::new).nameEquals("Looting bag").actionsEquals("Open").getFirstResult() != null;
			case CHECK:
				return isCheckLootingBagInterfaceOpen();
			case VIEW:
				return LootingBag.Banking.isViewing();
		}
		return false;
	}

	public static boolean select(BagOption bagOption) {
		if (isState(bagOption))
			return true;

		RSItem bag = getLootingBag();
		if (bag == null)
			return false;

		if (bag.click(bagOption.getAction()))
			return Condition.wait(() -> isState(bagOption));

		return isState(bagOption);
	}

	public static boolean hasItems() {
		return getItems().size() > 0;
	}

	public static List<RSItem> getItems() {
		if (!hasLootingBag())
			return new ArrayList<RSItem>();

		if (Banking.isOpen()) {
			return getItems(Banking.LOOTING_BAG_BANK_MASTER, Banking.LOOTING_BAG_BANK_ITEMS_CHILD);
		}
		else {
			if (!isCheckLootingBagInterfaceOpen())
				select(BagOption.CHECK);

			if (!isCheckLootingBagInterfaceOpen())
				return new ArrayList<RSItem>();

			return getItems(LOOTING_BAG_MASTER, LOOTING_BAG_ITEMS_CHILD);
		}
	}

	private static List<RSItem> getItems(int master, int child) {
		List<RSItem> items = new ArrayList<RSItem>();

		RSInterfaceChild inter = Interfaces.get(master, child);
		if (inter == null)
			return items;

		RSInterfaceComponent[] children = inter.getChildren();
		if (children == null)
			return items;

		for (RSInterfaceComponent c : children) {
			try {
				if (c.getComponentItem() == -1)
					break;
				items.add(new RSItem(c.getComponentName().replace("<.?>", ""), c.getActions(), c.getIndex(), c.getComponentItem(),
						c.getComponentStack(), RSItem.TYPE.OTHER));
			}
			catch (Exception e) {
			}
		}

		return items;
	}

	public static class Banking {

		private final static int LOOTING_BAG_BANK_MASTER = 15;
		private final static int LOOTING_BAG_BANK_ITEMS_CHILD = 10;

		public static RSItem getLootingBag() {
			if (!Banking.isOpen())
				return null;

			return Entities.find(BankItemEntity::new).nameEquals("Looting bag").getFirstResult();
		}

		public static boolean hasLootingBag() {
			if (!Banking.isOpen())
				return false;

			return getLootingBag() != null;
		}

		public static boolean isViewing() {
			if (!Banking.isOpen())
				return false;

			return Entities.find(InterfaceEntity::new)
					.inMaster(LOOTING_BAG_BANK_MASTER)
					.isSubstantiated()
					.actionEquals("Deposit loot")
					.getFirstResult() != null;
		}

		public static boolean view() {
			if (isViewing())
				return true;

			if (!isViewing())
				select(BagOption.VIEW);

			return isViewing();
		}

		private static RSInterface getDepositLootButton() {
			RSInterface inter = Entities.find(InterfaceEntity::new).inMaster(LOOTING_BAG_BANK_MASTER).actionEquals("Deposit loot").getFirstResult();
			return Interfaces.isInterfaceSubstantiated(inter) ? inter : null;
		}

		public static int getValue() {
			int value = 0;

			if (view()) {
				List<RSItem> items = getItems();
				for (RSItem item : items)
					value += item.getStack() * OSBuddy.get(item).getAveragePrice();
			}

			return value;
		}

		public static boolean depositLoot() {
			if (!Banking.isOpen())
				return false;

			if (getItems().size() == 0)
				return true;

			if (view()) {
				RSInterface inter = getDepositLootButton();
				if (inter == null)
					return false;

				if (inter.click()) {
					if (Condition.wait(() -> getItems().size() == 0))
						dismiss();
				}
			}

			return getItems().size() == 0;
		}

		private static RSInterface getDismissButton() {
			RSInterface inter = Entities.find(InterfaceEntity::new).inMaster(LOOTING_BAG_BANK_MASTER).actionEquals("Dismiss").getFirstResult();
			return Interfaces.isInterfaceSubstantiated(inter) ? inter : null;
		}

		public static boolean dismiss() {
			if (!Banking.isOpen())
				return false;

			if (!isViewing())
				return true;

			RSInterface inter = getDismissButton();
			if (inter == null)
				return false;

			if (inter.click())
				return Condition.wait(() -> !isViewing());

			return !isViewing();
		}

		private static boolean isOpen() {
			return scripts.usa.api2007.Banking.isOpen();
		}
	}
}
