package scripts.agility.paint;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.tribot.api.Timing;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.script.interfaces.EventBlockingOverride.OVERRIDE_RETURN;

import scripts.agility.data.Vars;
import scripts.usa.api.painting.PaintUtils;
import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.components.PaintBoxString;
import scripts.usa.api.painting.components.PaintCenteredString;
import scripts.usa.api.painting.components.PaintContainer;
import scripts.usa.api.painting.components.PaintPolygon;
import scripts.usa.api.painting.components.PaintRunescapeWidget;
import scripts.usa.api.painting.components.PaintString;
import scripts.usa.api.painting.components.PaintToNextLevel;
import scripts.usa.api.painting.components.PaintVisibilityButton;
import scripts.usa.api.painting.media.Fonts;
import scripts.usa.api.painting.media.Icons;
import scripts.usa.api.painting.models.PaintColumns;
import scripts.usa.api.util.Strings;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api2007.entity.Entity;

public class AgilityPaint extends Painter {
	private final Color TEXT_COLOR = Color.WHITE;
	private final Color TEXT_BACKGROUND_COLOR = PaintUtils.colorFromHex("#0b1a84");
	private final Color BORDER_COLOR = PaintUtils.colorFromHex("#FFFFFF", .5);
	private final Color BACKGROUND_COLOR = PaintUtils.colorFromHex("#192ac4", .9);

	public AgilityPaint() {
		super();
		getPaint().add(new PaintPolygon(() -> Entity.getCurrentEnclosedArea(), Color.GREEN));
		getPaint().add(new PaintRunescapeWidget(BACKGROUND_COLOR, Color.BLACK));
		getPaint().add(new PaintCenteredString(() -> Vars.get()
				.getScriptManifest()
				.name(), new Font(Fonts.Google.COURGETTE.getName(), 0, 28), new Rectangle(0, 314, 517, 38)));
		getPaint().add(new PaintString(() -> "v" + Vars.get()
				.getScriptManifest()
				.version(), new Font(Fonts.Google.ROBOTO.getName(), 0, 12), 333, 343));
		getPaint().add(new PaintVisibilityButton(() -> getPaint().isVisible(), Icons.Color.LIGHT, BACKGROUND_COLOR, BORDER_COLOR));
		getPaint().add(new PaintContainer(PaintColumns.AUTO,
				new Rectangle(7, 350, 506, 100),
				TEXT_COLOR,
				TEXT_BACKGROUND_COLOR,
				BORDER_COLOR,
				new PaintBoxString(() -> "Time: " + Timing.msToString(Vars.get()
						.getSkillsTracker()
						.getElapsedTime())),
				new PaintBoxString(() -> Vars.get().status),

				new PaintBoxString(() -> "Laps: " + Strings.format(Vars.get().laps) +
						" " +
						Vars.get()
								.getSkillsTracker()
								.getAmountPerHourString(Vars.get().laps)),
				new PaintBoxString(() -> "XP Gained: " + Vars.get()
						.getSkillsTracker()
						.getXPGained(SKILLS.AGILITY) +
						" (" +
						Strings.format(Vars.get()
								.getSkillsTracker()
								.getXPPerHour(SKILLS.AGILITY)) +
						"/hr)"),
				new PaintBoxString(() -> "Failed Obstacles: " + Strings.format(Vars.get().failedObstacles) +
						" " +
						Vars.get()
								.getSkillsTracker()
								.getAmountPerHourString(Vars.get().failedObstacles)),
				new PaintBoxString(() -> "Agility Level: " + Skills.getActualLevel(SKILLS.AGILITY) +
						" (+" +
						Vars.get()
								.getSkillsTracker()
								.getLevelsGained(SKILLS.AGILITY) +
						")"),
				new PaintBoxString(() -> "Mark of grace: " + Strings.format(Vars.get().markOfGraces) +
						" " +
						Vars.get()
								.getSkillsTracker()
								.getAmountPerHourString(Vars.get().markOfGraces)),
				new PaintBoxString(() -> "Profit: " + Strings.format(Vars.get().markOfGraces * OSBuddy.get("Amylase crystal")
						.getAveragePrice() * 10) +
						" " +
						Vars.get()
								.getSkillsTracker()
								.getAmountPerHourString(Vars.get().markOfGraces * OSBuddy.get("Amylase crystal")
										.getAveragePrice() * 10))));
		getPaint().add(new PaintToNextLevel(Vars.get()
				.getSkillsTracker(), SKILLS.AGILITY, new Rectangle(13, 452, 494, 20), BORDER_COLOR));
	}

	@Override
	public void paint(Graphics g) {
	}

	@Override
	public OVERRIDE_RETURN notifyMouseEvent(MouseEvent event) {
		if (event.getID() == MouseEvent.MOUSE_CLICKED) {
			if (isVisibilityButtonClicked(event.getPoint())) {
				getPaint().toggleVisibility();
				return OVERRIDE_RETURN.DISMISS;
			}
		}
		return OVERRIDE_RETURN.PROCESS;
	}

	@Override
	public OVERRIDE_RETURN notifyKeyEvent(KeyEvent event) {
		return OVERRIDE_RETURN.PROCESS;
	}
}
