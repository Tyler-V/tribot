package scripts.dragons;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;

public enum DRAGON {

	GRAVEYARD_GREEN_DRAGONS(
			"Green dragon",
			new RSTile(3141, 3701, 0),
			new RSArea(new RSTile(3130, 3688, 0), new RSTile(3172, 3721, 0)),
			new RSTile(3143, 3674, 0),
			new RSTile[] { new RSTile(3205, 3682, 0), new RSTile(3202, 3677, 0), new RSTile(3198, 3677, 0), new RSTile(3195, 3678, 0), new RSTile(3192, 3680, 0), new RSTile(3190, 3681, 0), new RSTile(3188, 3683, 0), new RSTile(3186, 3686, 0),
					new RSTile(3184, 3688, 0), new RSTile(3182, 3690, 0), new RSTile(3180, 3691, 0), new RSTile(3177, 3693, 0), new RSTile(3174, 3693, 0), new RSTile(3170, 3693, 0), new RSTile(3168, 3693, 0), new RSTile(3166, 3693, 0),
					new RSTile(3163, 3693, 0), new RSTile(3160, 3693, 0), new RSTile(3158, 3693, 0), new RSTile(3155, 3693, 0), new RSTile(3152, 3694, 0), new RSTile(3150, 3697, 0), new RSTile(3148, 3700, 0) }),

	BONEYARD_GREEN_DRAGONS(
			"Green dragon",
			new RSTile(3337, 3687, 0),
			new RSArea(new RSTile(3324, 3668, 0), new RSTile(3358, 3713, 0)),
			new RSTile(3337, 3660, 0),
			new RSTile[] { new RSTile(3205, 3684, 0), new RSTile(3210, 3685, 0), new RSTile(3215, 3685, 0), new RSTile(3220, 3683, 0), new RSTile(3225, 3683, 0), new RSTile(3230, 3682, 0), new RSTile(3235, 3683, 0), new RSTile(3240, 3684, 0),
					new RSTile(3245, 3685, 0), new RSTile(3250, 3686, 0), new RSTile(3255, 3686, 0), new RSTile(3260, 3687, 0), new RSTile(3265, 3687, 0), new RSTile(3270, 3688, 0), new RSTile(3275, 3688, 0), new RSTile(3280, 3689, 0),
					new RSTile(3285, 3691, 0), new RSTile(3290, 3691, 0), new RSTile(3295, 3691, 0), new RSTile(3300, 3690, 0), new RSTile(3305, 3690, 0), new RSTile(3310, 3690, 0), new RSTile(3315, 3691, 0), new RSTile(3320, 3690, 0),
					new RSTile(3325, 3690, 0), new RSTile(3330, 3691, 0) });

	private final String name;
	private final RSTile location;
	private final RSArea area;
	private final RSTile teleportTile;
	private final RSTile[] path;

	DRAGON(String name, RSTile location, RSArea area, RSTile teleportTile, RSTile[] path) {
		this.name = name;
		this.location = location;
		this.area = area;
		this.teleportTile = teleportTile;
		this.path = path;
	}

	public String getName() {
		return this.name;
	}

	public RSTile getTile() {
		return this.location;
	}

	public RSArea getArea() {
		return this.area;
	}

	public RSTile getTeleportTile() {
		return this.teleportTile;
	}

	public RSTile[] getPath() {
		return this.path;
	}

}
