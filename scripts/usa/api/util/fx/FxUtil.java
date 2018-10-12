package scripts.usa.api.util.fx;

import org.tribot.api2007.types.RSTile;

public class FxUtil {

	public static RSTile parseTile(String text) {
		try {
			String[] split = text.substring(1, text.length() - 1).replaceAll(" ", "").split(",");
			return new RSTile(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		}
		catch (Exception e) {
			return null;
		}
	}
}
