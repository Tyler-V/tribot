package scripts.starfox.interfaces;

/**
 * @author Nolan
 */
public interface Blockable {

    /**
     * Should be used to block until certain conditions are met.
     *
     * @return True if the block should continue, false otherwise.
     */
    public boolean block();
}
