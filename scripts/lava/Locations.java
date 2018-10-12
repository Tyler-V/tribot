package scripts.lava;

import org.tribot.api2007.types.RSTile;

public class Locations {

    public enum LAVA_DRAGON_LOCATIONS {

	GATE_ENTRANCE(new RSTile(3212, 3852, 0), 10, 36),

	WEST_LAVA_DRAGON_ISLE(new RSTile(3183, 3838, 0), 10, 39),

	SOUTH_LAVA_DRAGON_ISLE(new RSTile(3186, 3810, 0), 10, 33),

	EAST_LAVA_DRAGON_ISLE_1(new RSTile(3208, 3811, 0), 7, 33),

	EAST_LAVA_DRAGON_ISLE_2(new RSTile(3215, 3814, 0), 7, 33),

	RANDOM(null, 0, 0);

	private RSTile tile;
	private int maximum_distance;
	private int minimum_camera_angle;

	LAVA_DRAGON_LOCATIONS(RSTile tile, int maximum_distance, int minimum_camera_angle) {
	    this.tile = tile;
	    this.maximum_distance = maximum_distance;
	    this.minimum_camera_angle = minimum_camera_angle;
	}

	public RSTile getSafeTile() {
	    return tile;
	}

	public int getMaximumDistance() {
	    return maximum_distance;
	}

	public int getMinimumCameraAngle() {
	    return minimum_camera_angle;
	}

    }

}
