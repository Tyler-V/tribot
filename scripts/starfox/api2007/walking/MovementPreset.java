package scripts.starfox.api2007.walking;

import java.util.ArrayList;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.types.RSArea;
import scripts.starfox.interfaces.ui.Listable;
import scripts.starfox.interfaces.ui.Validatable;
import scripts.starfox.manager.moveSet.ActionSet;

/**
 * @author TacoManStan
 */
public class MovementPreset
        implements Listable, Validatable {

    private String name;
    private int plane;
    private ArrayList<Tile07> area;
    private ActionSet actionSet;
    private boolean isGetOut;
    private TeleportOption teleport;
    private boolean teleportEnabled;

    public MovementPreset() {
        area = new ArrayList<>();
        name = "";
        teleport = TeleportOption.VARROCK_TAB;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final ArrayList<Tile07> getPath() {
        return area;
    }

    public final Tile07[] getFormattedPath() {
        return area.toArray(new Tile07[area.size()]);
    }

    public final void setPath(ArrayList<Tile07> path) {
        this.area = path;
    }

    public final boolean addPathPoint(Tile07 point) {
        if (!area.contains(point)) {
            area.add(point);
            return true;
        } else {
            return false;
        }
    }

    public final void clearPath() {
        area.clear();
    }

    public final boolean removePathPoint(Tile07 point) {
        return area.remove(point);
    }

    public final TeleportOption getTeleport() {
        return teleport;
    }

    public final void setTeleport(TeleportOption teleport) {
        this.teleport = teleport;
    }

    public final boolean isTeleportEnabled() {
        return teleportEnabled;
    }

    public final void setTeleportEnabled(boolean teleportEnabled) {
        this.teleportEnabled = teleportEnabled;
    }

    public ActionSet getActionSet() {
        return actionSet;
    }

    public void setActionSet(ActionSet actionSet) {
        this.actionSet = actionSet;
    }

    public boolean isGetOut() {
        return isGetOut;
    }

    public void setIsGetOut(boolean isGetOut) {
        this.isGetOut = isGetOut;
    }

    public RSArea getArea() {
        return new RSArea(getFormattedPath());
    }

    public int getPlane() {
        return plane;
    }

    public void setPlane(int plane) {
        this.plane = plane;
    }

    public final boolean contains(Positionable entity) {
        return entity != null && getArea() != null && getArea().contains(entity);
    }

    @Override
    public boolean isValid() {
        return name != null && !name.isEmpty() && (teleport != null || teleportEnabled == false);
    }

    public boolean execute() {
        teleport.teleport();
        return (actionSet != null && actionSet.execute());
    }

    @Override
    public String getListDisplay() {
        String message;
        String formattedName = name;
        if (formattedName.isEmpty()) {
            formattedName = "(No Name)";
        }
        if (teleportEnabled) {
            message = "Teleporting to " + teleport.getFullName();
        } else {
            message = "Teleporting Disabled";
        }
        return formattedName + ": " + message;
    }

    @Override
    public String searchName() {
        return name;
    }

    @Override
    public String getPulldownDisplay() {
        return getListDisplay();
    }
}
