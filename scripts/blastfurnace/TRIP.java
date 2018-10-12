package scripts.blastfurnace;

public enum TRIP {

	TIN_LOAD(new ITEM("Tin ore", 438, 0, 1)),

	COPPER_LOAD(new ITEM("Copper ore", 436, 0, 1)),

	IRON_LOAD_1(new ITEM("Iron ore", 440, 0, 1)),

	MITHRIL_LOAD(new ITEM("Mithril ore", 447, 0, 1)),

	COAL_LOAD_1(new ITEM("Coal", 453, 0, 1)),

	COAL_LOAD_2(new ITEM("Coal", 453, 0, 1)),

	ADAMANTITE_LOAD(new ITEM("Adamantite ore", 449, 0, 1)),

	COAL_LOAD_3(new ITEM("Coal", 453, 0, 1)),

	RUNITE_LOAD(new ITEM("Runite ore", 451, 0, 1)),

	COAL_LOAD_4(new ITEM("Coal", 453, 0, 1));

	private final ITEM item;
	private boolean complete = false;

	TRIP(ITEM item) {
		this.item = item;
	}

	public ITEM getItem() {
		return this.item;
	}

	public boolean isComplete() {
		return this.complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

}
