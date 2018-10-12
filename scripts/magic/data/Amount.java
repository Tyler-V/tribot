package scripts.magic.data;

import scripts.starfox.api.util.Strings;

/**
 * @author Nolan
 */
public enum Amount {

    ONE(1),
    ALL(0);

    private final int amount;

    Amount(int amount) {
        this.amount = amount;
    }

    public final int getAmount() {
        return this.amount;
    }

    @Override
    public String toString() {
        return Strings.capitalizeFirst(name().toLowerCase());
    }
}
