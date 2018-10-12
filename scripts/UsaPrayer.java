package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JOptionPane;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Ending;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Prayer")
public class UsaPrayer extends Script implements Painting, Ending, MessageListening07 {

	private String version = "1.4";
	private ABCUtil abc;
	private boolean run = true;
	private int burried = 0;
	private int startXP = 0;
	private int startLVL = 0;
	private long startTime = 0;
	private String status = "Starting...";

	private String[] ALL_BONES = { "Big bones", "Bones" };
	private String BONES = "Bones";
	private String BIG_BONES = "Big bones";
	private RSTile BONE_YARD_TILE = new RSTile(3253, 3740, 0);

	private boolean bank = false;
	private boolean bone_yard = false;

	@Override
	public void run() {

		String[] options = new String[] { "Use bones in bank", "Pickup and bury at the Bone Yard (Level: 28)" };
		int response = JOptionPane.showOptionDialog(null, "USA Prayer: Choose an option!", "Title",
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

		if (response == 0) {
			bank = true;
		} else if (response == 1) {
			bone_yard = true;
		}

		startXP = Skills.getXP(SKILLS.PRAYER);
		startLVL = Skills.getActualLevel(SKILLS.PRAYER);
		startTime = System.currentTimeMillis();
		abc = new ABCUtil();

		while (run) {

			if (bank) {

				if (!buryBones()) {

					if (!withdrawBones()) {

						println("We are out of bones.");
						run = false;

					}

				}

			} else if (bone_yard) {

				if (!eat()) {

					if (Player.getPosition().distanceTo(BONE_YARD_TILE) > 50) {

						status = "Web Walking to Bone Yard";
						WebWalking.walkTo(BONE_YARD_TILE);

					} else {

						if (!buryBones()) {

							if (!pickUpBones(BIG_BONES))
								pickUpBones(BONES);

						}

					}

				}

			}

			abc.performXPCheck(SKILLS.PRAYER);
			abc.performRotateCamera();
			abc.performExamineObject();
			abc.performPickupMouse();
			abc.performRandomMouseMovement();
			abc.performRandomRightClick();
			abc.performQuestsCheck();
			abc.performFriendsCheck();
			abc.performMusicCheck();
			abc.performCombatCheck();

			sleep(100);

		}

	}

	private int getHPPercent() {
		double currentHP = Skills.SKILLS.HITPOINTS.getCurrentLevel();
		double totalHP = Skills.SKILLS.HITPOINTS.getActualLevel();
		return (int) (currentHP / totalHP * 100);
	}

	private boolean eat() {

		if (getHPPercent() <= abc.INT_TRACKER.NEXT_EAT_AT.next() || Inventory.isFull()) {

			if (closeBank() && Inventory.open()) {

				RSItem[] items = Inventory.getAll();

				if (items.length == 0)
					return false;

				for (RSItem item : items) {

					if (item == null)
						return false;

					RSItemDefinition def = item.getDefinition();
					if (def == null)
						return false;

					String name = def.getName();
					if (name == null)
						return false;

					String[] actions = def.getActions();
					if (actions.length == 0)
						return false;

					for (String action : actions) {

						if (action.contains("Eat")) {

							status = "Eating " + name;

							sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());

							final int health = Skills.getCurrentLevel(SKILLS.HITPOINTS);

							if (item.click()) {

								Timing.waitCondition(new Condition() {
									public boolean active() {
										sleep((long) General.randomSD(1000.3465346534654, 300.6103882150994));
										return Skills.getCurrentLevel(SKILLS.HITPOINTS) > (health + 2)
												|| (Skills.getActualLevel(SKILLS.HITPOINTS) == Skills
														.getCurrentLevel(SKILLS.HITPOINTS));
									}
								}, 2000);

								status = "We ate a " + name;
								abc.DELAY_TRACKER.ITEM_INTERACTION.reset();
								abc.INT_TRACKER.NEXT_EAT_AT.reset();

								return true;

							}

						}

					}

				}

			}

		}

		return false;
	}

