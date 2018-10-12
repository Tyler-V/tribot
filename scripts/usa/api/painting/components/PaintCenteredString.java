package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import scripts.usa.api.painting.PaintUtils;
import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.painting.interfaces.Text;

public class PaintCenteredString implements Paintable {
	private final Text text;
	private final Font font;
	private final Color color;
	private final Rectangle bounds;

	public PaintCenteredString(Text text, Font font, Color color, Rectangle bounds) {
		this.text = text;
		this.font = font;
		this.color = color;
		this.bounds = bounds;
	}

	public PaintCenteredString(Text text, Font font, Rectangle bounds) {
		this(text, font, Painter.Colors.COLOR, bounds);
	}

	@Override
	public void paint(Graphics2D g2) {
		try {
			g2.setFont(font);
			FontMetrics metrics = g2.getFontMetrics(font);
			int x = bounds.x + (bounds.width - metrics.stringWidth(text.getString())) / 2;
			int y = bounds.y + ((bounds.height - metrics.getHeight()) / 2) + metrics.getAscent() + 1;
			PaintUtils.drawOutline(g2, text.getString(), x, y);
			g2.setColor(color);
			g2.drawString(text.getString(), x, y);
		}
		catch (Exception e) {
		}
	}
}
