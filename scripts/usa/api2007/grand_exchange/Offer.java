package scripts.usa.api2007.grand_exchange;

public enum Offer {

	SET_ITEM_PRICE(-1),

	SUBTRACT_FIVE_PERCENT(Constants.OFFER_WINDOW_PRICE_SUBTRACT_FIVE_PERCENT),

	SUBTRACT_TEN_PERCENT(Constants.OFFER_WINDOW_PRICE_SUBTRACT_FIVE_PERCENT),

	GUIDE_PRICE(Constants.OFFER_WINDOW_PRICE_GUIDE),

	ADD_FIVE_PERCENT(Constants.OFFER_WINDOW_PRICE_ADD_FIVE_PERCENT),

	ADD_TEN_PERCENT(Constants.OFFER_WINDOW_PRICE_ADD_FIVE_PERCENT);

	private final int child;

	Offer(int child) {
		this.child = child;
	}

	public int getChild() {
		return this.child;
	}
}
