package scripts.agility_old;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Game;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.api.v1.api.items.Consumables;
import scripts.api.v1.api.walking.Walk;
import scripts.api.v1.api.worlds.TYPE;
import scripts.api.v1.api.worlds.WorldHopper;
import scripts.usa.api.antiban.ABC;
import scripts.usa.api.antiban.Time;
import scripts.usa.api.antiban.responder.AutoResponder;
import scripts.usa.api.ui.Paint;

public class UsaAgilityOLD extends Script implements Painting, MessageListening07, Ending {

	private String version = "v6.5";
	private final Image image = TabletsPaint.getImage("http://i.imgur.com/6r0qRIe.png");

	private String status = "";
	private long startTime;
	private int startXP = 0, startLVL = 0, laps = 0, marks = 0;

	private COURSE course = null;
	private boolean mark_of_grace;
	private boolean eatFood;
	private boolean autoSwitch;
	private boolean stamina;
	private boolean useAutoResponder;
	private boolean useABC2Reaction;
	private int mouseSpeed;
	private boolean worldHopping;
	private long hoppingTime;
	private long nextHopTime = 0L;
	private int maxPlayers;

	private OBSTACLE current = null;
	private OBSTACLE previous = null;
	private OBSTACLE next = null;

	private final String[] STAMINA_POTION = { "Stamina potion(4)", "Stamina potion(3)", "Stamina potion(2)", "Stamina potion(1)" };
	private final String VIAL = "Vial";

	private RSTile[] PAINT_PATH = null;
	private RSArea PAINT_AREA = null;
	private Polygon PAINT_OBJECT = null;
	private Polygon PAINT_NEXT_OBJECT = null;
	private Polygon PAINT_PLAYER = null;
	private long animationTimer = 0;
	private long movingTimer = 0;
	private boolean animating = false;
	private boolean moving = false;
	private long staminaPotionTimer = 0;
	private boolean paintToggle = true;

	private boolean run = true;
	private boolean gui_is_up = true;
	private gui g = new gui();

	private boolean attempting_obstacle = false;
	private PlayerInteraction interaction;

	private AutoResponder responder;

	@Override
	public void run() {

		g.setVisible(true);
		while (gui_is_up) {
			sleep(100);
		}

		while (!Login.getLoginState().equals(Login.STATE.INGAME)) {
			sleep(500);
		}

		if (useAutoResponder) {
			super.setAutoResponderState(false);
			responder = new AutoResponder();
		}

		System.out.println("Starting Player Interaction thread.");
		interaction = new PlayerInteraction();
		Thread interacting = new Thread(interaction);
		interacting.start();

		startTime = System.currentTimeMillis();

		if (autoSwitch) {

			if (Game.getPlane() != 0) {

				boolean found = false;

				for (COURSE c : COURSE.values()) {

					for (OBSTACLE o : c.getObstacles()) {

						if (atArea(o.getArea(), Player.getPosition())) {

							found = true;
							course = c;
							break;

						}

					}

					if (found)
						break;

				}

			}

			if (course == null) {

				for (COURSE c : COURSE.values()) {

					if (c.isAutoSwitchCourse() && Skills.getActualLevel(SKILLS.AGILITY) >= c.getLevelRequired()) {

						course = c;
						break;

					}

				}

			}

		}

		Mouse.setSpeed(mouseSpeed);
		println("Mouse speed set to " + mouseSpeed);
		println("Starting course is " + toTitleCase(course.toString()));

		while (run) {

			if (startXP == 0)
				startXP = Skills.getXP(SKILLS.AGILITY);

			if (startLVL == 0)
				startLVL = Skills.getActualLevel(SKILLS.AGILITY);

			if (ABC.activateRun()) {
				status = "Activating Run";
			}

			hopWorlds();

			if (!pickupMark() && !lowHealthCheck() && !Consumables.drink(STAMINA_POTION, null, false)) {

				if (autoSwitch && Player.getPosition().getPlane() == 0) {
					selectHighestCourse(course);
				}

				current = getCurrentObstacle(course);

				if (!obstacleGlitch(course) && current != null) {

					previous = getPreviousObstacle(course, current);
					next = getNextObstacle(course, current);

					if (previous != null && next != null) {

						final RSObject obj = getObstacle(current);

						if (obj != null) {

							if (interactWith(obj, current.getAction(), current.getName(), current.getStartTile(), current.getEndTile(), current.getAngle(), current.getRotation(), current.shouldMove(),
								current.shouldSleep())) {

								if (course.getObstacles().get(0).equals(next)) {

									status = "Course Complete";
									laps++;

								}
								else {

									status = "'" + current.getName() + "' Complete!";

								}

							}
							else {

								status = "Failed to " + current.getAction() + " '" + current.getName() + "'";

							}

							attempting_obstacle = false;
							PAINT_OBJECT = null;
							PAINT_NEXT_OBJECT = null;
							movingTimer = 0;
							animationTimer = 0;

						}

					}

				}
				else if (Player.getPosition().getPlane() == 0) {

					final OBSTACLE first = course.getStartingObstacle();
					status = "Walking to " + toTitleCase(course.toString());

					Condition c = new Condition() {
						@Override
						public boolean active() {
							return Player.getPosition().distanceTo(first.getStartTile()) <= 2;
						}
					};

					Walk.walkToTile(first.getStartTile(), 1, 2, c);

				}

			}

			if (ABC.performAntiban())
				status = "ABC2 Antiban";

			sleep(50);
		}
	}

