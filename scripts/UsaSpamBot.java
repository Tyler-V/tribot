package scripts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.ABCUtil;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Login;
import org.tribot.api2007.Magic;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Options;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Screen;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.WorldHopper;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSInterfaceMaster;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Painting;
import org.tribot.util.Util;

@ScriptManifest(authors = { "Usa" }, category = "Usa", name = "USA Spam Bot")
public class UsaSpamBot extends Script implements Painting {

	private String version = "4.3";

	private String status = "Starting";
	private LOCATIONS location = null;
	private int count = 0;
	private ArrayList<String> messages = new ArrayList<String>();
	private long startTime;
	private ABCUtil abc;
	private long relog = 0;
	private int mostPlayers = 0;
	private boolean run = true;
	private boolean settings = false;
	private long last_busy_time;

	public void run() {

		startTime = Timing.currentTimeMillis();

		boolean load = false;
		ArrayList<?> files = getProfileNames("USA Spam Bot");
		if (files.size() > 0) {

			int response = JOptionPane.showConfirmDialog(null, "Would you like to load a saved message?", "Confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (response == JOptionPane.YES_OPTION)
				load = true;

		}

		if (load) {

			String[] profiles = (String[]) files.toArray(new String[files.size()]);
			JComboBox<String> combo = new JComboBox<String>(profiles);
			JOptionPane.showMessageDialog(null, combo, "Select a saved profile", JOptionPane.QUESTION_MESSAGE);
			loadSettings("USA Spam Bot", combo.getSelectedItem().toString());

		} else {

			boolean add = true;

			while (add) {

				String input = JOptionPane.showInputDialog(null, "Enter Message: ");

				if (input != null && input.length() > 0)
					messages.add(input);

				int response = JOptionPane.showConfirmDialog(null, "Add another message?", "Confirm",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (response == JOptionPane.NO_OPTION)
					add = false;

			}

			if (messages.size() == 0)
				run = false;

			String name = JOptionPane.showInputDialog("Enter a name to save the settings");
			saveSettings("USA Spam Bot", name);

		}

		ArrayList<LOCATIONS> locationList = new ArrayList<LOCATIONS>();

		for (LOCATIONS loc : LOCATIONS.values()) {
			locationList.add(loc);
		}

		LOCATIONS[] locationArray = new LOCATIONS[locationList.size()];
		locationArray = locationList.toArray(locationArray);
		Object[] options = locationArray;
		Object value = JOptionPane.showInputDialog(null, "Choose a location", "Input", JOptionPane.INFORMATION_MESSAGE,
				null, options, options[0]);
		location = (LOCATIONS) value;

		Camera.setCameraAngle(100);
		abc = new ABCUtil();

		while (run) {

			int players = Players.getAll().length;

			if (players > mostPlayers)
				mostPlayers = players;

			if (location != null) {

				if (Player.getPosition().distanceTo(location.tile) > 2) {

					status = "Walking to " + location.city;
					WebWalking.walkTo(location.tile);

				} else {

					for (int i = 0; i < messages.size(); i++) {

						status = "Typing Message #" + (i + 1);
						Keyboard.typeSend(messages.get(i));
						count++;
						sleep(500, 2000);

					}

				}

			}

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

	private enum LOCATIONS {

		GRAND_EXCHANGE("Varrock", new RSTile(3163, 3486, 0)),

		VARROCK_WEST_BANK("Varrock", new RSTile(3183, 3433, 0)),

		DUEL_ARENA("Duel Arena", new RSTile(3368, 3269, 0)),

		LUMBRIDGE("Lumbridge", new RSTile(3234, 3220, 0));

		private String city;
		private RSTile tile;

		LOCATIONS(String city, RSTile tile) {
			this.city = city;
			this.tile = tile;
		}

	}

	@Override
	public void onPaint(Graphics g) {

		long time = System.currentTimeMillis() - startTime;
		int messagesPerHour = (int) (count * 3600000D / (System.currentTimeMillis() - startTime));

		Color background = new Color(24, 36, 82, 200);
		g.setColor(background);
		g.fillRoundRect(235, 345, 261, 115, 5, 5);
		g.setColor(Color.WHITE);
		g.drawRoundRect(235, 345, 261, 115, 5, 5);

		int x = 240;
		int y = 360;
		int spacing = 15;
		Font bold = new Font("Tahoma", Font.BOLD, 12);
		g.setFont(bold);

		g.drawString("USA Spam Bot             v" + version, x + 85, y);
		g.drawLine(235, 363, 495, 363);
		y += spacing + 3;
		g.drawString("Current World: " + WorldHopper.getWorld(), x, y);
		y += spacing;
		g.drawString("Time: " + Timing.msToString(time), x, y);
		y += spacing;
		g.drawString("Status: " + status, x, y);
		y += spacing;
		g.drawString("Messages Sent: " + count + " (" + messagesPerHour + "/hr)", x, y);
		y += spacing;
		g.drawString("Players In Area: " + Players.getAll().length + " (Max: " + mostPlayers + ")", x, y);
		y += spacing;
		g.drawString("Time until next re-login: " + Timing.msToString(relog - System.currentTimeMillis()), x, y);

	}

	private ArrayList<String> getProfileNames(String name) {
		File folder = new File(Util.getWorkingDirectory() + "/" + name);
		if (!folder.exists())
			folder.mkdir();
		ArrayList<String> files = new ArrayList<>();
		for (File file : folder.listFiles()) {
			files.add(file.getName().replaceAll(".txt", ""));
		}
		return files;
	}

	public void saveSettings(String directory, String name) {
		try {
			File folder = new File(Util.getWorkingDirectory() + "/" + directory);
			File file = new File(folder.toString() + "/" + name + ".txt");
			if (!folder.exists()) {
				folder.mkdir();
			}

			if (file.createNewFile()) {
				System.out.println("Added " + name + " to " + file.toString());
			} else {
				System.out.println("Updated " + name + " at " + folder.toString());
			}
			if (file.exists()) {
				Properties prop = new Properties();
				String concat = "";
				for (String message : messages) {
					concat = concat + " # " + message;
				}
				prop.put("messages", concat);
				prop.store(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"), null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadSettings(String directory, String name) {
		try {
			File folderLocation = new File(Util.getWorkingDirectory() + "/" + directory);
			File newFile = new File(folderLocation.toString() + "/" + name + ".txt");

			if (newFile.exists()) {
				Properties prop = new Properties();
				prop.load(new InputStreamReader(new FileInputStream(newFile), "UTF-8"));
				String concat = prop.getProperty("messages");
				String[] split = concat.split(" # ");
				for (String str : split) {
					if (str.length() > 0) {
						messages.add(str);
						println("Added Message: " + str);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}