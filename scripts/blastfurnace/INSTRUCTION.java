package scripts.blastfurnace;

public enum INSTRUCTION {

	PUMP("P"), STOP("S");

	private final String action;
	private long time = 0L;

	INSTRUCTION(String action) {
		this.action = action;
	}

	public String getAction() {
		return this.action;
	}

	public long getNextTime() {
		return this.time;
	}

	public void setNextTime(long time) {
		this.time = time;
	}

}