	private boolean hopWorlds() {

		if (!worldHopping)
			return false;

		if (hoppingTime > 0) {

			if (nextHopTime == 0) {

				nextHopTime = System.currentTimeMillis() + (hoppingTime + General.random(-600000, 600000));

				println("Next world change: " + Timing.msToString(nextHopTime - System.currentTimeMillis()));

			}
			else if (System.currentTimeMillis() > nextHopTime) {

				if (changeWorlds()) {

					nextHopTime = System.currentTimeMillis() + (hoppingTime + General.random(-600000, 600000));

					println("Next world change: " + Timing.msToString(nextHopTime - System.currentTimeMillis()));

				}

			}

		}

		if (playersDetected(maxPlayers)) {

			if (changeWorlds()) {

				nextHopTime = System.currentTimeMillis() + (hoppingTime + General.random(-600000, 600000));

				println("Next world change: " + Timing.msToString(nextHopTime - System.currentTimeMillis()));

			}

		}

		return true;

	}

	private boolean playersDetected(int maxPlayers) {

		if (maxPlayers == 0 || course == null)
			return false;

		if (Player.getPosition().getPlane() != 0) {

			String name = Player.getRSPlayer().getName();

			if (name == null)
				return false;

			int count = 0;
			String names = "\"";

			RSPlayer[] players = Players.getAll(Filters.Players.nameNotEquals(name));

			if (players.length > 0 && (players.length >= maxPlayers)) {

				for (RSPlayer player : players) {

					if (player.getPosition().getPlane() != 0) {

						for (OBSTACLE o : course.getObstacles()) {

							if (o.getArea().contains(player)) {

								names = names + player.getName() + ", ";
								count++;
								break;

							}

						}

					}

				}

				if (count >= maxPlayers) {

					names = names.substring(0, names.length() - 2);
					names = names + "\"";
					println("Found " + count + " player(s) " + names + " at " + toTitleCase(course.toString()) + ".");

					return true;
				}

			}

		}

		return false;

	}

	private boolean changeWorlds() {

		int previousWorld = WorldHopper.getCurrentWorld();

		int newWorld = WorldHopper.getRandomWorld(true);

		if (newWorld == previousWorld)
			return false;

		status = "Changing to world " + newWorld + ".";

		if (WorldHopper.changeWorld(newWorld)) {

			if (previousWorld != WorldHopper.getCurrentWorld()) {

				status = "Changed from " + previousWorld + " to " + newWorld + ".";

				sleep(General.randomSD(5000, 1000));

				return true;

			}

		}

		return false;

	}

	private boolean obstacleGlitch(COURSE course) {

		if (course == null)
			return false;

		if (course == COURSE.CANIFIS_ROOFTOP) {

			if (stuckAtTile(new RSTile(3505, 3489, 2))) {

				return clickRandomMinimapTile();

			}
			else if (stuckAtTile(new RSTile(3487, 3476, 3))) {

				return clickRandomMinimapTile();

			}

		}
		else if (course == COURSE.POLLNIVNEACH_ROOFTOP) {

			if (stuckAtTile(new RSTile(3351, 2962, 1)))
				return clickRandomMinimapTile();

		}

		return false;

	}

	private boolean stuckAtTile(RSTile t) {
		long timer = System.currentTimeMillis() + 5000;
		while (timer > System.currentTimeMillis()) {
			if (!Player.getPosition().equals(t))
				return false;
		}
		return true;
	}

	private boolean clickRandomMinimapTile() {
		Point p = Projection.tileToMinimap(Player.getPosition());
		p.x += General.random(-25, 25);
		p.y += General.random(-25, 25);
		return click(p);
	}

	private boolean click(Point p) {
		status = "Canifis Obstacle Glitch";
		Mouse.click(p, 1);
		sleep((long) General.randomSD(824.3465346534654, 289.6103882150994));
		return true;
	}

	private void selectHighestCourse(COURSE current) {

		for (COURSE c : COURSE.values()) {

			if (c.isAutoSwitchCourse() && !c.equals(current)) {

				if (Skills.getActualLevel(SKILLS.AGILITY) >= c.getLevelRequired() && c.getLevelRequired() > current.getLevelRequired()) {

					println("Switching from " + toTitleCase(current.toString()) + " to " + toTitleCase(c.toString()));
					course = c;
					break;

				}

			}

		}

	}

