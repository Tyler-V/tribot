package scripts.starfox.manager.orders;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Stack;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import scripts.starfox.manager.transfers.TransferEvent;

/**
 * @author Starfox
 */
public class VirtualOrders {

    private static final char[] KEYS = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm', 'Q',
        'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '1', '2', '3', '4', '5', '6', '7', '8',
        '9', '0', '`', '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '-', '+', '=', '{', '}', '[', ']', ';', ':', '\'', '"', ',', '<', '.', '>', '/', '?', ' '};

    public static void executeEvents(Stack<TransferEvent> events) {
        for (TransferEvent event : events) {
            if (event.getEvent() instanceof KeyEvent) {
                KeyEvent keyEvent = (KeyEvent) event.getEvent();
                if (isEnter(keyEvent)) {
                    Keyboard.pressEnter();
                } else {
                    char toType;
                    switch (event.getType()) {
                        case PRESS:
                            toType = getKeyChar(keyEvent, true);
                            Keyboard.sendPress(toType, Keyboard.getKeyCode(toType));
                            break;
                        case RELEASE:
                            toType = getKeyChar(keyEvent, true);
                            Keyboard.sendRelease(toType, Keyboard.getKeyCode(toType));
                            break;
                        case CLICK:
                            toType = getKeyChar(keyEvent, false);
                            Keyboard.sendType(toType);
                            break;
                    }
                }
            } else if (event.getEvent() instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent) event.getEvent();
                int button = mouseEvent.getButton();
                Point location = mouseEvent.getPoint();
                switch (event.getType()) {
                    case PRESS:
                        Mouse.sendPress(location, button);
                        break;
                    case RELEASE:
                        Mouse.sendRelease(location, button);
                        break;
                    case MOVE:
                        Mouse.hop(location);
                        break;
                    case CLICK:
                        //Mouse.sendClick(location, button);
                        break;
                }
            }
        }
    }

    private static boolean isEnter(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                return true;
        }
        return false;
    }

    private static char getKeyChar(KeyEvent event, boolean shouldPress) {
        if (shouldPress) {
            return (char) event.getKeyCode();
        } else {
            if (isValidKeyChar(event.getKeyChar())) {
                return event.getKeyChar();
            } else {
                switch (event.getKeyCode()) {
                    case KeyEvent.VK_TAB:
                        return '\t';
                    case KeyEvent.VK_BACK_SPACE:
                        return '\b';
                    default:
                        return '\u0000';
                }
            }
        }
    }

    private static boolean isValidKeyChar(char keyChar) {
        for (char tempChar : KEYS) {
            if (keyChar == tempChar) {
                return true;
            }
        }
        return false;
    }
}
