package scripts.starfox.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import scripts.starfox.interfaces.ui.Listable;

/**
 * @author Nolan
 * @param <T> The Listable that is being displayed in this search box.
 */
public class SearchBox<T extends Listable> extends JComboBox {

    private final ArrayList<Listable> searchList;
    private final Listable prompt;
    private final Listable noResults;
    private Listable item;

    public SearchBox(final String promptText) {
        super();
        item = null;
        this.prompt = new Listable() {
            @Override
            public String getListDisplay() {
                return promptText;
            }

            @Override
            public String searchName() {
                return promptText;
            }

            @Override
            public String getPulldownDisplay() {
                return promptText;
            }
        };
        this.noResults = new Listable() {
            @Override
            public String getListDisplay() {
                return "No Results...";
            }

            @Override
            public String searchName() {
                return "No Results...";
            }

            @Override
            public String getPulldownDisplay() {
                return "No Results...";
            }
        };
        this.searchList = new ArrayList<>();
        setEditable(true);
        setModel(new DefaultComboBoxModel<>());
        setAutoscrolls(true);
        final ComboBoxEditor e = new BasicComboBoxEditor() {
            @Override
            public void setItem(Object o) {
                if (o == null || o.equals(noResults)) {
                    return;
                }
                if (o instanceof Listable) {
                    super.setItem(((Listable) o).getPulldownDisplay());
                } else {
                    super.setItem(o);
                }
            }
        };
        final SearchBox s = this;

        e.getEditorComponent().addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.setItem(s.getSelectedItem());
                    return;
                }
                if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
                    return;
                }
                refresh();
            }
        });
        e.getEditorComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                clear();
                if (e.getItem() != null && e.getItem().toString().isEmpty()) {
                    refresh();
                }
            }
        });
        e.getEditorComponent().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (e.getItem().toString().isEmpty()) {
                    reset();
                }
            }
        });
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Listable item = (Listable) value;
                setText(item.getListDisplay());
                return this;
            }
        });

        e.getEditorComponent().setForeground(new Color(150, 150, 150, 200));
        setEditor(e);
    }

    public void setSearchList(T... items) {
        this.searchList.clear();
        this.searchList.addAll(Arrays.asList(items));
        getEditor().setItem(getPrompt());
    }

    public void setSearchList(ArrayList<T> items) {
        this.searchList.clear();
        this.searchList.addAll(items);
        getEditor().setItem(getPrompt());
    }

    public ArrayList<Listable> getSearchList() {
        return this.searchList;
    }

    public final Listable getPrompt() {
        return this.prompt;
    }

    public void refresh() {
        this.item = null;
        final ComboBoxEditor e = getEditor();
        Object testItem = e.getItem();
        if (testItem == null) {
            return;
        }
        if (e.getItem().toString().isEmpty()) {
            removeAllItems();
            for (Listable o : getSearchList()) {
                addItem(o);
            }
            e.setItem(testItem);
            setPopupVisible(false);
            showPopup();
            return;
        }
        String search;
        if (testItem instanceof Listable) {
            if (!testItem.equals(noResults)) {
                this.item = (Listable) testItem;
            }
            search = ((Listable) testItem).searchName();
        } else {
            search = testItem.toString();
        }
        removeAllItems();
//        System.out.println(getSearchList());
//        System.out.println(search);
        for (Listable o : getSearchList()) {
            if (o.searchName().toLowerCase().contains(search.toLowerCase())) {
                //General.println("Adding Item: " + o.searchName());
                addItem(o);
            }
        }
        if (!search.isEmpty() && getItemCount() == 0) {
            addItem(noResults);
        }
        //General.println("Item: " + testItem);
        e.setItem(testItem);
        JTextField textField = (JTextField) e.getEditorComponent();
//        if (getItemCount() != 0) {
//            textField.setText(((Listable) getSelectedItem()).getListDisplay());
//        }
        textField.setSelectionStart(search.length());
        textField.setSelectionEnd(search.length());
        setPopupVisible(false);
        showPopup();
    }

    public void clear() {
        final ComboBoxEditor e = getEditor();
        if (e.getItem().toString().equalsIgnoreCase(getPrompt().getListDisplay()) || getEditor().getEditorComponent().getForeground().equals(new Color(150, 150, 150, 200))) {
            e.setItem("");
            e.getEditorComponent().setForeground(null);
        }
    }

    public void reset() {
        final ComboBoxEditor e = getEditor();
        removeAllItems();
        setPopupVisible(false);
        e.getEditorComponent().setForeground(new Color(150, 150, 150, 200));
        e.setItem(getPrompt());
    }

    @Override
    public Object getSelectedItem() {
        if (getModel().getSelectedItem() != null && getModel().getSelectedItem() instanceof Listable && !getModel().getSelectedItem().equals(noResults)) {
            item = (Listable) getModel().getSelectedItem();
        }
        if (getEditor().getItem().toString().isEmpty()) {
            return getModel().getSelectedItem();
        }
        return item;
        //return getModel().getSelectedItem() instanceof Listable ? getModel().getSelectedItem() : null;
    }
}
