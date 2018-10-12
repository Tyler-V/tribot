package scripts;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;

import org.tribot.api.input.Mouse;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors = { "Usa" }, category = "Randoms", name = "Odd Appendage")
public class AppendageRandom extends Script {

	String version = "8/12/2013 8:58PM EST";

	@Override
	public void run() {

		if (inAppendageRandom()) {
			RSObject[] appendages = getAppendages();

			RSObject eastAppendage = appendages[0];
			RSObject northAppendage = appendages[1];
			RSObject westAppendage = appendages[2];
			RSObject southAppendage = appendages[3];

			RSModel east_model = eastAppendage.getModel();
			RSModel north_model = northAppendage.getModel();
			RSModel west_model = westAppendage.getModel();
			RSModel south_model = southAppendage.getModel();

			int east_points = east_model.getPoints().length;
			int north_points = north_model.getPoints().length;
			int west_points = west_model.getPoints().length;
			int south_points = south_model.getPoints().length;
			int[] appendages_length = { east_points, north_points, west_points, south_points };

			int east_height = 0, north_height = 0, west_height = 0, south_height = 0;
			for (int i = 0; i < east_model.getVerticiesY().length; i++) {
				east_height += east_model.getVerticiesY()[i];
			}
			for (int i = 0; i < north_model.getVerticiesY().length; i++) {
				north_height += north_model.getVerticiesY()[i];
			}
			for (int i = 0; i < west_model.getVerticiesY().length; i++) {
				west_height += west_model.getVerticiesY()[i];
			}
			for (int i = 0; i < south_model.getVerticiesY().length; i++) {
				south_height += south_model.getVerticiesY()[i];
			}
			int[] appendages_height = { east_height, north_height, west_height, south_height };

			Arrays.sort(appendages_length);
			Arrays.sort(appendages_height);
			println(Arrays.toString(appendages_length));
			println(Arrays.toString(appendages_height));

			if (appendages_height[0] != appendages_height[3]) {
				if (appendages_height[2] != appendages_height[3]) {
					if (appendages_height[3] == east_height) {
						println("Answer is the east appendage!");
						clickAppendage(eastAppendage);
					} else if (appendages_height[3] == north_height) {
						println("Answer is the north appendage!");
						clickAppendage(northAppendage);
					} else if (appendages_height[3] == west_height) {
						println("Answer is the west appendage!");
						clickAppendage(westAppendage);
					} else if (appendages_height[3] == south_height) {
						println("Answer is the south appendage!");
						clickAppendage(southAppendage);
					}
				} else if (appendages_height[0] != appendages_height[1]) {
					if (appendages_height[0] == east_height) {
						println("Answer is the east appendage!");
						clickAppendage(eastAppendage);
					} else if (appendages_height[0] == north_height) {
						println("Answer is the north appendage!");
						clickAppendage(northAppendage);
					} else if (appendages_height[0] == west_height) {
						println("Answer is the west appendage!");
						clickAppendage(westAppendage);
					} else if (appendages_height[0] == south_height) {
						println("Answer is the south appendage!");
						clickAppendage(southAppendage);
					}
				}

			} else if (appendages_length[0] != appendages_length[3]) {
				if (appendages_length[2] != appendages_length[3]) {
					if (appendages_length[3] == east_points) {
						println("Answer is the east appendage!");
						clickAppendage(eastAppendage);
					} else if (appendages_length[3] == north_points) {
						println("Answer is the north appendage!");
						clickAppendage(northAppendage);
					} else if (appendages_length[3] == west_points) {
						println("Answer is the west appendage!");
						clickAppendage(westAppendage);
					} else if (appendages_length[3] == south_points) {
						println("Answer is the south appendage!");
						clickAppendage(southAppendage);
					}
				} else if (appendages_length[0] != appendages_length[1]) {
					if (appendages_length[0] == east_points) {
						println("Answer is the east appendage!");
						clickAppendage(eastAppendage);
					} else if (appendages_length[0] == north_points) {
						println("Answer is the north appendage!");
						clickAppendage(northAppendage);
					} else if (appendages_length[0] == west_points) {
						println("Answer is the west appendage!");
						clickAppendage(westAppendage);
					} else if (appendages_length[0] == south_points) {
						println("Answer is the south appendage!");
						clickAppendage(southAppendage);
					}
				}
			}
		}
	}

	private void clickAppendage(RSObject appendage) {
		while (appendage != null) {
			Polygon model = appendage.getModel().getEnclosedArea();
			Point p = new Point((int) model.getBounds2D().getCenterX(), (int) model.getBounds2D().getCenterY());
			Mouse.move(p);
			sleep(50, 100);
			Mouse.click(3);
			sleep(50, 100);
			if (ChooseOption.isOpen()) {
				if (ChooseOption.select("Operate")) {
					sleep(3000);
					break;
				} else {
					ChooseOption.select("Cancel");
					sleep(150);
				}
			}
			sleep(250, 500);
		}
	}

	private boolean inAppendageRandom() {
		RSObject[] obj = Objects.getAll(3);
		if (obj != null && obj.length == 44) {
			if (getReachableTiles() == 1) {
				return true;
			}
		}
		return false;
	}

	private int getReachableTiles() {
		ArrayList<RSTile> list = new ArrayList<RSTile>();
		RSTile pos = Player.getPosition();
		int radius = 10;

		int diameter = (1 + (2 * radius));
		int plane = pos.getPlane();

		int x = pos.getX() - radius;
		int y = pos.getY() + radius;

		for (int i = 0; i < diameter; i++) {
			x = pos.getX() - radius;
			for (int j = 0; j < diameter; j++) {
				RSTile temp = new RSTile(x, y, plane);
				if (PathFinding.canReach(temp, false)) {
					list.add(temp);
				}
				x += 1;
			}
			y -= 1;
		}
		return list.size();
	}

	private RSObject[] getAppendages() {
		RSObject[] appendages = new RSObject[4];
		RSTile pos = Player.getPosition();
		if (pos != null) {
			appendages[0] = Objects.getAt(new RSTile(pos.getX() - 2, pos.getY()))[0];
			appendages[1] = Objects.getAt(new RSTile(pos.getX(), pos.getY() + 1))[0];
			appendages[2] = Objects.getAt(new RSTile(pos.getX() + 1, pos.getY()))[0];
			appendages[3] = Objects.getAt(new RSTile(pos.getX(), pos.getY() - 2))[0];
		}
		return appendages;
	}

}