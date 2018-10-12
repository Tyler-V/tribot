package scripts.usa.api.web.items.osrs;

public class ItemTrend {

	private final String trend;
	private final String change;

	public ItemTrend(String trend, String change) {
		this.trend = trend;
		this.change = change;
	}

	public String getTrend() {
		return this.trend;
	}

	public String getChange() {
		return this.change;
	}
}
