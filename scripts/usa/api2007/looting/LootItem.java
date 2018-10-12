package scripts.usa.api2007.looting;

import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyItem;

public class LootItem {

	private String name;
	private int id;
	private int count;
	private boolean mandatory;

	public LootItem(String name, boolean mandatory) {
		this.name = name;
		OSBuddyItem item = OSBuddy.get(name);
		if (item != null)
			this.id = item.getId();
		this.mandatory = mandatory;
	}

	public LootItem(int id, boolean mandatory) {
		this.id = id;
		OSBuddyItem item = OSBuddy.get(name);
		if (item != null)
			this.name = item.getName();
		this.mandatory = mandatory;
	}

	public LootItem(int id) {
		this(id, false);
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public OSBuddyItem getOSBuddyItem() {
		OSBuddyItem item = OSBuddy.get(id);
		return item != null ? OSBuddy.get(this.id) : null;
	}

	public int getCount() {
		return this.count;
	}

	public void addToCount(int count) {
		this.count += count;
	}

	public boolean isMandatory() {
		return this.mandatory;
	}
}
