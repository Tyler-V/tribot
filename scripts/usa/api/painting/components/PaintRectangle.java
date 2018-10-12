package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.interfaces.Paintable;

public class PaintRectangle implements Paintable {

	private final Rectangle bounds;
	private final Color backgroundColor;
	private final Color borderColor;

	public PaintRectangle(Rectangle bounds, Color backgroundColor, Color borderColor) {
		this.bounds = bounds;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
	}

	public PaintRectangle(Color backgroundColor, Color borderColor) {
		this(Painter.Bounds.GAME_CHAT_BOUNDS, backgroundColor, borderColor);
	}

	public PaintRectangle(Color backgroundColor) {
		this(Painter.Bounds.GAME_CHAT_BOUNDS, backgroundColor, Painter.Colors.TRANSPARENT);
	}

	public PaintRectangle() {
		this(Painter.Bounds.GAME_CHAT_BOUNDS, Painter.Colors.BACKGROUND_COLOR, Painter.Colors.BORDER_COLOR);
	}

	public Rectangle getBounds() {
		return this.bounds;
	}

	public Color getBackgroundColor() {
		return this.backgroundColor;
	}

	public Color getBorderColor() {
		return this.borderColor;
	}

	@Override
	public void paint(Graphics2D g2) {
		g2.setColor(backgroundColor);
		g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, Painter.ARC, Painter.ARC);
		g2.setColor(borderColor);
		g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, Painter.ARC, Painter.ARC);
	}
}
