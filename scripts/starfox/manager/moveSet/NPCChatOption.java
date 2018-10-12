package scripts.starfox.manager.moveSet;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import scripts.starfox.api2007.chatting.NPCChat07;

/**
 *
 * @author Spencer
 */
public class NPCChatOption extends Action {
    
    private static final long serialVersionUID = 1L;
    private String option;
    
    public NPCChatOption() {
        super(true);
    }
    
    public NPCChatOption(final String option, final boolean mustSucceed) {
        super(mustSucceed);
        this.option = option;
    }

    public String getOption() {
        return option;
    }
    
    @Override
    public boolean execute() {
        return NPCChat07.clickNPCOption(option);
    }
    
    @Override
    public String getListDisplay() {
        return "Click NPC chat option " + option + (mustSucceed ? " (Required)" : "");
    }

    @Override
    public String searchName() {
        return option;
    }

    @Override
    public String getPulldownDisplay() {
        return option + (mustSucceed ? " (Required)" : "");
    }
    
    @Override
    public boolean isValid() {
        return option != null;
    }

    @Override
    public String getName() {
        return "npc_chat_option";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(option);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        option = (String) in.readObject();
    }
}
