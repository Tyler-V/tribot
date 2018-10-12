package scripts.starfox.manager.transfers;

import java.awt.event.InputEvent;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author Starfox
 */
public class TransferEvent
        implements Externalizable {

    private static final long serialVersionUID = 1L;
    private InputEvent event;
    private TransferEventType type;
    
    public TransferEvent() {}

    public TransferEvent(InputEvent event, TransferEventType type) {
        this.event = event;
        this.type = type;
    }

    public InputEvent getEvent() {
        return event;
    }

    public TransferEventType getType() {
        return type;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(event);
        out.writeObject(type);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        event = (InputEvent) in.readObject();
        type = (TransferEventType) in.readObject();
    }
}
