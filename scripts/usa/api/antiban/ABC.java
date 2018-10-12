package scripts.usa.api.antiban;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api.util.abc.preferences.OpenBankPreference;
import org.tribot.api.util.abc.preferences.TabSwitchPreference;
import org.tribot.api.util.abc.preferences.WalkingPreference;
import org.tribot.api2007.Combat;

import scripts.usa.api.condition.Result;
import scripts.usa.api.condition.ResultCondition;
import scripts.usa.api.threads.SafeThread;
import scripts.usa.api.threads.VolatileRunnable;
import scripts.usa.api2007.Game;
import scripts.usa.api2007.Options;

/**
 * @author Usa
 *
 *         Static ABC class with useful methods to set properties, get reaction
 *         time, debug, and call reaction sleeps with a breakout condition, etc.
 */
public final class ABC {

	private static ABCUtil abc;
	private static Boolean antiban;
	private static Boolean sleepReaction;
	private static Boolean shouldHover;
	private static Boolean shouldOpenMenu;

	private static int nextRun;
	private static int nextEat;
	private static int nextEnergyPotion;

	private static Boolean hover;
	private static Boolean openMenu;
	private static Boolean moveToAnticipated;
	private static Boolean fixed = false;

	private static long startTime;
	private static long endTime;
	private static long actionTime;
	private static long combatTime;
	private static int reactionTime;

	private final static int MAXIMUM_REACTION = 30000;

	/**
	 * Prevent instantiation of this class.
	 */
	private ABC() {
	}

	/**
	 * Static initializer
	 */
	static {
		if (abc == null)
			abc = getABC();
		General.useAntiBanCompliance(true);
	}

	/**
	 * Gets the instance of ABCUtil
	 * 
	 * @return ABCUtil
	 */
	public static ABCUtil getABC() {
		if (abc != null)
			return abc;
		System.out.println("[ABC2] Initialized!");
		return abc = new ABCUtil();
	}

	/**
	 * Destroys the current instance of ABCUtil and stops all anti-ban threads.
	 * Call this at the end of your script.
	 */
	public static void destroy() {
		if (abc == null)
			return;
		abc.close();
		abc = null;
		System.out.println("[ABC2] Destroyed!");
	}

	/**
	 * Sets an override to always or never perform the action
	 */
	public static void setAntiban(boolean option) {
		antiban = option;
		System.out.println("[ABC2] Antiban actions set to " + (option ? "enabled" : "disabled"));
	}

	/**
	 * Sets an override to always or never perform the action
	 */
	public static void setReactionSleeping(boolean option) {
		sleepReaction = option;
		System.out.println("[ABC2] Reaction sleeping set to " + (option ? "enabled" : "disabled"));
	}

	/**
	 * Sets an override to always or never perform the action
	 */
	public static void setHover(boolean option) {
		shouldHover = option;
		System.out.println("[ABC2] Hovering set to " + (option ? "enabled" : "disabled"));
	}

	/**
	 * Sets an override to always or never perform the action
	 */
	public static void setOpenMenu(boolean option) {
		shouldOpenMenu = option;
		System.out.println("[ABC2] Opening menu set to " + (option ? "enabled" : "disabled"));
	}

	/**
	 * Checks if we should eat
	 * 
	 * @return true if we should eat
	 */
	public static boolean shouldEat() {
		if (nextEat == 0)
			nextEat = generateNextEat();
		if (Combat.getHPRatio() <= nextEat) {
			nextEat = generateNextEat();
			return true;
		}
		return false;
	}

	/**
	 * Generates the next value to eat at
	 * 
	 */
	private static int generateNextEat() {
		return getABC().generateEatAtHP();
	}

	/**
	 * Checks if we should activate run
	 * 
	 * @return true if we activated run
	 */
	public static boolean activateRun() {
		nextRun = nextRun > 0 ? nextRun : getABC().generateRunActivation();
		if (!Game.isRunOn() && Game.getRunEnergy() >= nextRun) {
			if (Options.setRunEnabled(true)) {
				nextRun = getABC().generateRunActivation();
				return true;
			}
		}
		return false;
	}

	/**
	 * Generates the next value to drink an energy potion at
	 * 
	 * @return next integer value
	 */
	private static int generateNextEnergyPotion() {
		return General.random(50, 90);
	}