	private boolean lowHealthCheck() {

		if (eatFood)
			return Consumables.eat(null, false);

		if (getHPPercent() <= 30) {

			status = "Our health is " + getHPPercent() + "%, stopping script!";
			println(status);
			run = false;
			return true;

		}

		return false;

	}

	private int getHPPercent() {

		double currentHP = Skills.SKILLS.HITPOINTS.getCurrentLevel();
		double totalHP = Skills.SKILLS.HITPOINTS.getActualLevel();
		return (int) (currentHP / totalHP * 100);

	}

	private boolean pickupMark() {

		if (!mark_of_grace)
			return false;

		if (Inventory.open()) {

			if (Inventory.isFull() && Inventory.getCount("Mark of grace") == 0)
				return false;

			RSGroundItem[] mark = GroundItems.find("Mark of grace");

			if (mark.length == 0 || mark[0] == null)
				return false;

			if (Player.getPosition().getPlane() == 0 || !PathFinding.canReach(mark[0], true))
				return false;

			status = "Found Mark of grace!";

			setCamera(100, Camera.getCameraRotation());

			if (!mark[0].isOnScreen()) {

				Condition c = new Condition() {
					@Override
					public boolean active() {
						return mark[0].isOnScreen();
					}
				};

				Walk.walkToTile(mark[0], 1, 2, c);

			}

			if (mark[0].isOnScreen()) {

				final int count = Inventory.getCount("Mark of grace");

				if (!Player.isMoving()) {

					status = "Picking up Mark of grace";

					if (DynamicClicking.clickRSGroundItem(mark[0], "Take Mark of grace")) {

						Timing.waitCondition(new Condition() {
							public boolean active() {
								sleep(General.random(700, 1200));
								return Inventory.getCount("Mark of grace") != count;
							}
						}, 3000);

						if (Inventory.getCount("Mark of grace") != count) {

							marks++;
							status = "Picked it up!";

						}

					}

				}

			}

			return true;

		}

		return false;

	}

	private RSObject getObstacle(final OBSTACLE obstacle) {

		if (obstacle == null)
			return null;

		RSTile t1 = new RSTile(obstacle.getStartTile().getX() - 5, obstacle.getStartTile().getY() - 5, obstacle.getStartTile().getPlane());

		RSTile t2 = new RSTile(obstacle.getStartTile().getX() + 5, obstacle.getStartTile().getY() + 5, obstacle.getStartTile().getPlane());

		RSObject[] objects = Objects.getAllIn(t1, t2, new Filter<RSObject>() {
			@Override
			public boolean accept(RSObject o) {

				RSObjectDefinition d = o.getDefinition();
				if (d == null)
					return false;

				String[] actions = d.getActions();
				if (actions.length == 0)
					return false;

				for (String a : actions) {

					if (a.equalsIgnoreCase(obstacle.getAction()))
						return true;

				}

				return false;

			}
		});

		if (objects.length > 1) {

			RSObject closest = null;
			int distance = Integer.MAX_VALUE;

			for (RSObject o : objects) {

				int temp = obstacle.getStartTile().distanceTo(o);

				if (temp < distance) {
					closest = o;
					distance = temp;
				}

			}

			return closest;

		}
		else if (objects.length > 0 && objects[0] != null) {

			return objects[0];

		}

		return null;

	}

	private OBSTACLE getCurrentObstacle(COURSE course) {

		if (course == null)
			return null;

		for (OBSTACLE o : course.getObstacles()) {

			if (atArea(o.getArea(), Player.getPosition()))
				return o;

		}

		return null;

	}

	private OBSTACLE getNextObstacle(COURSE course, OBSTACLE current) {

		if (course == null || course == null)
			return null;

		for (int i = 0; i < course.getObstacles().size(); i++) {

			if (course.getObstacles().get(i).equals(current)) {

				if (i == (course.getObstacles().size() - 1))
					return course.getObstacles().get(0);

				return course.getObstacles().get(i + 1);

			}

		}

		return null;

	}

	private OBSTACLE getPreviousObstacle(COURSE course, OBSTACLE current) {

		if (course == null || course == null)
			return null;

		for (int i = 0; i < course.getObstacles().size(); i++) {

			if (course.getObstacles().get(i).equals(current)) {

				if (i - 1 < 0)
					return course.getObstacles().get(course.getObstacles().size() - 1);

				return course.getObstacles().get(i - 1);

			}

		}

		return null;

	}

	private boolean atArea(RSArea a, RSTile p) {

		if (p == null || a == null)
			return false;

		return (a.contains(p) && a.plane == p.getPlane());

	}

