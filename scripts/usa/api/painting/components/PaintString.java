package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import scripts.usa.api.painting.PaintUtils;
import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.painting.interfaces.Text;

public class PaintString implements Paintable {

	private final Text text;
	private final Font font;
	private final Color color;
	private final int x;
	private final int y;

	public PaintString(Text text, Font font, Color color, int x, int y) {
		this.text = text;
		this.font = font;
		this.color = color;
		this.x = x;
		this.y = y;
	}

	public PaintString(Text text, Font font, int x, int y) {
		this(text, font, Painter.Colors.COLOR, x, y);
	}

	@Override
	public void paint(Graphics2D g2) {
		try {
			g2.setFont(font);
			PaintUtils.drawOutline(g2, text.getString(), x, y);
			g2.setColor(color);
			g2.drawString(text.getString(), x, y);
		}
		catch (Exception e) {
		}
	}
}
