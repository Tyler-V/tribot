package scripts.tutorial;

import org.tribot.api2007.types.RSTile;

public enum Location {

	NOWHERE(null),

	GRAND_EXCHANGE(new RSTile(3163, 3486, 0)),

	VARROCK_WEST_BANK(new RSTile(3183, 3433, 0)),

	DRAYNOR_BANK(new RSTile(3094, 3243, 0)),

	FALADOR_EAST_BANK(new RSTile(3012, 3356, 0)),

	EDGEVILLE_BANK(new RSTile(3093, 3490, 0)),

	DUEL_ARENA(new RSTile(3368, 3269, 0));

	private RSTile tile;

	Location(RSTile tile) {
		this.tile = tile;
	}

	public RSTile getTile() {
		return tile;
	}
}