	private void setCamera(int angle, int rotation) {

		if (Camera.getCameraRotation() > (rotation + 10) || Camera.getCameraRotation() < (rotation - 10)) {

			int random = General.random(rotation - 5, rotation + 5);

			if (random < 0)
				random = 360 + random;

			if (random > 360)
				random = random - 360;

			status = "Setting Rotation to " + random;

			Camera.setCameraRotation(random);

		}

		if (Camera.getCameraAngle() > (angle + 10) || Camera.getCameraAngle() < (angle - 10)) {

			int random = General.random(angle - 5, angle + 5);

			if (random > 100)
				random = 100;

			status = "Setting Angle to " + random;

			Camera.setCameraAngle(random);

		}

	}

	public class PlayerInteraction implements Runnable {
		private volatile boolean stop = false;

		@Override
		public void run() {

			while (!stop) {

				try {

					RSPlayer p = Player.getRSPlayer();
					if (p != null) {
						RSModel m = p.getModel();
						if (m != null) {
							Polygon a = m.getEnclosedArea();
							if (a != null) {
								PAINT_PLAYER = a;
							}
						}
					}

					if (attempting_obstacle) {
						if (current != null && previous != null) {
							long animation_sleep = (long) General.randomSD(3458, 213);
							long moving_sleep = (long) General.randomSD(1534, 209);

							moving = Player.isMoving();
							if (Player.isMoving())
								movingTimer = System.currentTimeMillis() + moving_sleep;

							animating = Player.getAnimation() != -1;
							if (animating && !Player.getPosition().equals(previous.getEndTile()))
								animationTimer = System.currentTimeMillis() + animation_sleep;
						}
					}

				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

				sleep(50);
			}
		}

		public void setStop(boolean stop) {
			System.out.println("Stopped Player Interaction thread.");
			this.stop = stop;
		}

	}

	private boolean interactWith(final RSObject obj, final String action, final String name, final RSTile start, final RSTile end, int angle, int rotation, boolean move, boolean sleep) {

		try {

			OBSTACLE next = getNextObstacle(course, current);

			if (next == null)
				return false;

			if (Player.getPosition().getPlane() == 0 && start.getPlane() != 0 && end.getPlane() != 0)
				return false;

			if (Player.getPosition().getPlane() == 0 && start.getPlane() != 0 && end.getPlane() == 0)
				return true;

			if (Player.getPosition().distanceTo(end) <= 1 && Player.getPosition().getPlane() == end.getPlane() && atArea(next.getArea(), Player.getPosition()))
				return true;

			try {
				ABC.sleepReactionTime();
			}
			catch (InterruptedException e) {
			}

			ABC.setTime(Time.ACTION);

			if (ChooseOption.isOpen()) {

				if (ChooseOption.isOptionValid(action + " " + name)) {

					status = "Selecting option " + action;

					ABC.setTime(Time.START);

					if (ChooseOption.select(action + " " + name)) {

						attempting_obstacle = true;
						boolean next_camera = false;
						PAINT_OBJECT = null;

						int value = General.random(800, 1200);

						if (sleep)
							value = General.random(2000, 2500);

						long moving_sleep = (long) General.randomSD(value, 200);

						movingTimer = System.currentTimeMillis() + moving_sleep;
						status = action + " '" + name + "'";

						while (animationTimer > System.currentTimeMillis() || movingTimer > System.currentTimeMillis()) {

							if (Player.getPosition().getPlane() == 0 && start.getPlane() != 0 && end.getPlane() != 0)
								return false;

							if (Player.getPosition().distanceTo(end) <= 1 && Player.getPosition().getPlane() == end.getPlane() && atArea(next.getArea(), Player.getPosition()) || (Player.getPosition()
								.getPlane() == 0 && start.getPlane() != 0 && end.getPlane() == 0)) {

								if (sleep)
									sleep((long) General.randomSD(500, 100));

								return true;

							}

							if (Player.getRSPlayer().isInCombat())
								return false;

							if (next != null) {

								if (!next_camera) {

									setCamera(next.getAngle(), next.getRotation());
									next_camera = true;

								}
								else {

									hoverOverObstacle(next);

								}

							}

							if (obstacleGlitch(course)) {

								println("Found obstacle glitch at " + name + " in " + course.toString());
								return false;

							}

							sleep(General.random(25, 50));

						}

					}

					return false;

				}

				ChooseOption.close();

			}

			if (obj == null)
				return false;

			setCamera(angle, rotation);

			if ((move && Player.getPosition().distanceTo(start) > 2) || !obj.isOnScreen()) {

				status = "Moving closer to " + name;

				Condition c = new Condition() {
					@Override
					public boolean active() {
						return obj.isOnScreen() && Player.getPosition().distanceTo(obj) <= General.random(1, 5);
					}
				};

				Walk.walkToTile(start, 1, 1, c);

			}

			if (obj.isOnScreen()) {

				status = "Searching for " + name;

				long timer = System.currentTimeMillis() + 3000;

				while (timer > System.currentTimeMillis()) {

					if (obj == null)
						return false;

					RSModel m = obj.getModel();
					if (m == null)
						return false;

					Polygon area = m.getEnclosedArea();
					if (area == null)
						return false;

					Point p = m.getHumanHoverPoint();

					if (p != null) {

						PAINT_OBJECT = area;

						if (Player.getPosition().getPlane() != start.getPlane())
							return false;

						if (Game.isUptext(action + " " + name) && area.contains(Mouse.getPos())) {
							status = "Found " + name;
							break;
						}

						Mouse.move(p);

					}

					sleep((long) General.randomSD(50, 25));

				}

				if (Game.isUptext(action + " " + name)) {

					ABC.setTime(Time.START);

					status = "Left Clicking " + name;

					Mouse.click(Mouse.getPos(), 1);

					attempting_obstacle = true;
					boolean next_camera = false;
					PAINT_OBJECT = null;

					int value = General.random(800, 1200);

					if (sleep)
						value = General.random(2000, 2500);

					long moving_sleep = (long) General.randomSD(value, 200);

					movingTimer = System.currentTimeMillis() + moving_sleep;

					status = action + " '" + name + "'";

					while (animationTimer > System.currentTimeMillis() || movingTimer > System.currentTimeMillis()) {

						if (Player.getPosition().getPlane() == 0 && start.getPlane() != 0 && end.getPlane() != 0)
							return false;

						if (Player.getPosition().distanceTo(end) <= 1 && Player.getPosition().getPlane() == end.getPlane() && atArea(next.getArea(), Player.getPosition()) || (Player.getPosition()
							.getPlane() == 0 && start.getPlane() != 0 && end.getPlane() == 0)) {

							if (sleep)
								sleep((long) General.randomSD(500, 100));

							return true;

						}

						if (Player.getRSPlayer().isInCombat())
							return false;

						if (next != null) {

							if (!next_camera) {

								setCamera(next.getAngle(), next.getRotation());
								next_camera = true;

							}
							else {

								hoverOverObstacle(next);
							}

						}

						if (obstacleGlitch(course)) {

							println("Found obstacle glitch at " + name + " in " + course.toString());
							return false;

						}

						sleep(General.random(25, 50));

					}

				}

			}

			ABC.resetTimers();

			return false;

		}
		finally {

			ABC.resetShouldHover();
			ABC.resetShouldOpenMenu();
			ABC.setTime(Time.END);

		}

	}

