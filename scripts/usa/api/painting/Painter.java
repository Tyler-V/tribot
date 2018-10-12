package scripts.usa.api.painting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.tribot.script.interfaces.EventBlockingOverride.OVERRIDE_RETURN;

import scripts.usa.api.painting.components.PaintVisibilityButton;
import scripts.usa.api.painting.media.Fonts;

public abstract class Painter {

	public static class Images {
		public static final String GAME_CHAT = "https://i.imgur.com/rdcHprH.png";
		public static final String BORDER_TOP_LEFT = "https://i.imgur.com/gB9VjKV.png";
		public static final String BORDER_TOP_RIGHT = "https://i.imgur.com/jItGvtT.png";
		public static final String BORDER_BOTTOM_LEFT = "https://i.imgur.com/eBnyHln.png";
		public static final String BORDER_BOTTOM_RIGHT = "https://i.imgur.com/xVRhihD.png";
	}

	public static class Bounds {
		public static final Rectangle GAME_CHAT_BOUNDS = new Rectangle(1, 338, 516, 140);
	}

	public static class Colors {
		public static final Color COLOR = new Color(255, 255, 255, 255);
		public static final Color BACKGROUND_COLOR = new Color(150, 150, 150, 225);
		public static final Color BORDER_COLOR = new Color(0, 0, 0, 255);
		public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	}

	public static final Font FONT = new Font(Fonts.Google.ROBOTO.getName(), 0, 12);
	public static final int PADDING = 6;
	public static final int BOX_HEIGHT = 18;
	public static final int ARC = 5;

	private PaintCollection paintCollection;

	public Painter() {
		this.paintCollection = new PaintCollection();
	}

	public PaintCollection getPaint() {
		return this.paintCollection;
	}

	public boolean isVisibilityButtonClicked(Point point) {
		PaintVisibilityButton paintVisibilityButton = getPaint().getVisibilityButton();
		if (paintVisibilityButton != null)
			return paintVisibilityButton.isClicked(point);
		return false;
	}

	public void onPaint(Graphics2D g2) {
		try {
			paint(g2);
			getPaint().draw(g2);
		}
		catch (Exception e) {
			System.out.println("Painter: " + e);
		}
		finally {
			g2.dispose();
		}
	}

	public abstract void paint(Graphics g);

	public abstract OVERRIDE_RETURN notifyMouseEvent(MouseEvent event);

	public abstract OVERRIDE_RETURN notifyKeyEvent(KeyEvent event);

}
