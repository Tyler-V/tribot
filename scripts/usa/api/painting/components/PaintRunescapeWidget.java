package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.web.WebUtils;

public class PaintRunescapeWidget extends PaintRectangle implements Paintable {

	private final BufferedImage TOP_LEFT = WebUtils.getImage(Painter.Images.BORDER_TOP_LEFT);
	private final BufferedImage TOP_RIGHT = WebUtils.getImage(Painter.Images.BORDER_TOP_RIGHT);
	private final BufferedImage BOTTOM_LEFT = WebUtils.getImage(Painter.Images.BORDER_BOTTOM_LEFT);
	private final BufferedImage BOTTOM_RIGHT = WebUtils.getImage(Painter.Images.BORDER_BOTTOM_RIGHT);

	public PaintRunescapeWidget(Rectangle bounds, Color backgroundColor, Color borderColor) {
		super(bounds, backgroundColor, borderColor);
	}

	public PaintRunescapeWidget(Color backgroundColor, Color borderColor) {
		super(Painter.Bounds.GAME_CHAT_BOUNDS, backgroundColor, borderColor);
	}

	public PaintRunescapeWidget(Color backgroundColor) {
		super(Painter.Bounds.GAME_CHAT_BOUNDS, backgroundColor, Painter.Colors.TRANSPARENT);
	}

	public PaintRunescapeWidget() {
		super();
	}

	@Override
	public void paint(Graphics2D g2) {
		g2.setColor(getBackgroundColor());
		g2.fillRoundRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height, Painter.ARC, Painter.ARC);
		g2.setColor(getBorderColor());
		g2.drawRoundRect(getBounds().x, getBounds().y, getBounds().width, getBounds().height, Painter.ARC, Painter.ARC);
		g2.drawImage(TOP_LEFT, getBounds().x, getBounds().y + 1, null);
		g2.drawImage(TOP_RIGHT, getBounds().x + getBounds().width - BOTTOM_RIGHT.getWidth() + 1, getBounds().y + 1, null);
		g2.drawImage(BOTTOM_LEFT, getBounds().x, getBounds().y + getBounds().height - BOTTOM_RIGHT.getHeight() + 1, null);
		g2.drawImage(BOTTOM_RIGHT, getBounds().x + getBounds().width - BOTTOM_RIGHT.getWidth() + 1, getBounds().y + getBounds().height -
				BOTTOM_RIGHT.getHeight() +
				1, null);
	}
}
