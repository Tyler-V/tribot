package scripts.starfox.manager.orders;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Objects;
import scripts.starfox.interfaces.ui.Listable;
import scripts.starfox.interfaces.ui.Validatable;
import scripts.starfox.manager.MuleUtil;

/**
 * @author Starfox
 */
public class MuleOrder
        implements Listable, Validatable, Externalizable, Cloneable {

    private static final long serialVersionUID = 1L;
    private ArrayList<MuleOrderPart> orderParts;
    private String name;
    private boolean wait;
    private boolean bankTrade;
    private final long hashID;

    public MuleOrder(long hashID) {
        orderParts = new ArrayList<>();
        name = "";
        this.hashID = hashID != 0 ? hashID : MuleUtil.generateHash();
    }
    
    public MuleOrder() {
        this(0);
    }
    
    public final long getHash() {
        return hashID;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final boolean isWait() {
        return wait;
    }

    public final void setWait(boolean wait) {
        this.wait = wait;
    }

    public final boolean isBankTrade() {
        return bankTrade;
    }

    public final void setBankTrade(boolean bankTrade) {
        this.bankTrade = bankTrade;
    }

    public final ArrayList<MuleOrderPart> getParts() {
        return orderParts;
    }

    public final void addOrderPart(MuleOrderPart part) {
        orderParts.add(part);
    }

    public final void addOrderParts(ArrayList<MuleOrderPart> parts) {
        orderParts.addAll(parts);
    }

    public final void clearOrderParts() {
        orderParts.clear();
    }

    @Override
    public boolean isValid() {
        return name != null && !name.isEmpty() && orderParts != null && !orderParts.isEmpty() && (!wait || !bankTrade || (wait && bankTrade)) && arePartsValid();
    }

    private boolean arePartsValid() {
        for (MuleOrderPart part : orderParts) {
            if (!part.isValid()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getListDisplay() {
        String banking = bankTrade ? "bank" : "inventory";
        String waiting = wait ? "waiting" : "not waiting";
        String formattedName = name;
        if (formattedName.isEmpty()) {
            formattedName = "(No Name)";
        }
        return formattedName + ": Trading from " + banking + " and " + waiting;
    }

    @Override
    public String searchName() {
        return name;
    }

    @Override
    public String toString() {
        return "\nORDER" + "\n"
                + "Name: " + name + "\n"
                + "Parts: " + orderParts + "\n"
                + "Waiting: " + wait + "\n"
                + "Trading Bank: " + bankTrade + "\n";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MuleOrder other = (MuleOrder) obj;
        return Objects.equals(this.name, other.name);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(orderParts);
        out.writeObject(name);
        out.writeBoolean(wait);
        out.writeBoolean(bankTrade);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        orderParts = (ArrayList<MuleOrderPart>) in.readObject();
        name = (String) in.readObject();
        wait = in.readBoolean();
        bankTrade = in.readBoolean();
    }

    public static MuleOrder newInstance(MuleOrder order) {
        MuleOrder newOrder = new MuleOrder(order.getHash());
        ArrayList<MuleOrderPart> clonedParts = new ArrayList<>();
        for (MuleOrderPart part : order.getParts()) {
            clonedParts.add(part.clone());
        }
        newOrder.addOrderParts(clonedParts);
        newOrder.setName(order.getName());
        newOrder.setWait(order.isWait());
        newOrder.setBankTrade(order.isBankTrade());
        return newOrder;
    }

    @Override
    public MuleOrder clone() {
        MuleOrder cloned = new MuleOrder();
        ArrayList<MuleOrderPart> clonedParts = new ArrayList<>();
        for (MuleOrderPart part : orderParts) {
            clonedParts.add(part.clone());
        }
        cloned.orderParts = clonedParts;
        cloned.name = name;
        cloned.wait = wait;
        cloned.bankTrade = bankTrade;
        return cloned;
    }

    @Override
    public String getPulldownDisplay() {
        return name.isEmpty() ? "(no name)" : name;
    }
}
