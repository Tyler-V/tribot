package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.tribot.api.Timing;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.KeyActions;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Tools", name = "Reaction Timer Tool")
public class ReactionTimer extends Script implements Painting, Ending,
		KeyActions, MouseActions {

	private List<Integer> reaction_times = new ArrayList<Integer>();
	private List<Point> points = new ArrayList<Point>();
	private String status = "Starting";
	private long total_time = 0;
	private long time = 0;
	private boolean start = false;
	private boolean stop = false;
	private Thread thread;

	//
	private int hash = 0;

	public void run() {
		total_time = System.currentTimeMillis();
		thread = new Thread(starting_condition);
		thread.start();
		while (true) {
			if (stop && time > 0) {
				start = false;
				stop = false;
				int reaction_time = (int) (System.currentTimeMillis() - time);
				time = 0;
				reaction_times.add(reaction_time);
				println("Reaction time: " + reaction_time);
				status = "Complete";
			}
			sleep(10);
		}
	}

	public Runnable starting_condition = new Runnable() {
		public void run() {
			while (true) {
				RSPlayer player = Player.getRSPlayer();
				if (player != null) {
					RSCharacter character = player.getInteractingCharacter();
					if (character != null && character.hashCode() != hash
							&& character.getMaxHealth() > 0
							&& character.getHealth() == 0) {
						if (time == 0) {
							time = System.currentTimeMillis();
							hash = character.hashCode();
							start = true;
							status = "Timing";
						}
					}
				}
				sleep(10);
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public void onEnd() {
		thread.stop();
		double[] reaction = calculateReactionTime(reaction_times);
		println("Total Test Cases: " + reaction_times.size());
		println("MIN: " + reaction[0]);
		println("MAX: " + reaction[1]);
		println("MEAN: " + reaction[2]);
		println("SD: " + reaction[3]);
	}

	private double[] calculateReactionTime(final List<Integer> reaction_times) {
		if (reaction_times.size() == 0)
			return new double[] { 0, 0, 0, 0 };

		double sum = 0;
		double min = reaction_times.get(0);
		double max = reaction_times.get(0);

		for (final int time : reaction_times) {
			if (time < min)
				min = time;
			if (time > max)
				max = time;
			sum += time;
		}

		final double mean_time = sum / reaction_times.size();

		double variance = 0;

		for (final int time : reaction_times)
			variance += Math.pow(time - mean_time, 2);

		variance /= reaction_times.size();

		final double sd = Math.sqrt(variance);

		return new double[] { min, max, mean_time, sd };
	}

	@Override
	public void keyPressed(int arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(int arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(int arg0, boolean arg1) {
		if (!start && time == 0) {
			time = System.currentTimeMillis();
			start = true;
			status = "Timing";
		} else if (!stop) {
			stop = true;
		}
	}

	@Override
	public void mouseClicked(Point arg0, int arg1, boolean arg2) {
		if (!arg2)
			points.add(arg0);
	}

	@Override
	public void mouseDragged(Point arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(Point arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(Point arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPaint(Graphics g) {
		g.setFont(new Font("Verdana", Font.BOLD, 20));
		g.setColor(Color.RED);
		int x = 320;
		int y = 375;
		int spacing = 25;
		g.drawString("Tests: " + reaction_times.size(), x, y);
		y += spacing;
		g.drawString(
				"" + Timing.msToString(System.currentTimeMillis() - total_time),
				x, y);
		y += spacing;
		g.drawString(status, x, y);
		y += spacing;
		if (start) {
			g.drawString("Time: " + (System.currentTimeMillis() - time), x, y);
		} else {
			g.drawString("Time: 0", x, y);
		}
		if (points.size() > 0) {
			for (Point p : points) {
				g.fillRect(p.x, p.y, 2, 2);
			}
		}
	}
}