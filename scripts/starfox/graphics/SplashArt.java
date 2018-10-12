package scripts.starfox.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;
import javax.swing.JWindow;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Images;

/**
 * The SplashArt class is used to display a splash art before an application starts.
 *
 * @author Nolan
 */
public class SplashArt extends JWindow {

    /**
     * The current instance of the splash art.
     */
    private static SplashArt instance;

    /**
     * The image which is will be splashed onto the screen.
     */
    private final Image image;

    /**
     * The point on the splash art at which to draw the version of the application.
     */
    private final Point version_point;

    /**
     * The version of the application.F
     */
    private final String version;

    /**
     * Constructs a new SplashArt.
     *
     * @param image the splash image.
     */
    private SplashArt(Image image, Point version_point, String version) {
        super(new JFrame());
        this.image = image;
        this.version_point = version_point;
        this.version = version;

        // Load the image
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        try {
            mt.waitForID(0);
        } catch (InterruptedException ie) {
        }

        if (mt.isErrorID(0)) {
            setSize(0, 0);
            System.err.println("Warning: SplashArt couldn't load splash image.");
            synchronized (this) {
                notifyAll();
            }
            return;
        }
        init();
    }

    /**
     * Initializes the image.
     */
    private void init() {
        int imgWidth = image.getWidth(this);
        int imgHeight = image.getHeight(this);
        setSize(imgWidth, imgHeight);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((dim.width - imgWidth) / 2, (dim.height - imgHeight) / 2);
    }

    /**
     * Splashes the specified image with a transparent background on the center of the screen.
     *
     * @param image         The image to splash.
     * @param version       The version of the application.
     * @param version_point The point on the splash art to draw the version.
     * @param timeout       The amount of time (in milliseconds) before the splash is disposed of.
     */
    public static void splash(final Image image, final String version, final Point version_point, long timeout) {
        if (instance == null && image != null) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        instance = new SplashArt(image, version_point, version);
                        instance.setBackground(new Color(255, 255, 255, 0));
                        instance.setVisible(true);
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                ex.printStackTrace();
            }

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            close();
        }
    }

    /**
     * Splashes the image at the specified url with a transparent background on the center of the screen.
     *
     * The version will be displayed in the bottom right hand corner of the splash art.
     *
     * @param url     The url of the iamge.
     * @param version The version of the image.
     * @param timeout The amount of time (int milliseconds) before the splash are is disposed of.
     */
    public static void splash(String url, String version, long timeout) {
        splash(Images.getImageFromUrl(url), version, null, timeout);
    }

    /**
     * Splashes the image at the specified url with a transparent background on the center of the screen.
     *
     * @param url     The url of the image.
     * @param timeout The amount of time (in milliseconds) before the splash art is disposed of.
     */
    public static void splash(String url, long timeout) {
        if (url != null) {
            splash(url, "", timeout);
        }
    }

    /**
     * Splashes the image at the specified url with a transparent background on the center of the screen.
     *
     * @param url           The url of the image.
     * @param version       The version of the application.
     * @param version_point The point on the splash art to draw the version.
     * @param timeout       The amount of time (in ms) before the splash is disposed of.
     */
    public static void splash(String url, double version, Point version_point, long timeout) {
        if (url != null) {
            splash(Images.getImageFromUrl(url), "" + version, version_point, timeout);
        }
    }

    /**
     * Checks to see if there is an image being splashed.
     *
     * @return True if an image is being splashed, false otherwise.
     */
    public static boolean isSplashing() {
        return instance != null;
    }

    /**
     * Disposes of the splash art and set's the current instance to be null.
     */
    public static void close() {
        if (instance != null) {
            instance.getOwner().dispose();
            instance = null;
        }
    }

    /**
     * Puts the current thread to sleep until the splash art is closed.
     */
    public static void waitForSplash() {
        while (isSplashing()) {
            Client.sleep(50);
        }
    }

    /**
     * Gets the point in which the version should be displayed at.
     *
     * @param g The graphics drawing the version.
     * @return The point in which the version should be displayed at.
     */
    private Point getPoint(Graphics2D g) {
        if (version_point == null) {
            Rectangle2D rect = g.getFontMetrics().getStringBounds(version, g);
            int xLoc = (int) (image.getWidth(this) - rect.getWidth() - 50);
            int yLoc = (int) (image.getHeight(this) - rect.getHeight() - 30);
            return new Point(xLoc, yLoc);
        } else {
            return version_point;
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g2) {
        Graphics2D g = GraphicsUtil.create2D(g2);
        g.drawImage(image, 0, 0, this);
        if (version_point == null) {
            return;
        }
        g.setColor(new Color(220, 220, 220));
        g.drawString(version, getPoint(g).x, getPoint(g).y);
    }
}
