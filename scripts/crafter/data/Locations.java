package scripts.crafter.data;

import java.util.Arrays;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

import scripts.usa.api.util.Strings;

public enum Locations {

	LUMBRIDGE_WHEEL(Type.SPINNING_WHEEL, "Spinning wheel", new RSTile(3209, 3213, 1)),

	NEITIZNOT_WHEEL(Type.SPINNING_WHEEL, "Spinning wheel", new RSTile(2353, 3794, 0)),

	PORT_PHASMATYS_FURNACE(Type.FURNACE, "Furnace", new RSTile(3687, 3479, 0)),

	NEITIZNOT_FORGE(Type.FURNACE, "Clay forge", new RSTile(2344, 3810, 0)),

	AL_KHARID_FURNACE(Type.FURNACE, "Furnace", new RSTile(3275, 3186, 0)),

	FALADOR_FURNACE(Type.FURNACE, "Furnace", new RSTile(2974, 3369, 0)),

	EDGEVILLE_FURNACE(Type.FURNACE, "Furnace", new RSTile(3108, 3499, 0));

	private final Type type;
	private final String objectName;
	private final RSTile tile;

	Locations(Type type, String objectName, RSTile tile) {
		this.type = type;
		this.objectName = objectName;
		this.tile = tile;
	}

	public Type getType() {
		return this.type;
	}

	public String getObjectName() {
		return this.objectName;
	}

	public RSTile getTile() {
		return this.tile;
	}

	public String getName() {
		return Strings.toProperCase(this.toString());
	}

	public static String[] getLocations(Type type) {
		return Arrays.stream(Locations.values())
				.filter(location -> location.getType() == type)
				.sorted((a, b) -> Integer.compare(Player.getPosition().distanceTo(a.getTile()), Player.getPosition().distanceTo(b.getTile())))
				.map(Enum::name)
				.map(name -> Strings.toProperCase(name))
				.toArray(String[]::new);
	}

	public static Locations getLocation(String name) {
		if (name == null)
			return null;
		for (Locations location : Locations.values()) {
			if (name.equals(location.getName()))
				return location;
		}
		return null;
	}

	public enum Type {
		BANK,
		FURNACE,
		SPINNING_WHEEL
	}
}
