package scripts.usa.api.web.items.osbuddy;

import java.io.IOException;
import java.util.HashMap;

import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;

import scripts.usa.api.timer.Timer;
import scripts.usa.api.web.WebUtils;
import scripts.usa.api.web.json.JsonObject;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

public class OSBuddy {

	private static final String URL = "https://storage.googleapis.com/osbuddy-exchange/summary.json";
	private static final int timeout = 900000; // 15m

	private static Timer timer;
	private static HashMap<Object, OSBuddyItem> items;

	static {
		reload();
	}

	public static void reload() {
		items = getItems();
		timer = new Timer(timeout);
	}

	private static boolean shouldReload() {
		return timer.isUp();
	}

	public static OSBuddyItem get(int id) {
		if (shouldReload())
			reload();
		return items.get(id);
	}

	public static OSBuddyItem get(String name) {
		if (shouldReload())
			reload();
		return items.get(name);
	}

	public static OSBuddyItem get(RSItem item) {
		if (shouldReload())
			reload();

		if (item == null)
			return null;

		RSItemDefinition definition = item.getDefinition();
		if (definition == null)
			return null;

		if (items.containsKey(item.getID()))
			return items.get(definition.isNoted() ? item.getID() - 1 : item.getID());

		return items.get(definition.getName());
	}

	public static OSBuddyItem get(RSGroundItem item) {
		if (shouldReload())
			reload();

		if (item == null)
			return null;

		RSItemDefinition definition = item.getDefinition();
		if (definition == null)
			return null;

		if (items.containsKey(item.getID()))
			return items.get(definition.isNoted() ? item.getID() - 1 : item.getID());

		return items.get(definition.getName());
	}

	public static HashMap<Object, OSBuddyItem> getItems() {
		System.out.println("Loading OSBuddy Exchange");
		try {
			HashMap<Object, OSBuddyItem> items = new HashMap<Object, OSBuddyItem>();

			JsonObject.readFrom(WebUtils.read(URL)).forEach(i -> {
				final JsonObject value = i.getValue().asObject();
				String name = value.get("name").asString();
				int id = value.get("id").asInt();
				boolean members = value.get("members").asBoolean();
				int buyAverage = value.get("buy_average").asInt();
				int buyQuantity = value.get("buy_quantity").asInt();
				int averagePrice = value.get("overall_average").asInt();
				int averageQuantity = value.get("overall_quantity").asInt();
				int sellAverage = value.get("sell_average").asInt();
				int sellQuantity = value.get("sell_quantity").asInt();
				int storePrice = value.get("sp").asInt();
				OSBuddyItem item = new OSBuddyItem(id, name, members, buyAverage, buyQuantity, averagePrice, averageQuantity, sellAverage,
						sellQuantity, storePrice);
				items.put(item.getId(), item);
				items.put(item.getName(), item);
			});

			OSBuddyItem coins = new OSBuddyItem(995, "Coins", false, 1, 0, 1, 0, 1, 0, 1);
			items.put(coins.getName(), coins);
			items.put(coins.getId(), coins);

			return items;
		}
		catch (IOException e) {
			System.out.println("Failed loading OSBuddy Exchange!");
			return items;
		}
	}
}
