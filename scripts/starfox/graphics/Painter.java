package scripts.starfox.graphics;

import org.tribot.script.interfaces.EventBlockingOverride.OVERRIDE_RETURN;
import scripts.starfox.graphics.mouse.MousePaint;
import scripts.starfox.graphics.components.PaintBlock;
import scripts.starfox.graphics.paint.PaintBlockManager;
import scripts.starfox.graphics.components.PaintString;
import scripts.starfox.graphics.paint.PaintStringContainer;
import scripts.starfox.tracking.loot.BasicLootTracker;
import scripts.starfox.tracking.loot.LootTracker;
import scripts.starfox.tracking.skills.SkillTracker;
import scripts.starfox.tracking.time.TimeTracker;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * @author Nolan
 */
public abstract class Painter {

    private final LootTracker lootTracker;
    private double version;

    private PaintBlockManager paintBlockManager;
    private PaintStringContainer paintStringContainer;
    private MousePaint mousePaint;

    private String status;
    private boolean visible;
    private boolean drawVerticalStack;

    /**
     * Constructs a new {@link Painter}.
     *
     * The {@link MousePaint} of the {@link Painter} will be set to null by default.
     */
    public Painter() {
        this(new PaintBlockManager(), new PaintStringContainer(), null, new BasicLootTracker());
    }

    /**
     * Constructs a new {@link Painter}.
     *
     * @param mousePaint The {@link MousePaint}.
     */
    public Painter(MousePaint mousePaint) {
        this(new PaintBlockManager(), new PaintStringContainer(), mousePaint, new BasicLootTracker());
    }

    /**
     * Constructs a new {@link Painter}.
     *
     * The status of the {@link Painter} will be set to be an empty {@link String} by default.
     * The {@link Painter}'s visibility will be set to true by default.
     *
     * @param paintBlockManager    The {@link PaintBlockManager}.
     * @param paintStringContainer The {@link PaintStringContainer}.
     * @param mousePaint           The {@link MousePaint}.
     * @param lootTracker          The {@link LootTracker}.
     */
    public Painter(PaintBlockManager paintBlockManager, PaintStringContainer paintStringContainer, MousePaint mousePaint, LootTracker lootTracker) {
        this.paintBlockManager = paintBlockManager;
        this.paintStringContainer = paintStringContainer;
        this.mousePaint = mousePaint;
        this.lootTracker = lootTracker;
        this.status = "";
        this.visible = true;
    }

    /**
     * Gets the {@link LootTracker}.
     *
     * @return The {@link LootTracker}.
     */
    public LootTracker getLootTracker() {
        return lootTracker;
    }

    /**
     * Gets the version for the {@link Painter} to display.
     *
     * @return The version.
     */
    public double version() {
        return this.version;
    }

    /**
     * Gets the {@link PaintBlockManager}.
     *
     * @return The {@link PaintBlockManager}.
     */
    public PaintBlockManager getPaintBlockManager() {
        return this.paintBlockManager;
    }

    /**
     * Gets the {@link PaintStringContainer}.
     *
     * @return The {@link PaintStringContainer}.
     */
    public PaintStringContainer getPaintStringContainer() {
        return this.paintStringContainer;
    }

    /**
     * Gets the {@link MousePaint}.
     *
     * @return The {@link MousePaint}.
     * Null if no {@link MousePaint} has been set.
     */
    public MousePaint getMousePaint() {
        return this.mousePaint;
    }

    /**
     * Gets the {@link Painter}'s status.
     *
     * @return The {@link Painter}'s status.
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Checks to see if the {@link Painter} is visible.
     *
     * @return True if the {@link Painter} is visible, false otherwise.
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Checks to see if the {@link PaintBlockManager} should draw its {@link PaintBlock}'s in a vertical stack.
     *
     * @return True if the {@link PaintBlockManager} should draw the {@link PaintBlock}'s in a vertical stack, false
     * otherwise.
     */
    public boolean shouldDrawVerticalStack() {
        return this.drawVerticalStack;
    }

    /**
     * Sets the version.
     *
     * @param version The version to be set.
     */
    public void setVersion(double version) {
        this.version = version;
    }

    /**
     * Sets the {@link PaintBlockManager}.
     *
     * @param paintBlockManager The {@link PaintBlockManager} to set.
     */
    public void setPaintBlockManager(PaintBlockManager paintBlockManager) {
        this.paintBlockManager = paintBlockManager;
    }

