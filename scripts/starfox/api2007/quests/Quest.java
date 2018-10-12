package scripts.starfox.api2007.quests;

import org.tribot.api2007.types.RSInterfaceChild;

/**
 * @author Nolan
 */
public class Quest {
    
    private static final int COMPLETE_COLOR = 65280;

    private final String name;
    private final boolean members;
    private final RSInterfaceChild questInterface;

    Quest(RSInterfaceChild questInterface, boolean members) {
        this.name = questInterface.getText();
        this.questInterface = questInterface;
        this.members = members;
    }
    
    public String getName() {
        return name;
    }

    public boolean isMembers() {
        return members;
    }

    public boolean isCompleted() {
        return questInterface.getTextColour() == COMPLETE_COLOR;
    }

    public RSInterfaceChild getInterface() {
        return questInterface;
    }

    @Override
    public String toString() {
        return String.format("Name: %s - Members: %b - Completed: %b", name, members, isCompleted());
    }
}
