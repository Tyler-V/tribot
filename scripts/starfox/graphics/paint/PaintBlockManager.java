package scripts.starfox.graphics.paint;

import scripts.starfox.graphics.components.PaintBlock;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The PaintBlockManager class provides and easy way to store and paint a group of paint blocks.
 *
 * @author Nolan
 */
public final class PaintBlockManager {

    /**
     * The default stack point for blocks if drawing in a vertical stack.
     */
    public static final Point DEFAULT_STACK_POINT = new Point(11, 348);

    /**
     * The list of paint blocks in the manager.
     */
    private final ArrayList<PaintBlock> paintBlocks;

    /**
     * Constructs a new PaintBlockManager.
     *
     * @param paintBlocks The paint blocks to add to the manager.
     */
    public PaintBlockManager(PaintBlock... paintBlocks) {
        this.paintBlocks = new ArrayList<>();
        this.paintBlocks.addAll(Arrays.asList(paintBlocks));
    }

    /**
     * Gets the list of paint blocks in the paint block manager.
     *
     * @return The list of paint blocks.
     */
    public final ArrayList<PaintBlock> getPaintBlocks() {
        return this.paintBlocks;
    }

    /**
     * Adds the specified paint block(s) to the paint block manager.
     *
     * @param paintBlocks The paint blocks to add.
     * @return True if the list of blocks changed as a result of this method call, false otherwise.
     */
    public final boolean addPaintBlocks(PaintBlock... paintBlocks) {
        return getPaintBlocks().addAll(Arrays.asList(paintBlocks));
    }

    /**
     * Removes the specified paint block from the manager.
     *
     * @param paintBlock The paint block to remove.
     * @return True if the list of blocks changed as a result of this method call, false otherwise.
     */
    public final boolean removePaintBlock(PaintBlock paintBlock) {
        return getPaintBlocks().remove(paintBlock);
    }

    /**
     * Draws the paint blocks in the paint block manager.
     *
     * @param g The graphics.
     */
    public final void drawPaintBlocks(Graphics2D g) {
        for (PaintBlock paintBlock : getPaintBlocks()) {
            paintBlock.paint(g);
        }
    }

    /**
     * Draws the paint blocks in a vertical stack at the default stack point (11, 348).
     *
     * @param g       The graphics.
     * @param spacing The vertical spacing between blocks in pixels.
     */
    public final void drawPaintBlocksStack(Graphics2D g, int spacing) {
        drawPaintBlocksStack(g, DEFAULT_STACK_POINT, spacing);
    }

    /**
     * Draws the paint blocks in a vertical stack.
     *
     * All of the paint blocks locations are ignored when this method is used to draw the paint blocks.
     *
     * @param g       The graphics
     * @param point   The location of the stack (upper left-hand corner).
     * @param spacing The vertical spacing between blocks in pixels.
     */
    public final void drawPaintBlocksStack(Graphics2D g, Point point, int spacing) {
        Point p = new Point(point.x, point.y);
        for (PaintBlock paintBlock : getPaintBlocks()) {
            paintBlock.setLocation(new Point(p.x, p.y));
            paintBlock.paint(g);
            p.move(p.x, p.y + paintBlock.getArea(g).height + spacing);
        }
    }
}
