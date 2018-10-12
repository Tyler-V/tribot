package scripts.starfox.manager.moveSet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.tribot.api.General;

/**
 *
 * @author Spencer
 */
public class Wait extends Action {

    private static final long serialVersionUID = 1L;
    private long timeout;

    public Wait() {
        super(true);
    }

    public Wait(final long timeout) {
        super(false);
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public boolean execute() {
        General.sleep(timeout);
        return true;
    }

    @Override
    public String getListDisplay() {
        return "Wait for " + timeout + "ms";
    }

    @Override
    public String searchName() {
        return "wait" + timeout;
    }

    @Override
    public String getPulldownDisplay() {
        return "Wait " + timeout + "ms";
    }

    @Override
    public boolean isValid() {
        return timeout > 0;
    }

    @Override
    public String getName() {
        return "wait";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(timeout);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        timeout = in.readLong();
    }
}
