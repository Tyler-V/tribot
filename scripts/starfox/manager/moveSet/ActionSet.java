package scripts.starfox.manager.moveSet;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import scripts.starfox.interfaces.ui.Listable;
import scripts.starfox.interfaces.ui.Validatable;
import scripts.starfox.manager.MuleUtil;

/**
 *
 * @author Spencer
 */
public class ActionSet implements Listable, Validatable, Externalizable {

    private static final long serialVersionUID = 1L;
    private ArrayList<Action> actions;
    private String name;
    private final long hashID;
    
    public ActionSet(long hashID) {
        this.actions = new ArrayList<>();
        this.hashID = hashID != 0 ? hashID : MuleUtil.generateHash();
    }

    public ActionSet() {
        this(0);
    }
    
    public long getHash() {
        return hashID;
    }

    public final ArrayList<Action> getActions() {
        return actions;
    }

    public final void removeActions(final Action... actions) {
        for (Action action : actions) {
            this.actions.remove(action);
        }
    }

    public final void addActions(final Action... actions) {
        this.actions.addAll(Arrays.asList(actions));
    }

    public final void addActions(List<Action> actions) {
        addActions(actions.toArray(new Action[actions.size()]));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean execute() {
        return Action.executeAll(actions);
    }

    @Override
    public String getListDisplay() {
        return (name != null ? name : "(no name)") + ": " + actions.size() + " actions";
    }

    @Override
    public String searchName() {
        return name;
    }

    @Override
    public String getPulldownDisplay() {
        return (name != null ? name : "(no name)") + ": " + actions.size();
    }

    @Override
    public boolean isValid() {
        for (Action action : actions) {
            if (!action.isValid()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActionSet other = (ActionSet) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeInt(actions.size());
        for (Action action : actions) {
            out.writeObject(action);
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        int size = in.readInt();
        actions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            actions.add((Action) in.readObject());
        }
    }
}
