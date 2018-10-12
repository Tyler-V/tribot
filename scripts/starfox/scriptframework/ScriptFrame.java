package scripts.starfox.scriptframework;

import scripts.starfox.api.util.PropertiesUtil;
import scripts.starfox.swing.SwingUtil;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Created by Nolan on 9/30/2015.
 */
public abstract class ScriptFrame
        extends JFrame {

    private final String directory;

    /**
     * Constructs a new ScriptFrame.
     *
     * @param clazz The class name of the script.
     */
    public ScriptFrame(String clazz) {
        //Set the directory
        this.directory = PropertiesUtil.directory() + clazz + "/";
        File f = new File(directory);
        //Check if the directory already exists, if not then create it
        if (!f.exists()) {
            f.mkdirs();
        }
        //Add a WindowListener that stops the script if the window is being closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
                    dispose();
                    Vars.get().getScriptKit().stopScript();
                }
            }
        });
    }

    /**
     * Gets the script's directory for saving and loading files.
     *
     * @return The script's directory.
     */
    public String getScriptDirectory() {
        return this.directory;
    }

    /**
     * Notifies the ScriptKit.
     */
    public void notifyScriptKit() {
        synchronized (Vars.get().getScriptKit()) {
            Vars.get().getScriptKit().notifyAll();
        }
    }

    /**
     * This method should be used to save settings.
     */
    public void load() {

    }

    /**
     * This method should be used to load settings.
     */
    public void save() {

    }
}