	/**
	 * Checks if we should drink an energy potion
	 * 
	 * @return true if we should drink an energy potion
	 */
	public static boolean shouldDrinkEnergyPotion() {
		nextEnergyPotion = nextEnergyPotion > 0 ? nextEnergyPotion : generateNextEnergyPotion();
		if (Game.getRunEnergy() <= nextEnergyPotion) {
			nextEnergyPotion = generateNextEnergyPotion();
			return true;
		}
		return false;
	}

	/**
	 * Performs all timed antiban actions
	 * 
	 * @return true if one action was performed
	 */
	public static boolean performAntiban() {
		if (antiban != null && !antiban)
			return false;
		return performRotateCamera() || performXPCheck() ||
				performPickupMouse() ||
				performLeaveGame() ||
				performExamineObject() ||
				performRandomRightClick() ||
				performRandomMouseMovement() ||
				performTabsCheck();
	}

	/**
	 * Performs the camera rotation
	 * 
	 * @return true if we rotated the camera
	 */
	public static boolean performRotateCamera() {
		if (getABC().shouldRotateCamera()) {
			getABC().rotateCamera();
			return true;
		}
		return false;
	}

	/**
	 * Performs the XP check
	 * 
	 * @return true if we checked the XP
	 */
	public static boolean performXPCheck() {
		if (getABC().shouldCheckXP()) {
			getABC().checkXP();
			return true;
		}
		return false;
	}

	/**
	 * Performs the mouse pickup
	 * 
	 * @return true if we picked up the mouse
	 */
	public static boolean performPickupMouse() {
		if (getABC().shouldPickupMouse()) {
			getABC().pickupMouse();
			return true;
		}
		return false;
	}

	/**
	 * Performs the leave game
	 * 
	 * @return true if we left the game
	 */
	public static boolean performLeaveGame() {
		if (getABC().shouldLeaveGame()) {
			getABC().leaveGame();
			return true;
		}
		return false;
	}

	/**
	 * Performs the examine object
	 * 
	 * @return true if we examined the object
	 */
	public static boolean performExamineObject() {
		if (getABC().shouldExamineEntity()) {
			getABC().examineEntity();
			return true;
		}
		return false;
	}

	/**
	 * Performs the random right click
	 * 
	 * @return true if we randomly right click
	 */
	public static boolean performRandomRightClick() {
		if (getABC().shouldRightClick()) {
			getABC().rightClick();
			return true;
		}
		return false;
	}

	/**
	 * Performs the random mouse movements
	 * 
	 * @return true if we randomly move the mouse
	 */
	public static boolean performRandomMouseMovement() {
		if (getABC().shouldMoveMouse()) {
			getABC().moveMouse();
			return true;
		}
		return false;
	}

	/**
	 * Performs the tabs check
	 * 
	 * @return true if we checked the tabs
	 */
	public static boolean performTabsCheck() {
		if (getABC().shouldCheckTabs()) {
			getABC().checkTabs();
			return true;
		}
		return false;
	}

	/**
	 * Sets your ABC properties by params
	 */
	public static void setProperties(int time, Boolean menu, Boolean hover, Boolean combat, Boolean fixed) {
		ABCProperties properties = new ABCProperties();
		properties.setWaitingTime(time <= 0 ? getWaitingTime() : time);
		properties.setMenuOpen(menu == null ? shouldOpenMenu() : menu);
		properties.setHovering(hover == null ? shouldHover() : hover);
		properties.setUnderAttack(combat == null ? wasCombatRecent() : combat);
		properties.setWaitingFixed(fixed == null ? false : fixed);
		getABC().setProperties(properties);
	}

	/**
	 * Sets your ABC properties by current values
	 */
	public static void setProperties() {
		setProperties(0, null, null, null, null);
	}

	/**
	 * Checks if we should hover
	 * 
	 * @return true if we should hover
	 */
	public static boolean shouldHover() {
		if (shouldHover != null && !shouldHover)
			return false;
		if (!Mouse.isInBounds())
			return false;
		hover = hover != null ? hover : generateNextHover();
		return hover || (shouldHover != null && shouldHover);
	}

	/**
	 * Generates new hover
	 */
	public static void resetShouldHover() {
		hover = null;
	}

	/**
	 * Generates the next value for hovering over an entity
	 * 
	 * @return true if we should hover
	 */
	public static boolean generateNextHover() {
		hover = getABC().shouldHover();
		return hover;
	}

