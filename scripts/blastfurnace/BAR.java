package scripts.blastfurnace;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum BAR {

	BRONZE("Bronze bar", 2349, 1, 108, 162, Arrays.asList(TRIP.TIN_LOAD, TRIP.COPPER_LOAD), true),

	IRON("Iron bar", 2351, 15, 109, 325, Arrays.asList(TRIP.IRON_LOAD_1), true),

	STEEL("Steel bar", 2353, 30, 110, 455, Arrays.asList(TRIP.COAL_LOAD_1, TRIP.IRON_LOAD_1), false),

	MITHRIL("Mithril bar", 2359, 50, 111, 780, Arrays.asList(TRIP.MITHRIL_LOAD, TRIP.COAL_LOAD_1, TRIP.COAL_LOAD_2),
			true),

	ADAMANTITE("Adamantite bar", 2361, 70, 112, 975,
			Arrays.asList(TRIP.ADAMANTITE_LOAD, TRIP.COAL_LOAD_1, TRIP.COAL_LOAD_2, TRIP.COAL_LOAD_3), true),

	RUNITE("Runite bar", 2363, 85, 113, 1300,
			Arrays.asList(TRIP.RUNITE_LOAD, TRIP.COAL_LOAD_1, TRIP.COAL_LOAD_2, TRIP.COAL_LOAD_3, TRIP.COAL_LOAD_4),
			true);

	private final String name;
	private final int id;
	private final int level;
	private final int child;
	private final int xp;
	private final List<TRIP> trips;
	private final boolean randomize;
	private int count;
	private int profit;

	BAR(String name, int id, int level, int child, int xp, List<TRIP> trips, boolean randomize) {
		this.name = name;
		this.id = id;
		this.level = level;
		this.child = child;
		this.xp = xp;
		this.trips = trips;
		this.randomize = randomize;
	}

	public String getName() {
		return this.name;
	}

	public int getID() {
		return this.id;
	}

	public int getRequiredLevel() {
		return this.level;
	}

	public int getChild() {
		return this.child;
	}

	public int getExpectedXP() {
		return this.xp;
	}

	public List<TRIP> getTrips() {
		return this.trips;
	}

	public void setTrips(boolean complete) {

		for (TRIP trip : this.trips) {

			trip.setComplete(complete);

		}

	}

	public TRIP getNextTrip() {

		List<TRIP> list = this.trips;

		if (this.randomize)
			Collections.shuffle(list);

		for (TRIP trip : list) {

			if (!trip.isComplete())
				return trip;

		}

		return null;

	}

	public boolean canRandomize() {
		return this.randomize;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count += count;
	}

	public int getProfit() {
		return this.profit;
	}

	public void setProfit(int profit) {
		this.profit = profit;
	}

}