	private boolean pickUpBones(String names) {

		if (Inventory.isFull() || Inventory.getCount(ALL_BONES) > 0)
			return false;

		status = "Searching for " + names;

		RSGroundItem[] items = GroundItems.findNearest(names);

		if (items.length == 0 || items[0] == null)
			return false;

		RSItemDefinition d = items[0].getDefinition();
		if (d == null)
			return false;

		String name = d.getName();
		if (name == null)
			return false;

		if (!Player.isMoving()) {

			if (Player.getPosition().distanceTo(items[0]) > 3) {

				status = "Walking to " + name;
				PathFinding.aStarWalk(items[0]);

			} else {

				final int before = Inventory.getCount(ALL_BONES);

				if (items[0].click("Take " + names)) {

					status = "Picking up " + name;
					long timer = System.currentTimeMillis() + 1500;

					while (timer > System.currentTimeMillis()) {
						if (Player.isMoving())
							timer = System.currentTimeMillis() + 1500;
						if (Inventory.getCount(ALL_BONES) != before)
							break;
						sleep(200);

					}

				}

			}

		}

		return true;

	}

	private boolean buryBones() {

		RSItem bone = findItemInInventoryWithAction("Bury");
		if (bone == null)
			return false;

		RSItemDefinition def = bone.getDefinition();
		if (def == null)
			return false;

		String name = def.getName();
		if (name == null)
			return false;

		if (closeBank()) {

			sleep(abc.DELAY_TRACKER.ITEM_INTERACTION.next());

			status = "Clicking " + name;

			final int before = Inventory.getCount(name);

			if (bone.click()) {

				burried++;

				status = "Burying " + name;

				Timing.waitCondition(new Condition() {
					public boolean active() {
						sleep(50);
						return Player.getAnimation() != -1 && before != Inventory.getCount(name)
								&& !Player.getRSPlayer().isInCombat();
					}
				}, 1000);

				status = "Burried " + name;

				return true;

			}

			abc.DELAY_TRACKER.ITEM_INTERACTION.reset();

		}

		return false;

	}

	private RSItem findItemInInventoryWithAction(String action) {

		RSItem[] inventory = Inventory.getAll();
		if (inventory.length == 0)
			return null;

		for (RSItem item : inventory) {

			if (item != null) {

				RSItemDefinition def = item.getDefinition();

				if (def != null) {

					String[] actions = def.getActions();

					if (actions.length > 0) {

						for (String a : actions) {

							if (a.contains(action))
								return item;

						}

					}

				}

			}

		}

		return null;

	}

	private boolean withdrawBones() {

		if (openBank()) {

			RSItem[] items = Banking.getAll();
			if (items.length == 0)
				return true;

			for (RSItem item : items) {

				RSItemDefinition def = item.getDefinition();

				if (def != null) {

					String name = def.getName();

					if (name != null) {

						String[] actions = def.getActions();

						if (actions.length > 0) {

							for (String action : actions) {

								if (action.contains("Bury")) {

									Banking.withdrawItem(item, 0);
									status = "Withdrawing " + name;

									sleep((long) General.randomSD(1000, 200));

									return true;

								}

							}

						}

					}

				}

			}

			return false;

		}

		return true;

	}

	private boolean openBank() {

		if (Banking.isBankScreenOpen() && isBankLoaded()) {
			status = "Bank is open";
			return true;
		}

		if (!Banking.isInBank()) {

			WebWalking.walkToBank();

		}

		if (Banking.isInBank()) {

			status = "Opening Bank";

			Banking.openBank();

			long timer = System.currentTimeMillis() + 3000;

			while (timer > System.currentTimeMillis()) {

				if (Player.isMoving())
					timer = System.currentTimeMillis() + 3000;

				if (Banking.isBankScreenOpen() && isBankLoaded()) {
					status = "Bank is open";
					return true;
				}

				sleep(100);

			}

		}

		return false;
	}

	private boolean isBankLoaded() {

		if (!Interfaces.isInterfaceValid(12))
			return false;

		status = "Waiting for bank to load";

		long timer = System.currentTimeMillis() + 5000;

		while (timer > System.currentTimeMillis()) {

			RSInterfaceChild child = Interfaces.get(12, 5);
			if (child == null)
				return false;

			String text = child.getText();
			if (text == null || text.isEmpty())
				return false;

			final int TOTAL_ITEMS = Banking.getAll().length;
			if (TOTAL_ITEMS == 0)
				return false;

			if (Integer.parseInt(text) == TOTAL_ITEMS) {
				status = "Bank is loaded";
				return true;
			}

			sleep(100);

		}

		return false;
	}

