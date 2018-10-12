package scripts.starfox.graphics.mouse.types;

import scripts.starfox.api.util.Images;
import scripts.starfox.graphics.GraphicsUtil;
import scripts.starfox.graphics.mouse.MousePaint;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nolan on 10/8/2015.
 */
public class ImageMouse extends MousePaint {

    private final ImageIcon imageIcon;

    /**
     * Constructs a new ImageMouse.
     *
     * @param imageUrl
     *            The URL location of the image being used.
     */
    public ImageMouse(String imageUrl) {
	this(Images.getIconFromUrl(imageUrl));
    }

    /**
     * Constructs a new ImageMouse.
     *
     * @param imageIcon
     *            The image being used.
     */
    public ImageMouse(ImageIcon imageIcon) {
	this.imageIcon = imageIcon;
    }

    /**
     * Gets the image.
     *
     * @return The image.
     */
    public ImageIcon getImageIcon() {
	return this.imageIcon;
    }

    @Override
    public void paint(Graphics g) {
	if (getImageIcon() != null) {
	    Graphics2D g2 = GraphicsUtil.create2D(g);
	    g2.drawImage(getImageIcon().getImage(), getLocation().x, getLocation().y, null);
	    g2.dispose();
	}
    }
}
