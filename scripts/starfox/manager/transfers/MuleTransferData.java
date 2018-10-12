package scripts.starfox.manager.transfers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import scripts.starfox.manager.moveSet.ActionSet;
import scripts.starfox.manager.orders.MuleOrder;

/**
 * Contains all of the information that is sent from the Mule to the Slave.
 *
 * @author Starfox
 */
public class MuleTransferData
        implements Externalizable {

    private static final long serialVersionUID = 1L;
    private String rsn;
    private boolean removed;
    private boolean muleReady;
    private MuleOrder order;
    private ArrayList<ActionSet> preparationActionSets;
    private ArrayList<ActionSet> postTradeActionSets;

    public MuleTransferData() {
    }

    public MuleTransferData(final String rsn, final boolean removed, final boolean muleReady, final MuleOrder order,
            final ArrayList<ActionSet> preparationActionSets, final ArrayList<ActionSet> postTradeActionSets) {
        this.rsn = rsn;
        this.removed = removed;
        this.muleReady = muleReady;
        this.order = order;
        this.preparationActionSets = preparationActionSets;
        this.postTradeActionSets = postTradeActionSets;
    }

    public final String getRsn() {
        return rsn;
    }

    public final boolean isRemoved() {
        return removed;
    }

    public final boolean isMuleReady() {
        return muleReady;
    }

    public final MuleOrder getOrder() {
        return order;
    }

    public final ArrayList<ActionSet> getPreparationActionSets() {
        return preparationActionSets;
    }
    
    public final ArrayList<ActionSet> getPostTradeActionSets() {
        return postTradeActionSets;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(rsn);
        out.writeBoolean(removed);
        out.writeBoolean(muleReady);
        out.writeObject(order);
        out.writeInt(preparationActionSets != null ? preparationActionSets.size() : 0);
        if (preparationActionSets != null) {
            for (ActionSet actionSet : preparationActionSets) {
                out.writeObject(actionSet);
            }
        }
        out.writeInt(postTradeActionSets != null ? postTradeActionSets.size() : 0);
        if (postTradeActionSets != null) {
            for (ActionSet actionSet : postTradeActionSets) {
                out.writeObject(actionSet);
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        rsn = (String) in.readObject();
        removed = in.readBoolean();
        muleReady = in.readBoolean();
        order = (MuleOrder) in.readObject();
        int prepSize = in.readInt();
        preparationActionSets = new ArrayList<>();
        for (int i = 0; i < prepSize; i++) {
            preparationActionSets.add((ActionSet) in.readObject());
        }
        int postTradeSize = in.readInt();
        postTradeActionSets = new ArrayList<>();
        for (int i = 0; i < postTradeSize; i++) {
            postTradeActionSets.add((ActionSet) in.readObject());
        }
    }
}
