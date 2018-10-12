package scripts.starfox.manager.moveSet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import scripts.starfox.interfaces.ui.Listable;
import scripts.starfox.interfaces.ui.Validatable;

/**
 *
 * @author Spencer
 */
public abstract class Action implements Listable, Validatable, Externalizable {

    private static final long serialVersionUID = 1L;
    protected boolean mustSucceed;

    public Action(final boolean mustSucceed) {
        this.mustSucceed = mustSucceed;
    }

    public static boolean executeAll(Action... actions) {
        for (Action action : actions) {
            int i = 3;
            while (i != 0 && !action.execute() && action.mustSucceed) {
                i--;
            }
            if (i == 0) {
                return false;
            }
        }
        return true;
    }

    protected static boolean executeAll(List<Action> actions) {
        return executeAll(actions.toArray(new Action[actions.size()]));
    }

    public abstract boolean execute();

    public abstract String getName();

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(mustSucceed);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        mustSucceed = in.readBoolean();
    }
}
