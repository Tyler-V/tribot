package scripts.blastfurnace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

public enum TEMPERATURE {

	WHITE(new ArrayList<Point>(Arrays.asList(new Point(289, 169), new Point(284, 175), new Point(279, 181),
			new Point(272, 184), new Point(266, 188), new Point(259, 189), new Point(255, 191), new Point(250, 192),
			new Point(239, 211), new Point(231, 211), new Point(225, 216), new Point(286, 164)))),

	UPPER_GREEN(new ArrayList<Point>(Arrays.asList(new Point(198, 180), new Point(194, 178)))),

	MID_GREEN(new ArrayList<Point>(
			Arrays.asList(new Point(196, 193), new Point(202, 199), new Point(203, 201), new Point(204, 202)))),

	LOWER_GREEN(new ArrayList<Point>(Arrays.asList(new Point(219, 205), new Point(221, 198)))),

	RED(new ArrayList<Point>(Arrays.asList(new Point(206, 168), new Point(216, 163), new Point(216, 163),
			new Point(229, 154), new Point(244, 157)))),

	UNKNOWN(null);

	private ArrayList<Point> points;

	TEMPERATURE(ArrayList<Point> points) {

		this.points = points;

	}

	public ArrayList<Point> getPoints() {

		return this.points;

	}

}
