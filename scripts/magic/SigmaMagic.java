package scripts.magic;

import org.tribot.api2007.types.RSModel;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.*;
import scripts.magic.data.Pie;
import scripts.magic.data.Plank;
import scripts.magic.data.SuperheatBar;
import scripts.magic.data.VarsM;
import scripts.magic.guis.SigmaMagicGui;
import scripts.magic.listeners.MagicEXPListener;
import scripts.magic.paint.MagicPainter;
import scripts.magic.tasks.*;
import scripts.starfox.api.AntiBan;
import scripts.starfox.api.Client;
import scripts.starfox.api2007.skills.magic.books.LunarSpell;
import scripts.starfox.api2007.skills.magic.books.NormalSpell;
import scripts.starfox.scriptframework.ScriptKit;
import scripts.starfox.scriptframework.Vars;
import scripts.starfox.scriptframework.taskframework.Task;
import scripts.starfox.scriptframework.taskframework.TaskManager;

@ScriptManifest(
        name = "Sigma Magic",
        authors = "Starfox",
        category = "Magic",
        version = 4.32)
public class SigmaMagic
        extends ScriptKit
        implements Arguments, Breaking, Painting, Pausing, MousePainting, MouseSplinePainting, EventBlockingOverride, Ending {

    //PRIVATE-KEY 8E5181A386A6937F
    //VECTOR      83D7BD63E711EFD4

    /**
     * Constructs a new {@link SigmaMagic}.
     */
    public SigmaMagic() {
        super(new MagicPainter(new MagicEXPListener()), new SigmaMagicGui());
    }

    @Override
    public void onScriptStart() {

    }

    @Override
    public void processArguments(String arguments) {
        println("Using script arguments [" + arguments + "]");
        //Split the arguments to parse the spell
        String[] parts = arguments.split(":");
        //Parse the spell
        NormalSpell normalSpell = NormalSpell.forName(parts[0]);
        LunarSpell lunarSpell = LunarSpell.forName(parts[0]);
        Task t = null;
        //Check if it is a normal or lunar spell
        if (normalSpell != null) {
            if (normalSpell == NormalSpell.CURSE_ALCHEMY) {
                t = new CurseAlchemy(normalSpell, NormalSpell.forName(parts[1]), parts[2], parts[3]);
            } else {
                //Check what type the spell is
                switch (normalSpell.getType()) {
                    case ALCHEMY:
                        t = new Alchemy(normalSpell, parts[1]);
                        break;
                    case BONES_TO_FRUIT:
                        t = new BonesToFruit(normalSpell, parts[1], 0);
                        break;
                    case CURSE:
                        t = new Curse(normalSpell, parts[1]);
                        break;
                    case ENCHANTMENT:
                        t = new Enchant(normalSpell, parts[1]);
                        break;
                    case BOLT_ENCHANTMENT:
                        t = new EnchantBolt(normalSpell, parts[1]);
                        break;
                    case SUPERHEAT_ITEM:
                        t = new Superheat(SuperheatBar.forName(parts[1]));
                        break;
                    case TELEPORT:
                        t = new Teleport(normalSpell);
                        break;
                }
            }
            //it is a lunar spell
        } else if (lunarSpell != null) {
            //Check what type the spell is
            switch (lunarSpell) {
                case BAKE_PIE:
                    t = new BakePie(Pie.forName(parts[1]));
                    break;
                case HUMIDIFY:
                    t = new Humidify(parts[1]);
                    break;
                case PLANK_MAKE:
                    t = new PlankMake(Plank.forName(parts[1]));
                    break;
                case STRING_JEWELRY:
                    t = new StringJewelry(parts[1]);
                    break;
                case SUPERGLASS_MAKE:
                    t = new SuperglassMake(parts[1]);
                    break;
            }
        }
        //Set the task so the script can start
        TaskManager.setTask(t);
    }

    @Override
    protected Vars loadVars() {
        return new VarsM(this);
    }

    @Override
    public void runScript() {
        //Continue running while the task manager should not terminate
        while (!TaskManager.shouldTerminate()) {
            //Run the task loop
            TaskManager.loop();
            //Perform anti-ban actions if they are ready
            AntiBan.timedActions();
            //Sleep between loops
            AntiBan.sleep();
        }
    }


    @Override
    public void onScriptEnd() {
        //Thank our sexy users for buying our script and giving us their money.
        println("Thank you " + Client.getUsername() + " for using Sigma Magic.");
    }
}
