package scripts.starfox.manager.orders;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import scripts.starfox.interfaces.ui.Listable;
import scripts.starfox.interfaces.ui.Validatable;

/**
 * @author Starfox
 */
public class MuleItem
        implements Listable, Validatable, Externalizable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String pluralName;
    private int id;
    private int notedId;
    private int value;
    private boolean stackable;

    public MuleItem() {
        name = "";
        pluralName = "";
        id = -1;
        notedId = -1;
        value = -1;
        stackable = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public void setPluralName(String pluralName) {
        this.pluralName = pluralName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNotedId() {
        return notedId;
    }

    public void setNotedId(int notedId) {
        this.notedId = notedId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isValid() {
        return name != null && !name.isEmpty() && id > 0;
    }

    public boolean isStackable() {
        return stackable;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    @Override
    public String getListDisplay() {
        String formattedName = name;
        if (formattedName.isEmpty()) {
            formattedName = "(No Name)";
        }
        String formattedId = "" + id;
        if (id < 0) {
            formattedId = "No ID";
        }
        return formattedName + ": " + formattedId;
    }
    
    @Override
    public String searchName() {
        return name + "" + id;
    }

    @Override
    public String toString() {
        return "(Name: " + name + " | "
                + "Plural Name: " + pluralName + " | "
                + "ID: " + id + " | "
                + "Noted ID: " + notedId + " | "
                + "Value: " + value + " | "
                + "Stackable: " + stackable + ")";
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(pluralName);
        out.writeInt(id);
        out.writeInt(notedId);
        out.writeInt(value);
        out.writeBoolean(stackable);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        pluralName = (String) in.readObject();
        id = in.readInt();
        notedId = in.readInt();
        value = in.readInt();
        stackable = in.readBoolean();
    }

    @Override
    public String getPulldownDisplay() {
        return getListDisplay();
    }
}
