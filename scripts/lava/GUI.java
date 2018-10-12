package scripts.lava;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.util.Util;

import scripts.lava.Locations.LAVA_DRAGON_LOCATIONS;
import scripts.lava.Spells.CastingStyle;
import scripts.lava.Spells.Spell;
import scripts.usa.api2007.worlds.WorldType;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static JComboBox<TeleportItems> toLavaDragonsItemBox;
	static JComboBox<LAVA_DRAGON_LOCATIONS> locationBox;
	static JComboBox<Spell> spellBox;
	static JComboBox<CastingStyle> castingStyleBox;
	static JSpinner spellsSpinner;
	static JSlider mouseSlider;
	static JCheckBox autoResponderBox;
	static JCheckBox grindScalesBox;
	static JCheckBox trueProfitBox;
	static JCheckBox buryBonesBox;
	static JCheckBox abcReactionBox;
	static JCheckBox abcAntibanBox;
	static JCheckBox paintLootTableBox;
	static JTextField foodText;
	static JSpinner foodSpinner;
	static JSpinner energyPotionSpinner;
	static JSpinner magicPotionSpinner;
	static JSpinner lootSpinner;
	static JCheckBox lootingBagBox;
	static JTextPane equipmentText;
	static JCheckBox smokeBox;
	static JCheckBox occultBox;
	static JCheckBox ignoreBelowBox;
	static JSpinner levelSpinner;
	static JComboBox<WorldType> toDragonBox;
	static JComboBox<WorldType> toBankBox;

	private ArrayList<String> getProfileNames(String name) {
		File folder = new File(Util.getWorkingDirectory() + "/" + name);
		if (!folder.exists())
			folder.mkdir();
		ArrayList<String> files = new ArrayList<>();
		for (File file : folder.listFiles()) {
			if (file.getName()
					.contains(".txt"))
				files.add(file.getName()
						.replaceAll(".txt", ""));
		}
		return files;
	}

	public void saveSettings(String directory, String name) {
		try {
			File folder = new File(Util.getWorkingDirectory() + "/" + directory);
			File file = new File(folder.toString() + "/" + name + ".txt");
			if (!folder.exists())
				folder.mkdir();
			if (file.createNewFile()) {
				System.out.println("Added " + name + " to " + file.toString());
			}
			else {
				System.out.println("Updated " + name + " at " + folder.toString());
			}
			if (file.exists()) {
				Properties prop = new Properties();
				prop.put("toLavaDragonsItem", toLavaDragonsItemBox.getSelectedItem()
						.toString());
				prop.put("location", locationBox.getSelectedItem()
						.toString());
				prop.put("spell", spellBox.getSelectedItem()
						.toString());
				prop.put("style", castingStyleBox.getSelectedItem()
						.toString());
				prop.put("spellsPerTrip", Integer.toString((int) spellsSpinner.getValue()));
				prop.put("mouse", String.valueOf(mouseSlider.getValue()));
				prop.put("occult", String.valueOf(Boolean.valueOf(occultBox.isSelected())));
				prop.put("smoke", String.valueOf(Boolean.valueOf(smokeBox.isSelected())));
				prop.put("autoResponder", String.valueOf(Boolean.valueOf(autoResponderBox.isSelected())));
				prop.put("grind", String.valueOf(Boolean.valueOf(grindScalesBox.isSelected())));
				prop.put("lootingBag", String.valueOf(Boolean.valueOf(lootingBagBox.isSelected())));
				prop.put("trueProfit", String.valueOf(Boolean.valueOf(trueProfitBox.isSelected())));
				prop.put("abcReaction", String.valueOf(Boolean.valueOf(abcReactionBox.isSelected())));
				prop.put("abcAntiban", String.valueOf(Boolean.valueOf(abcAntibanBox.isSelected())));
				prop.put("paintLootTable", String.valueOf(Boolean.valueOf(paintLootTableBox.isSelected())));
				prop.put("bury", String.valueOf(Boolean.valueOf(buryBonesBox.isSelected())));
				prop.put("foodName", foodText.getText());
				prop.put("foodAmount", Integer.toString((int) foodSpinner.getValue()));
				prop.put("energyPotions", Integer.toString((int) energyPotionSpinner.getValue()));
				prop.put("magicPotions", Integer.toString((int) magicPotionSpinner.getValue()));
				prop.put("lootValue", Integer.toString((int) lootSpinner.getValue()));
				prop.put("equipment", equipmentText.getText());
				prop.put("ignorePlayers", String.valueOf(Boolean.valueOf(ignoreBelowBox.isSelected())));
				prop.put("playerLevel", Integer.toString((int) levelSpinner.getValue()));
				prop.put("toDragon", toDragonBox.getSelectedItem()
						.toString());
				prop.put("toBank", toBankBox.getSelectedItem()
						.toString());
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
				toLavaDragonsItemBox.setSelectedItem(TeleportItems.valueOf(prop.getProperty("toLavaDragonsItem")));
				locationBox.setSelectedItem(LAVA_DRAGON_LOCATIONS.valueOf(prop.getProperty("location")));
				spellBox.setSelectedItem(Spell.valueOf(prop.getProperty("spell")));
				castingStyleBox.setSelectedItem(CastingStyle.valueOf(prop.getProperty("style")));
				spellsSpinner.setValue(Integer.parseInt(prop.getProperty("spellsPerTrip")));
				mouseSlider.setValue(Integer.parseInt(prop.getProperty("mouse")));
				occultBox.setSelected(Boolean.parseBoolean(prop.getProperty("occult")));
				smokeBox.setSelected(Boolean.parseBoolean(prop.getProperty("smoke")));
				autoResponderBox.setSelected(Boolean.parseBoolean(prop.getProperty("autoResponder")));
				grindScalesBox.setSelected(Boolean.parseBoolean(prop.getProperty("grind")));
				lootingBagBox.setSelected(Boolean.parseBoolean(prop.getProperty("lootingBag")));
				trueProfitBox.setSelected(Boolean.parseBoolean(prop.getProperty("trueProfit")));
				abcReactionBox.setSelected(Boolean.parseBoolean(prop.getProperty("abcReaction")));
				abcAntibanBox.setSelected(Boolean.parseBoolean(prop.getProperty("abcAntiban")));
				paintLootTableBox.setSelected(Boolean.parseBoolean(prop.getProperty("paintLootTable")));
				buryBonesBox.setSelected(Boolean.parseBoolean(prop.getProperty("bury")));
				foodText.setText(prop.getProperty("foodName"));
				foodSpinner.setValue(Integer.parseInt(prop.getProperty("foodAmount")));
				energyPotionSpinner.setValue(Integer.parseInt(prop.getProperty("energyPotions")));
				magicPotionSpinner.setValue(Integer.parseInt(prop.getProperty("magicPotions")));
				lootSpinner.setValue(Integer.parseInt(prop.getProperty("lootValue")));
				equipmentText.setText(prop.getProperty("equipment"));
				ignoreBelowBox.setSelected(Boolean.parseBoolean(prop.getProperty("ignorePlayers")));
				levelSpinner.setValue(Integer.parseInt(prop.getProperty("playerLevel")));
				toDragonBox.setSelectedItem(WorldType.valueOf(prop.getProperty("toDragon")));
				toBankBox.setSelectedItem(WorldType.valueOf(prop.getProperty("toBank")));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	public GUI() {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 406, 549);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(178, 34, 34));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		JLabel lblNewLabel_2 = new JLabel("USA Lava Dragons");
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("Verdana", Font.BOLD, 24));
		lblNewLabel_2.setBounds(0, 22, 382, 39);
		contentPane.add(lblNewLabel_2);
		JLabel lblV = new JLabel(UsaLavaDragons.version);
		lblV.setForeground(Color.WHITE);
		lblV.setBounds(324, 40, 38, 14);
		contentPane.add(lblV);
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setBounds(21, 75, 350, 390);
		contentPane.add(tabbedPane);
		JPanel mainTab = new JPanel();
		mainTab.setBackground(Color.WHITE);
		tabbedPane.addTab("Main", null, mainTab, null);
		mainTab.setLayout(null);
		JLabel lblLocation = new JLabel("Location:");
		lblLocation.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLocation.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblLocation.setBounds(10, 40, 95, 20);
		mainTab.add(lblLocation);
		locationBox = new JComboBox(LAVA_DRAGON_LOCATIONS.values());
		locationBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		locationBox.setBounds(115, 40, 206, 20);
		mainTab.add(locationBox);
		JLabel lblSpell = new JLabel("Spell:");
		lblSpell.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpell.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblSpell.setBounds(10, 70, 95, 20);
		mainTab.add(lblSpell);
		spellBox = new JComboBox(Spell.values());
		spellBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				Spell spell = (Spell) spellBox.getSelectedItem();
				if (spell.isTrident()) {
					spellsSpinner.setValue(2500);
					spellsSpinner.setEnabled(false);
				}
				else {
					spellsSpinner.setEnabled(true);
				}
			}
		});
		spellBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		spellBox.setBounds(115, 70, 206, 20);
		mainTab.add(spellBox);
		JLabel lblCastingMode = new JLabel("Casting Style:");
		lblCastingMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCastingMode.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblCastingMode.setBounds(10, 100, 95, 20);
		mainTab.add(lblCastingMode);
		castingStyleBox = new JComboBox(CastingStyle.values());
		castingStyleBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		castingStyleBox.setBounds(115, 100, 206, 20);
		mainTab.add(castingStyleBox);
		JLabel lblSpellsPerTrip = new JLabel("Spells Per Trip:");
		lblSpellsPerTrip.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSpellsPerTrip.setBounds(10, 130, 95, 20);
		mainTab.add(lblSpellsPerTrip);
		spellsSpinner = new JSpinner();
		spellsSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		spellsSpinner.setModel(new SpinnerNumberModel(new Integer(1500), new Integer(0), null, new Integer(200)));
		spellsSpinner.setBounds(115, 130, 206, 20);
		mainTab.add(spellsSpinner);
		JSeparator separator_5 = new JSeparator();
		separator_5.setBounds(0, 160, 345, 2);
		mainTab.add(separator_5);
		JLabel lblMouse = new JLabel("Mouse Speed:");
		lblMouse.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMouse.setBounds(10, 175, 79, 40);
		mainTab.add(lblMouse);
		mouseSlider = new JSlider();
		mouseSlider.setBackground(Color.WHITE);
		mouseSlider.setMaximum(200);
		mouseSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		mouseSlider.setPaintLabels(true);
		mouseSlider.setPaintTicks(true);
		mouseSlider.setValue(105);
		mouseSlider.setMajorTickSpacing(20);
		mouseSlider.setMinorTickSpacing(10);
		mouseSlider.setMinimum(100);
		mouseSlider.setBounds(102, 175, 231, 40);
		mainTab.add(mouseSlider);
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 230, 345, 2);
		mainTab.add(separator);
		JSeparator separator_4 = new JSeparator();
		separator_4.setOrientation(SwingConstants.VERTICAL);
		separator_4.setBounds(175, 230, 2, 190);
		mainTab.add(separator_4);
		grindScalesBox = new JCheckBox("Grind Lava scales");
		grindScalesBox.setSelected(true);
		grindScalesBox.setHorizontalAlignment(SwingConstants.LEFT);
		grindScalesBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		grindScalesBox.setBackground(Color.WHITE);
		grindScalesBox.setBounds(10, 240, 154, 23);
		mainTab.add(grindScalesBox);
		autoResponderBox = new JCheckBox("Use Auto Responder");
		autoResponderBox.setHorizontalAlignment(SwingConstants.LEFT);
		autoResponderBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		autoResponderBox.setBackground(Color.WHITE);
		autoResponderBox.setBounds(185, 240, 154, 23);
		mainTab.add(autoResponderBox);
		trueProfitBox = new JCheckBox("Calculate True Profit");
		trueProfitBox.setHorizontalAlignment(SwingConstants.LEFT);
		trueProfitBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		trueProfitBox.setBackground(Color.WHITE);
		trueProfitBox.setBounds(10, 300, 154, 23);
		mainTab.add(trueProfitBox);
		abcReactionBox = new JCheckBox("Enable ABC2 Reaction");
		abcReactionBox.setSelected(true);
		abcReactionBox.setHorizontalAlignment(SwingConstants.LEFT);
		abcReactionBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		abcReactionBox.setBackground(Color.WHITE);
		abcReactionBox.setBounds(185, 270, 154, 23);
		mainTab.add(abcReactionBox);
		abcAntibanBox = new JCheckBox("Enable ABC2 Antiban");
		abcAntibanBox.setSelected(true);
		abcAntibanBox.setHorizontalAlignment(SwingConstants.LEFT);
		abcAntibanBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		abcAntibanBox.setBackground(Color.WHITE);
		abcAntibanBox.setBounds(185, 300, 154, 23);
		mainTab.add(abcAntibanBox);
		paintLootTableBox = new JCheckBox("Paint Looting Table");
		paintLootTableBox.setSelected(true);
		paintLootTableBox.setHorizontalAlignment(SwingConstants.LEFT);
		paintLootTableBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		paintLootTableBox.setBackground(Color.WHITE);
		paintLootTableBox.setBounds(10, 330, 154, 23);
		mainTab.add(paintLootTableBox);
		buryBonesBox = new JCheckBox("Bury Lava Dragon Bones");
		buryBonesBox.setHorizontalAlignment(SwingConstants.LEFT);
		buryBonesBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		buryBonesBox.setBackground(Color.WHITE);
		buryBonesBox.setBounds(10, 270, 154, 23);
		mainTab.add(buryBonesBox);

		JLabel lblTeleportToLava = new JLabel("To Lava Dragons:");
		lblTeleportToLava.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTeleportToLava.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblTeleportToLava.setBounds(10, 10, 95, 20);
		mainTab.add(lblTeleportToLava);

		toLavaDragonsItemBox = new JComboBox(TeleportItems.values());
		toLavaDragonsItemBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		toLavaDragonsItemBox.setBounds(115, 10, 206, 20);
		mainTab.add(toLavaDragonsItemBox);
		JPanel consumables_tab = new JPanel();
		consumables_tab.setBackground(Color.WHITE);
		tabbedPane.addTab("Consumables", null, consumables_tab, null);
		consumables_tab.setLayout(null);
		JLabel lblFoodName = new JLabel("Food Name:");
		lblFoodName.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFoodName.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblFoodName.setBounds(10, 11, 134, 20);
		consumables_tab.add(lblFoodName);
		foodText = new JTextField();
		foodText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		foodText.setHorizontalAlignment(SwingConstants.CENTER);
		foodText.setText("Tuna");
		foodText.setBounds(154, 11, 81, 20);
		consumables_tab.add(foodText);
		foodText.setColumns(10);
		JLabel lblFoodPerTrip = new JLabel("Food Per Trip:");
		lblFoodPerTrip.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFoodPerTrip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblFoodPerTrip.setBounds(10, 44, 134, 20);
		consumables_tab.add(lblFoodPerTrip);
		foodSpinner = new JSpinner();
		foodSpinner.setModel(new SpinnerNumberModel(new Integer(8), new Integer(0), null, new Integer(1)));
		foodSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		foodSpinner.setBounds(154, 44, 81, 20);
		consumables_tab.add(foodSpinner);
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(0, 81, 363, 2);
		consumables_tab.add(separator_1);
		JLabel lblPotionsPerTrip = new JLabel("Energy Potions Per Trip:");
		lblPotionsPerTrip.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPotionsPerTrip.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblPotionsPerTrip.setBounds(10, 94, 134, 20);
		consumables_tab.add(lblPotionsPerTrip);
		energyPotionSpinner = new JSpinner();
		energyPotionSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(0), null, new Integer(1)));
		energyPotionSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		energyPotionSpinner.setBounds(154, 94, 81, 20);
		consumables_tab.add(energyPotionSpinner);
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(0, 127, 363, 2);
		consumables_tab.add(separator_2);
		magicPotionSpinner = new JSpinner();
		magicPotionSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(0), null, new Integer(1)));
		magicPotionSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		magicPotionSpinner.setBounds(154, 140, 81, 20);
		consumables_tab.add(magicPotionSpinner);
		JLabel lblMagicPotionsPer = new JLabel("Magic Potions Per Trip:");
		lblMagicPotionsPer.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMagicPotionsPer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblMagicPotionsPer.setBounds(10, 140, 134, 20);
		consumables_tab.add(lblMagicPotionsPer);
		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(0, 171, 363, 2);
		consumables_tab.add(separator_3);

		JPanel looting_tab = new JPanel();
		looting_tab.setLayout(null);
		looting_tab.setBackground(Color.WHITE);
		tabbedPane.addTab("Looting", null, looting_tab, null);

		JLabel label_4 = new JLabel("Loot items over:");
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		label_4.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		label_4.setBounds(10, 11, 134, 20);
		looting_tab.add(label_4);

		lootSpinner = new JSpinner();
		lootSpinner.setModel(new SpinnerNumberModel(new Integer(2000), null, null, new Integer(1)));
		lootSpinner.setBounds(154, 12, 81, 20);
		looting_tab.add(lootSpinner);

		JLabel label_5 = new JLabel("gp.");
		label_5.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		label_5.setBounds(245, 11, 30, 20);
		looting_tab.add(label_5);

		JSeparator separator_12 = new JSeparator();
		separator_12.setBounds(0, 42, 363, 2);
		looting_tab.add(separator_12);

		lootingBagBox = new JCheckBox("Use Looting Bag");
		lootingBagBox.setSelected(true);
		lootingBagBox.setHorizontalAlignment(SwingConstants.LEFT);
		lootingBagBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lootingBagBox.setBackground(Color.WHITE);
		lootingBagBox.setBounds(61, 51, 154, 23);
		looting_tab.add(lootingBagBox);

		JSeparator separator_6 = new JSeparator();
		separator_6.setBounds(0, 83, 363, 2);
		looting_tab.add(separator_6);
		JPanel equipment_tab = new JPanel();
		equipment_tab.setBackground(Color.WHITE);
		tabbedPane.addTab("Equipment", null, equipment_tab, null);
		equipment_tab.setLayout(null);
		equipmentText = new JTextPane();
		equipmentText.setFont(new Font("Tahoma", Font.PLAIN, 11));
		equipmentText.setBounds(82, 42, 195, 156);
		equipmentText.setEditable(false);
		equipmentText.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		equipment_tab.add(equipmentText);
		JLabel lblNewLabel_1 = new JLabel("Starting Equipment");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblNewLabel_1.setBounds(10, 11, 325, 20);
		equipment_tab.add(lblNewLabel_1);
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				String text = "";
				RSItem[] items = Equipment.getItems();
				for (RSItem r : items) {
					if (r != null) {
						RSItemDefinition d = r.getDefinition();
						if (d != null) {
							String name = d.getName();
							if (name != null) {
								text = text + name + "\r\n";
							}
						}
					}
				}
				equipmentText.setText(text);
			}
		});
		btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnRefresh.setBounds(104, 209, 153, 23);
		equipment_tab.add(btnRefresh);

		occultBox = new JCheckBox("Use Occult necklace");
		occultBox.setHorizontalAlignment(SwingConstants.LEFT);
		occultBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		occultBox.setBackground(Color.WHITE);
		occultBox.setBounds(10, 255, 154, 23);
		equipment_tab.add(occultBox);

		smokeBox = new JCheckBox("Use Smoke battlestaff");
		smokeBox.setHorizontalAlignment(SwingConstants.LEFT);
		smokeBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		smokeBox.setBackground(Color.WHITE);
		smokeBox.setBounds(10, 285, 154, 23);
		equipment_tab.add(smokeBox);

		JSeparator separator_7 = new JSeparator();
		separator_7.setBounds(0, 246, 363, 2);
		equipment_tab.add(separator_7);
		JPanel threat_tab = new JPanel();
		threat_tab.setBackground(Color.WHITE);
		tabbedPane.addTab("Anti-PK", null, threat_tab, null);
		threat_tab.setLayout(null);
		ignoreBelowBox = new JCheckBox("Ignore Players Below Level:");
		ignoreBelowBox.setHorizontalAlignment(SwingConstants.LEFT);
		ignoreBelowBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		ignoreBelowBox.setBackground(Color.WHITE);
		ignoreBelowBox.setBounds(28, 11, 163, 23);
		threat_tab.add(ignoreBelowBox);
		levelSpinner = new JSpinner();
		levelSpinner.setModel(new SpinnerNumberModel(new Integer(10), new Integer(3), null, new Integer(1)));
		levelSpinner.setBounds(193, 13, 42, 20);
		threat_tab.add(levelSpinner);
		JSeparator separator_8 = new JSeparator();
		separator_8.setBounds(0, 44, 363, 2);
		threat_tab.add(separator_8);
		JLabel lblTravelToLava = new JLabel("Travel to Lava Dragon Isle in:");
		lblTravelToLava.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTravelToLava.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblTravelToLava.setBounds(10, 57, 171, 20);
		threat_tab.add(lblTravelToLava);
		toDragonBox = new JComboBox(WorldType.values());
		toDragonBox.setBounds(190, 57, 131, 20);
		threat_tab.add(toDragonBox);
		toBankBox = new JComboBox(WorldType.values());
		toBankBox.setBounds(190, 89, 131, 20);
		threat_tab.add(toBankBox);
		JLabel lblTravelToLevel = new JLabel("Travel to Level 30 Wilderness in:");
		lblTravelToLevel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTravelToLevel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblTravelToLevel.setBounds(10, 88, 171, 20);
		threat_tab.add(lblTravelToLevel);
		JSeparator separator_9 = new JSeparator();
		separator_9.setBounds(0, 122, 363, 2);
		threat_tab.add(separator_9);
		JButton btnLoadSettings = new JButton("Load");
		btnLoadSettings.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				ArrayList<?> files = getProfileNames("USA Lava Dragons");
				String[] profiles = (String[]) files.toArray(new String[files.size()]);
				if (profiles.length == 0) {
					JOptionPane.showMessageDialog(null, "No saved profiles found.");
				}
				else if (profiles.length == 1) {
					loadSettings("USA Lava Dragons", profiles[0]);
				}
				else {
					JComboBox<String> combo = new JComboBox<String>(profiles);
					JOptionPane.showMessageDialog(null, combo, "Select a saved profile", JOptionPane.QUESTION_MESSAGE);
					loadSettings("USA Lava Dragons", combo.getSelectedItem()
							.toString());
				}
			}
		});
		btnLoadSettings.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnLoadSettings.setBounds(21, 476, 110, 23);
		contentPane.add(btnLoadSettings);
		JButton btnSaveSettings = new JButton("Save");
		btnSaveSettings.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Enter a name to save the profile");
				saveSettings("USA Lava Dragons", name);
			}
		});
		btnSaveSettings.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnSaveSettings.setBounds(261, 476, 110, 23);
		contentPane.add(btnSaveSettings);
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				UsaLavaDragons.loadGUI();
			}
		});
		btnStart.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		btnStart.setBounds(146, 476, 100, 23);
		contentPane.add(btnStart);
	}
}
