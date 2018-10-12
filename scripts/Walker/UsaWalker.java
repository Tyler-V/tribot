package scripts.Walker;

import java.awt.Graphics;
import java.awt.Point;
import java.net.MalformedURLException;
import java.net.URL;

import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.Painting;

import javafx.application.Platform;
import scripts.usa.api2007.webwalker_logic.WebWalker;
import scripts.usa.api2007.webwalker_logic.local.walker_engine.WebWalkerPaint;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Walker")
public class UsaWalker extends Script implements Painting, Ending {

	private FxLoader gui;
	private RSTile destination;

	public void run() {
		try {
			URL fxml = new URL("http://usa-tribot.org/fxml/UsaWalker.fxml");
			gui = new FxLoader(this.getClass(), fxml, null, false);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}

		while (gui.isShowing())
			sleep(100);

		destination = TileUtil.getTile(gui.getController().pointClicked);
		println("Walking to " + destination);
		WebWalker.walkTo(destination);
	}

	@Override
	public void onPaint(Graphics g) {
		WebWalkerPaint.getInstance()
				.drawDebug(g, true);
	}

	public void onEnd() {
		Platform.runLater(new Runnable() {
			public void run() {
				if (gui != null && gui.isShowing())
					gui.close();
			}
		});
	}

	/**
	 * Credits to dunnkers for the constants
	 */
	public static class TileUtil {
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

		public static RSTile getTile(final Point point) {
			final int x = (int) Math.round(MAP_TILES_HORIZONTAL_WEST + point.x / PIXELS_PER_TILE_HORIZONTAL);
			final int y = (int) Math.round(MAP_TILES_VERTICAL_NORTH + point.y / PIXELS_PER_TILE_VERTICAL);
			return new RSTile(x, y, 0);
		}

		public static Point getPoint(final Point tile) {
			final int x = (int) ((tile.x - MAP_TILES_HORIZONTAL_WEST) * PIXELS_PER_TILE_HORIZONTAL);
			final int y = (int) ((tile.y - MAP_TILES_VERTICAL_NORTH) * PIXELS_PER_TILE_VERTICAL);
			return new Point(x, y);
		}
	}
}