	private boolean hoverOverObstacle(OBSTACLE next) {

		if (next == null || !ABC.shouldHover())
			return false;

		if (Player.getPosition().getPlane() != next.getStartTile().getPlane())
			return false;

		if (ChooseOption.isOpen()) {

			if (ChooseOption.isOptionValid(next.getAction() + " " + next.getName())) {

				status = "Right clicking next obstacle";
				return true;

			}

			ChooseOption.close();

		}

		final RSObject obj = getObstacle(next);
		if (obj == null)
			return false;

		if (!obj.isOnScreen())
			return false;

		RSModel mod = obj.getModel();
		if (mod == null)
			return false;

		Polygon area = mod.getEnclosedArea();
		if (area == null)
			return false;

		PAINT_NEXT_OBJECT = area;

		Point p = mod.getHumanHoverPoint();
		if (p == null)
			return false;

		if (!area.contains(Mouse.getPos()) || (Player.getPosition().getPlane() == next.getStartTile().getPlane() && Game.isUptext(next.getAction() + " " + next.getName())))
			Mouse.move(p);

		sleep((long) General.randomSD(75, 25));

		if (Game.isUptext(next.getAction() + " " + next.getName())) {

			status = "Right Clicking " + next.getName();
			Mouse.click(3);

		}

		return ChooseOption.isOptionValid(next.getAction() + " " + next.getName());

	}

	private String addCommasToNumericString(String digits) {
		String result = "";
		int len = digits.length();
		int nDigits = 0;

		if (digits.length() < 4)
			return digits;

		for (int i = len - 1; i >= 0; i--) {
			result = digits.charAt(i) + result;
			nDigits++;
			if (((nDigits % 3) == 0) && (i > 0)) {
				result = "," + result;
			}
		}
		return (result);
	}

	private String toTitleCase(String givenString) {
		if (givenString.length() == 0)
			return null;

		givenString = givenString.toLowerCase();
		String[] arr = givenString.split("_");
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < arr.length; i++) {
			sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
		}
		return sb.toString().trim();
	}

