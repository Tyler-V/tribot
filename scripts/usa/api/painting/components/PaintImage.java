package scripts.usa.api.painting.components;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.web.WebUtils;

public class PaintImage implements Paintable {

	private final BufferedImage image;
	private final int x;
	private final int y;

	public PaintImage(String url, int x, int y) {
		this.image = WebUtils.getImage(url);
		this.x = x;
		this.y = y;
	}

	@Override
	public void paint(Graphics2D g2) {
		g2.drawImage(image, x, y, null);
	}
}
