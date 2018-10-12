package scripts.starfox.api2007.grandexchange;

/**
 * @author Nolan
 */
public class GEItem {
    
    public static final GEItem EMPTY = new GEItem(null, -1, -1);

    private final String name;
    private final int id;
    private final int price;

    public GEItem(String name, int id, int price) {
        this.name = name;
        this.id = id;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }
}
