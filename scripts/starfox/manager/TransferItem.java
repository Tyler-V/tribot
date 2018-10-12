package scripts.starfox.manager;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.tribot.api2007.types.RSItem;

/**
 * @author Starfox
 */
public class TransferItem
        implements Externalizable {

    private static final long serialVersionUID = 1L;
    private int id;
    private int amount;

    public TransferItem() {
    }
    
    public TransferItem(final int id, final int amount) {
        this.id = id;
        this.amount = amount;
    }

    public final int getId() {
        return id;
    }

    public final int getAmount() {
        return amount;
    }
    
    public static ArrayList<TransferItem> toTransferItems(RSItem[] items, boolean noNoted) {
        ArrayList<TransferItem> transferItems = new ArrayList<>();
        for (RSItem item : items) {
            int count = 0;
            //No null check because the array of RSItems must always be real items, therefore the definition cannot be null.
            int id = noNoted && item.getDefinition().isNoted() ? item.getID() - 1 : item.getID();
            if (transferItems.contains(new TransferItem(id, -1))) {
                int preCount = transferItems.get(transferItems.indexOf(new TransferItem(id, -1))).getAmount();
                count += preCount;
                transferItems.remove(new TransferItem(id, -1));
            }
            int totalCount = count + item.getStack();
            if (totalCount != 0) {
                transferItems.add(new TransferItem(id, totalCount));
            }
        }
        return transferItems;
    }
    
    /**
     * Converts an RSItem into a TransferItem
     *
     * @param item The item being converted.
     * @param amount The amount of the item.
     * @return The TransferItem that was converted from the specified RSItem.
     */
    public static TransferItem toTransferItem(RSItem item, int amount) {
        return new TransferItem(item.getID(), amount);
    }

    /**
     * Converts an RSItem into a TransferItem
     *
     * @param item The item being converted.
     * @return The TransferItem that was converted from the specified RSItem.
     */
    public static TransferItem toTransferItem(RSItem item) {
        return toTransferItem(item, item.getStack());
    }

    @Override
    public String toString() {
        return "[ID: " + id + " | " + "Amount: " + amount + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.id;
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
        final TransferItem other = (TransferItem) obj;
        return this.id == other.id;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);
        out.writeInt(amount);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readInt();
        amount = in.readInt();
    }
}
