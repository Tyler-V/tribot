package scripts.starfox.api;

/**
 * @author Nolan
 */
public class Printing {

    /**
     * Prints the specified text via the Client Debug with the header "Debug: ".
     *
     * @param ln The text that is being printed.
     */
    public static void dev(Object ln) {
        Client.println("Debug: " + ln);
    }

    /**
     * Prints the specified text via the Client Debug with "WARNING: " as a prefix.
     *
     * @param ln The text that is being printed.
     */
    public static void warn(Object ln) {
        Client.println("WARNING: " + ln);
    }
    
    /**
     * Prints the specified text via the Client Debug with "ERROR: " as a prefix.
     *
     * @param ln The text that is being printed.
     */
    public static void err(Object ln) {
        Client.println("ERROR: " + ln);
    }

    /**
     * Prints the specified text via the Client Debug with "Status: " as a prefix.
     *
     * @param ln The text that is being printed.
     */
    public static void status(Object ln) {
        Client.println("Status: " + ln);
    }
    
    /**
     * Prints the specified text via the Bot Debug.
     *
     * @param ln The text that is being printed.
     */
    public static void bot(String ln) {
        System.out.println(ln);
    }
}
