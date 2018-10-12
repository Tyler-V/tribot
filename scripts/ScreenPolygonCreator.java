package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Tools", name = "Screen Polygon Creator")
public class ScreenPolygonCreator extends Script implements Ending,
		KeyListener, Painting, MouseActions {

	private ArrayList<Point> captured = new ArrayList<Point>();

	private Polygon poly = new Polygon(new int[] { 638, 629, 625, 617, 607,
			603, 598, 594, 590, 586, 583, 580, 577, 575, 574, 572, 570, 571,
			571, 571, 574, 579, 583, 589, 595, 604, 609, 613, 617, 619, 623,
			623, 628, 635, 640, 648, 653, 658, 662, 670, 678, 688, 695, 703,
			709, 712, 714, 716, 713, 707, 701, 689, 675, 649 }, new int[] { 8,
			8, 9, 13, 18, 20, 23, 27, 30, 35, 40, 40, 45, 49, 52, 57, 66, 74,
			80, 89, 105, 112, 116, 122, 126, 130, 132, 134, 138, 141, 146, 150,
			154, 159, 159, 158, 157, 152, 145, 137, 132, 127, 122, 115, 107,
			89, 77, 68, 58, 44, 35, 23, 14, 8 }, 54);

	boolean run = true;

	public void run() {

		while (run) {
			sleep(25);
		}
	}

	private String getXPoints() {
		String temp = "";
		for (Point p : captured) {
			if (temp.length() == 0) {
				temp = "" + p.x;
			} else {
				temp = temp + ", " + p.x;
			}

		}
		return temp;
	}

	private String getYPoints() {
		String temp = "";
		for (Point p : captured) {
			if (temp.length() == 0) {
				temp = "" + p.y;
			} else {
				temp = temp + ", " + p.y;
			}

		}
		return temp;
	}

	private String getNPoints() {
		return "" + captured.size();
	}

	@Override
	public void onEnd() {
		println("Polygon poly = new Polygon(new int[] {" + getXPoints()
				+ "}, new int[] {" + getYPoints() + "}, " + getNPoints() + ");");
	}

	@Override
	public void onPaint(Graphics g) {
		Color c = new Color(255, 0, 0, 100);
		g.setColor(c);

		g.fillPolygon(poly);

		Font bold = new Font("Tahoma", Font.BOLD, 16);
		g.setFont(bold);
		int x = 10;
		int y = 25;
		g.drawString("Click on the point you want to add.", x, y);
		y += 20;
		g.drawString("Press F1 to remove last point.", x, y);
		y += 20;
		g.drawString("Stop Script when complete!", x, y);

		Polygon draw = new Polygon();
		for (Point p : captured) {
			draw.addPoint(p.x, p.y);
		}
		g.fillPolygon(draw);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_F1) {
			println("Removing " + captured.get(captured.size() - 1));
			captured.remove(captured.size() - 1);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(Point arg0, int arg1, boolean arg2) {
		println("Added: " + arg0);
		captured.add(arg0);
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
}