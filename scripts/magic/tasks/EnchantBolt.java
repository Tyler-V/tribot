package scripts.magic.tasks;

import org.tribot.api.Clicking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSInterfaceMaster;
import scripts.magic.data.VarsM;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.Timer;
import scripts.starfox.api.waiting.Waiting;
import scripts.starfox.api2007.Clicking07;
import scripts.starfox.api2007.Interfaces07;
import scripts.starfox.api2007.Inventory07;
import scripts.starfox.api2007.Player07;
import scripts.starfox.api2007.skills.magic.Magic07;
import scripts.starfox.api2007.skills.magic.Spell;
import scripts.starfox.scriptframework.TerminateConditions;
import scripts.starfox.scriptframework.taskframework.Task;


/**
 * @author Nolan
 */
public class EnchantBolt
        extends Task {

    private final int BOLT_ENCHANT_MASTER_INDEX = 80;

    private final Spell spell;
    private final String boltName;

    /**
     * Constructs a new EnchantBolt task.
     *
     * @param spell    The spell being used.
     * @param boltName The name of the bolt being enchanted.
     */
    public EnchantBolt(Spell spell, String boltName) {
        this.spell = spell;
        this.boltName = boltName;
    }

    @Override
    public void loop() {
        if (!Interfaces07.isUp(Interfaces07.MAKE_SET_MASTER_INDEX)) {
            VarsM.get().setStatus("Opening bolt enchant menu");
            if (Magic07.selectSpell("Enchant Crossbow Bolt")) {
                Waiting.waitUntil(() -> Interfaces07.isUp(Interfaces07.MAKE_SET_MASTER_INDEX), 2000);
            }
        } else {
            AntiBan.sleep();
            int startCount = Inventory07.getCount(boltName);
            if (Clicking.click(Interfaces07.get(Interfaces07.MAKE_SET_MASTER_INDEX, Interfaces07.MAKE_SET_CHILD_INDEX))) {
                VarsM.get().setStatus("Enchanting bolts");
                Timer t = new Timer(2500);
                t.start();
                while (t.timeLeft() > 0) {
                    if (Inventory07.getCount(boltName) == startCount - 100) {
                        break;
                    }
                    if (Player07.isAnimating()) {
                        t.reset();
                    }
                    AntiBan.sleep();
                }
            }
//            RSInterfaceMaster master = Interfaces.get(BOLT_ENCHANT_MASTER_INDEX);
//            if (master != null) {
//                RSInterfaceChild[] children = master.getChildren();
//                if (children != null) {
//                    for (RSInterfaceChild child : children) {
//                        if (child != null) {
//                            RSInterfaceComponent[] components = child.getChildren();
//                            if (components != null && components.length > 0) {
//                                for (RSInterfaceComponent component : components) {
//                                    if (component != null) {
//                                        String text = component.getText();
//                                        if (text != null && !text.isEmpty() && (boltName.contains("Dragon") ? text.contains("Dragon") : boltName.contains(text))) {
//                                            VarsM.get().setStatus("Casting " + spell.getName());
//                                            if (Clicking07.click("Enchant Bolts", child)) {
//                                                AntiBan.sleep();
//                                                Magic07.hoverSpell("Enchant Crossbow Bolt");
//                                                Waiting.waitUntil(() -> !Interfaces07.isUp(BOLT_ENCHANT_MASTER_INDEX), 1500);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        }
    }

    @Override
    public void loadTerminateConditions() {
        addTerminateCondition(TerminateConditions.outOfRunes(spell));
        addTerminateCondition(TerminateConditions.countOfItem(boltName, 1, false));
    }
}
