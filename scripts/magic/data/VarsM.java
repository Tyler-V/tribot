package scripts.magic.data;

import scripts.starfox.scriptframework.ScriptKit;
import scripts.starfox.scriptframework.Vars;

/**
 * Created by Nolan on 10/14/2015.
 */
public class VarsM
        extends Vars {

    //The amount of times the script has failed to withdraw an item. Used to determine whether or not the script should terminate.
    public int failedAttempts = 0;

    /**
     * Constructs a new Vars.
     *
     * @param scriptKit The ScriptKit being used.
     */
    public VarsM(ScriptKit scriptKit) {
        super(scriptKit);
    }

    public static VarsM get() {
        return (VarsM) Vars.get();
    }
}
