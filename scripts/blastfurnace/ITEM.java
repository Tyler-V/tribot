package scripts.blastfurnace;

public class ITEM {

	private final String name;
	private final int id;
	private int amount;
	private final int minimumAmount;

	public ITEM(String name, int id, int amount, int minimumAmount) {
		this.name = name;
		this.id = id;
		this.amount = amount;
		this.minimumAmount = minimumAmount;
	}

	public String getName() {
		return this.name;
	}

	public int getID() {
		return this.id;
	}

	public int getAmount() {
		return this.amount;
	}

	public int getMinimumAmount() {
		return this.minimumAmount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
