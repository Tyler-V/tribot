package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.painting.interfaces.Text;

public class PaintBoxString implements Paintable {

	private final Text text;
	private final int height;
	private final Font font;
	private Color textColor;
	private Color backgroundColor;
	private Color borderColor;
	private Rectangle bounds;

	public PaintBoxString(Text text, int height, Font font, Color textColor, Color backgroundColor, Color borderColor) {
		this.text = text;
		this.height = height;
		this.font = font;
		this.textColor = textColor;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
	}

	public PaintBoxString(Text text, Color textColor, Color backgroundColor, Color borderColor) {
		this(text, Painter.BOX_HEIGHT, Painter.FONT, textColor, backgroundColor, borderColor);
	}

	public PaintBoxString(Text text) {
		this(text, Painter.BOX_HEIGHT, Painter.FONT, Painter.Colors.COLOR, Painter.Colors.BACKGROUND_COLOR, Painter.Colors.BORDER_COLOR);
	}

	public String getString() {
		return this.text.getString();
	}

	public int getHeight() {
		return this.height;
	}

	public Rectangle getBounds() {
		return this.bounds;
	}

	public void setTextColor(Color color) {
		this.textColor = color;
	}

	public void setBackgroundColor(Color color) {
		this.backgroundColor = color;
	}

	public void setBorderColor(Color color) {
		this.borderColor = color;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	@Override
	public void paint(Graphics2D g2) {
		try {
			g2.setColor(backgroundColor);
			g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, Painter.ARC, Painter.ARC);
			g2.setColor(borderColor);
			g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, Painter.ARC, Painter.ARC);
			g2.setColor(textColor);
			g2.setFont(font);
			FontMetrics metrics = g2.getFontMetrics(Painter.FONT);
			int x = bounds.x + 3;
			int y = getBounds().y + ((getBounds().height - metrics.getHeight()) / 2) + metrics.getAscent() + 1;
			g2.drawString(text.getString(), x, y);
		}
		catch (Exception e) {
		}
	}
}
