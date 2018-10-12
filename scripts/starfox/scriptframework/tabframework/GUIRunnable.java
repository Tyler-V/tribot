package scripts.starfox.scriptframework.tabframework;

import javax.swing.JFrame;

/**
 * @author Spencer
 * @param <E> The frame.
 */
public abstract class GUIRunnable<E extends JFrame> implements Runnable {

    private E gui;

    public void setGUI(E gui) {
        this.gui = gui;
    }

    public E getGUI() {
        return gui;
    }
}
