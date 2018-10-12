package scripts.usa.api.web.items.osbuddy;

import java.awt.image.BufferedImage;

import org.tribot.api.General;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import scripts.usa.api.web.WebUtils;
import scripts.usa.api.web.items.osrs.OSRSItem;

public class OSBuddyItem extends OSRSItem {

	private final int id;
	private final String name;
	private final boolean members;
	private final int buyAverage;
	private final int buyQuantity;
	private final int averagePrice;
	private final int averageQuantity;
	private final int sellAverage;
	private final int sellQuantity;
	private final int storePrice;
	private BufferedImage bufferedImage;
	private WritableImage writableImage;

	public OSBuddyItem(int id, String name, boolean members, int buyAverage, int buyQuantity, int averagePrice, int averageQuantity, int sellAverage,
			int sellQuantity, int storePrice) {
		super(id);
		this.id = id;
		this.name = name;
		this.members = members;
		this.buyAverage = buyAverage;
		this.buyQuantity = buyQuantity;
		this.averagePrice = averagePrice;
		this.averageQuantity = averageQuantity;
		this.sellAverage = sellAverage;
		this.sellQuantity = sellQuantity;
		this.storePrice = storePrice;
	}

	public final int getId() {
		return this.id;
	}

	public final String getName() {
		return this.name;
	}

	public final boolean isMembers() {
		return this.members;
	}

	public final int getAveragePrice() {
		return this.averagePrice > 0 ? this.averagePrice : this.getCurrent().getPrice();
	}

	public final int getBuyAverage() {
		return this.buyAverage > 0 ? this.buyAverage : this.getCurrent().getPrice();
	}

	public final int getSellAverage() {
		return this.sellAverage > 0 ? this.sellAverage : this.getCurrent().getPrice();
	}

	public final int getAverageQuantity() {
		return this.averageQuantity;
	}

	public final int getBuyQuantity() {
		return this.buyQuantity;
	}

	public final int getSellQuantity() {
		return this.sellQuantity;
	}

	public final int getStorePrice() {
		return this.storePrice;
	}

	public final BufferedImage getBufferedImage() {
		if (this.bufferedImage != null)
			return this.bufferedImage;

		BufferedImage bufferedImage = WebUtils.getImage("http://cdn.rsbuddy.com/items/" + this.id + ".png");

		return this.bufferedImage = bufferedImage != null ? bufferedImage : null;
	}

	public final WritableImage getWritableImage() {
		if (this.writableImage != null)
			return this.writableImage;

		return this.writableImage = this.getBufferedImage() != null ? SwingFXUtils.toFXImage(this.getBufferedImage(), null) : null;
	}
}