	/**
	 * Checks if we should open the menu
	 * 
	 * @return true if we should open the menu
	 */
	public static boolean shouldOpenMenu() {
		if (shouldOpenMenu != null && !shouldOpenMenu)
			return false;
		hover = hover != null ? hover : generateNextHover();
		openMenu = openMenu != null ? openMenu : generateNextOpenMenu();
		return hover && (openMenu || (shouldOpenMenu != null && shouldOpenMenu));
	}

	/**
	 * Generates new open menu
	 */
	public static void resetShouldOpenMenu() {
		openMenu = null;
	}

	/**
	 * Generates the next value for opening a menu
	 * 
	 * @return true if we should open the menu
	 */
	public static Boolean generateNextOpenMenu() {
		openMenu = getABC().shouldOpenMenu();
		return openMenu;
	}

	/**
	 * Checks if we should move to anticipated
	 * 
	 * @return true if we should move to anticipated
	 */
	public static boolean shouldMoveToAnticipated() {
		if (moveToAnticipated == null)
			moveToAnticipated = generateMoveToAnticipated();
		return moveToAnticipated;
	}

	/**
	 * Generates new move to anticipated
	 */
	public static void resetMoveToAnticipated() {
		moveToAnticipated = null;
	}

	/**
	 * Generates the next value for move to anticipated
	 * 
	 * @return true if we should move to anticipated
	 */
	public static Boolean generateMoveToAnticipated() {
		moveToAnticipated = getABC().shouldMoveToAnticipated();
		return moveToAnticipated;
	}

	/**
	 * Gets your waiting time from the time you last performed an action to the
	 * time that you completed the task.
	 * 
	 * If the waiting time is under a second above 3 minutes, we will treat it
	 * as invalid.
	 */
	public static int getWaitingTime() {
		if ((startTime == 0 && actionTime == 0) || endTime == 0)
			return 0;
		int time = (int) (startTime > actionTime ? (endTime - startTime) : (endTime - actionTime));
		return time < 120000 ? time : 0;
	}

	/**
	 * Caches the last time an action was performed
	 * 
	 * @param long
	 *            time
	 */
	public static void setTime(Time time) {
		if (sleepReaction != null && !sleepReaction)
			return;
		StringBuilder sb = new StringBuilder();
		if (time == Time.START) {
			sb.append("[ABC2] Set Start Time");
			startTime = System.currentTimeMillis();
		}
		else if (time == Time.END) {
			sb.append("[ABC2] Set End Time");
			endTime = System.currentTimeMillis();
		}
		else if (time == Time.ACTION) {
			sb.append("[ABC2] Set Action Time");
			actionTime = System.currentTimeMillis();
		}
		else if (time == Time.COMBAT) {
			if (!wasCombatRecent())
				sb.append("[ABC2] Set Combat Time");
			combatTime = System.currentTimeMillis();
		}
		System.out.println(sb.toString());
	}

	/**
	 * Checks if you were recently in combat
	 * 
	 * @return true if combat was in the last 5 minutes
	 */
	public static boolean wasCombatRecent() {
		if (combatTime == 0)
			return false;
		return (System.currentTimeMillis() - combatTime) < 300000;
	}

	/**
	 * Set fixed time for fixed, static actions not involving a reaction time
	 */
	public static void setFixed(boolean option) {
		fixed = option;
	}

	/**
	 * Returns whether the time is fixed or not
	 */
	public static boolean isFixed() {
		return fixed;
	}

	/**
	 * Gets your start time
	 * 
	 * @return long time
	 */
	public static long getStartTime() {
		return startTime;
	}

	/**
	 * Gets your end time
	 * 
	 * @return long time
	 */
	public static long getEndTime() {
		return endTime;
	}

	/**
	 * Resets both start and end timers
	 */
	public static void resetTimers() {
		startTime = 0;
		endTime = 0;
		actionTime = 0;
		reactionTime = 0;
	}

	public static int getReactionTime() {
		return reactionTime;
	}

	public static int generateReactionTime() {
		if (getWaitingTime() == 0)
			return 0;
		setProperties();
		generateTrackers();
		return getABC().generateReactionTime();
	}

	/**
	 * Sleeps for the reaction time created by ABC properties
	 * 
	 * @throws InterruptedException
	 */
	public static void sleepReactionTime() {
		sleepReactionTime(null);
	}

