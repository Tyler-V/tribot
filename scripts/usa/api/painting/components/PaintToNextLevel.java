package scripts.usa.api.painting.components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.tribot.api.Timing;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.interfaces.Paintable;
import scripts.usa.api.util.Strings;
import scripts.usa.api2007.tracking.SkillsTracker;

public class PaintToNextLevel implements Paintable {

	private final static Color GREEN = new Color(25, 170, 0, 255);
	private final static Color RED = new Color(160, 0, 0, 255);

	private final SkillsTracker skillsTracker;
	private final SKILLS skill;
	private final Rectangle bounds;
	private final Color progressColor;
	private final Color backgroundColor;
	private final Color borderColor;

	public PaintToNextLevel(SkillsTracker skillsTracker, SKILLS skill, Rectangle bounds, Color progressColor, Color backgroundColor,
			Color borderColor) {
		this.skillsTracker = skillsTracker;
		this.skill = skill;
		this.bounds = bounds;
		this.progressColor = progressColor;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
	}

	public PaintToNextLevel(SkillsTracker skillsTracker, SKILLS skill, Rectangle bounds, Color progressColor, Color borderColor) {
		this(skillsTracker, skill, bounds, progressColor, RED, borderColor);
	}

	public PaintToNextLevel(SkillsTracker skillsTracker, SKILLS skill, Rectangle bounds, Color borderColor) {
		this(skillsTracker, skill, bounds, GREEN, RED, borderColor);
	}

	public PaintToNextLevel(SkillsTracker skillsTracker, SKILLS skill, Rectangle bounds) {
		this(skillsTracker, skill, bounds, GREEN, RED, Painter.Colors.BORDER_COLOR);
	}

	private String getString() {
		return SkillsTracker.getXPToNextLevel(skill) + " XP to " +
				SkillsTracker.getNextLevel(skill) +
				" " +
				Strings.toProperCase(skill.toString() + " (" + Timing.msToString(skillsTracker.getTimeToNextLevel(skill)) + ")");
	}

	@Override
	public void paint(Graphics2D g2) {
		try {
			String text = getString();
			g2.setColor(backgroundColor);
			g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, Painter.ARC, Painter.ARC);
			g2.setColor(progressColor);
			g2.fillRoundRect(bounds.x, bounds.y, (bounds.width * Skills.getPercentToNextLevel(skill)) / 100, bounds.height, Painter.ARC, Painter.ARC);
			g2.setColor(borderColor);
			g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, Painter.ARC, Painter.ARC);
			g2.setColor(Color.WHITE);
			g2.setFont(Painter.FONT);
			FontMetrics metrics = g2.getFontMetrics(Painter.FONT);
			int x = bounds.x + (bounds.width - metrics.stringWidth(text)) / 2;
			int y = bounds.y + ((bounds.height - metrics.getHeight()) / 2) + metrics.getAscent() + 1;
			g2.drawString(text, x, y);
		}
		catch (Exception e) {
		}
	}
}
