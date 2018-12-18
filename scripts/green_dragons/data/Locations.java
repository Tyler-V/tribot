package scripts.green_dragons.data;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

import scripts.usa.api.util.Strings;

public enum Locations {

	GRAVEYARD(new RSTile(3143, 3704, 0), new RSTile(3139, 3672, 0), new RSArea(new RSTile(3130, 3690, 0), new RSTile(3170, 3721, 0))),

	BONEYARD(new RSTile(3337, 3687, 0), new RSTile(3338, 3672, 0), new RSArea(new RSTile(3324, 3668, 0), new RSTile(3358, 3713, 0)));

	private final RSTile centerTile;
	private final RSTile teleportTile;
	private final RSArea area;

	Locations(RSTile centerTile, RSTile teleportTile, RSArea area) {
		this.centerTile = centerTile;
		this.teleportTile = teleportTile;
		this.area = area;
	}

	public String getName() {
		return Strings.toProperCase(this.name()) + " Green Dragons";
	}

	public RSTile getCenterTile() {
		return this.centerTile;
	}

	public RSTile getTeleportTile() {
		return this.teleportTile;
	}

	public RSArea getArea() {
		return this.area;
	}
}
