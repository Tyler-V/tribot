package scripts.usa.api2007.looting;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyItem;

public class LootItem {

	private String name;
	private int id;
	private int minimumStack;
	private Condition condition;
	private int count;

	public LootItem(String name, int minimumStack, Condition condition) {
		this.name = name;
		this.minimumStack = minimumStack;
		OSBuddyItem item = OSBuddy.get(name);
		if (item != null)
			this.id = item.getId();
		this.condition = condition;
	}

	public LootItem(int id, int minimumStack, Condition condition) {
		this.id = id;
		this.minimumStack = minimumStack;
		OSBuddyItem item = OSBuddy.get(name);
		if (item != null)
			this.name = item.getName();
		this.condition = condition;
	}

	public LootItem(int id) {
		this(id, 1, null);
	}

	public String getName() {
		return this.name;
	}

	public int getId() {
		return this.id;
	}

	public int getMinimumStack() {
		return this.minimumStack;
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

	public boolean shouldLoot() {
		return this.condition != null || this.condition.isTrue();
	}
}
