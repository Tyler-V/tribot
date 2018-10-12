package scripts.dragons;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.util.Util;

public class GUI extends JFrame {

	static String[] STARTING_EQUIPMENT;
	static String[] DANGEROUS_EQUIPMENT;

	static JPanel contentPane;
	static JComboBox locationComboBox;
	static JTextField foodNameTextField;
	static JSpinner lootMinSpinner;
	static JSpinner lootMaxSpinner;
	static JSpinner foodQuantitySpinner;
	static JSpinner maxPlayersSpinner;
	static JSpinner specialAttackSpinner;
	static JCheckBox specialAttackCheckBox;
	static JCheckBox quickPrayerCheckBox;
	static JCheckBox autoResponderCheckBox;
	static JCheckBox useABC2Box;
	static JCheckBox deathWalkCheckBox;
	static JCheckBox clueScrollsCheckBox;
	static JComboBox lootingBagOptionBox;
	static JCheckBox useCombatPotionsCheckBox;
	static JCheckBox useSuperPotionsCheckBox;
	static JTextPane dangerousEquipmentText;
	static JTextPane startingEquipmentText;

	private ArrayList<String> getProfileNames(String name) {
		File folder = new File(Util.getWorkingDirectory() + "/" + name);
		if (!folder.exists())
			folder.mkdir();
		ArrayList<String> files = new ArrayList<>();
		for (File file : folder.listFiles()) {
			files.add(file.getName()
					.replaceAll(".txt", ""));
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
			}
			else {
				System.out.println("Updated " + name + " at " + folder.toString());
			}
			if (file.exists()) {
				Properties prop = new Properties();

				prop.put("location", locationComboBox.getSelectedItem()
						.toString());
				prop.put("players", Integer.toString((int) maxPlayersSpinner.getValue()));
				prop.put("responder", String.valueOf(Boolean.valueOf(autoResponderCheckBox.isSelected())));
				prop.put("special", String.valueOf(Boolean.valueOf(specialAttackCheckBox.isSelected())));
				prop.put("specialPercent", Integer.toString((int) specialAttackSpinner.getValue()));
				prop.put("abc", String.valueOf(Boolean.valueOf(useABC2Box.isSelected())));
				prop.put("deathwalk", String.valueOf(Boolean.valueOf(deathWalkCheckBox.isSelected())));
				prop.put("equipment", startingEquipmentText.getText());

				prop.put("minLoot", Integer.toString((int) lootMinSpinner.getValue()));
				prop.put("maxLoot", Integer.toString((int) lootMaxSpinner.getValue()));
				prop.put("clue", String.valueOf(Boolean.valueOf(clueScrollsCheckBox.isSelected())));
				prop.put("bag", lootingBagOptionBox.getSelectedItem()
						.toString());
				prop.put("food", foodNameTextField.getText());
				prop.put("foodQuantity", Integer.toString((int) foodQuantitySpinner.getValue()));
				prop.put("combatPotions", String.valueOf(Boolean.valueOf(useCombatPotionsCheckBox.isSelected())));
				prop.put("superPotions", String.valueOf(Boolean.valueOf(useSuperPotionsCheckBox.isSelected())));

				prop.put("prayer", String.valueOf(Boolean.valueOf(quickPrayerCheckBox.isSelected())));
				prop.put("dangerous", dangerousEquipmentText.getText());

				prop.store(new FileOutputStream(file), null);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadSettings(String directory, String name) {
		try {
			File folderLocation = new File(Util.getWorkingDirectory() + "/" + directory);
			File newFile = new File(folderLocation.toString() + "/" + name + ".txt");
			if (newFile.exists()) {
				Properties prop = new Properties();
				prop.load(new FileInputStream(newFile));

				locationComboBox.setSelectedItem(prop.getProperty("location"));
				maxPlayersSpinner.setValue(Integer.parseInt(prop.getProperty("players")));
				autoResponderCheckBox.setSelected(Boolean.parseBoolean(prop.getProperty("responder")));
				specialAttackCheckBox.setSelected(Boolean.parseBoolean(prop.getProperty("special")));
				specialAttackSpinner.setValue(Integer.parseInt(prop.getProperty("specialPercent")));
				useABC2Box.setSelected(Boolean.parseBoolean(prop.getProperty("abc")));
				deathWalkCheckBox.setSelected(Boolean.parseBoolean(prop.getProperty("deathwalk")));
				startingEquipmentText.setText(prop.getProperty("equipment"));

				lootMinSpinner.setValue(Integer.parseInt(prop.getProperty("minLoot")));
				lootMaxSpinner.setValue(Integer.parseInt(prop.getProperty("maxLoot")));
				clueScrollsCheckBox.setSelected(Boolean.parseBoolean(prop.getProperty("clue")));
				lootingBagOptionBox.setSelectedItem(prop.getProperty("bag"));
				foodNameTextField.setText(prop.getProperty("food"));
				foodQuantitySpinner.setValue(Integer.parseInt(prop.getProperty("foodQuantity")));
				useCombatPotionsCheckBox.setSelected(Boolean.parseBoolean(prop.getProperty("combatPotions")));
				useSuperPotionsCheckBox.setSelected(Boolean.parseBoolean(prop.getProperty("superPotions")));

				quickPrayerCheckBox.setSelected(Boolean.parseBoolean(prop.getProperty("prayer")));
				dangerousEquipmentText.setText(prop.getProperty("dangerous"));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 407, 556);
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(51, 58, 294, 412);
		contentPane.add(tabbedPane);

		JPanel Main = new JPanel();
		Main.setBackground(UIManager.getColor("CheckBox.background"));
		tabbedPane.addTab("Main", null, Main, null);
		Main.setLayout(null);

		locationComboBox = new JComboBox(DRAGON.values());
		locationComboBox.setFont(new Font("Tahoma", Font.PLAIN, 10));
		locationComboBox.setBounds(81, 11, 184, 21);
		Main.add(locationComboBox);

		JLabel lblLocation = new JLabel("Location:");
		lblLocation.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblLocation.setBounds(21, 15, 57, 16);
		Main.add(lblLocation);

		JLabel lblNewLabel = new JLabel("Max players before changing worlds:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(21, 43, 204, 16);
		Main.add(lblNewLabel);

		maxPlayersSpinner = new JSpinner();
		maxPlayersSpinner.setModel(new SpinnerNumberModel(new Integer(3), new Integer(1), null, new Integer(1)));
		maxPlayersSpinner.setBounds(225, 40, 40, 21);
		Main.add(maxPlayersSpinner);

		autoResponderCheckBox = new JCheckBox("Use Auto Responder V2");
		autoResponderCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		autoResponderCheckBox.setBounds(18, 66, 247, 21);
		Main.add(autoResponderCheckBox);

		specialAttackCheckBox = new JCheckBox("Use Special Attack at");
		specialAttackCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		specialAttackCheckBox.setBounds(18, 90, 127, 21);
		Main.add(specialAttackCheckBox);

		specialAttackSpinner = new JSpinner();
		specialAttackSpinner.setModel(new SpinnerNumberModel(100, 0, 100, 5));
		specialAttackSpinner.setBounds(148, 89, 40, 21);
		Main.add(specialAttackSpinner);

		JLabel lblEnergy = new JLabel("% energy");
		lblEnergy.setBounds(192, 90, 55, 21);
		Main.add(lblEnergy);

		useABC2Box = new JCheckBox("Use ABC2 Reaction Timing");
		useABC2Box.setFont(new Font("Tahoma", Font.PLAIN, 11));
		useABC2Box.setBounds(18, 114, 247, 21);
		Main.add(useABC2Box);

		deathWalkCheckBox = new JCheckBox("Use Starting Equipment and Death Walk");
		deathWalkCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
		deathWalkCheckBox.setBounds(18, 138, 223, 21);
		Main.add(deathWalkCheckBox);

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.GRAY);
		separator.setBounds(-36, 165, 325, 1);
		Main.add(separator);

		JLabel lblStartingEquipment = new JLabel("Equipment");
		lblStartingEquipment.setHorizontalAlignment(SwingConstants.CENTER);
		lblStartingEquipment.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblStartingEquipment.setBounds(71, 170, 153, 16);
		Main.add(lblStartingEquipment);

		startingEquipmentText = new JTextPane();
		startingEquipmentText.setFont(new Font("Tahoma", Font.PLAIN, 12));
		startingEquipmentText.setBounds(72, 191, 153, 156);
		Main.add(startingEquipmentText);

		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.GRAY);
		separator_1.setBounds(71, 190, 155, 1);
		Main.add(separator_1);

		JSeparator separator_2 = new JSeparator();
		separator_2.setForeground(Color.GRAY);
		separator_2.setBounds(71, 347, 155, 1);
		Main.add(separator_2);

		JSeparator separator_3 = new JSeparator();
		separator_3.setForeground(Color.GRAY);
		separator_3.setOrientation(SwingConstants.VERTICAL);
		separator_3.setBounds(225, 191, 2, 156);
		Main.add(separator_3);

		JSeparator separator_4 = new JSeparator();
		separator_4.setForeground(Color.GRAY);
		separator_4.setOrientation(SwingConstants.VERTICAL);
		separator_4.setBounds(71, 191, 1, 156);
		Main.add(separator_4);

		JButton equipmentRefreshButton = new JButton("Refresh");
		equipmentRefreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				RSItem[] items = Equipment.getItems();
				for (RSItem r : items) {
					if (r != null) {
						RSItemDefinition d = r.getDefinition();
						if (d != null) {
							String name = d.getName();
							if (name != null)
								sb.append(name + "\r\n");
						}
					}
				}
				startingEquipmentText.setText(sb.toString());
			}
		});
		equipmentRefreshButton.setBounds(107, 355, 89, 21);
		Main.add(equipmentRefreshButton);

