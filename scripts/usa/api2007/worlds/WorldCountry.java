package scripts.usa.api2007.worlds;

public enum WorldCountry {

	UNITED_STATES(0), // 1133 texture id

	UNITED_KINGDOM(1), // 1135 texture id

	GERMANY(7); // 1140 texture id

	private int id;

	WorldCountry(int id) {
		this.id = id;
	}

	public int getID() {
		return this.id;
	}

	public static WorldCountry get(int id) {
		switch (id) {
			case 0:
				return WorldCountry.UNITED_STATES;
			case 1:
				return WorldCountry.UNITED_KINGDOM;
			case 7:
				return WorldCountry.GERMANY;
		}
		return null;
	}
}
