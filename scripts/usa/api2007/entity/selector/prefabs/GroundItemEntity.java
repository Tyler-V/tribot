package scripts.usa.api2007.entity.selector.prefabs;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.lang3.ArrayUtils;
import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSNPC;

import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyItem;
import scripts.usa.api2007.entity.selector.PositionableFinder;
import scripts.usa.api2007.looting.Looter;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

/**
 * @author Laniax
 */
public class GroundItemEntity extends PositionableFinder<RSGroundItem, GroundItemEntity> {

	/**
	 * {@link Filters.GroundItems#idEquals}
	 */
	public GroundItemEntity idEquals(int... id) {
		filters.add(Filters.GroundItems.idEquals(id));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#idNotEquals}
	 */
	public GroundItemEntity idNotEquals(int... id) {
		filters.add(Filters.GroundItems.idNotEquals(id));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#nameNotEquals}
	 */
	public GroundItemEntity nameNotEquals(String... names) {
		filters.add(Filters.GroundItems.nameNotEquals(names));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#nameEquals}
	 */
	public GroundItemEntity nameEquals(String... names) {
		filters.add(Filters.GroundItems.nameEquals(names));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#nameContains}
	 */
	public GroundItemEntity nameContains(String... names) {
		filters.add(Filters.GroundItems.nameContains(names));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#nameNotContains}
	 */
	public GroundItemEntity nameNotContains(String... names) {
		filters.add(Filters.GroundItems.nameNotContains(names));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#actionsContains}
	 */
	public GroundItemEntity actionsContains(String... actions) {
		filters.add(Filters.GroundItems.actionsContains(actions));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#actionsNotContains}
	 */
	public GroundItemEntity actionsNotContains(String... actions) {
		filters.add(Filters.GroundItems.actionsNotContains(actions));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#actionsEquals}
	 */
	public GroundItemEntity actionsEquals(String... actions) {
		filters.add(Filters.GroundItems.actionsEquals(actions));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#actionsNotEquals}
	 */
	public GroundItemEntity actionsNotEquals(String... actions) {
		filters.add(Filters.GroundItems.actionsNotEquals(actions));
		return this;
	}

	/**
	 * {@link Filters.GroundItems#inArea}
	 */
	public GroundItemEntity inArea(int minimum, int maximum) {
		filters.add(new Filter<RSGroundItem>() {
			public boolean accept(RSGroundItem item) {
				int id = item.getID();
				OSBuddyItem osbuddyItem = OSBuddy.get(id);
				if (osbuddyItem == null)
					return false;
				int value = osbuddyItem.getAveragePrice() * item.getStack();
				return value >= minimum && value <= maximum;
			}
		});
		return this;
	}

	/**
	 * {@link Filters.GroundItems#actionsNotEquals}
	 */
	public GroundItemEntity valueBetween(int minimum, int maximum) {
		filters.add(new Filter<RSGroundItem>() {
			public boolean accept(RSGroundItem item) {
				OSBuddyItem osbuddyItem = OSBuddy.get(item);
				if (osbuddyItem == null)
					return false;

				int value = osbuddyItem.getAveragePrice() * item.getStack();
				return value >= minimum && value <= maximum;
			}
		});
		return this;
	}

	/**
	 * {@link Filters.GroundItems#actionsNotEquals}
	 */
	public GroundItemEntity isValid(Looter looter) {
		filters.add(new Filter<RSGroundItem>() {
			public boolean accept(RSGroundItem item) {
				if (looter.getItems().containsKey(item.getID()) && looter.getItems().get(item.getID()).isMandatory())
					return true;

				String name = RSItemUtils.getName(item);
				if (looter.getItems().containsKey(name) && looter.getItems().get(name).isMandatory())
					return true;

				OSBuddyItem osbuddyItem = OSBuddy.get(item);
				if (osbuddyItem == null)
					return false;

				int value = osbuddyItem.getAveragePrice() * item.getStack();
				return value >= looter.getMinimumValue() && value <= looter.getMaximumValue();
			}
		});
		return this;
	}

	public GroundItemEntity sortByValue() {
		comparators.add(new Comparator<RSGroundItem>() {
			public int compare(RSGroundItem a, RSGroundItem b) {
				OSBuddyItem osbuddyA = OSBuddy.get(a);
				OSBuddyItem osbuddyB = OSBuddy.get(b);
				if (osbuddyA == null)
					return -1;
				if (osbuddyB == null)
					return 0;
				int valueA = osbuddyA.getAveragePrice() * a.getStack();
				int valueB = osbuddyB.getAveragePrice() * b.getStack();
				return valueA > valueB ? -1 : (valueA < valueB ? 1 : 0);
			}
		});
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public RSGroundItem[] getResults() {
		Filter<RSGroundItem> filter = super.buildFilter();

		RSGroundItem[] items = GroundItems.getAll(filter);
		if (items.length == 0)
			return items;

		if (super.shouldSort()) {
			Comparator<RSGroundItem> comparator = super.buildComparator();
			items = Arrays.stream(items).sorted(comparator).toArray(RSGroundItem[]::new);
		}

		return items;
	}
}
