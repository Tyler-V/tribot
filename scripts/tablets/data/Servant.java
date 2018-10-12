package scripts.tablets.data;

public enum Servant {

	DEMON_BUTLER("Demon butler", 26);

	private final String name;
	private final int capacity;

	Servant(String name, int capacity) {
		this.name = name;
		this.capacity = capacity;
	}

	public String getName() {
		return this.name;
	}

	public int getCapacity() {
		return this.capacity;
	}
}
