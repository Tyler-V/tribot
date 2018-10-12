package scripts.starfox.api2007.walking;

import org.tribot.api2007.types.RSTile;
import scripts.starfox.api2007.entities.Entities;
import scripts.starfox.interfaces.ui.Listable;

/**
 * @author TacoManStan
 */
public class Tile07
        extends RSTile
        implements Listable {

    public Tile07(int x, int y, int plane) {
        super(x, y, plane);
    }

    @Override
    public String getListDisplay() {
        return Entities.tileToString(this);
    }

    @Override
    public String searchName() {
        return getListDisplay();
    }

    @Override
    public String getPulldownDisplay() {
        return getListDisplay();
    }
    
    public static Tile07 fromRSTile(RSTile tile) {
        return tile != null ? new Tile07(tile.getX(), tile.getY(), tile.getPlane()) : null;
    }
}
