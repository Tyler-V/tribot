package scripts.usa.api.painting;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PaintUtils {

	public static Graphics2D create2D(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		return g2;
	}

	public static Color colorFromHex(String hex) {
		return colorFromHex(hex, 1);
	}

	public static Color colorFromHex(String hex, double opacityPercent) {
		Color color = Color.decode(hex);
		int opacity = (int) (opacityPercent * 255);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);

	}

	public static FontMetrics getFontMetrics(Font font) {
		Canvas c = new Canvas();
		return c.getFontMetrics(font);
	}

	private static int ShiftUp(int y, int distance) {
		return y - distance;
	}

	private static int ShiftDown(int y, int distance) {
		return y + distance;
	}

	private static int ShiftLeft(int x, int distance) {
		return x + distance;
	}

	private static int ShiftRight(int x, int distance) {
		return x - distance;
	}

	public static void drawOutline(Graphics2D g2, String text, int x, int y) {
		g2.setColor(new Color(50, 50, 50));
		g2.drawString(text, ShiftRight(x, 1), ShiftUp(y, 1));
		g2.drawString(text, ShiftRight(x, 1), ShiftDown(y, 1));
		g2.drawString(text, ShiftLeft(x, 1), ShiftUp(y, 1));
		g2.drawString(text, ShiftLeft(x, 1), ShiftDown(y, 1));
	}

	public static void drawShadow(Graphics2D g2, String text, int x, int y) {
		g2.setColor(new Color(50, 50, 50));
		g2.drawString(text, ShiftRight(x, 2), ShiftDown(y, 2));
	}

}