		JPanel LootingAndEating = new JPanel();
		tabbedPane.addTab("Looting / Eating / Potions", null, LootingAndEating, null);
		LootingAndEating.setLayout(null);

		JLabel lblLooting = new JLabel("Looting");
		lblLooting.setHorizontalAlignment(SwingConstants.CENTER);
		lblLooting.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblLooting.setBounds(0, 15, 288, 16);
		LootingAndEating.add(lblLooting);

		JSeparator separator_6 = new JSeparator();
		separator_6.setForeground(Color.GRAY);
		separator_6.setBounds(-24, 22, 140, 2);
		LootingAndEating.add(separator_6);

		JSeparator separator_7 = new JSeparator();
		separator_7.setForeground(Color.GRAY);
		separator_7.setBounds(173, 22, 133, 2);
		LootingAndEating.add(separator_7);

		JLabel lblLootAnythingOver = new JLabel("Loot items above");
		lblLootAnythingOver.setBounds(37, 43, 100, 16);
		LootingAndEating.add(lblLootAnythingOver);
		lblLootAnythingOver.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lootMinSpinner = new JSpinner();
		lootMinSpinner.setBounds(145, 42, 67, 21);
		LootingAndEating.add(lootMinSpinner);
		lootMinSpinner.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(0), null, new Integer(100)));

		JLabel lblNewLabel_1 = new JLabel("gp\r\n");
		lblNewLabel_1.setBounds(217, 45, 20, 14);
		LootingAndEating.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("and below");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_2.setBounds(37, 70, 100, 14);
		LootingAndEating.add(lblNewLabel_2);

		lootMaxSpinner = new JSpinner();
		lootMaxSpinner.setModel(new SpinnerNumberModel(new Integer(50000), new Integer(0), null, new Integer(10000)));
		lootMaxSpinner.setBounds(145, 68, 67, 21);
		LootingAndEating.add(lootMaxSpinner);

		JLabel label = new JLabel("gp\r\n");
		label.setBounds(217, 71, 20, 14);
		LootingAndEating.add(label);

		clueScrollsCheckBox = new JCheckBox("Pickup Clue Scrolls?");
		clueScrollsCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		clueScrollsCheckBox.setBounds(0, 95, 288, 23);
		LootingAndEating.add(clueScrollsCheckBox);

		JLabel lblDepositItemsInto = new JLabel("Use Looting Bag when");
		lblDepositItemsInto.setHorizontalAlignment(SwingConstants.LEFT);
		lblDepositItemsInto.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDepositItemsInto.setBounds(10, 125, 129, 20);
		LootingAndEating.add(lblDepositItemsInto);

		lootingBagOptionBox = new JComboBox(LOOTING_BAG_OPTION.values());
		lootingBagOptionBox.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lootingBagOptionBox.setBounds(140, 125, 140, 20);
		LootingAndEating.add(lootingBagOptionBox);

		JLabel lblEating = new JLabel("Eating");
		lblEating.setHorizontalAlignment(SwingConstants.CENTER);
		lblEating.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblEating.setBounds(3, 156, 288, 16);
		LootingAndEating.add(lblEating);

		JSeparator separator_8 = new JSeparator();
		separator_8.setForeground(Color.GRAY);
		separator_8.setBounds(0, 163, 123, 2);
		LootingAndEating.add(separator_8);

		JSeparator separator_9 = new JSeparator();
		separator_9.setForeground(Color.GRAY);
		separator_9.setBounds(172, 163, 133, 2);
		LootingAndEating.add(separator_9);

		JLabel lblFood = new JLabel("Food:");
		lblFood.setBounds(34, 184, 35, 16);
		LootingAndEating.add(lblFood);
		lblFood.setFont(new Font("Tahoma", Font.PLAIN, 12));

		foodQuantitySpinner = new JSpinner();
		foodQuantitySpinner.setBounds(71, 181, 40, 21);
		LootingAndEating.add(foodQuantitySpinner);
		foodQuantitySpinner.setModel(new SpinnerNumberModel(new Integer(24), new Integer(0), null, new Integer(1)));

		foodNameTextField = new JTextField();
		foodNameTextField.setBounds(117, 181, 86, 21);
		LootingAndEating.add(foodNameTextField);
		foodNameTextField.setHorizontalAlignment(SwingConstants.CENTER);
		foodNameTextField.setText("Tuna");
		foodNameTextField.setColumns(10);

		JLabel lblPotions = new JLabel("Potions");
		lblPotions.setHorizontalAlignment(SwingConstants.CENTER);
		lblPotions.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblPotions.setBounds(0, 220, 288, 16);
		LootingAndEating.add(lblPotions);

		JSeparator separator_14 = new JSeparator();
		separator_14.setForeground(Color.GRAY);
		separator_14.setBounds(3, 228, 113, 2);
		LootingAndEating.add(separator_14);

		JSeparator separator_15 = new JSeparator();
		separator_15.setForeground(Color.GRAY);
		separator_15.setBounds(175, 228, 150, 2);
		LootingAndEating.add(separator_15);

		useCombatPotionsCheckBox = new JCheckBox("Use Combat Potions?");
		useCombatPotionsCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		useCombatPotionsCheckBox.setBounds(0, 243, 288, 23);
		LootingAndEating.add(useCombatPotionsCheckBox);

		useSuperPotionsCheckBox = new JCheckBox("Use Super Attack & Super Strength potions?");
		useSuperPotionsCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		useSuperPotionsCheckBox.setBounds(0, 269, 288, 23);
		LootingAndEating.add(useSuperPotionsCheckBox);

		JSeparator separator_16 = new JSeparator();
		separator_16.setForeground(Color.GRAY);
		separator_16.setBounds(-24, 304, 373, 2);
		LootingAndEating.add(separator_16);

		JPanel Evasion = new JPanel();
		tabbedPane.addTab("Anti-PK", null, Evasion, null);
		Evasion.setLayout(null);

		JLabel lblDangerousEquipment = new JLabel("Dangerous Equipment");
		lblDangerousEquipment.setBounds(10, 11, 265, 16);
		Evasion.add(lblDangerousEquipment);
		lblDangerousEquipment.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDangerousEquipment.setHorizontalAlignment(SwingConstants.CENTER);

		dangerousEquipmentText = new JTextPane();
		dangerousEquipmentText.setFont(new Font("Tahoma", Font.PLAIN, 12));
		dangerousEquipmentText.setBounds(59, 31, 164, 295);
		Evasion.add(dangerousEquipmentText);
		dangerousEquipmentText.setText("Staff\r\nMystic\r\nZamorak cape\r\nGuthix cape\r\nSaradomin cape\r\nEnchanted\r\nInfinity\r\nAhrim\r\nGhostly\r\nVoid\r\nMage's book\r\nBook of darkness\r\nOccult necklace");
		dangerousEquipmentText.setBackground(Color.WHITE);

		quickPrayerCheckBox = new JCheckBox("Use Quick Prayer when evading?");
		quickPrayerCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
		quickPrayerCheckBox.setBounds(10, 333, 265, 21);
		Evasion.add(quickPrayerCheckBox);
		quickPrayerCheckBox.setBackground(UIManager.getColor("CheckBox.background"));
		quickPrayerCheckBox.setFont(new Font("Tahoma", Font.PLAIN, 11));

		JSeparator separator_5 = new JSeparator();
		separator_5.setForeground(Color.GRAY);
		separator_5.setBounds(58, 29, 166, 2);
		Evasion.add(separator_5);

		JSeparator separator_11 = new JSeparator();
		separator_11.setOrientation(SwingConstants.VERTICAL);
		separator_11.setForeground(Color.GRAY);
		separator_11.setBounds(57, 29, 2, 297);
		Evasion.add(separator_11);

		JSeparator separator_13 = new JSeparator();
		separator_13.setOrientation(SwingConstants.VERTICAL);
		separator_13.setForeground(Color.GRAY);
		separator_13.setBounds(223, 29, 2, 297);
		Evasion.add(separator_13);

		JSeparator separator_12 = new JSeparator();
		separator_12.setForeground(Color.GRAY);
		separator_12.setBounds(57, 326, 166, 2);
		Evasion.add(separator_12);

		JLabel lblUsaDragonKiller = new JLabel("USA Dragon Killer");
		lblUsaDragonKiller.setForeground(Color.GREEN);
		lblUsaDragonKiller.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsaDragonKiller.setFont(new Font("Tahoma", Font.BOLD, 24));
		lblUsaDragonKiller.setBounds(0, 11, 391, 36);
		contentPane.add(lblUsaDragonKiller);

		JLabel lblV = new JLabel(UsaDragonKiller.version);
		lblV.setForeground(Color.GREEN);
		lblV.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblV.setBounds(312, 28, 46, 16);
		contentPane.add(lblV);

		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UsaDragonKiller.loadGUI();
			}
		});
		startButton.setForeground(Color.BLACK);
		startButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
		startButton.setBounds(157, 483, 89, 23);
		contentPane.add(startButton);

		JButton loadSettingsButton = new JButton("Load");
		loadSettingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<?> files = getProfileNames("USA Dragon Killer");
				String[] profiles = (String[]) files.toArray(new String[files.size()]);
				if (profiles.length == 0) {
					JOptionPane.showMessageDialog(null, "No saved profiles found.");
				}
				else if (profiles.length == 1) {
					loadSettings("USA Dragon Killer", profiles[0]);
				}
				else {
					JComboBox<String> combo = new JComboBox<String>(profiles);
					JOptionPane.showMessageDialog(null, combo, "Select a saved profile", JOptionPane.QUESTION_MESSAGE);
					loadSettings("USA Dragon Killer", combo.getSelectedItem()
							.toString());
				}
			}
		});
		loadSettingsButton.setBounds(22, 483, 110, 23);
		contentPane.add(loadSettingsButton);

		JButton saveSettingsButton = new JButton("Save");
		saveSettingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Enter a name to save the profile");
				saveSettings("USA Dragon Killer", name);
			}
		});
		saveSettingsButton.setBounds(271, 483, 110, 23);
		contentPane.add(saveSettingsButton);
	}
}
