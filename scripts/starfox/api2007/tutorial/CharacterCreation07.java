package scripts.starfox.api2007.tutorial;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import scripts.starfox.api.Client;

/**
 *
 * @author Spencer
 */
public class CharacterCreation07 {

    private static final int base;
    private static final Point[] optionButtons;
    private static final int acceptButton;
    private static final Point genderButton;

    static {
        base = 269;
        optionButtons = new Point[]{new Point(106, 113), new Point(107, 114), new Point(108, 115), new Point(109, 116), new Point(110, 117), new Point(111, 118), new Point(112, 119),
            new Point(105, 121), new Point(123, 127), new Point(122, 129), new Point(124, 130), new Point(125, 131)};
        acceptButton = 99;
        genderButton = new Point(138, 139);
    }

    private int patience;
    private final int total;
    private final int sleepDelay;

    public CharacterCreation07() {
        total = 1000;
        sleepDelay = General.random(65, 95);
        patience = General.random((int) (total * .45), (int) (total * 1.2));
    }

    public void makeCharacter() {
        final boolean gender = meets(65);
        if (!gender || meets(30)) {
            patience = (int) (patience * 1.2);
        }
        sleep(1250, 7500);
        clickGender(gender);
        sleep(750, 3000);
        final ArrayList<Point> shuffled = new ArrayList<>(Arrays.asList(optionButtons));
        Collections.shuffle(shuffled);
        for (Point p : shuffled) {
            makeStep(p);
            sleep(1500, 8000);
        }
        sleep(2000, 4000);
        clickAccept();
    }

    private void makeStep(Point step) {
        final int id = meets(70) ? step.x : step.y;
        for (int i = 0; i < General.random(0, 12); i++) {
            Clicking.click(Interfaces.get(base, id));
            Client.println("Sleeping...");
            sleep(750, 1500);
        }
    }
    
    private void clickGender(boolean gender) {
        Clicking.click(Interfaces.get(base, gender ? genderButton.x : genderButton.y));
    }
    
    private void clickAccept() {
        Clicking.click(Interfaces.get(base, acceptButton));
    }

    private void sleep(int wait) {
        Client.println("Sleeping for: " + (int) (getPercent(sleepDelay) * wait * getSleepOffset()));
        Client.sleep((int) (getPercent(sleepDelay) * wait * getSleepOffset()));
        //return ((int) (1 + (((double) sleepDelay * (double) patience / (double) total)) * getSleepOffset())) * wait;
    }

    private void sleep(int wait1, int wait2) {
         sleep((int) (getValue(wait2 - wait1) + wait1));
    }
    
    private double getValue(double value) {
        return value * ((double) patience / (double) total);
    }
    
    private double getPercent(double value) {
        return (getValue(value) / 100d);
    }

    private double getSleepOffset() {
        return 1 + (General.random(50, 150) / 1000d);
    }

    private boolean meets(int value) {
        return General.random(1, 100) < value;
    }
}
