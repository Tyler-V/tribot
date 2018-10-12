package scripts.crafter.data;

public class Tool extends Material {

	private final boolean stackable;

	public Tool(String name, int id, int amount, boolean action, boolean stackable) {
		super(name, id, amount, action);
		this.stackable = stackable;
	}

	public Tool(String name, int id, int amount, boolean action) {
		super(name, id, amount, action);
		this.stackable = false;
	}

	public Tool(String name, int id, int amount) {
		super(name, id, amount);
		this.stackable = false;
	}

	public boolean isStackable() {
		return this.stackable;
	}
}
