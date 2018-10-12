package scripts.usa.api2007.teleporting.interfaces;

/**
 * Check limits of a teleport, e.g. wilderness level under 20 or 30.
 */
public interface TeleportLimit {

	boolean canCast();

}
