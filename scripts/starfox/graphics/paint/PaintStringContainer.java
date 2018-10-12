package scripts.starfox.graphics.paint;

import scripts.starfox.graphics.components.PaintString;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Nolan
 */
public class PaintStringContainer {

    private final ArrayList<PaintString> paintStrings;

    /**
     * Constructs a new PaintStringContainer.
     *
     * @param paintStrings The paint strings (if any) to add to the container.
     */
    public PaintStringContainer(PaintString... paintStrings) {
        this.paintStrings = new ArrayList<>();
        Collections.addAll(this.paintStrings, paintStrings);
    }

    /**
     * Constructs a new PaintStringContainer.
     *
     * @param paintStrings The list of paint strings to add to the container.
     */
    public PaintStringContainer(ArrayList<PaintString> paintStrings) {
        this(paintStrings.toArray(new PaintString[0]));
    }

    /**
     * Gets the list of paint strings in the container.
     *
     * @return The list of paint strings.
     */
    public List<PaintString> getPaintStrings() {
        return paintStrings;
    }

    /**
     * Adds the specified paint string(s) to the container.
     *
     * @param paintStrings The paint string(s) to add.
     * @return True if the addition was successful, false otherwise.
     */
    public boolean addPaintStrings(PaintString... paintStrings) {
        return Collections.addAll(getPaintStrings(), paintStrings);
    }

    /**
     * Paints all of the paint strings in the container.
     *
     * @param g The graphics to render with.
     */
    public void paint(Graphics g) {
        for (PaintString paintString : getPaintStrings()) {
            paintString.draw(g);
        }
    }
}