	@Override
	public void onPaint(Graphics g) {

		long currentTime = System.currentTimeMillis();

		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g2.setRenderingHints(rh);

		if (course != null) {

			g.setColor(Color.WHITE);

			g2.drawImage(image, 0, 0, null);

			long time = currentTime - startTime;
			int lapsPerHour = (int) (laps * 3600000D / (currentTime - startTime));
			int marksPerHour = (int) (marks * 3600000D / (currentTime - startTime));
			int xpGained = Skills.getXP(SKILLS.AGILITY) - startXP;
			int xpPerHour = (int) (xpGained * 3600000D / (currentTime - startTime));
			int currentLVL = Skills.getActualLevel(SKILLS.AGILITY);

			g2.setFont(new Font("Tahoma", Font.PLAIN, 10));
			g2.drawString(version, 463, 357);

			int x = 311;
			int y = 357;
			int spacing = 15;
			g.setFont(new Font("Tahoma", Font.BOLD, 11));

			g2.drawString(toTitleCase(course.toString()), x, y);
			y += spacing + 3;
			g2.drawString(Timing.msToString(time), x, y);
			y += spacing;
			g2.drawString(status, x, y);
			y += spacing;
			g2.drawString(addCommasToNumericString(Integer.toString(laps)) + " (" + addCommasToNumericString(Integer.toString(lapsPerHour)) + "/hr)", x, y);
			y += spacing;
			g2.drawString(addCommasToNumericString(Integer.toString(marks)) + " (" + addCommasToNumericString(Integer.toString(marksPerHour)) + "/hr)", x, y);
			y += spacing;
			g2.drawString(currentLVL + " (+" + (currentLVL - startLVL) + ")", x, y);
			y += spacing;
			g2.drawString(addCommasToNumericString(Integer.toString(xpGained)) + " (" + addCommasToNumericString(Integer.toString(xpPerHour)) + "/hr)", x, y);
			y += 6;

			int xpTNL = Skills.getXPToNextLevel(SKILLS.AGILITY);
			int percentTNL = Skills.getPercentToNextLevel(SKILLS.AGILITY);
			long TTNL = 0;
			if (xpPerHour > 0)
				TTNL = (long) (((double) xpTNL / (double) xpPerHour) * 3600000);
			int percentFill = (265 * percentTNL) / 100;

			x = 215;

			g2.setColor(Color.RED);
			g2.fillRoundRect(x, y, 265, 15, 5, 5);
			Color green = new Color(10, 150, 10);
			g2.setColor(green);
			g2.fillRoundRect(x, y, percentFill, 15, 5, 5);
			g2.setColor(Color.WHITE);
			g2.drawRoundRect(x, y, 265, 15, 5, 5);
			g2.drawString(addCommasToNumericString(Integer.toString(xpTNL)) + " xp to " + (currentLVL + 1) + " (" + Timing.msToString(TTNL) + ")", x + 60, y + 12);

			if (PAINT_OBJECT != null) {
				g2.setColor(new Color(0, 255, 0, 50));
				g2.fillPolygon(PAINT_OBJECT);
			}

			if (PAINT_NEXT_OBJECT != null) {
				g2.setColor(new Color(255, 0, 0, 50));
				g2.fillPolygon(PAINT_NEXT_OBJECT);
			}

			if (PAINT_PLAYER != null) {

				if (moving || animating) {

					g2.setColor(new Color(0, 0, 255, 50));

				}
				else {

					g2.setColor(new Color(255, 255, 255, 50));

				}

				g2.fillPolygon(PAINT_PLAYER);

			}

			if (previous != null && current != null && next != null) {

				x = 555;
				y = 431;
				spacing = 14;
				g2.setColor(Color.RED);
				g2.drawString(previous.toString(), x, y);
				y += spacing;
				g2.setColor(Color.YELLOW);
				g2.drawString(current.toString(), x, y);
				y += spacing;
				g2.setColor(Color.GREEN);
				g2.drawString(next.toString(), x, y);
				y += spacing;

				g2.setColor(Color.WHITE);
				x = 657;
				y = 413;

				if (animationTimer > currentTime) {
					g2.drawString((animationTimer - currentTime) + " ms", x, y);
				}
				else {
					g2.drawString("0 ms", x, y);
				}
				y -= spacing;

				if (movingTimer > currentTime) {
					g2.drawString((movingTimer - currentTime) + " ms", x, y);
				}
				else {
					g2.drawString("0 ms", x, y);
				}

			}

		}

	}

	public class gui extends JFrame {

		private JPanel contentPane;
		private JComboBox coursesBox;
		private JCheckBox autoSwitchBox;
		private JCheckBox eatFoodBox;
		private JCheckBox graceBox;
		private JCheckBox staminaBox;
		private JCheckBox autoResponderBox;
		private JCheckBox abc2Box;
		private JSlider mouseSpeedSlider;

		private JCheckBox hoppingBox;
		private JSlider timeSlider;
		private JSpinner playerSpinner;

