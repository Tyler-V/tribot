package scripts.usa.api.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * The Images class contains helper methods for getting images along with other
 * utilities relating to images.
 *
 * @author Nolan
 */
public class Images {

    /**
     * Gets the image from the specified url.
     *
     * @param url
     *            The url to get the image from.
     * @return The image retrieved. Null if no image was found.
     */
    public static BufferedImage getImageFromUrl(String url) {
	try {
	    System.out.println("Reading BufferedImage from: " + url);
	    return ImageIO.read(new URL(url));
	} catch (IOException e) {
	    return null;
	}
    }

    /**
     * Gets the image icon from the specified url.
     *
     * @param url
     *            The url to get the image icon from.
     * @return The image icon retrieved. Null if no icon was found.
     */
    public static ImageIcon getIconFromUrl(String url) {
	try {
	    System.out.println("Loading ImageIcon from: " + url);
	    return new ImageIcon(new URL(url));
	} catch (MalformedURLException e) {
	    return null;
	}
    }

    /**
     * This class contains constants of URLs for images.
     */
    public class URLs {

	/**
	 * The URL for the legacy RuneScape 2 cursor.
	 */
	public static final String RUNESCAPE_CURSOR_URL = "http://i.imgur.com/1VimVim.png";

	/**
	 * The URL for the www.crimsgold.com advertisement image.
	 */
	public static final String CRIMSON_AD_URL = "http://i.imgur.com/I8tJxqI.gif";
    }
}
