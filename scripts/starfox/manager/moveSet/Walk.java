package scripts.starfox.manager.moveSet;

import java.awt.Point;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.tribot.api2007.types.RSTile;
import scripts.starfox.api2007.walking.Walking07;

/**
 *
 * @author Spencer
 */
public class Walk extends Action {

    private static final long serialVersionUID = 1L;
    private Point target;
    
    public Walk() {
        super(true);
    }
    
    public Walk(final Point target, final boolean mustSucceed) {
        super(mustSucceed);
        this.target = target;
    }

    public Point getTarget() {
        return target;
    }
    
    @Override
    public boolean execute() {
        return Walking07.aStarWalk(new RSTile(target.x, target.y));
    }

    @Override
    public String getListDisplay() {
        return "Walk to point [" + target.x + ", " + target.y + "]" + (mustSucceed ? " (Required)" : "");
    }

    @Override
    public String searchName() {
        return "tiletargetpoint" + target.x + target.y;
    }

    @Override
    public String getPulldownDisplay() {
        return "Walk [" + target.x + ", " + target.y + "]" + (mustSucceed ? " (Required)" : "");
    }
    
    @Override
    public boolean isValid() {
        return target != null && target.x > 0 && target.y > 0;
    }

    @Override
    public String getName() {
        return "walk";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(target.x);
        out.writeInt(target.y);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        int x = in.readInt();
        int y = in.readInt();
        target = new Point(x, y);
    }
}
