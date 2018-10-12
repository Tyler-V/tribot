package scripts.starfox.manager.transfers;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import scripts.starfox.manager.SlaveStatus;
import scripts.starfox.manager.TransferItem;

/**
 * Contains all of the information that is sent from the Slave to the Mule.
 *
 * @author Starfox
 */
public class SlaveTransferData
        implements Externalizable {

    private final Object BANK_LOCK;
    private final Object INVENT_LOCK;
    private static final long serialVersionUID = 1L;
    private SlaveStatus status;
    private ArrayList<TransferItem> inventoryItems;
    private ArrayList<TransferItem> bankItems;
    private boolean bankReady;
    private int world;
    private int gameState;
    private int xLocation;
    private int yLocation;
    private String profile;

    public SlaveTransferData() {
        BANK_LOCK = new Object();
        INVENT_LOCK = new Object();
    }

    public SlaveTransferData(final SlaveStatus status, final ArrayList<TransferItem> inventoryItems, final ArrayList<TransferItem> bankItems,
            final boolean isBankReady, final int world, final int gameState, final int xLocation, final int yLocation, final String profile) {
        this();
        this.status = status;
        this.inventoryItems = inventoryItems;
        this.bankItems = bankItems;
        this.bankReady = isBankReady;
        this.world = world;
        this.gameState = gameState;
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.profile = profile;
    }

    public final SlaveStatus getStatus() {
        return status;
    }

    public final ArrayList<TransferItem> getInventoryItems() {
        synchronized (INVENT_LOCK) {
            return inventoryItems;
        }
    }

    public final ArrayList<TransferItem> getBankItems() {
        synchronized (BANK_LOCK) {
            return bankItems;
        }
    }

    public final boolean isBankReady() {
        return bankReady;
    }

    public final int getWorld() {
        return world;
    }

    public final int getGameState() {
        return gameState;
    }

    public final int getxLocation() {
        return xLocation;
    }

    public final int getyLocation() {
        return yLocation;
    }

    public final String getProfile() {
        return profile;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(status);
        synchronized (INVENT_LOCK) {
            out.writeObject(inventoryItems);
        }
        synchronized (BANK_LOCK) {
            out.writeObject(bankItems);
        }
        out.writeBoolean(bankReady);
        out.writeInt(world);
        out.writeInt(gameState);
        out.writeInt(xLocation);
        out.writeInt(yLocation);
        out.writeObject(profile);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        status = (SlaveStatus) in.readObject();
        synchronized (INVENT_LOCK) {
            inventoryItems = (ArrayList<TransferItem>) in.readObject();
        }
        synchronized (BANK_LOCK) {
            bankItems = (ArrayList<TransferItem>) in.readObject();
        }
        bankReady = in.readBoolean();
        world = in.readInt();
        gameState = in.readInt();
        xLocation = in.readInt();
        yLocation = in.readInt();
        profile = (String) in.readObject();
    }
}
