package scripts.usa.api2007.servant;

import java.util.Arrays;

import org.tribot.api2007.types.RSNPC;

public enum Servants {

	DEMON_BUTLER("Demon butler", 26);

	private final String name;
	private final int capacity;

	Servants(String name, int capacity) {
		this.name = name;
		this.capacity = capacity;
	}

	public String getName() {
		return this.name;
	}

	public int getCapacity() {
		return this.capacity;
	}

	public static Servants getServant(RSNPC npc) {
		if (npc == null)
			return null;
		String name = npc.getName();
		if (name == null)
			return null;
		return Arrays.stream(Servants.values()).filter(servant -> servant.getName().equalsIgnoreCase(name)).findFirst().get();
	}

	public static String[] getNames() {
		return Arrays.stream(Servants.values()).map(Servants::getName).toArray(String[]::new);
	}
}
