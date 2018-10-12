package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.Graphics2D;

import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.painting.interfaces.Polygon;

public class PaintPolygon implements Paintable {

	private final Polygon polygon;
	private final Color color;

	public PaintPolygon(Polygon polygon, Color color) {
		this.polygon = polygon;
		this.color = color;
	}

	@Override
	public void paint(Graphics2D g2) {
		try {
			g2.setColor(color);
			g2.drawPolygon(polygon.getPolygon());
		}
		catch (Exception e) {
		}
	}
}
