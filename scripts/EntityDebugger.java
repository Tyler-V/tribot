package scripts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

import org.tribot.api.input.Mouse;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Tools", name = "Entity Debugger")
public class EntityDebugger extends Script implements Painting {

	private boolean paintNPC = true;
	private boolean paintObject = true;

	RSNPC npc = null;
	RSObject obj = null;
	RSPlayer player = null;

	public void run() {

		while (true) {

			RSNPC[] npcs = NPCs.getAll();
			if (npcs.length > 0) {
				for (int i = 0; i < npcs.length; i++) {
					Point p = Projection.tileToScreen(npcs[i], 0);
					if (Mouse.getPos().distance(p) <= 25) {
						npc = npcs[i];
						break;
					}
				}
			}

			RSObject[] objects = Objects.getAll(50);
			if (objects.length > 0) {
				for (int i = 0; i < objects.length; i++) {
					Point p = Projection.tileToScreen(objects[i], 0);
					if (Mouse.getPos().distance(p) <= 25) {
						if (!objects[i].getDefinition().getName()
								.equals("null")) {
							obj = objects[i];
							break;
						}
					}
				}
			}

			RSPlayer[] players = Players.getAll();
			if (players.length > 0) {
				for (int i = 0; i < players.length; i++) {
					Point p = Projection.tileToScreen(players[i], 0);
					if (Mouse.getPos().distance(p) <= 25) {
						// if (!players[i].getName().equals(
						// Player.getRSPlayer().getName())) {
						player = players[i];
						break;
						// }
					}
				}
			}

			sleep(100);
		}
	}

	public static int stringLength(String s, Graphics g) {
		int x = 0;
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			x += g.getFontMetrics().charWidth(ch);
		}
		return x;
	}

	@Override
	public void onPaint(Graphics g) {

		Point pos = null;
		int x = 0;
		int y = 0;
		int width = 0;
		Polygon[] triangles;

		if (player != null && player.isOnScreen()) {
			pos = Projection.tileToScreen(player, 0);
			x = (int) pos.getX();
			y = (int) pos.getY();

			y -= 100;

			String[] player_paint = {
					"\"" + player.getName() + "\"" + " (Level "
							+ player.getCombatLevel() + ")",
					"Health: (" + player.getHealth() + "/"
							+ player.getMaxHealth() + ")",
					"Tile: " + player.getPosition(),
					"Model length: " + player.getModel().getPoints().length,
					"NPC Height: " + player.getHeight() };

			width = 0;
			for (String s : player_paint) {
				int temp = stringLength(s, g);
				if (temp > width) {
					width = temp;
				}
			}

			g.setColor(Color.MAGENTA);
			triangles = player.getModel().getTriangles();
			for (int i = 0; i < triangles.length; i++) {
				g.drawPolygon(triangles[i]);
			}

			g.setColor(Color.BLACK);
			g.fillRoundRect(x - (((int) (width / 2)) + 5), y - 15, width + 8,
					(player_paint.length * 13) + 8, 10, 10);
			g.setColor(Color.MAGENTA);
			g.drawRoundRect(x - (((int) (width / 2)) + 5), y - 15, width + 8,
					(player_paint.length * 13) + 8, 10, 10);

			for (String s : player_paint) {
				g.drawString(s, (x - ((int) ((stringLength(s, g) / 2)))), y);
				y += 13;
			}
		}

		if (npc != null && npc.isOnScreen()) {
			pos = Projection.tileToScreen(npc, npc.getHeight());
			x = (int) pos.getX();
			y = (int) pos.getY();

			y -= 70;

			String[] npc_paint = {
					"\"" + npc.getName() + "\"" + " (Level "
							+ npc.getCombatLevel() + ")",
					"ID: " + npc.getID() + ", Health: (" + npc.getHealth()
							+ "/" + npc.getMaxHealth() + ")",
					"Tile: " + npc.getPosition(),
					"Model length: " + npc.getModel().getPoints().length,
					"NPC Height: " + npc.getHeight() };

			width = 0;
			for (String s : npc_paint) {
				int temp = stringLength(s, g);
				if (temp > width) {
					width = temp;
				}
			}

			if (npc.getCombatCycle() > 0) {
				g.setColor(Color.RED);
			} else {
				g.setColor(Color.GREEN);
			}
			triangles = npc.getModel().getTriangles();
			for (int i = 0; i < triangles.length; i++) {
				g.drawPolygon(triangles[i]);
			}

			g.setColor(Color.BLACK);
			g.fillRoundRect(x - (((int) (width / 2)) + 5), y - 15, width + 8,
					(npc_paint.length * 13) + 8, 10, 10);
			g.setColor(Color.GREEN);
			g.drawRoundRect(x - (((int) (width / 2)) + 5), y - 15, width + 8,
					(npc_paint.length * 13) + 8, 10, 10);

			for (String s : npc_paint) {
				g.drawString(s, (x - ((int) ((stringLength(s, g) / 2)))), y);
				y += 13;
			}
		}

		if (obj != null && obj.isOnScreen()) {
			pos = Projection.tileToScreen(obj, 0);
			x = (int) pos.getX();
			y = (int) pos.getY();

			y -= 100;

			String[] obj_paint = {
					"\"" + obj.getDefinition().getName() + "\"" + " (ID: "
							+ obj.getID() + ")", "Tile: " + obj.getPosition(),
					"Model length: " + obj.getModel().getPoints().length,
					"Actions: " + obj.getDefinition().getActions().length };

			width = 0;
			for (String s : obj_paint) {
				int temp = stringLength(s, g);
				if (temp > width) {
					width = temp;
				}
			}

			g.setColor(Color.YELLOW);
			triangles = obj.getModel().getTriangles();
			for (int i = 0; i < triangles.length; i++) {
				g.drawPolygon(triangles[i]);
			}

			g.setColor(Color.BLACK);
			g.fillRoundRect(x - (((int) (width / 2)) + 5), y - 15, width + 8,
					(obj_paint.length * 13) + 8, 10, 10);
			g.setColor(Color.YELLOW);
			g.drawRoundRect(x - (((int) (width / 2)) + 5), y - 15, width + 8,
					(obj_paint.length * 13) + 8, 10, 10);

			for (String s : obj_paint) {
				g.drawString(s, (x - ((int) ((stringLength(s, g) / 2)))), y);
				y += 13;
			}
		}
	}
}
