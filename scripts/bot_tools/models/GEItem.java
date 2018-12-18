package scripts.bot_tools.models;

import scripts.usa.api2007.grand_exchange.enums.OfferType;

public class GEItem {

	private final OfferType offerType;
	private final String name;
	private final int quantity;
	private final double pricePercentage;

	public GEItem(OfferType offerType, String name, int quantity, double pricePercentage) {
		this.offerType = offerType;
		this.name = name;
		this.quantity = quantity;
		this.pricePercentage = pricePercentage;
	}

	public OfferType getOfferType() {
		return this.offerType;
	}

	public String getName() {
		return this.name;
	}

	public int getQuantity() {
		return this.quantity;
	}

	public double getPricePercentage() {
		return this.pricePercentage;
	}
}