		/**
		 * Create the frame.
		 */
		public gui() {

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 398, 392);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			contentPane.setBackground(new Color(102, 153, 204));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblNewLabel = new JLabel("USA Agility");
			lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 24));
			lblNewLabel.setForeground(Color.BLACK);
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel.setBounds(0, 7, 365, 36);
			contentPane.add(lblNewLabel);

			JLabel lblV = new JLabel(version);
			lblV.setForeground(Color.WHITE);
			lblV.setBounds(319, 24, 46, 14);
			contentPane.add(lblV);

			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
			tabbedPane.setBounds(10, 51, 362, 261);
			contentPane.add(tabbedPane);

			JPanel General = new JPanel();
			tabbedPane.addTab("General", null, General, null);
			General.setLayout(null);

			JLabel lblSelectCourse = new JLabel("Select Course:");
			lblSelectCourse.setBounds(10, 10, 97, 20);
			General.add(lblSelectCourse);
			lblSelectCourse.setFont(new Font("Verdana", Font.PLAIN, 12));

			coursesBox = new JComboBox(new DefaultComboBoxModel(COURSE.values()));
			coursesBox.setFont(new Font("Segoe UI", Font.PLAIN, 10));
			coursesBox.setBounds(110, 11, 140, 20);
			General.add(coursesBox);

			autoSwitchBox = new JCheckBox("Auto-switch");
			autoSwitchBox.setSelected(true);
			autoSwitchBox.setBounds(253, 9, 104, 23);
			General.add(autoSwitchBox);
			autoSwitchBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					coursesBox.setEnabled(!autoSwitchBox.isSelected());
				}
			});
			autoSwitchBox.setFont(new Font("Verdana", Font.PLAIN, 12));

			JSeparator separator_1 = new JSeparator();
			separator_1.setBounds(0, 41, 357, 1);
			General.add(separator_1);

			eatFoodBox = new JCheckBox("Eat food?");
			eatFoodBox.setBounds(6, 75, 204, 23);
			eatFoodBox.setFont(new Font("Verdana", Font.PLAIN, 12));
			eatFoodBox.setSelected(true);
			General.add(eatFoodBox);

			graceBox = new JCheckBox("Pickup 'Mark of grace'");
			graceBox.setSelected(true);
			graceBox.setBounds(6, 49, 204, 23);
			General.add(graceBox);
			graceBox.setFont(new Font("Verdana", Font.PLAIN, 12));

			staminaBox = new JCheckBox("Use Stamina potions?");
			staminaBox.setBounds(6, 101, 204, 23);
			General.add(staminaBox);
			staminaBox.setFont(new Font("Verdana", Font.PLAIN, 12));

			autoResponderBox = new JCheckBox("Use Auto Responder V2");
			autoResponderBox.setSelected(true);
			autoResponderBox.setBounds(6, 127, 204, 23);
			General.add(autoResponderBox);
			autoResponderBox.setFont(new Font("Verdana", Font.PLAIN, 12));

			abc2Box = new JCheckBox("Use ABC2 Reaction Timing");
			abc2Box.setSelected(true);
			abc2Box.setFont(new Font("Verdana", Font.PLAIN, 12));
			abc2Box.setBounds(6, 154, 204, 23);
			General.add(abc2Box);

			JSeparator separator_2 = new JSeparator();
			separator_2.setBounds(0, 183, 357, 1);
			General.add(separator_2);

			mouseSpeedSlider = new JSlider();
			mouseSpeedSlider.setBounds(105, 188, 242, 45);
			General.add(mouseSpeedSlider);
			mouseSpeedSlider.setBorder(null);
			mouseSpeedSlider.setValue(100);
			mouseSpeedSlider.setMajorTickSpacing(20);
			mouseSpeedSlider.setMinorTickSpacing(10);
			mouseSpeedSlider.setMinimum(100);
			mouseSpeedSlider.setMaximum(200);
			mouseSpeedSlider.setPaintTicks(true);
			mouseSpeedSlider.setPaintLabels(true);

			JLabel lblMouseSpeed = new JLabel("Mouse Speed:");
			lblMouseSpeed.setBounds(10, 188, 95, 45);
			General.add(lblMouseSpeed);
			lblMouseSpeed.setFont(new Font("Verdana", Font.PLAIN, 12));

			JPanel hopping = new JPanel();
			tabbedPane.addTab("World Hopping", null, hopping, null);
			hopping.setLayout(null);

			hoppingBox = new JCheckBox("Hop to a new world after...");
			hoppingBox.setSelected(true);
			hoppingBox.setFont(new Font("Verdana", Font.PLAIN, 12));
			hoppingBox.setBounds(6, 7, 223, 23);
			hopping.add(hoppingBox);

			JLabel lblTime = new JLabel("Time (minutes):");
			lblTime.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblTime.setBounds(20, 62, 104, 50);
			hopping.add(lblTime);

			timeSlider = new JSlider();
			timeSlider.setValue(60);
			timeSlider.setPaintTicks(true);
			timeSlider.setPaintLabels(true);
			timeSlider.setMajorTickSpacing(30);
			timeSlider.setMinorTickSpacing(15);
			timeSlider.setMaximum(180);
			timeSlider.setBounds(123, 62, 223, 50);
			hopping.add(timeSlider);

			JLabel lblPlayersDetected = new JLabel("Player(s) detected:");
			lblPlayersDetected.setFont(new Font("Verdana", Font.PLAIN, 12));
			lblPlayersDetected.setBounds(20, 140, 131, 20);
			hopping.add(lblPlayersDetected);

			playerSpinner = new JSpinner();
			playerSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(0), null, new Integer(1)));
			playerSpinner.setBounds(148, 141, 40, 20);
			hopping.add(playerSpinner);

			JSeparator separator = new JSeparator();
			separator.setBounds(0, 39, 357, 2);
			hopping.add(separator);

			JButton btnStart = new JButton("Start");
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {

					autoSwitch = autoSwitchBox.isSelected();

					eatFood = eatFoodBox.isSelected();
					if (eatFood)
						println("Using food.");

					mark_of_grace = graceBox.isSelected();
					if (mark_of_grace)
						println("We are picking up Mark of grace.");

					stamina = staminaBox.isSelected();
					if (stamina)
						println("Using Stamina potions.");

					useAutoResponder = autoResponderBox.isSelected();
					if (useAutoResponder)
						println("Using Auto Responder V2.");

					useABC2Reaction = abc2Box.isSelected();
					if (useABC2Reaction) {
						println("Using ABC2 Reaction Timing");
					}
					else {
						println("Disabled ABC2 Reaction Timing");
						ABC.setSleepReaction(false);
					}

					mouseSpeed = (int) mouseSpeedSlider.getValue();
					println("Mouse speed set to " + mouseSpeed);

					if (!autoSwitch)
						course = (COURSE) coursesBox.getSelectedItem();

					worldHopping = hoppingBox.isSelected();

					if (worldHopping) {

						hoppingTime = ((int) timeSlider.getValue()) * 60 * 1000;
						if (hoppingTime > 0)
							println("Changing worlds every " + timeSlider.getValue() + " minutes (+/- 10 minutes).");

						maxPlayers = (int) playerSpinner.getValue();
						if (maxPlayers > 0)
							println("Changing worlds if " + maxPlayers + " or more players are detected.");

					}

					gui_is_up = false;
					g.dispose();

				}
			});
			btnStart.setBounds(122, 319, 140, 23);
			contentPane.add(btnStart);
		}
	}

	@Override
	public void clanMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void duelRequestReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void personalMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerMessageReceived(String username, String message) {

		if (useAutoResponder)
			responder.generateResponse(username, message);

	}

	@Override
	public void serverMessageReceived(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void tradeRequestReceived(String arg0) {
		// TODO Auto-generated method stub

	}

	private boolean sendServerData(String username, long duration, int xp, int levels, int marks, int laps, String courseName, String version) {

		try {

			URL url = new URL("http://usa-tribot.org/agility/data.php?username=" + username + "&duration=" + duration + "&xp=" + xp + "&levels=" + levels + "&marks=" + marks + "&laps=" + laps +
					"&course=" + courseName + "&version=" + version);

			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			try {

				String response = br.readLine();
				return Boolean.valueOf(response);

			}
			finally {

				br.close();

			}

		}
		catch (MalformedURLException e) {

			System.out.println("MalformedURLException when sending session data.");

		}
		catch (IOException e) {

			System.out.println("IOException when sending session data.");

		}

		return false;

	}

	@Override
	public void onEnd() {

		interaction.setStop(true);
		System.out.println("Ending Player Interaction thread.");

		long time = System.currentTimeMillis() - startTime;
		println("Total Duration: " + Timing.msToString(time));
		int xpGained = Skills.getXP(SKILLS.AGILITY) - startXP;
		int levelsGained = Skills.getActualLevel(SKILLS.AGILITY) - startLVL;
		String courseName = "Null";

		if (course != null) {

			try {

				courseName = URLEncoder.encode(toTitleCase(course.toString()), "UTF-8");

			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

		}

		println("Thank you for using USA Agility " + version + "!");

		if (startTime > 0 && xpGained > 0) {

			try {

				if (sendServerData(URLEncoder.encode(General.getTRiBotUsername(), "UTF-8"), time, xpGained, levelsGained, marks, laps, courseName, version)) {

					println("Sent session data!");

				}
				else {

					println("Error sending session data!");

				}

			}
			catch (UnsupportedEncodingException e) {

				e.printStackTrace();

			}

		}

	}
}
