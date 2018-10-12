package scripts.usa.api.web.items.osbuddy;

import java.awt.image.BufferedImage;
import java.util.regex.Pattern;

import org.tribot.api.General;
import org.tribot.api2007.types.RSItem;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import scripts.usa.api.web.WebUtils;
import scripts.usa.api2007.utils.RSItem.RSItemUtils;

public class OSBuddyUtils {

	public static BufferedImage getBufferedImage(RSItem item) {
		if (item == null)
			return null;

		int id = item.getID();
		BufferedImage bufferedImage = WebUtils.getImage("http://cdn.rsbuddy.com/items/" + id + ".png");
		if (bufferedImage != null)
			return bufferedImage;

		String name = RSItemUtils.getName(id);
		if (name == null)
			return null;

		if (!Pattern.compile("(.?)").matcher(name).find())
			return null;

		name = name.replaceAll("(.?)", "");
		for (int i = 8; i >= 6; i--) {
			General.println(name);
			OSBuddyItem osbuddyItem = OSBuddy.get(name + "(" + i + ")");
			if (item != null)
				return osbuddyItem.getBufferedImage();
		}

		System.out.println("Could not find image for " + name);
		return null;
	}

	public static WritableImage getWritableImage(RSItem item) {
		BufferedImage bufferedImage = getBufferedImage(item);
		if (bufferedImage == null)
			return null;

		return SwingFXUtils.toFXImage(bufferedImage, null);
	}
}
