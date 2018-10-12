package scripts.usa.api.painting.models;

public enum PaintColumns {

	AUTO(0),
	ONE(1),
	TWO(2),
	THREE(3),
	FOUR(4),
	FIVE(5);

	private int columns;

	private PaintColumns(int columns) {
		this.columns = columns;
	}

	public int get() {
		return this.columns;
	}

}