    /**
     * Sets the {@link PaintStringContainer}.
     *
     * @param paintStringContainer The {@link PaintStringContainer} to set.
     */
    public void setPaintStringContainer(PaintStringContainer paintStringContainer) {
        this.paintStringContainer = paintStringContainer;
    }

    /**
     * Sets the {@link MousePaint}.
     *
     * @param mousePaint The {@link MousePaint} to set.
     */
    public void setMousePaint(MousePaint mousePaint) {
        this.mousePaint = mousePaint;
    }

    /**
     * Sets the {@link Painter}'s status.
     *
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the visibility of the {@link Painter}.
     *
     * @param visible True to set the visibility of the {@link Painter} to be visible, false otherwise.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Sets the {@link PaintBlockManager} of the {@link Painter} to draw the {@link PaintBlock}'s (if there are any)
     * in a vertical stack.
     *
     * @param drawVerticalStack True to draw the {@link PaintBlock}'s in a vertical stack, false otherwise.
     */
    public void setShouldDrawVerticalStack(boolean drawVerticalStack) {
        this.drawVerticalStack = drawVerticalStack;
    }

    /**
     * Calls {@link #paint(Graphics)}, then paints the {@link PaintBlock}'s and {@link PaintString}'s inside their
     * respective containers onto the screen.
     *
     * To clarify, anything painted inside {@link #paint(Graphics)} will be painted under the {@link PaintBlock}'s and
     * {@link PaintString}'s (if there are any).
     *
     * Note: The {@link PaintBlock}'s and {@link PaintString}'s will not be painted if the {@link Painter} is not visible.
     *
     * @param g The {@link Graphics} to render with.
     */
    public void onPaint(Graphics g) {
        onPaint(g, PaintBlockManager.DEFAULT_STACK_POINT, 2);
    }

    /**
     * Calls {@link #paint(Graphics)}, then paints the {@link PaintBlock}'s and {@link PaintString}'s inside their
     * respective containers onto the screen.
     *
     * To clarify, anything painted inside {@link #paint(Graphics)} will be painted under the {@link PaintBlock}'s and
     * {@link PaintString}'s (if there are any).
     *
     * Note: The {@link PaintBlock}'s and {@link PaintString}'s will not be painted if the {@link Painter} is not visible.
     *
     * @param g          The {@link Graphics} to render with.
     * @param stackPoint The stack {@link Point} for the {@link PaintBlock}'s (if drawing in a vertical stack).
     * @param spacing    The vertical spacing between {@link PaintBlock}'s (if drawing in a vertical stack).
     */
    public void onPaint(Graphics g, Point stackPoint, int spacing) {
        Graphics2D g2 = GraphicsUtil.create2D(g);
        paint(g2);
        if (isVisible()) {
            if (shouldDrawVerticalStack()) {
                getPaintBlockManager().drawPaintBlocksStack(g2, stackPoint, spacing);
            } else {
                getPaintBlockManager().drawPaintBlocks(g2);
            }
        }
        getPaintStringContainer().paint(g2);
        g2.dispose();
    }

    /**
     * Paints the {@link MousePaint} onto the screen.
     *
     * @param g The {@link Graphics} to render with.
     */
    public void paintMouse(Graphics g) {
        if (getMousePaint() != null) {
            Graphics2D g2 = GraphicsUtil.create2D(g);
            getMousePaint().paint(g2);
        }
    }

    /**
     * This method should be used to paint anything on the screen.
     *
     * @param g The {@link Graphics} to render with.
     */
    public abstract void paint(Graphics g);

    /**
     * Notifies the {@link Painter} that a {@link MouseEvent} has been fired.
     *
     * @param event The {@link MouseEvent} that was fired.
     * @return The {@link OVERRIDE_RETURN} that will be sent to the {@link org.tribot.script.Script}.
     */
    public abstract OVERRIDE_RETURN notifyMouseEvent(MouseEvent event);

    /**
     * Notifies the {@link Painter} that a {@link KeyEvent} has been fired.
     *
     * @param event The {@link KeyEvent} that was fired.
     * @return The {@link OVERRIDE_RETURN} that will be sent to the {@link org.tribot.script.Script}.
     */
    public abstract OVERRIDE_RETURN notifyKeyEvent(KeyEvent event);
}
