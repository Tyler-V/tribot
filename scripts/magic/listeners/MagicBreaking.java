package scripts.magic.listeners;

import org.tribot.script.interfaces.Breaking;
import scripts.starfox.api2007.Mouse07;

/**
 * @author Nolan
 */
public class MagicBreaking
        implements Breaking {

    @Override
    public void onBreakEnd() {
    }

    @Override
    public void onBreakStart(long break_time) {
        Mouse07.fixSelected();
    }
}