	/**
	 * Sleeps for the reaction time created by ABC properties, breaking out when
	 * a Condition is active
	 * 
	 * @throws InterruptedException
	 */
	public static void sleepReactionTime(ResultCondition condition) {
		if (sleepReaction != null && !sleepReaction)
			return;
		try {
			reactionTime = generateReactionTime();
			if (reactionTime == 0)
				return;
			if (reactionTime > MAXIMUM_REACTION) {
				System.out.println("Reaction time of " + reactionTime + " ms is above the maximum sleep. Adjusting...");
				while (reactionTime > MAXIMUM_REACTION)
					reactionTime = (int) (reactionTime * General.randomDouble(.1, .9));
				System.out.println("Reaction time adjusted to " + reactionTime);
			}
			ABCProperties properties = getABC().getProperties();
			System.out.println("[ABC2] Reaction Time: " + reactionTime +
					" ms | Waiting Time: " +
					properties.getWaitingTime() +
					" ms | Hovering: " +
					properties.isHovering() +
					" | Open Menu: " +
					properties.isMenuOpen() +
					" | Combat: " +
					properties.isUnderAttack() +
					" | Waiting: " +
					properties.isWaitingFixed());
			sleep(reactionTime, condition);
		}
		finally {
			resetTimers();
			resetShouldHover();
			resetShouldOpenMenu();
			resetMoveToAnticipated();
		}
	}

	/**
	 * Sleeps the ABC reaction time, breaking out early if a condition is
	 * active.
	 * 
	 * @param int
	 *            time
	 * @param Condition
	 *            condition
	 */
	private static void sleep(int time, ResultCondition condition) {
		SafeThread thread = new SafeThread(new ReactionSleep(time, condition));
		try {
			getABC().sleep(time);
		}
		catch (InterruptedException e) {
			System.out.println("[ABC2] Reaction Sleeping Interrupted.");
		}
		finally {
			thread.stop();
		}
	}

	/**
	 * Thread to sleep for a reaction time breaking out when a condition is
	 * valid.
	 * 
	 * (i.e. Player appears in Wilderness and it is unsafe to continue sleeping)
	 */
	static class ReactionSleep extends VolatileRunnable {
		private final int time;
		private final ResultCondition condition;

		ReactionSleep(int time, ResultCondition condition) {
			this.time = time;
			this.condition = condition;
		}

		@Override
		public void execute() {
			Result result = ResultCondition.wait(time, condition);
			if (result == Result.INTERRUPTED) {
				System.out.println("[ABC2] Reaction condition active!");
				stop();
			}
		}
	}

	/**
	 * Generates trackers from ABC properties
	 * 
	 * Will return if waiting time is 0 to avoid adding erroneous data
	 */
	public static void generateTrackers(int waiting, boolean hovering, boolean menu_open, boolean combat, boolean fixed_waiting) {
		if (waiting == 0)
			return;
		final ABCProperties props = getABC().getProperties();
		props.setWaitingTime(waiting);
		props.setHovering(hovering);
		props.setMenuOpen(menu_open);
		props.setUnderAttack(combat);
		props.setWaitingFixed(fixed_waiting);
		getABC().generateTrackers();
	}

	/**
	 * Generates trackers from the current ABC properties values
	 */
	public static void generateTrackers() {
		getABC().generateTrackers();
	}

	/**
	 * Generates trackers from bit flags Will return if waiting time is 0 to
	 * avoid adding erroneous data
	 */
	public static void generateTrackers(int waiting, long... options) {
		if (waiting == 0)
			return;
		getABC().generateTrackers(getABC().generateBitFlags(waiting, options));
	}

	/**
	 * Selects the next target, using persistence.
	 * 
	 * @return {@link Positionable}
	 */
	public static Positionable selectTarget(final Positionable[] targets) {
		return getABC().selectNextTarget(targets);
	}

	/**
	 * Checks if you should switch resources
	 * 
	 * @param competition_count
	 * @return true if you should switch
	 */
	public static boolean shouldSwitchResources(int competition_count) {
		return getABC().shouldSwitchResources(competition_count);
	}

	/**
	 * Gets your Walking Preference
	 * 
	 * @param int
	 *            distance
	 * @return WalkinPreference
	 */
	public static WalkingPreference getWalkingPreference(int distance) {
		return getABC().generateWalkingPreference(distance);
	}

	/**
	 * Gets your Tab Switching Preference
	 * 
	 * @return TabSwitchPreference
	 */
	public static TabSwitchPreference getTabSwitchPreference() {
		return getABC().generateTabSwitchPreference();
	}

	/**
	 * Gets your Bank Opening Preference
	 * 
	 * @return OpenBankPreference
	 */
	public static OpenBankPreference getOpenBankPreference() {
		return getABC().generateOpenBankPreference();
	}
}
