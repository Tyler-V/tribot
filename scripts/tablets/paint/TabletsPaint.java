package scripts.tablets.paint;

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

import scripts.tablets.data.Vars;
import scripts.usa.api.painting.PaintUtils;
import scripts.usa.api.painting.Painter;
import scripts.usa.api.painting.components.PaintBoxString;
import scripts.usa.api.painting.components.PaintCenteredString;
import scripts.usa.api.painting.components.PaintContainer;
import scripts.usa.api.painting.components.PaintRunescapeWidget;
import scripts.usa.api.painting.components.PaintString;
import scripts.usa.api.painting.components.PaintToNextLevel;
import scripts.usa.api.painting.components.PaintVisibilityButton;
import scripts.usa.api.painting.media.Fonts;
import scripts.usa.api.painting.media.Icons;
import scripts.usa.api.painting.models.PaintColumns;
import scripts.usa.api.util.Strings;

public class TabletsPaint extends Painter {
	private final Color TEXT_COLOR = Color.WHITE;
	private final Color TEXT_BACKGROUND_COLOR = PaintUtils.colorFromHex("#0b1a84");
	private final Color BORDER_COLOR = PaintUtils.colorFromHex("#FFFFFF", .5);
	private final Color BACKGROUND_COLOR = PaintUtils.colorFromHex("#192ac4", .9);

	public TabletsPaint() {
		super();
		getPaint().add(new PaintRunescapeWidget(BACKGROUND_COLOR, Color.BLACK));
		getPaint().add(new PaintCenteredString(() -> Vars.get()
				.getScriptManifest()
				.name(), new Font(Fonts.Google.COURGETTE.getName(), 0, 24), new Rectangle(1, 338, 516, 38)));
		getPaint().add(new PaintString(() -> "v" + Vars.get()
				.getScriptManifest()
				.version(), new Font(Fonts.Google.ROBOTO.getName(), 0, 10), 482, 366));
		getPaint().add(new PaintVisibilityButton(() -> getPaint().isVisible(), Icons.Color.LIGHT, BACKGROUND_COLOR, BORDER_COLOR));
		getPaint().add(new PaintContainer(PaintColumns.AUTO,
				new Rectangle(7, 371, 506, 75),
				TEXT_COLOR,
				TEXT_BACKGROUND_COLOR,
				BORDER_COLOR,
				new PaintBoxString(() -> "Time: " + Timing.msToString(Vars.get()
						.getSkillsTracker()
						.getElapsedTime())),
				new PaintBoxString(() -> Vars.get().status),
				new PaintBoxString(() -> Vars.get().tablet.getName() + "s: " +
						Vars.get().tablets +
						" " +
						Vars.get()
								.getSkillsTracker()
								.getAmountPerHourString(Vars.get().tablets)),
				new PaintBoxString(() -> "Profit: " + Strings.format(Vars.get().profit) +
						" " +
						Vars.get()
								.getSkillsTracker()
								.getAmountPerHourString(Vars.get().profit)),
				new PaintBoxString(() -> "Magic Level: " + Skills.getActualLevel(SKILLS.MAGIC) +
						" (+" +
						Vars.get()
								.getSkillsTracker()
								.getLevelsGained(SKILLS.MAGIC) +
						")"),
				new PaintBoxString(() -> "XP Gained: " + Vars.get()
						.getSkillsTracker()
						.getXPGained(SKILLS.MAGIC) +
						" " +
						Vars.get()
								.getSkillsTracker()
								.getXPPerHourString(SKILLS.MAGIC))));
		getPaint().add(new PaintToNextLevel(Vars.get()
				.getSkillsTracker(), SKILLS.MAGIC, new Rectangle(12, 449, 496, 20), BORDER_COLOR));
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
