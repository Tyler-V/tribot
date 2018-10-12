package scripts.crafter.data;

import scripts.usa.api.web.items.osbuddy.OSBuddy;

public class Material {

	private final String name;
	private final int id;
	private final int amount;
	private final boolean action;

	public Material(String name, int id, int amount, boolean action) {
		this.name = name;
		this.id = id;
		this.amount = amount;
		this.action = action;
	}

	public Material(String name, int id, int amount) {
		this(name, id, amount, false);
	}

	public String getName() {
		return this.name;
	}

	public int getID() {
		return this.id;
	}

	public int getRequiredAmount() {
		return this.amount;
	}

	public boolean isActionItem() {
		return this.action;
	}

	public int getValue() {
		return OSBuddy.get(this.id).getAveragePrice();
	}
}
