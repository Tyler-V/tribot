package scripts.starfox.api2007.zybez;

import java.util.Date;

/**
 * This class wraps information correlating to an offer that is attached to a specific {@link ZybezItem}.
 *
 * @author Nolan
 */
public final class Offer {

    private final int quantity, price;
    private final Type type;
    private final String playerName, notes;
    private final ContactMethod contactMethod;
    private final Date date;

    /**
     * Constructs a new Offer.
     *
     * @param quantity      The quantity of the offer.
     * @param price         The price of the offer (per item).
     * @param type          The {@link Type} of offer.
     * @param playerName    The name of the person who posted the offer.
     * @param contactMethod The contact method that person who posted the offer prefers.
     * @param notes         The notes attached to the offer.
     * @param date          The date of when the offer was posted.
     */
    public Offer(int quantity, int price, Type type, String playerName, String notes, ContactMethod contactMethod, Date date) {
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.playerName = playerName;
        this.notes = notes;
        this.contactMethod = contactMethod;
        this.date = date;
    }

    /**
     * Gets the offer type.
     *
     * This can either be {@link Type#BUYING} or {@link Type#SELLING}.
     *
     * @return The offer type.
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the offer quantity.
     *
     * @return The offer quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Gets the price of the offer.
     *
     * @return The price of the offer.f
     */
    public int getPrice() {
        return price;
    }

    /**
     * Gets the name of the player who posted the offer.
     *
     * @return The name of the player.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the notes attached to the offer.
     *
     * @return The notes.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Gets the contact method of the offer that the player prefers.
     *
     * This can either be {@link ContactMethod#CC} or {@link ContactMethod#PM}.
     *
     * @return The contact method.F
     */
    public ContactMethod getContactMethod() {
        return contactMethod;
    }

    /**
     * Gets the date on which the offer was posted.
     *
     * @return The date.
     */
    public Date getDate() {
        return new Date(date.getTime());
    }
    
    @Override
    public String toString() {
        return getPlayerName() + " is " + getType().name().toLowerCase() + " " + getQuantity() + " for " + getPrice() + " each. Posted on " + getDate() + ".";
    }

    /**
     * This enum is used to represent the types of offers.
     */
    public enum Type {

        SELLING, BUYING
    }

    /**
     * This enum is used to represent the contact methods an offer can have.
     */
    public enum ContactMethod {

        CC, PM
    }
}
