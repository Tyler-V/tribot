package scripts.starfox.api2007.quests;

import java.util.ArrayList;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceMaster;

/**
 * @author Nolan
 */
public class Quests {
    
    private static final int PARENT = 274;
    private static final int LIST_START = 14;

    public static Quest[] getAll() {
        RSInterfaceMaster masterInterface = Interfaces.get(PARENT);
        if (masterInterface == null)  {
            TABS.QUESTS.open();
            if (masterInterface == null) return new Quest[0];
        }
        RSInterfaceChild[] questInterfaces = masterInterface.getChildren();
        ArrayList<Quest> quests = new ArrayList<>();

        if (questInterfaces != null) {
            boolean member = false;
            for (int i = LIST_START; i < questInterfaces.length; i++) {
                if (questInterfaces[i].getText().equals("Members' Quests")) {
                    member = true;
                    continue;
                }
                quests.add(new Quest(questInterfaces[i], member));
            }
        }
        return quests.toArray(new Quest[quests.size()]);
    }

    public static Quest[] getAll(boolean completed) {
        final ArrayList<Quest> quests = new ArrayList<>();
        for (Quest quest : getAll()) {
            if (quest.isCompleted() == completed) {
                quests.add(quest);
            }
        }
        return quests.toArray(new Quest[quests.size()]);
    }

    public static Quest getQuest(String name) {
        for (Quest quest : getAll()) {
            if (quest.getName().equalsIgnoreCase(name))
                return quest;
        }
        return null;
    }

    public static boolean isCompleted(String name) {
        Quest quest = getQuest(name);
        if (quest == null)
            throw new IllegalArgumentException("No quest found with name: " + name);
        return quest.isCompleted();
    }
}
