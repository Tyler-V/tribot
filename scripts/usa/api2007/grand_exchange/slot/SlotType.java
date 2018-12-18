package scripts.usa.api2007.grand_exchange.slot;

import scripts.usa.api.util.Strings;

public enum SlotType {

	BUY,
	SELL,
	EMPTY;

	public String getText() {
		return Strings.toProperCase(this.name());
	}
}
