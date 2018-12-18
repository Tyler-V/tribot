package scripts.usa.api2007.grand_exchange.enums;

public enum CollectionMethod {

	INVENTORY("Collect to inventory", "Collect-items", "Collect-item"),
	NOTE("Collect-notes", "Collect-note"),
	BANK("Collect to bank", "Bank");

	private final String[] text;

	CollectionMethod(String... text) {
		this.text = text;
	}

	public String[] getText() {
		return this.text;
	}
}
