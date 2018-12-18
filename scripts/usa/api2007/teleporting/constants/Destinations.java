package scripts.usa.api2007.teleporting.constants;

import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

import scripts.usa.api.condition.Condition;
import scripts.usa.api.util.Strings;
import scripts.usa.api2007.House;

public enum Destinations {

	AL_KHARID(new RSTile(3292, 3168, 0)),
	VARROCK_CENTER(new RSTile(3212, 3424, 0)),
	LUMBRIDGE_CASTLE(new RSTile(3221, 3218, 0)),
	DUEL_ARENA(new RSTile(3316, 3235, 0)),
	DRAYNOR_VILLAGE(new RSTile(3104, 3249, 0)),
	FALADOR_CENTER(new RSTile(2965, 3379, 0)),
	HOUSE(() -> House.isInside() || House.isOutside()),
	CAMELOT(new RSTile(2757, 3480, 0)),
	RANGED_GUILD(new RSTile(2654, 3440, 0)),
	ARDOUGNE_MARKET_PLACE(new RSTile(2631, 3300, 0)),
	CASTLE_WARS(new RSTile(2440, 3089, 0)),
	BURTHORPE_GAMES_ROOM(new RSTile(2899, 3552, 0)),
	CLAN_WARS(new RSTile(3389, 3158, 0)),
	KARAMJA_BANANA_PLANTATION(new RSTile(2919, 3174, 0)),
	WINTERTODT_CAMP(new RSTile(1624, 3940, 0)),
	CORPOREAL_BEAST(new RSTile(2966, 4383, 2)),
	BARBARIAN_OUTPOST(new RSTile(2518, 3572, 0)),
	WARRIORS_GUILD(new RSTile(2882, 3548, 0)),
	CHAMPIONS_GUILD(new RSTile(3191, 3366, 0)),
	MONASTRY_EDGE(new RSTile(3053, 3489, 0)),
	EDGEVILLE(new RSTile(3087, 3497, 0)),
	ECTO(new RSTile(3660, 3524, 0)),

	FISHING_GUILD(new RSTile(2610, 3391, 0)),
	MOTHERLOAD_MINE(new RSTile(3737, 5690, 0)),
	CRAFTING_GUILD(new RSTile(2934, 3293, 0)),
	COOKING_GUILD(new RSTile(3143, 3443, 0)),
	WOODCUTTING_GUILD(new RSTile(1660, 3504, 0)),

	GRAND_EXCHANGE(new RSTile(3164, 3465, 0)),
	FALADOR_PARK(new RSTile(2995, 3376, 0)),

	CHAOS_TEMPLE(new RSTile(3236, 3635, 0)),
	BANDIT_CAMP(new RSTile(3039, 3652, 0)),
	LAVA_MAZE(new RSTile(3029, 3843, 0));

	private RSTile tile;
	private Condition condition;

	Destinations(RSTile tile) {
		this.tile = tile;
	}

	Destinations(Condition condition) {
		this.condition = condition;
	}

	public String getName() {
		return Strings.toProperCase(this.name());
	}

	public RSTile getTile() {
		return this.tile;
	}

	public boolean hasArrived() {
		if (this.tile != null)
			return Player.getPosition().distanceTo(this.tile) < 5;
		return this.condition.isTrue();
	}
}
