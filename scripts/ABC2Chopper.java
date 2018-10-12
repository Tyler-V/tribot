package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;

import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSAnimableEntity;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.api.v1.api.entity.Entity;
import scripts.api.v1.api.entity.Targets;
import scripts.api.v1.api.entity.Entity.Types;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.condition.Conditional;
import scripts.usa.api.condition.Conditions;
import scripts.usa.api.tracking.Tracker;
import scripts.usa.api.ui.Paint;

@ScriptManifest(authors = { "Usa" }, category = "ABC", name = "ABC2 Chopper")
public class ABC2Chopper extends Script implements Painting, Ending, MessageListening07 {

	private Tracking tracking;
	private Positionable interacting;
	private Positionable[] targets;

	private String[] names = new String[] { "Willow" };
	private int resources = 0;

	public void run() {
		// ABC.setSleepReaction(false);
		// ABC.alwaysHover();
		// ABC.alwaysOpenMenu();

		while (Login.getLoginState() != Login.getLoginState())
			sleep(100);

		Thread tracking = new Thread(new Tracking());
		tracking.setName("Tracking");
		tracking.start();

		while (true) {
			ABC.activateRun();
			if (Inventory.isFull())
				Inventory.dropAllExcept(new String[] { "Bronze axe" });
			Entity.click(Types.RSObject, "Chop down", new Targets(this.names),
					new Conditional(Conditions.Moving(), Conditions.Animation(879)), Conditions.Animation(879),
					Conditions.Animating(false), 3000);
			ABC.performAntiban();
			sleep(50, 100);
		}
	}

	public void onPaint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);

		if (targets != null && targets.length > 0) {
			g2.setColor(new Color(0, 255, 0, 150));
			if (targets instanceof RSNPC[]) {
				for (Positionable target : targets) {
					RSModel model = ((RSAnimableEntity) target).getModel();
					if (model != null && model.isClickable()) {
						Polygon area = model.getEnclosedArea();
						if (area != null)
							g2.fillPolygon(area);
					}
				}
			} else if (targets instanceof RSObject[]) {
				for (Positionable target : targets) {
					RSModel model = ((RSObject) target).getModel();
					if (model != null && model.isClickable()) {
						Polygon area = model.getEnclosedArea();
						if (area != null)
							g2.fillPolygon(area);
					}
				}
			}
		}

		if (Entity.current != null) {
			g2.setColor(new Color(255, 255, 255, 200));
			Positionable entity = Entity.current;
			if (entity != null) {
				RSModel model = null;
				if (entity instanceof RSNPC) {
					model = ((RSAnimableEntity) entity).getModel();
				} else if (entity instanceof RSObject) {
					model = ((RSObject) entity).getModel();
				}
				if (model != null && model.isClickable()) {
					Polygon area = model.getEnclosedArea();
					if (area != null)
						g2.fillPolygon(area);
				}
			}
		}

		g2.setFont(new Font("Consolas", Font.PLAIN, 12));
		Color background = new Color(24, 66, 4, 230);
		Color border = new Color(0, 0, 0, 255);
		Color text = Color.WHITE;
		int height = 15;
		int width = 200;
		int spacing = 20;
		int x = 293;
		int y = 363;

		int resourcesPerHour = (int) (resources * 3600000D / Tracker.getElapsedTime());

		ArrayList<String> list = new ArrayList<String>();
		try {
			list.add("    USA ABC2 Woodchopper");
			list.add("Hover: " + ABC.shouldHover() + " | Menu: " + ABC.shouldOpenMenu());
			list.add("Time: " + Timing.msToString(Tracker.getElapsedTime()));
			list.add("Logs: " + TabletsPaint.format(resources) + " (" + TabletsPaint.format(resourcesPerHour) + "/hr)");
			list.add("XP: " + Tracker.getXPGained() + " (" + Tracker.getXPPerHour() + "/hr)");
			list.add("Level: " + Skills.getCurrentLevel(SKILLS.WOODCUTTING) + " (+" + Tracker.getLevelsGained() + ")");
			for (String print : list) {
				g2.setColor(background);
				g2.fillRoundRect(x, y - height, width, height, 4, 4);
				g2.setColor(border);
				g2.drawRoundRect(x, y - height, width, height, 4, 4);
				g2.setColor(text);
				g2.drawString(print, x + 3, y - 3);
				y += spacing;
			}
		} catch (Exception e) {
		}
	}

	public class Tracking implements Runnable {
		private volatile boolean stop = false;

		public void run() {
			while (!stop) {
				try {
					if (Login.getLoginState() == Login.STATE.INGAME) {
						targets = new Targets(names).getObjects();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				sleep(0, 50);
			}
		}

		public void setStop(boolean stop) {
			System.out.println("Stopped Threat Searching thread.");
			this.stop = stop;
		}
	}

	public void onEnd() {
		if (tracking != null)
			tracking.setStop(true);
	}

	@Override
	public void clanMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void duelRequestReceived(String arg0, String arg1) {
	}

	@Override
	public void personalMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void playerMessageReceived(String arg0, String arg1) {
	}

	@Override
	public void serverMessageReceived(String message) {
		if (message.contains("You get some"))
			resources++;
	}

	@Override
	public void tradeRequestReceived(String arg0) {
	}
}
