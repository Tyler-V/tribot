package scripts.starfox.api2007;

import org.tribot.api.General;
import org.tribot.api.Screen;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.Screenshots;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * The Screen07 class provides methods related to the game screen.
 *
 * @author Nolan
 */
public class Screen07 {

    /**
     * Gets all of the points on the screen.
     *
     * @return All of the points on the screen.
     */
    public static Point[] getAllPoints() {
        List<Point> points = new ArrayList<>();
        Dimension dim = Screen.getDimension();
        for (int x = 0; x < dim.width; x++) {
            for (int y = 0; y < dim.height; y++) {
                points.add(new Point(x, y));
            }
        }
        return points.toArray(new Point[points.size()]);
    }

    /**
     * Gets all of the points contained within the specified shape.
     *
     * @param shape The shape.
     * @return The points in the shape.
     */
    public static Point[] getPoints(Shape shape) {
        if (shape == null) {
            return new Point[0];
        }
        List<Point> points = new ArrayList<>();
        for (Point p : getAllPoints()) {
            if (shape.contains(p)) {
                points.add(p);
            }
        }
        return points.toArray(new Point[points.size()]);
    }

    /**
     * Gets a random point inside the specified shape.
     *
     * @param shape The shape.
     * @return A random point.
     */
    public static Point getRandomPoint(Shape shape) {
        if (shape == null) {
            return new Point();
        }
        Point[] points = getPoints(shape);
        if (points.length == 0) {
            return new Point();
        }
        return points[General.random(0, points.length - 1)];
    }

    /**
     * Gets the amount pixels in the specified shape that match the specified color.
     *
     * @param color     The color.
     * @param shape     The shape.
     * @param threshold The amount of rgb values that the color can be off by (+/-) for each value.
     * @return The number of matching pixels.
     */
    public static int getMatchingPixelCount(Color color, Shape shape, int threshold) {
        int matchingPixelCount = 0;
        for (Point p : getPoints(shape)) {
            if (threshold == 0) {
                if (Screen.getColourAt(p).equals(color)) {
                    matchingPixelCount++;
                }
            } else {
                Color pixelColor = Screen.getColourAt(p);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                int pixelRed = pixelColor.getRed();
                int pixelGreen = pixelColor.getGreen();
                int pixelBlue = pixelColor.getBlue();
                if (Math.abs(red - pixelRed) <= threshold && Math.abs(green - pixelGreen) <= threshold && Math.abs(blue - pixelBlue) <= threshold) {
                    matchingPixelCount++;
                }
            }
        }
        return matchingPixelCount;
    }

    /**
     * Gets the an image of the screen in its current state.
     *
     * @return An image of the screen.
     */
    public static BufferedImage getGameImage() {
        return getGameImage(null);
    }

    /**
     * Gets the game image.
     *
     * @param mouseImage The mouse paint. Provide null if you want the default (small white square) mouse.
     * @return A BufferedImage representing the screen.
     */
    public static BufferedImage getGameImage(BufferedImage mouseImage) {
        return getGameImage(null, mouseImage, null);
    }

    /**
     * Gets the game image.
     *
     * @param gameImage     The game image (null for current image).
     * @param mouseImage    The mouse image (null for default).
     * @param mousePosition The mouse position (null for default).
     * @return The game image.
     */
    public static BufferedImage getGameImage(BufferedImage gameImage, BufferedImage mouseImage, Point mousePosition) {
        BufferedImage image1 = gameImage == null ? Screenshots.getScreenshotImage() : gameImage;
        BufferedImage image2 = new BufferedImage(image1.getWidth(), image1.getHeight(), image1.getType());
        Point mousePos = mousePosition == null ? Mouse.getPos() : mousePosition;
        Graphics2D g2 = image2.createGraphics();
        g2.drawImage(image1, 0, 0, null);
        if (mouseImage == null) {
            g2.drawRect(mousePos.x - 3, mousePos.y - 3, 6, 6);
        } else {
            g2.drawImage(mouseImage, 0, 0, null);
        }
        return image2;
    }
}
