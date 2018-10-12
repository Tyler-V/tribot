package scripts.starfox.scriptframework.tabframework;

import javax.swing.JFrame;

/**
 *
 * @author Spencer
 */
public class SGUI extends JFrame {

    private final Object lock;

    public SGUI() {
        lock = new Object();
    }
    
    public final Object getLock() {
        return lock;
    }
}
