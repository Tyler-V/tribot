package scripts.starfox.graphics.mouse.types;

import scripts.starfox.api.util.Timer;
import scripts.starfox.graphics.mouse.MousePaint;
import scripts.starfox.graphics.shapes.ProgressArc;
import scripts.starfox.graphics.shapes.SpinningArc;
import scripts.starfox.graphics.trails.BasicTrail;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

/**
 * @author Nolan
 */
public class SigmaMouse
        extends MousePaint {

    private final SpinningArc sarc;
    private final ProgressArc parc;
    private final Ellipse2D.Double oval;
    private final BasicTrail trail;
    private final Color color;
    private final Timer clickTimer;
    private final int size;

    /**
     * Constructs a new SigmaMouse.
     *
     * @param size  The size of the mouse.
     * @param speed The speed of the rotations.
     * @param color The color of the mouse.
     */
    public SigmaMouse(int size, double speed, Color color) {
        super();
        this.sarc = new SpinningArc(0, 0, size * 5, size * 5, 360, 300, Arc2D.OPEN, speed);
        this.parc = new ProgressArc(0, 0, size * 3, size * 3, 0, 0, Arc2D.OPEN, speed);
        this.oval = new Ellipse2D.Double(0, 0, size, size);
        this.trail = new BasicTrail(1250, color);
        this.color = color;
        this.clickTimer = new Timer(250);
        this.clickTimer.start();
        this.size = size;
        this.sarc.setReverse(true);
    }

    /**
     * Constructs a new SigmaMouse with default values for size and speed.
     *
     * The default values are:
     * Size: 6
     * Speed: .0175
     * @param color The color of the mouse.
     */
    public SigmaMouse(Color color) {
        this(6, .0175, color);
    }

    @Override
    public void paint(Graphics gr) {
        Point m = getLocation();
        Color rColor = new Color(color.getRGB());
        if (!clickTimer.timedOut()) {
            double timeLeft = (double) clickTimer.timeLeft();
            double timeout = (double) clickTimer.getTimeOut();
            double percent = timeLeft / timeout;
            int r = (int) ((percent * (255 - color.getRed())) + color.getRed());
            int g = (int) ((percent * (255 - color.getGreen())) + color.getGreen());
            int b = (int) ((percent * (255 - color.getBlue())) + color.getBlue());
            rColor = new Color(r, g, b, color.getAlpha());

            int size1 = (int) (percent * ((size * 5) / 4)) + size * 5;
            int size2 = (int) (percent * ((size * 3) / 4)) + size * 3;
            sarc.height = size1;
            sarc.width = size1;
            parc.height = size2;
            parc.width = size2;
        } else {
            sarc.height = size * 5;
            sarc.width = size * 5;
            parc.height = size * 3;
            parc.width = size * 3;
        }
        sarc.setLocation((int) (m.x - sarc.width / 2), (int) (m.y - sarc.height / 2));
        parc.setLocation((int) (m.x - parc.width / 2), (int) (m.y - parc.height / 2));
        oval.x = (int) (m.x - oval.width / 2);
        oval.y = (int) (m.y - oval.height / 2);
        trail.paint(gr);
        ((Graphics2D) gr).setStroke(new BasicStroke(3));
        gr.setColor(rColor);
        ((Graphics2D) gr).fill(oval);
        parc.paint(gr);
        gr.setColor(rColor.brighter());
        sarc.paint(gr);
    }

    @Override
    public void mouseReleased(Point arg0, int arg1, boolean arg2) {
        clickTimer.reset();
    }
}
