package scripts.usa.api.web.items.osrs;

public class ItemPrice {

	private final String trend;
	private final int price;

	public ItemPrice(String trend, int price) {
		this.trend = trend;
		this.price = price;
	}

	public String getTrend() {
		return this.trend;
	}

	public int getPrice() {
		return this.price;
	}
}
