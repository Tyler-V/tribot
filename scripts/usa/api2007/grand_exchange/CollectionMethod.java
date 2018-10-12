package scripts.usa.api2007.grand_exchange;

/**
 * * METHOD represents the possible methods of collecting your items from the
 * Grand Exchange
 */
public enum CollectionMethod {

	DEFAULT(null),

	NOTES(new String[] { "Collect-note", "Collect-notes", "Collect to inventory", "Collect" }),

	ITEMS(new String[] { "Collect-item", "Collect-items", "Collect to inventory", "Collect" }),

	BANK(new String[] { "Bank", "Collect to bank" });

	public final String[] actions;

	CollectionMethod(String[] actions) {
		this.actions = actions;
	}

	public String[] getActions() {
		return this.actions;
	}

}