	private boolean closeBank() {

		if (!Banking.isBankScreenOpen())
			return true;

		status = "Closing Bank";

		long timer = System.currentTimeMillis() + 3000;

		while (timer > System.currentTimeMillis()) {

			Banking.close();

			if (!Banking.isBankScreenOpen())
				return true;

			sleep(100);

		}

		return false;
	}

	@Override
	public void onEnd() {
		println("We are level " + Skills.getActualLevel(SKILLS.PRAYER) + " prayer. We gained "
				+ (Skills.getActualLevel(SKILLS.PRAYER) - startLVL) + " levels");
		println("We gained " + (Skills.getXP(SKILLS.PRAYER) - startXP) + " XP.");
	}

	private String addCommasToNumericString(String digits) {
		String result = "";
		int len = digits.length();
		int nDigits = 0;

		if (digits.length() < 4)
			return digits;

		for (int i = len - 1; i >= 0; i--) {
			result = digits.charAt(i) + result;
			nDigits++;
			if (((nDigits % 3) == 0) && (i > 0)) {
				result = "," + result;
			}
		}
		return (result);
	}

	@Override
	public void onPaint(Graphics g) {
		long time = System.currentTimeMillis() - startTime;
		int bonesPerHour = (int) (burried * 3600000D / (System.currentTimeMillis() - startTime));
		int xpGained = Skills.getXP(SKILLS.PRAYER) - startXP;
		int xpPerHour = (int) (xpGained * 3600000D / (System.currentTimeMillis() - startTime));
		int currentLVL = Skills.getActualLevel(SKILLS.PRAYER);

		Color background = new Color(24, 36, 82, 200);
		g.setColor(background);
		g.fillRoundRect(235, 345, 261, 132, 5, 5);
		g.setColor(Color.WHITE);
		g.drawRoundRect(235, 345, 261, 132, 5, 5);

		int x = 240;
		int y = 360;
		int spacing = 15;
		Font bold = new Font("Tahoma", Font.BOLD, 12);
		g.setFont(bold);

		g.drawString("USA Prayer                                        v" + version, x, y);
		g.drawLine(235, 363, 495, 363);
		y += spacing + 3;
		g.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		g.drawString("Status: " + status, x, y);
		y += spacing;
		g.drawString("Bones Burried: " + addCommasToNumericString(Integer.toString(burried)) + " ("
				+ addCommasToNumericString(Integer.toString(bonesPerHour)) + "/hr)", x, y);
		y += spacing;
		g.drawString("Prayer Level: " + currentLVL + " (+" + (currentLVL - startLVL) + ")", x, y);
		y += spacing;
		g.drawString("XP Gained: " + addCommasToNumericString(Integer.toString(xpGained)) + " ("
				+ addCommasToNumericString(Integer.toString(xpPerHour)) + "/hr)", x, y);
		y += spacing;

		int xpTNL = Skills.getXPToNextLevel(SKILLS.PRAYER);
		int percentTNL = Skills.getPercentToNextLevel(SKILLS.PRAYER);
		long TTNL = 0;
		if (xpPerHour > 0) {
			TTNL = (long) (((double) xpTNL / (double) xpPerHour) * 3600000);
		}
		int percentFill = (250 * percentTNL) / 100;
		g.setColor(Color.RED);
		g.fillRoundRect(x, y, 250, 16, 5, 5);
		Color green = new Color(10, 150, 10);
		g.setColor(green);
		g.fillRoundRect(x, y, percentFill, 16, 5, 5);
		g.setColor(Color.WHITE);
		g.drawRoundRect(x, y, 250, 16, 5, 5);
		g.drawString(addCommasToNumericString(Integer.toString(xpTNL)) + " xp to " + (currentLVL + 1) + " | "
				+ Timing.msToString(TTNL), x + 40, y + 13);
		g.setColor(Color.BLACK);
		g.fillRoundRect(10, 459, 90, 16, 2, 2);
	}

	@Override
	public void clanMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void duelRequestReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void personalMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverMessageReceived(String e) {
		if (e.contains("Oh dear, you are dead!")) {
			println(e);
			run = false;
		}
	}

	@Override
	public void tradeRequestReceived(String arg0) {
		// TODO Auto-generated method stub

	}

}
