package scripts.starfox.api.util;

import java.awt.Component;
import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import scripts.starfox.api.Client;
import scripts.starfox.api.ErrorReporting;

/**
 * The UI class is a singleton pattern that can easily create no-args components on the event dispatch thread.
 * JOptionPane components may also be created with the necessary arguments being passable.
 *
 * @author Nolan
 */
public class UI {

    /**
     * The UI instance.
     */
    private static UI ui = null;

    /**
     * The last JFrame that was created.
     */
    private JFrame frame;

    /**
     * The last JDialog that was created.
     */
    private JDialog dialog;

    /**
     * The input from the last input dialog created.
     */
    private String input;

    /**
     * The option from the last option dialog created.
     */
    private int option;

    /**
     * Prevent instantiation of this class.
     */
    private UI() {

    }

    /**
     * Gets the UI instance.
     *
     * @return The UI instance.
     */
    public static UI get() {
        if (ui == null) {
            ui = new UI();
        }
        return ui;
    }

    /**
     * Gets the current JFrame held by the UI. Null if a JFrame has not yet been created.
     *
     * @return The current JFrame.
     */
    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Gets the current JDialog held by the UI. Null if a JDialog has not yet been created.
     *
     * @return The current JDialog.
     */
    public JDialog getDialog() {
        return this.dialog;
    }

    /**
     * Gets the current input held by the UI. Null if an input dialog has not yet been created.
     *
     * @return The current input.
     */
    public String getInput() {
        return this.input;
    }

    /**
     * Gets the current option held by the UI. 0 if an option dialog has not yet been created.
     *
     * @return The current option.
     */
    public int getOption() {
        return this.option;
    }

    /**
     * Creates a JFrame on the event dispatch thread.
     *
     * @param s       The class you want to create a new instance of on the event dispatch thread.
     * @param visible Whether or not to make the JFrame visible after creating it.
     * @return The JFrame that was created.
     */
    public JFrame createFrameEDT(final Class<? extends JFrame> s, final boolean visible) {
        try {
            EventQueue.invokeAndWait(() -> {
                try {
                    frame = s.newInstance();
                    frame.setVisible(visible);
                } catch (InstantiationException | IllegalAccessException ex) {
                    Client.println(ex.getMessage());
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            ErrorReporting.report(ex);
        }
        return frame;
    }

    /**
     * Creates a JDialog on the event dispatch thread.
     *
     * @param s       The class you want to create a new instance of.
     * @param visible Whether or not to make the JDialog visible after creating it.
     * @return The JDialog that was created.
     */
    public JDialog createDialogEDT(final Class<? extends JDialog> s, final boolean visible) {
        try {
            EventQueue.invokeAndWait(() -> {
                try {
                    dialog = s.newInstance();
                    dialog.setVisible(visible);
                } catch (InstantiationException | IllegalAccessException ex) {
                    Client.println(ex.getMessage());
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            ErrorReporting.report(ex);
        }
        return dialog;
    }

    /**
     * Creates a JOptionPane message dialog on the event dispatch thread.
     *
     * @param message The message of the dialog.
     * @param title   The title of the dialog.
     * @param type    The type of dialog.
     */
    public void createMessageDialogEDT(final String message, final String title, final int type) {
        try {
            EventQueue.invokeAndWait(() -> JOptionPane.showMessageDialog(null, message, title, type));
        } catch (InterruptedException | InvocationTargetException ex) {
            ErrorReporting.report(ex);
        }
    }

    /**
     * Creates a JOptionPane input dialog on the event dispatch thread.
     *
     * @param message The message to display on the dialog.
     * @return The input returned by the dialog.
     */
    public String createInputDialogEDT(final String message) {
        try {
            EventQueue.invokeAndWait(() -> input = JOptionPane.showInputDialog(message));
        } catch (InterruptedException | InvocationTargetException ex) {
            ErrorReporting.report(ex);
        }
        return input;
    }

    /**
     * Creates a JOptionPane option dialog on the event dispatch thread.
     *
     * @param parent      The parent component.
     * @param message     The message to display on the dialog.
     * @param title       The title.
     * @param optionType  The option type.
     * @param messageType The message type.
     * @return The option returned by the dialog.
     */
    public int createOptionDialogEDT(final Component parent, final String message, final String title, final int optionType, final int messageType) {
        try {
            EventQueue.invokeAndWait(() -> option = JOptionPane.showOptionDialog(parent, message, title, optionType, messageType, null, null, null));
        } catch (InterruptedException | InvocationTargetException ex) {
            ErrorReporting.report(ex);
        }
        return option;
    }
}
