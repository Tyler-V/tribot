package scripts.construction.data;

public class Material {

	private String name;
	private int requiredAmount;

	public Material(String name, int requiredAmount) {
		this.name = name;
		this.requiredAmount = requiredAmount;
	}

	public String getName() {
		return this.name;
	}

	public int getRequiredAmount() {
		return this.requiredAmount;
	}
}
