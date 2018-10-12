package scripts.starfox.manager;

import java.io.File;
import scripts.starfox.api.util.FileUtil;

/**
 * @author Starfox
 */
public class MuleFiles {

    /**
     * Returns the xml file that is used to save the following save file.
     *
     * @param fileName The name of the file.
     * @param extension The extension of the file.
     * @return The xml file that is used to save the following save file.
     */
    public static File getSaveFile(String fileName, String extension) {
        String[] path = {"Sigma Mule", "Sigma Save Data"};
        if (FileUtil.createFile(false, fileName, extension, path)) {
            return FileUtil.getFile(true, fileName, extension, path);
        } else {
            return null;
        }
    }

    /**
     * Returns the xml file that is used to save all of the items.
     *
     * @return the xml file that is used to save all of the items.
     */
    public static File getItemSaveFile() {
        return getSaveFile("Items", "xml");
    }

    /**
     * Returns the xml file that is used to save all of the conditions.
     *
     * @return the xml file that is used to save all of the conditions.
     */
    public static File getConditionSaveFile() {
        return getSaveFile("Conditions", "xml");
    }

    /**
     * Returns the xml file that is used to save all of the orders.
     *
     * @return the xml file that is used to save all of the orders.
     */
    public static File getOrderSaveFile() {
        return getSaveFile("Orders", "xml");
    }

    /**
     * Returns the xml file that is used to save all of the orders.
     *
     * @return the xml file that is used to save all of the orders.
     */
    public static File getMovementSaveFile() {
        return getSaveFile("Movements", "xml");
    }

    /**
     * Returns the xml file that is used to save all of the action sets.
     *
     * @return the xml file that is used to save all of the action sets.
     */
    public static File getActionSetSaveFile() {
        return getSaveFile("Action Sets", "xml");
    }

    /**
     * Returns the xml file that is used to save all of the order profiles.
     *
     * @param name The name of the order profile.
     * @return the xml file that is used to save all of the order profiles.
     */
    public static File getOrderProfileSaveFile(String name) {
        return getSaveFile("op_" + name, "opSAVE");
    }
}
