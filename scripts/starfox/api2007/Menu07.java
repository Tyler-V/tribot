package scripts.starfox.api2007;

import java.awt.Rectangle;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.ChooseOption;

/**
 * @author Nolan
 */
public class Menu07 {

    /**
     * Gets the area of the specified option in the chose option menu.
     *
     * @param option The option.
     * @return The area.
     *         Null if the menu is not open or the menu does not contain the specified option.
     */
    public static Rectangle getArea(String option) {
        String[] options = ChooseOption.getOptions();
        if (option == null || !ChooseOption.isOpen()) {
            return null;
        }
        Rectangle area = ChooseOption.getArea();
        if (area == null) {
            return null;
        }
        int index = -1;
        for (int i = 0; i < options.length; i++) {
            if (options[i].contains(option)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return null;
        }
        int y = area.y + 19 + (index * 15);
        return new Rectangle(area.x, y, area.width, 15);
    }

    /**
     * Selects the specified option in the choose option menu quickly.
     *
     * @param option The option to select.
     * @return True if the option was selected successfully, false otherwise.
     *         Returns false if the menu is not option or the menu does not contain the specified option.
     */
    public static boolean selectOptionQuick(String option) {
        Rectangle r = getArea(option);
        if (r == null) {
            return false;
        }
        Mouse07.hopClick(Screen07.getRandomPoint(r), 1, 25);
        return Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                return !ChooseOption.isOpen();
            }
        }, 500);
    }
}
