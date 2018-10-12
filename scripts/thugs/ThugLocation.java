package scripts.thugs;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

public enum ThugLocation {

	WEST(new RSArea(new RSTile[] { new RSTile(3127, 9932, 0), new RSTile(3127, 9930, 0), new RSTile(3128, 9930, 0),
			new RSTile(3129, 9930, 0), new RSTile(3129, 9931, 0) })),

	NORTH(new RSArea(new RSTile[] { new RSTile(3131, 9934, 0), new RSTile(3131, 9932, 0), new RSTile(3133, 9932, 0),
			new RSTile(3133, 9933, 0) })),

	SOUTH(new RSArea(new RSTile[] { new RSTile(3131, 9929, 0), new RSTile(3131, 9931, 0), new RSTile(3129, 9931, 0),
			new RSTile(3129, 9929, 0) }));

	private final RSArea area;

	ThugLocation(RSArea area) {
		this.area = area;
	}

	public RSArea getArea() {
		return this.area;
	}

	public RSTile getRandomTile() {
		return this.area.getRandomTile();
	}

}
