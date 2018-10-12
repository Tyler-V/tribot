package scripts.blastfurnace;

public enum DISPENSER_STATUS {

	EMPTY(new int[] { 9093 }),

	COOL(new int[] { 9094, 9095 }),

	TAKE(new int[] { 9096 });

	private final int[] id;

	DISPENSER_STATUS(int[] id) {
		this.id = id;
	}

	public int[] getID() {
		return this.id;
	}

}
