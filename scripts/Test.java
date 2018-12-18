package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.Projectiles;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSProjectile;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

import scripts.green_dragons.data.Vars;
import scripts.usa.api2007.Walking;
import scripts.usa.api2007.worlds.World;
import scripts.usa.api2007.worlds.WorldHopper;
import scripts.usa.api2007.worlds.WorldType;

@ScriptManifest(authors = { "Usa" }, category = "Test", name = "Test")
public class Test extends Script implements Painting {

	public List<World> worlds = new ArrayList<World>();

	public void run() {

		// super.setLoginBotState(false);

		// RSTile end = new RSTile(3097, 3485, 0);
		// RSTile[] path = Walking.generatePath(end);
		// if (Walking.walkPath(Style.Screen, null, path)) {
		// println("End");
		// }

		Options.setQuickPrayersEnabled(true);

		while (true) {
			// int world = 378;
			// General.println("Changing to World " + world);
			// General.println(WorldHopper.changeWorld(world));
			sleep(1000);
		}
	}

	@Override
	public void onPaint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		for (World world : worlds) {
			g2.setColor(Color.BLACK);
			g2.fillRect(world.getWorldSelectBounds().x, world.getWorldSelectBounds().y, world.getWorldSelectBounds().width, world.getWorldSelectBounds().height);
			g2.setColor(Color.WHITE);
			g2.drawString(Integer.toString(world.getNumber()), world.getWorldSelectBounds().x + 10, world.getWorldSelectBounds().y + 14);
		}
		if (Walking.getPaintPath().length > 0) {
			for (RSTile tile : Walking.getPaintPath()) {
				if (Walking.isTileOnMinimap(tile)) {
					Point point = Projection.tileToMinimap(tile);
					if (point != null) {
						g2.setColor(new Color(50, 200, 50, 255));
						g2.fillRect(point.x, point.y, 2, 2);
					}
					if (tile.isOnScreen()) {
						Polygon poly = Projection.getTileBoundsPoly(tile, 0);
						if (poly != null) {
							g2.setColor(new Color(50, 200, 50, 50));
							g2.fillPolygon(poly);
							g2.setColor(new Color(50, 200, 50, 255));
							g2.drawPolygon(poly);
						}
					}
				}
			}
		}
	}
}
