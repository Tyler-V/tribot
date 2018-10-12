package scripts.magic.paint;

import org.tribot.api2007.Skills.SKILLS;
import org.tribot.script.interfaces.EventBlockingOverride.OVERRIDE_RETURN;
import scripts.magic.data.VarsM;
import scripts.magic.listeners.MagicEXPListener;
import scripts.starfox.api.util.Calculations;
import scripts.starfox.api.util.Images;
import scripts.starfox.api.util.Strings;
import scripts.starfox.api2007.Clicking07;
import scripts.starfox.graphics.Drawing;
import scripts.starfox.graphics.Painter;
import scripts.starfox.graphics.components.PaintBlock;
import scripts.starfox.graphics.components.ShowHideButton;
import scripts.starfox.graphics.mouse.types.ImageMouse;
import scripts.starfox.scriptframework.Vars;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @author Nolan
 */
public class MagicPainter extends Painter {

	public static final Color BLUE = new Color(28, 134, 238);

	private final ShowHideButton showHideButton;

	/**
	 * Constructs a new {@link MagicPainter}.
	 *
	 * @param expListener
	 *            The {@link MagicEXPListener} being used.
	 */
	public MagicPainter(final MagicEXPListener expListener) {
		super(new ImageMouse(Images.URLs.RUNESCAPE_CURSOR_URL));
		this.showHideButton = new ShowHideButton();
		setShouldDrawVerticalStack(true);

		getPaintBlockManager()
				.addPaintBlocks(new PaintBlock(() -> "Sigma Magic v" + version(), PaintBlock.DEFAULT_FONT.deriveFont(1), BLUE), new PaintBlock(
						() -> "Time running: " + Strings.msToString(Vars.get().getTimeTracker().getRunningTime(), false)), new PaintBlock(() -> {
							int exp = Vars.get().getSkillTracker().getExpGained(SKILLS.MAGIC);
							long expHour = Calculations.getPerHour(exp, Vars.get().getTimeTracker().getStartTime());
							return "Exp gained: " + Strings.kmb(exp, false) + " (" + Strings.kmb(expHour, false) + " / Hour)";
						}), new PaintBlock(() -> {
							int spellsCast = expListener.getSpellsCasted();
							long spellsCastHour = Calculations.getPerHour(spellsCast, Vars.get().getTimeTracker().getStartTime());
							return "Spells cast: " + Strings.kmb(spellsCast, false) + " (" + Strings.kmb(spellsCastHour, false) + " / Hour)";
						}), new PaintBlock(() -> "Time until next level: " + Strings.msToString(Calculations.getTimeToLevel(SKILLS.MAGIC, Vars.get()
								.getSkillTracker()
								.getExpGained(SKILLS.MAGIC), Vars.get().getTimeTracker().getStartTime()), false)), new PaintBlock(() -> {
									String string = getLootTracker().getProfit() >= 0 ? "Profit" : "Loss";
									int profit = getLootTracker().getProfit();
									long profitHour = Calculations.getPerHour(profit, Vars.get().getTimeTracker().getStartTime());
									return string + ": " + Strings.kmb(profit, false) + " (" + Strings.kmb(profitHour, false) + " / Hour)";
								}), new PaintBlock(() -> {
									VarsM vars = VarsM.get();
									String status = "";
									if (vars != null) {
										status = vars.getStatus();
									}
									return "Status: " + status;
								}));
	}

	@Override
	public void paint(Graphics g) {
		showHideButton.paint(g);
		Drawing.draw(g, new Color(255, 255, 255, 175), Clicking07.getCurrentClickable());
	}

	@Override
	public OVERRIDE_RETURN notifyMouseEvent(MouseEvent event) {
		if (event.getID() == MouseEvent.MOUSE_PRESSED) {
			if (showHideButton.getArea().contains(event.getPoint())) {
				showHideButton.press();
				setVisible(!isVisible());
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
