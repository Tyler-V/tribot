package scripts.starfox.api2007;

import org.tribot.api2007.Game;

/**
 * Created by Nolan on 10/9/2015.
 */
public class VarBits {

    /**
     * Gets the value at the specified VarBit index.
     *
     * @param index The index to get the value of.
     * @return The value of the VarBit.
     */
    public static int get(int index) {
        return Game.getVarBit(index);
    }
}
