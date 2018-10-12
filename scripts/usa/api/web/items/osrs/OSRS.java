package scripts.usa.api.web.items.osrs;

import java.io.IOException;

import scripts.usa.api.web.WebUtils;
import scripts.usa.api.web.json.JsonObject;
import scripts.usa.api.web.json.JsonValue;

public class OSRS {

	public static final String URL = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=";

	public static OSRSItem get(int id) {
		JsonObject jsonObject;
		try {
			jsonObject = JsonObject.readFrom(WebUtils.read(URL + "" + id)).get("item").asObject();
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		String name = jsonObject.get("name").asString();
		String description = jsonObject.get("description").asString();
		boolean members = Boolean.parseBoolean(jsonObject.get("members").asString());
		String icon = jsonObject.get("icon").asString();
		String icon_large = jsonObject.get("icon_large").asString();
		String type = jsonObject.get("type").asString();
		String typeIcon = jsonObject.get("typeIcon").asString();
		ItemPrice current = new ItemPrice(jsonObject.get("current").asObject().get("trend").asString(),
				parseInt(jsonObject.get("current").asObject().get("price")));
		ItemTrend today = new ItemTrend(jsonObject.get("today").asObject().get("trend").asString(),
				parseString(jsonObject.get("today").asObject().get("price")));
		ItemTrend day30 = new ItemTrend(jsonObject.get("day30").asObject().get("trend").asString(),
				jsonObject.get("day30").asObject().get("change").asString());
		ItemTrend day90 = new ItemTrend(jsonObject.get("day90").asObject().get("trend").asString(),
				jsonObject.get("day90").asObject().get("change").asString());
		ItemTrend day180 = new ItemTrend(jsonObject.get("day30").asObject().get("trend").asString(),
				jsonObject.get("day180").asObject().get("change").asString());
		System.out.println("Successfully fetched OSRS data for '" + name + "'");
		return new OSRSItem(id, name, description, members, icon, icon_large, type, typeIcon, current, today, day30, day90, day180);
	}

	private static String parseString(JsonValue value) {
		try {
			return value.asString();
		}
		catch (UnsupportedOperationException e) {
			return Integer.toString(value.asInt());
		}
	}

	private static int parseInt(JsonValue value) {
		try {
			return value.asInt();
		}
		catch (UnsupportedOperationException e) {
			String text = value.asString().replaceAll(",", "");
			if (text.contains("m")) {
				return Integer.parseInt(String.format("%.0f", Double.parseDouble(text.replaceAll("[^\\d.]", "")) * 1000000));
			}
			else if (text.contains("k")) {
				return Integer.parseInt(String.format("%.0f", Double.parseDouble(text.replaceAll("[^\\d.]", "")) * 1000));
			}
			return Integer.parseInt(text);
		}
	}
}
