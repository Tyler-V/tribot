package scripts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import scripts.api.v1.api.walking.Walk;
import scripts.webwalker_logic.WebWalker;

@SuppressWarnings("serial")
@ScriptManifest(authors = { "Usa" }, category = "Walking", name = "USA World Web Walker")
public class UsaWorldWalker extends Script implements Painting {

    private boolean map_is_up = true;
    private Positionable destination = null;
    private String status = "Starting...";
    private long startTime;

    public void run() {
	Map map = new Map();
	while (map_is_up) {
	    sleep(500);
	}
	startTime = System.currentTimeMillis();
	status = "Web Walking";
	WebWalker.walkTo(destination);
    }

    private BufferedImage getImage(String url) {
	try {
	    return ImageIO.read(new URL(url));
	} catch (IOException e) {
	    return null;
	}
    }

    @Override
    public void onPaint(Graphics g) {

	long time = System.currentTimeMillis() - startTime;

	Color background = new Color(24, 36, 82, 150);
	g.setColor(background);
	int spacing = 17;
	int rectX = 250;
	int rectY = 345;
	int width = 260;
	int height = 75;
	g.fillRoundRect(rectX, rectY, width, height, 5, 5);
	g.setColor(Color.WHITE);
	g.drawRoundRect(rectX, rectY, width, height, 5, 5);
	g.drawLine(rectX, rectY + spacing, rectX + width, rectY + spacing);

	int x = 260;
	int y = 358;
	Font bold = new Font("Tahoma", Font.BOLD, 12);
	g.setFont(bold);

	g.drawString("USA World Web Walker", x, y);
	y += spacing + 3;
	g.drawString("Time: " + Timing.msToString(time), x, y);
	y += spacing;
	g.drawString("Status: " + status, x, y);
	y += spacing;
	g.drawString("" + (Player.getPosition().distanceTo(destination) + " tiles away from tile " + destination), x,
		y);
	y += spacing;
    }

    public class Map extends JFrame {

	public Map() {

	    try {

		final JFrame f = new JFrame("Usa © | World Web Walker");
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		f.setLocation(dim.width / 4, 20);
		f.setSize(1115, 1000);

		final Image image = getImage("http://i.imgur.com/3fcc1WN.png");
		JLabel label = new JLabel(new ImageIcon(image));
		f.add(label);

		f.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mousePressed(MouseEvent e) {
			Point p = TileCalculations.getTile(e.getPoint());
			destination = new RSTile(p.x, p.y, 0);
			System.out.println("Usa World Web Walker: Walking to " + destination);
			map_is_up = false;
			image.flush();
			f.dispose();
		    }
		});

		f.setVisible(true);

	    } catch (Exception exp) {
		exp.printStackTrace();
	    }
	}
    }

    public static class TileCalculations {

	/**
	 * Credits to Dunnker for the Tile constants
	 */

	/** The most western horizontal Tile. */
	private static final double MAP_TILES_HORIZONTAL_WEST = 2047.5;
	/** The most eastern horizontal Tile. */
	private static final double MAP_TILES_HORIZONTAL_EAST = 3904;
	/** The most northern vertical tile. */
	private static final double MAP_TILES_VERTICAL_NORTH = 4190;
	/** The most southern vertical tile. */
	private static final double MAP_TILES_VERTICAL_SOUTH = 2494.5;
	/** The amount of horizontal Tiles. */
	private static final double MAP_TILES_HORIZONTAL = MAP_TILES_HORIZONTAL_EAST - MAP_TILES_HORIZONTAL_WEST;
	/** The amount of vertical Tiles. */
	private static final double MAP_TILES_VERTICAL = MAP_TILES_VERTICAL_SOUTH - MAP_TILES_VERTICAL_NORTH;

	/** The amount of horizontal pixels in the world map image. */
	public static final double MAP_IMAGE_PIXELS_HORIZONTAL = 1115;
	/** The amount of vertical pixels in the world map image. */
	public static final double MAP_IMAGE_PIXELS_VERTICAL = 1000;

	/** The amount of horizontal world map pixels for one tile. */
	public static final double PIXELS_PER_TILE_HORIZONTAL = MAP_IMAGE_PIXELS_HORIZONTAL / MAP_TILES_HORIZONTAL;
	/** The amount of vertical world map pixels for one tile. */
	public static final double PIXELS_PER_TILE_VERTICAL = MAP_IMAGE_PIXELS_VERTICAL / MAP_TILES_VERTICAL;

	public static Point getTile(final Point point) {
	    final int x = (int) Math.round(MAP_TILES_HORIZONTAL_WEST + point.x / PIXELS_PER_TILE_HORIZONTAL);
	    final int y = (int) Math.round(MAP_TILES_VERTICAL_NORTH + point.y / PIXELS_PER_TILE_VERTICAL);
	    return new Point(x, y);
	}

	public static Point getPoint(final Point tile) {
	    final int x = (int) ((tile.x - MAP_TILES_HORIZONTAL_WEST) * PIXELS_PER_TILE_HORIZONTAL);
	    final int y = (int) ((tile.y - MAP_TILES_VERTICAL_NORTH) * PIXELS_PER_TILE_VERTICAL);
	    return new Point(x, y);
	}
    }
}