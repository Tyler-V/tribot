package scripts.construction.paint;

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

import scripts.construction.data.Vars;
import scripts.usa.api.util.Strings;
import scripts.usa.painting.Painter;
import scripts.usa.painting.media.Fonts;
import scripts.usa.painting.media.Icons;
import scripts.usa.painting.models.PaintColumns;
import scripts.usa.painting.paintables.PaintBoxString;
import scripts.usa.painting.paintables.PaintCenteredString;
import scripts.usa.painting.paintables.PaintContainer;
import scripts.usa.painting.paintables.PaintRunescapeWidget;
import scripts.usa.painting.paintables.PaintString;
import scripts.usa.painting.paintables.PaintToNextLevel;
import scripts.usa.painting.paintables.PaintVisibilityButton;

public class Paint extends Painter {
	private final Color TEXT_COLOR = Color.WHITE;
	private final Color TEXT_BACKGROUND_COLOR = new Color(181, 129, 28, 255);
	private final Color BORDER_COLOR = new Color(201, 166, 48, 255);
	private final Color BACKGROUND_COLOR = new Color(71, 55, 31, 225);

	public Paint() {
		super();
		getPaint().add(new PaintRunescapeWidget(BACKGROUND_COLOR, Color.BLACK));
		getPaint().add(new PaintCenteredString(() -> Vars.get().getScriptManifest().name(), new Font(Fonts.Google.COURGETTE.getName(), 0, 24),
				new Rectangle(1, 338, 516, 38)));
		getPaint()
				.add(new PaintString(() -> "v" + Vars.get().getScriptManifest().version(), new Font(Fonts.Google.ROBOTO.getName(), 0, 10), 324, 366));
		getPaint().add(new PaintVisibilityButton(() -> getPaint().isVisible(), Icons.Color.DARK, TEXT_BACKGROUND_COLOR, Color.BLACK));
		getPaint().add(new PaintContainer(PaintColumns.AUTO, new Rectangle(7, 371, 506, 75), TEXT_COLOR, TEXT_BACKGROUND_COLOR, BORDER_COLOR,
				new PaintBoxString(() -> "Time: " + Timing.msToString(Vars.get().getSkillsTracker().getElapsedTime())),
				new PaintBoxString(() -> "Construction Level: " + Skills.getActualLevel(SKILLS.CONSTRUCTION) +
						" (+" +
						Vars.get().getSkillsTracker().getLevelsGained(SKILLS.CONSTRUCTION) +
						")"),
				new PaintBoxString(() -> "XP Gained: " + Vars.get().getSkillsTracker().getXPGained(SKILLS.CONSTRUCTION) +
						" (" +
						Strings.format(Vars.get().getSkillsTracker().getXPPerHour(SKILLS.CONSTRUCTION)) +
						"/hr)"),
				new PaintBoxString(() -> Vars.get().getStatus())));

		getPaint().add(new PaintToNextLevel(SKILLS.CONSTRUCTION, new Rectangle(12, 449, 496, 20)));
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
