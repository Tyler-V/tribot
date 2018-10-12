package scripts.starfox.api2007;

import org.tribot.api2007.Camera;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.types.RSTile;

import java.awt.*;

/**
 * Created by nolan on 7/21/2017.
 */
public class Tiles {

    public static Point method() {
        RSTile tile = Player.getPosition();
        final RSTile local = tile.toLocalTile();
        RSTile animable = Player.getPosition().toAnimableTile();
        final int regionX = (local.getX() * 4 + 2) - animable.getX() / 32;
        final int regionY = (local.getY() * 4 + 2) - animable.getY() / 32;

        final int angle = /* cache.CAMERA_YAW + */Camera.getCameraRotation() & 0x7FF;
        final int j = regionX * regionX + regionY * regionY;

        if (j > 6000)
            return new Point(-1, -1);

        final int sin = Projection.SINE[angle];// * 256
        // / (cache.MINIMAP_SCALE + 256);
        final int cos = Projection.COSINE[angle];// * 256
        // / (cache.MINIMAP_SCALE + 256);

        final int x = regionY * sin + regionX * cos >> 16;
        final int y = regionY * cos - regionX * sin >> 16;

        return new Point(643 + x, 83 - y);
    }
}
