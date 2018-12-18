package scripts.usa.api2007.grand_exchange.enums;

import scripts.usa.api.util.Strings;

public enum OfferType {

	SELL,
	BUY;

	public String getText() {
		return Strings.toProperCase(this.name());
	}
}
