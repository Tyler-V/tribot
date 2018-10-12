package scripts.thugs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.types.RSPlayerDefinition;
import org.tribot.util.Util;

import scripts.api.v1.api.wilderness.Wilderness;

public class ThreatSearch implements Runnable {

	private static long lastDeathTimer = 0L;
	private static long lastAttackTimer = 0L;

	private volatile boolean stop = false;

	@Override
	public void run() {

		while (!stop) {
			if (Login.getLoginState() == Login.STATE.INGAME) {

				String PLAYER_NAME = Player.getRSPlayer().getName();
				if (PLAYER_NAME != null) {

					int PLAYER_LEVEL = Player.getRSPlayer().getCombatLevel();

					try {
						if (Skills.getCurrentLevel(SKILLS.HITPOINTS) == 0
								&& lastDeathTimer < System.currentTimeMillis()) {
							try {

								SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy, hh-mm-ss a");
								Calendar cal = Calendar.getInstance();
								String date = dateFormat.format(cal.getTime());

								File f1 = new File(Util.getWorkingDirectory() + "/USA Thugs");
								if (!f1.exists())
									f1.mkdir();

								File f2 = new File(Util.getWorkingDirectory() + "/USA Thugs/deaths");
								if (!f2.exists())
									f2.mkdir();

								ImageIO.write(Screen.getGameImage(), "png",
										new File(Util.getWorkingDirectory() + "/USA Thugs/deaths",
												PLAYER_NAME + " " + date + ".png"));

								lastDeathTimer = System.currentTimeMillis() + 5000;

							} catch (IOException e) {

								e.printStackTrace();

							}

						}

						if (!Thugs.isEvade()) {

							int WILDERNESS_LEVEL = Wilderness.getLevel();

							if (WILDERNESS_LEVEL > 0) {

								RSPlayer[] POTENTIAL_ENEMY_PLAYERS = Players.getAll(new Filter<RSPlayer>() {

									public boolean accept(RSPlayer p) {

										if (p == null)
											return false;

										String name = p.getName();

										if (name == null || name.equals(PLAYER_NAME))
											return false;

										if (p.getCombatLevel() < (PLAYER_LEVEL - WILDERNESS_LEVEL))
											return false;

										if (p.getCombatLevel() > (PLAYER_LEVEL + WILDERNESS_LEVEL))
											return false;

										return true;

									}

								});

								for (RSPlayer player : POTENTIAL_ENEMY_PLAYERS) {

									if (player != null) {

										String name = player.getName();

										int level = player.getCombatLevel();

										String item = isWearing(player, Thugs.DANGEROUS_EQUIPMENT);

										boolean threat = false;

										if (player.getSkullIcon() != -1 && player.isInteractingWithMe()) {

											General.println("\"" + name + "\" (" + "Level " + level
													+ ") is skulled and interacting with us "
													+ Player.getPosition().distanceTo(player)
													+ " tile(s) away in (Level: " + WILDERNESS_LEVEL + ") [World: "
													+ WorldHopper.getWorld() + "]");

											threat = true;

										} else if (item != null) {

											General.println("\"" + name + "\" (" + "Level " + level + ") is wearing "
													+ item + " and " + Player.getPosition().distanceTo(player)
													+ " tile(s) away in (Level: " + WILDERNESS_LEVEL + ") [World: "
													+ WorldHopper.getWorld() + "]");

											threat = true;

										}

										if (threat) {

											if (lastAttackTimer < System.currentTimeMillis()) {

												try {

													SimpleDateFormat dateFormat = new SimpleDateFormat(
															"d MMM yyyy, hh-mm-ss a");
													Calendar cal = Calendar.getInstance();
													String date = dateFormat.format(cal.getTime());

													File f1 = new File(Util.getWorkingDirectory() + "/USA Thugs");
													if (!f1.exists())
														f1.mkdir();

													File f2 = new File(Util.getWorkingDirectory() + "/USA Thugs/pkers");
													if (!f2.exists())
														f2.mkdir();

													ImageIO.write(Screen.getGameImage(), "png",
															new File(Util.getWorkingDirectory() + "/USA Thugs/pkers",
																	PLAYER_NAME + " " + date + ".png"));

													lastAttackTimer = System.currentTimeMillis() + 5000;

												} catch (IOException e) {

													e.printStackTrace();

												}

											}

											Thugs.setEvade(true);

										}

									}

								}

							}

						}

					} catch (Exception ex) {

						ex.printStackTrace();

					}

				}

			}

			General.sleep(200);

		}

	}

	public void setStop(boolean stop) {
		System.out.println("Stopped Threat Searching thread.");
		this.stop = stop;
	}

	private String isWearing(RSPlayer player, String... DANGEROUS_EQUIPMENT) {

		if (player == null)
			return null;

		RSPlayerDefinition player_d = player.getDefinition();
		if (player_d == null)
			return null;

		RSItem[] equipment = player_d.getEquipment();
		if (equipment.length == 0)
			return null;

		for (RSItem e : equipment) {

			RSItemDefinition item_d = e.getDefinition();

			if (item_d != null) {

				String name = item_d.getName();

				if (name != null) {

					String copy = name.toLowerCase();

					for (String item : DANGEROUS_EQUIPMENT) {

						if (copy.contains(item.toLowerCase()))
							return name;

					}

				}

			}

		}

		return null;

	}

}