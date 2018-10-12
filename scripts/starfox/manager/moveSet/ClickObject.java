package scripts.starfox.manager.moveSet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.tribot.api.Clicking;
import scripts.starfox.api2007.entities.Objects07;
import scripts.starfox.interfaces.ui.Listable;

/**
 *
 * @author Spencer
 */
public class ClickObject extends Action implements Listable {

    private static final long serialVersionUID = 1L;
    private int id;
    private String option;
    
    public ClickObject() {
        super(true);
    }

    public ClickObject(final int id, final String option, final boolean mustSucceed) {
        super(mustSucceed);
        this.id = id;
        this.option = option;
    }

    public int getId() {
        return id;
    }

    public String getOption() {
        return option;
    }

    @Override
    public boolean execute() {
        return Clicking.click(option, Objects07.getObject(id, 25));
    }
    
    @Override
    public String getListDisplay() {
        return "Click object with id " + id + " and option " + option + (mustSucceed ? " (Required)" : "");
    }

    @Override
    public String searchName() {
        return option + id;
    }

    @Override
    public String getPulldownDisplay() {
        return option + ": " + id + (mustSucceed ? " (Required)" : "");
    }
    
    @Override
    public boolean isValid() {
        return option != null && !option.isEmpty() && id > 0;
    }

    @Override
    public String getName() {
        return "click_object";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(id);
        out.writeObject(option);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        id = in.readInt();
        option = (String) in.readObject();
    }
}
