package scripts.starfox.swing;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * @author Spencer
 */
public class SwingUtil {

    /**
     * Attempts to set the windows location to a visible location determined by the platform.
     *
     * @param window The window being moved.
     */
    public static void bringToFront(Window window) {
        window.setLocationByPlatform(true);
        if (!window.isFocused()) {
            window.toFront();
        }
    }

    /**
     * Makes the specified window visible and brings it to the front of the screen.
     *
     * @param window Thw window being made visible.
     */
    public static void makeVisible(Window window) {
        window.setVisible(true);
        if (!window.isFocused()) {
            window.requestFocus();
        }
    }

    /**
     * Returns the elements in the specified JList in the form of a DefaultListModel.
     *
     * @param list The JList.
     * @return The elements in the specified JList in the form of a DefaultListModel.
     */
    public static DefaultListModel getModel(JList list) {
        return (DefaultListModel) list.getModel();
    }

    /**
     * Returns the elements in the specified JComboBox in the form of a DefaultComboBoxModel.
     *
     * @param comboBox The JComboBox.
     * @return The elements in the specified JComboBox in the form of a DefaultComboBoxModel.
     */
    public static DefaultComboBoxModel getModel(JComboBox comboBox) {
        return (DefaultComboBoxModel) comboBox.getModel();
    }

    /**
     * Deletes the selected value from the specified JList, and the selects the next appropriate element accordingly.
     *
     * @param list The JList.
     */
    public static void deleteSelected(JList list) {
        DefaultListModel tempModel = SwingUtil.getModel(list);
        int currentLocation = list.getSelectedIndex();
        SwingUtil.getModel(list).removeElement(list.getSelectedValue());
        if (tempModel.size() > currentLocation) {
            list.setSelectedIndex(currentLocation);
        } else {
            if (!tempModel.isEmpty()) {
                list.setSelectedIndex(currentLocation - 1);
            }
        }
    }

    /**
     * Deletes the selected value from the specified JComboBox, and the selects the next appropriate element accordingly.
     *
     * @param comboBox The JComboBox.
     */
    public static void deleteSelected(JComboBox comboBox) {
        DefaultComboBoxModel tempModel = getModel(comboBox);
        int currentLocation = comboBox.getSelectedIndex();
        getModel(comboBox).removeElement(comboBox.getSelectedItem());
        if (tempModel.getSize() > currentLocation) {
            comboBox.setSelectedIndex(currentLocation);
        } else {
            if (tempModel.getSize() != 0) {
                comboBox.setSelectedIndex(currentLocation - 1);
            }
        }
    }

    /**
     * Deselects the specified JList, clearing it's selection and focus.
     *
     * @param list The JList being deselected.
     */
    public static void deselect(JList list) {
        list.clearSelection();
        list.setFocusable(false);
        list.setFocusable(true);
    }

    /**
     * Returns the specified JList as an ArrayList of the elements it contains.
     *
     * @param <T>  The type of JList.
     * @param list The JList.
     * @return The specified JList as an ArrayList of the elements it contains.
     */
    public static <T> ArrayList<T> getListAsArray(JList<T> list) {
        DefaultListModel model = getModel(list);
        ArrayList<T> arrL = new ArrayList<>();
        Object[] arr = model.toArray();
        for (Object o : arr) {
            arrL.add((T) o);
        }
        return arrL;
    }

    /**
     * Updates the specified JList to contain the elements contained within the specified ArrayList.
     *
     * If an element contained within the specified ArrayList is already contained in the JList, and clear is true, then the element will be skipped.
     *
     * @param <T>       The type of element.
     * @param list      The JList being updated.
     * @param arrayList The ArrayList being transferred into the JList.
     * @param clear     True if the JList should be cleared of all elements before the new elements are added, false otherwise.
     */
    public static <T> void updateModel(JList<T> list, ArrayList<? extends T> arrayList, boolean clear) {
        DefaultListModel model = (DefaultListModel) list.getModel();
        ArrayList<T> toRemove = new ArrayList<>();
        for (T o : arrayList) {
            if (!model.contains(o)) {
                model.addElement(o);
            }
        }
        for (Object o : model.toArray()) {
            if (!arrayList.contains((T) o)) {
                toRemove.add((T) o);
            }
        }
        for (T o : toRemove) {
            model.removeElement(o);
        }
    }

    /**
     * Centers the specified component relative to it's parent's location.
     *
     * @param component The Component that is being centered.
     */
    public static void centerAroundParent(Component component) {
        Container parent = SwingUtil.getWindowParent(component);
        if (parent != null) {
            int pWidth = parent.getWidth() / 2;
            int pHeight = parent.getHeight() / 2;
            int cWidth = component.getWidth() / 2;
            int cHeight = component.getHeight() / 2;
            int pX = parent.getX();
            int pY = parent.getY();
            component.setLocation(new Point(pWidth - cWidth + pX, pHeight - cHeight + pY));
        }
    }

    /**
     * Returns all of the children components of the specified JComponent.
     *
     * @param component The JComponent.
     * @return All of the children components of the specified JComponent.
     */
    public static ArrayList<JComponent> getChildren(JComponent component) {
        ArrayList<JComponent> children = new ArrayList<>();
        for (Component c : component.getComponents()) {
            if (c instanceof JComponent) {
                JComponent c2 = (JComponent) c;
                ArrayList<JComponent> children2 = getChildren(c2);
                if (!children2.isEmpty()) {
                    children.addAll(children2);
                }
                children.add(c2);
            }
        }
        return children;
    }

    /**
     * Applies the operation contained within the {@link ChildEditable#edit(JComponent)} method to all children of the specified host JComponent.
     *
     * @param host     The host JComponent.
     * @param editable The ChildEditable.
     */
    public static void editChildren(JComponent host, ChildEditable editable) {
        for (JComponent child : getChildren(host)) {
            editable.edit(child);
        }
    }

    /**
     * Returns the Window parent of the specified Component, or null if the parent container is not a Window.
     *
     * @param component The JComponent.
     * @return The Window parent of the specified Component, or null if the parent container is not a Window.
     */
    public static Window getWindowParent(Component component) {
        return SwingUtilities.getWindowAncestor(component);
    }

    public interface ChildEditable {

        public static final ChildEditable DISABLE = new ChildEditable() {
            @Override
            public void edit(JComponent component) {
                component.setEnabled(false);
            }
        };

        public static final ChildEditable ENABLE = new ChildEditable() {
            @Override
            public void edit(JComponent component) {
                component.setEnabled(true);
            }
        };

        public static final ChildEditable HIDE = new ChildEditable() {
            @Override
            public void edit(JComponent component) {
                component.setVisible(false);
            }
        };

        public static final ChildEditable SHOW = new ChildEditable() {
            @Override
            public void edit(JComponent component) {
                component.setVisible(true);
            }
        };

        void edit(JComponent component);
    }
}
