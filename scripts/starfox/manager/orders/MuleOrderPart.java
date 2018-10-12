package scripts.starfox.manager.orders;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * @author Starfox
 */
public class MuleOrderPart
        implements Externalizable, Cloneable {

    private static final long serialVersionUID = 1L;
    private boolean tradeMule;
    private boolean noted;
    private MuleItem item;
    private int amount;
    private boolean bank;

    public MuleOrderPart() {
    }

    public MuleOrderPart(final boolean tradeMule, final boolean noted, final MuleItem item, final int amount, final boolean bank) {
        this.tradeMule = tradeMule;
        this.noted = noted;
        this.item = item;
        this.amount = amount;
        this.bank = bank;
    }

    public final boolean isTradeMule() {
        return tradeMule;
    }

    public final void setTradeMule(boolean tradeMule) {
        this.tradeMule = tradeMule;
    }

    public final boolean isNoted() {
        return noted;
    }

    public final void setNoted(boolean noted) {
        this.noted = noted;
    }

    public final MuleItem getItem() {
        return item;
    }

    public final void setItem(MuleItem item) {
        this.item = item;
    }

    public final int getAmount() {
        return amount;
    }

    public final void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isBank() {
        return bank;
    }

    public void setBank(boolean bank) {
        this.bank = bank;
    }

    public final int getItemId() {
        return noted ? item.getNotedId() : item.getId();
    }

    public boolean isValid() {
        return item != null && amount >= 0 && (tradeMule || amount > 0);
    }

    public void setFrom(MuleOrderPart part) {
        this.amount = part.amount;
        this.bank = part.bank;
        this.item = part.item;
        this.noted = part.noted;
        this.tradeMule = part.tradeMule;
    }

    @Override
    public final String toString() {
        String tAmount = amount == 0 ? "all" : "" + amount;
        return "Trade " + tAmount + " " + (noted ? "noted " : "") + item.getPluralName() + " to " + (tradeMule ? "mule" : "slave");
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeBoolean(tradeMule);
        out.writeBoolean(noted);
        out.writeObject(item);
        out.writeInt(amount);
        out.writeBoolean(bank);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tradeMule = in.readBoolean();
        noted = in.readBoolean();
        item = (MuleItem) in.readObject();
        amount = in.readInt();
        bank = in.readBoolean();
    }

    @Override
    public MuleOrderPart clone() {
        MuleOrderPart cloned = new MuleOrderPart();
        cloned.amount = amount;
        cloned.item = item; //No need to clone because the item is simply used as a reference, and is never modified differently across MuleOrderParts.
        cloned.noted = noted;
        cloned.tradeMule = tradeMule;
        return cloned;
    }
}
