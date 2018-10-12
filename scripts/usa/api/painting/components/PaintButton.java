package scripts.usa.api.painting.components;

import java.awt.Point;
import java.awt.Rectangle;

public abstract class PaintButton {
	private Rectangle bounds;
	private Point center;
	private int radius;

	public PaintButton(Rectangle bounds) {
		this.bounds = bounds;
	}

	public PaintButton(Point center, int radius) {
		this.center = center;
		this.radius = radius;
	}

	public Rectangle getBounds() {
		return this.bounds;
	}

	public Point getCenter() {
		return this.center;
	}

	public int getRadius() {
		return this.radius;
	}

	public boolean isClicked(Point point) {
		if (getBounds() != null) {
			return getBounds().contains(point);
		}
		else if (getCenter() != null) {
			return getCenter().distance(point) <= getRadius();
		}
		return false;
	}
}
