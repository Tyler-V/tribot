package scripts.starfox.manager.moveSet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import scripts.starfox.api2007.chatting.NPCChat07;
import scripts.starfox.interfaces.ui.Listable;

/**
 *
 * @author Spencer
 */
public class NPCContinue extends Action implements Listable {

    private static final long serialVersionUID = 1L;
    private String messageReq;
    
    public NPCContinue() {
        super(true);
    }
    
    public NPCContinue(final String messageReq, final boolean mustSucceed) {
        super(mustSucceed);
        this.messageReq = messageReq;
    }

    public String getMessageReq() {
        return messageReq;
    }
    
    @Override
    public boolean execute() {
        return NPCChat07.clickContinue(messageReq == null || messageReq.isEmpty() ? null : messageReq);
    }
    
    @Override
    public String getListDisplay() {
        return "Click continue with req: " + messageReq + (mustSucceed ? " (Required)" : "");
    }

    @Override
    public String searchName() {
        return "continue" + messageReq + (mustSucceed ? " (Required)" : "");
    }

    @Override
    public String getPulldownDisplay() {
        return "NPC Cont: " + messageReq;
    }
    
    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String getName() {
        return "click_continue";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(messageReq);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        messageReq = (String) in.readObject();
    }
}
