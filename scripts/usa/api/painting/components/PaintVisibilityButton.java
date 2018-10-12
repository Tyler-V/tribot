package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.painting.interfaces.Visibility;
import scripts.usa.api.painting.media.Icons;

public class PaintVisibilityButton extends PaintButton implements Paintable {

	private final static Point CENTER = new Point(515, 338);
	private final static int RADIUS = 26;

	private final Visibility visible;
	private final Icons.Color color;
	private final Color backgroundColor;
	private final Color borderColor;

	public PaintVisibilityButton(Visibility visible, Point center, int radius, Icons.Color color, Color backgroundColor, Color borderColor) {
		super(center, RADIUS);
		this.visible = visible;
		this.color = color;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
	}

	public PaintVisibilityButton(Visibility visible, Icons.Color color, Color backgroundColor, Color borderColor) {
		this(visible, CENTER, RADIUS, color, backgroundColor, borderColor);
	}

	public PaintVisibilityButton(Visibility visible, Icons.Color color, Color backgroundColor) {
		this(visible, CENTER, RADIUS, color, backgroundColor, Painter.Colors.BORDER_COLOR);
	}

	public PaintVisibilityButton(Visibility visible, Icons.Color color) {
		this(visible, CENTER, RADIUS, color, Painter.Colors.BACKGROUND_COLOR, Painter.Colors.BORDER_COLOR);
	}

	public boolean isVisible() {
		return visible.isVisible();
	}

	public Color getBackgroundColor() {
		return this.backgroundColor;
	}

	public Color getBorderColor() {
		return this.borderColor;
	}

	public BufferedImage getIcon() {
		return isVisible()
				? (color == Icons.Color.LIGHT ? Icons.MaterialDesign.VISIBILITY_WHITE.getImage() : Icons.MaterialDesign.VISIBILITY_BLACK.getImage())
				: (color == Icons.Color.LIGHT ? Icons.MaterialDesign.VISIBILITY_OFF_WHITE.getImage()
						: Icons.MaterialDesign.VISIBILITY_OFF_BLACK.getImage());
	}

	@Override
	public void paint(Graphics2D g2) {
		g2.setPaint(getBackgroundColor());
		g2.fillOval(this.getCenter().x - RADIUS / 2, this.getCenter().y - RADIUS / 2, RADIUS, RADIUS);
		g2.setPaint(getBorderColor());
		g2.drawOval(this.getCenter().x - RADIUS / 2, this.getCenter().y - RADIUS / 2, RADIUS, RADIUS);
		g2.drawImage(getIcon(), this.getCenter().x - getIcon().getWidth() / 2, this.getCenter().y - getIcon().getHeight() / 2, null);
	}

}
