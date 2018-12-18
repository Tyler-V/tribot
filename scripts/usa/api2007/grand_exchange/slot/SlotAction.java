package scripts.usa.api2007.grand_exchange.slot;

import scripts.usa.api.util.Strings;

public enum SlotAction {

	BUY,
	SELL,
	VIEW_OFFER,
	ABORT_OFFER;

	public String getText() {
		return Strings.toSentenceCase(this.name());
	}
}
