package scripts.tutorial;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.tribot.api.General;
import org.tribot.util.Util;

import scripts.usa.api.GameSettings.RunOptions;
import scripts.usa.api.util.Profile;
import scripts.usa.api.web.captcha.AccountCreator;
import scripts.usa.api.web.methods.Web;

public class GUI extends JFrame {

	String version;

	// MAIN
	static JPanel contentPane;
	static JComboBox locationBox;
	static JComboBox itemBox;
	static JComboBox endingActionBox;
	static JCheckBox randomizeConfigurationOption;
	static JCheckBox turnOffMusicBox;
	static JCheckBox setMaxBrightnessBox;
	static JCheckBox removeRoofsBox;

	// CONFIGURATION
	static JSlider generalSleepSlider;
	static JSlider continueChatSlider;
	static JSlider hoverSlider;
	static JSpinner designArrowSpinner;
	static JSpinner minArrowSpinner;
	static JSpinner maxArrowSpinner;
	static JComboBox<Gender> genderBox;
	static JComboBox<RunOptions> runBox;
	static JComboBox questGuidePathBox;
	static JComboBox brotherBracePathBox;
	static JComboBox magicInstructorPathBox;
	static JCheckBox wieldWeaponsCheckbox;
	static JCheckBox catchTwoShrimpsCheckbox;
	static JCheckBox useKeyboardCheckbox;

	// LOAD ACCOUNTS
	static JCheckBox useLoadOption;
	static JTextField loadUsernameText;
	static JTextField loadPasswordText;
	static JList<String> createdAccountsList;
	static DefaultListModel<String> accountModel;
	static JScrollPane accountScrollPane;

	// CREATE ACCOUNTS
	static JCheckBox useCreateOption;
	static JTextField captchaText;
	static JSpinner createAccountsSpinner;
	static JSlider createAgeSlider;
	static JTextField createDisplayText;
	static JTextField createEmailText;
	static JTextField createPasswordText;
	static JTextField generatorText;

	private void loadProfile(String folderName) {
		String fileName = null;
		ArrayList<?> files = Profile.getSavedProfiles(folderName);
		String[] profiles = (String[]) files.toArray(new String[files.size()]);
		if (profiles.length == 0) {
			JOptionPane.showMessageDialog(null, "No saved profiles found!");
		} else if (profiles.length == 1) {
			fileName = profiles[0];
		} else {
			JComboBox<String> options = new JComboBox<String>(profiles);
			JOptionPane.showMessageDialog(null, options, "Select a saved profile", JOptionPane.QUESTION_MESSAGE);
			fileName = options.getSelectedItem().toString();
		}
		if (fileName == null) {
			System.out.println("No profile was selected!");
			return;
		}
		try {
			File folder = new File(Util.getWorkingDirectory() + "/" + folderName);
			File file = new File(folder.toString() + "/" + fileName + ".txt");
			if (file.exists()) {
				Properties prop = new Properties();
				prop.load(new FileInputStream(file));

				locationBox.setSelectedItem(Location.valueOf(prop.getProperty("location")));
				itemBox.setSelectedItem(ItemOptions.valueOf(prop.getProperty("itemAction")));
				endingActionBox.setSelectedItem(LogOptions.valueOf(prop.getProperty("logAction")));
				randomizeConfigurationOption
						.setSelected(Boolean.parseBoolean(prop.getProperty("randomizeConfiguration")));
				turnOffMusicBox.setSelected(Boolean.parseBoolean(prop.getProperty("turnOffMusic")));
				setMaxBrightnessBox.setSelected(Boolean.parseBoolean(prop.getProperty("setMaxBrightness")));
				removeRoofsBox.setSelected(Boolean.parseBoolean(prop.getProperty("removeRoofs")));

				generalSleepSlider.setValue(Integer.parseInt(prop.getProperty("generalSleep")));
				continueChatSlider.setValue(Integer.parseInt(prop.getProperty("continueSleep")));
				hoverSlider.setValue(Integer.parseInt(prop.getProperty("hoverSleep")));
				designArrowSpinner.setValue(Integer.parseInt(prop.getProperty("designArrow")));
				minArrowSpinner.setValue(Integer.parseInt(prop.getProperty("minArrowClicks")));
				maxArrowSpinner.setValue(Integer.parseInt(prop.getProperty("maxArrowClicks")));
				genderBox.setSelectedItem(prop.getProperty("gender"));
				runBox.setSelectedItem(prop.getProperty("run"));
				questGuidePathBox.setSelectedItem(prop.getProperty("questGuidePath"));
				brotherBracePathBox.setSelectedItem(prop.getProperty("brotherBracePath"));
				magicInstructorPathBox.setSelectedItem(prop.getProperty("magicInstructorPath"));
				wieldWeaponsCheckbox.setSelected(Boolean.parseBoolean(prop.getProperty("wieldWeapons")));
				catchTwoShrimpsCheckbox.setSelected(Boolean.parseBoolean(prop.getProperty("catchTwoShrimps")));
				useKeyboardCheckbox.setSelected(Boolean.parseBoolean(prop.getProperty("useKeyboard")));

				useCreateOption.setSelected(Boolean.parseBoolean(prop.getProperty("createAccounts")));
				createAccountsSpinner.setValue(Integer.parseInt(prop.getProperty("createAmount")));
				captchaText.setText(prop.getProperty("2captcha"));
				createAgeSlider.setValue(Integer.parseInt(prop.getProperty("age")));
				createDisplayText.setText(prop.getProperty("createDisplay"));
				createEmailText.setText(prop.getProperty("createEmail"));
				createPasswordText.setText(prop.getProperty("createPassword"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveProfile(String folderName) {
		Map<String, Object> properties = new LinkedHashMap<>();
		properties.put("location", locationBox.getSelectedItem().toString());
		properties.put("itemAction", itemBox.getSelectedItem().toString());
		properties.put("logAction", endingActionBox.getSelectedItem().toString());
		properties.put("randomizeConfiguration",
				String.valueOf(Boolean.valueOf(randomizeConfigurationOption.isSelected())));
		properties.put("turnOffMusic", String.valueOf(Boolean.valueOf(turnOffMusicBox.isSelected())));
		properties.put("setMaxBrightness", String.valueOf(Boolean.valueOf(setMaxBrightnessBox.isSelected())));
		properties.put("removeRoofs", String.valueOf(Boolean.valueOf(removeRoofsBox.isSelected())));

		properties.put("generalSleep", Integer.toString((int) generalSleepSlider.getValue()));
		properties.put("continueSleep", Integer.toString((int) continueChatSlider.getValue()));
		properties.put("hoverSleep", Integer.toString((int) hoverSlider.getValue()));
		properties.put("designArrow", Integer.toString((int) designArrowSpinner.getValue()));
		properties.put("minArrowClicks", Integer.toString((int) minArrowSpinner.getValue()));
		properties.put("maxArrowClicks", Integer.toString((int) maxArrowSpinner.getValue()));
		properties.put("gender", genderBox.getSelectedItem().toString());
		properties.put("run", runBox.getSelectedItem().toString());
		properties.put("questGuidePath", questGuidePathBox.getSelectedItem().toString());
		properties.put("brotherBracePath", brotherBracePathBox.getSelectedItem().toString());
		properties.put("magicInstructorPath", magicInstructorPathBox.getSelectedItem().toString());
		properties.put("wieldWeapons", String.valueOf(Boolean.valueOf(wieldWeaponsCheckbox.isSelected())));
		properties.put("catchTwoShrimps", String.valueOf(Boolean.valueOf(catchTwoShrimpsCheckbox.isSelected())));
		properties.put("useKeyboard", String.valueOf(Boolean.valueOf(useKeyboardCheckbox.isSelected())));

		properties.put("createAccounts", String.valueOf(Boolean.valueOf(useCreateOption.isSelected())));
		properties.put("createAmount", Integer.toString((int) createAccountsSpinner.getValue()));
		properties.put("2captcha", captchaText.getText());
		properties.put("age", Integer.toString((int) createAgeSlider.getValue()));
		properties.put("createDisplay", createDisplayText.getText());
		properties.put("createEmail", createEmailText.getText());
		properties.put("createPassword", createPasswordText.getText());
		Profile.saveProfile(folderName, properties);
	}

	public GUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 371, 723);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 102, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblVersion = new JLabel(version);
		lblVersion.setText(UsaTutorial.version);
		lblVersion.setForeground(new Color(255, 255, 255));
		lblVersion.setBounds(316, 55, 30, 14);
		contentPane.add(lblVersion);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 10));
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(15, 50, 323, 621);
		contentPane.add(tabbedPane);

		JPanel Main = new JPanel();
		Main.setBackground(Color.WHITE);
		Main.setBorder(null);
		tabbedPane.addTab("Main", null, Main, null);
		tabbedPane.setBackgroundAt(0, Color.WHITE);
		Main.setLayout(null);

		JLabel title = new JLabel("USA Tutorial Island");
		title.setFont(new Font("Verdana", Font.BOLD, 24));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBounds(0, 5, 318, 38);
		Main.add(title);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 50, 318, 2);
		Main.add(separator);

		locationBox = new JComboBox();
		locationBox.setModel(new DefaultComboBoxModel(Location.values()));
		locationBox.setBounds(10, 92, 298, 22);
		Main.add(locationBox);

		JLabel lblAfterCompletion = new JLabel("After Completion of Tutorial, Travel to...");
		lblAfterCompletion.setHorizontalAlignment(SwingConstants.CENTER);
		lblAfterCompletion.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblAfterCompletion.setBounds(0, 60, 318, 20);
		Main.add(lblAfterCompletion);

		itemBox = new JComboBox();
		itemBox.setModel(new DefaultComboBoxModel(ItemOptions.values()));
		itemBox.setBounds(10, 123, 298, 22);
		Main.add(itemBox);

		endingActionBox = new JComboBox();
		endingActionBox.setModel(new DefaultComboBoxModel(LogOptions.values()));
		endingActionBox.setBounds(10, 154, 298, 22);
		Main.add(endingActionBox);

		JSeparator separator_10 = new JSeparator();
		separator_10.setBounds(0, 190, 318, 2);
		Main.add(separator_10);

		randomizeConfigurationOption = new JCheckBox("Randomize Configuration after each account");
		randomizeConfigurationOption.setBackground(Color.WHITE);
		randomizeConfigurationOption.setBounds(10, 200, 294, 23);
		Main.add(randomizeConfigurationOption);

		JSeparator separator_21 = new JSeparator();
		separator_21.setBounds(0, 230, 318, 2);
		Main.add(separator_21);

		turnOffMusicBox = new JCheckBox("Turn Music Off");
		turnOffMusicBox.setBackground(Color.WHITE);
		turnOffMusicBox.setBounds(10, 240, 294, 23);
		Main.add(turnOffMusicBox);

		JSeparator separator_12 = new JSeparator();
		separator_12.setBounds(0, 270, 318, 2);
		Main.add(separator_12);

		setMaxBrightnessBox = new JCheckBox("Set Maximum Brightness");
		setMaxBrightnessBox.setBackground(Color.WHITE);
		setMaxBrightnessBox.setBounds(10, 279, 294, 23);
		Main.add(setMaxBrightnessBox);

		JSeparator separator_22 = new JSeparator();
		separator_22.setBounds(0, 309, 318, 2);
		Main.add(separator_22);

		removeRoofsBox = new JCheckBox("Remove Roofs");
		removeRoofsBox.setBackground(Color.WHITE);
		removeRoofsBox.setBounds(10, 318, 294, 23);
		Main.add(removeRoofsBox);

		JSeparator separator_23 = new JSeparator();
		separator_23.setBounds(0, 348, 318, 2);
		Main.add(separator_23);

		JPanel Configuration = new JPanel();
		Configuration.setBorder(null);
		Configuration.setBackground(Color.WHITE);
		Configuration.setBorder(new LineBorder(UIManager.getColor("Button.shadow")));
		tabbedPane.addTab("Configuration", null, Configuration, null);
		tabbedPane.setBackgroundAt(1, Color.WHITE);
		Configuration.setLayout(null);

		JButton btnRandomizeProfile = new JButton("Randomize Profile");
		btnRandomizeProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Random r = new Random();
				int value = 0;
				generalSleepSlider.setValue(General.random(1000, 3000));
				hoverSlider.setValue(General.random(200, 1000));
				continueChatSlider.setValue(General.random(1000, 3000));
				designArrowSpinner.setValue(General.random(0, 10));
				minArrowSpinner.setValue(General.random(0, 1));
				maxArrowSpinner.setValue(General.random(1, 3));
				value = General.random(0, 1);
				genderBox.setSelectedIndex(value);
				value = General.random(0, 1);
				runBox.setSelectedIndex(value);
				value = General.random(0, 2);
				questGuidePathBox.setSelectedIndex(value);
				value = General.random(0, 1);
				brotherBracePathBox.setSelectedIndex(value);
				value = General.random(0, 2);
				magicInstructorPathBox.setSelectedIndex(value);
				wieldWeaponsCheckbox.setSelected(r.nextBoolean());
				catchTwoShrimpsCheckbox.setSelected(r.nextBoolean());
				useKeyboardCheckbox.setSelected(r.nextBoolean());
			}
		});
		btnRandomizeProfile.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnRandomizeProfile.setBounds(69, 9, 173, 23);
		Configuration.add(btnRandomizeProfile);

		JSeparator separator_9 = new JSeparator();
		separator_9.setBounds(0, 40, 318, 2);
		Configuration.add(separator_9);

		JLabel lblGeneralSleep = new JLabel("General Sleep:");
		lblGeneralSleep.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblGeneralSleep.setBounds(10, 63, 82, 14);
		Configuration.add(lblGeneralSleep);

		generalSleepSlider = new JSlider();
		generalSleepSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		generalSleepSlider.setMajorTickSpacing(500);
		generalSleepSlider.setValue(1500);
		generalSleepSlider.setPaintTicks(true);
		generalSleepSlider.setMinorTickSpacing(250);
		generalSleepSlider.setMinimum(1000);
		generalSleepSlider.setMaximum(3000);
		generalSleepSlider.setPaintLabels(true);
		generalSleepSlider.setBackground(Color.WHITE);
		generalSleepSlider.setBounds(96, 53, 212, 39);
		Configuration.add(generalSleepSlider);

		JSeparator separator_0 = new JSeparator();
		separator_0.setBounds(0, 103, 318, 2);
		Configuration.add(separator_0);

		JLabel lblContinueChatSleep = new JLabel("Continue Chat Sleep:");
		lblContinueChatSleep.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblContinueChatSleep.setBounds(10, 125, 119, 14);
		Configuration.add(lblContinueChatSleep);

		continueChatSlider = new JSlider();
		continueChatSlider.setValue(750);
		continueChatSlider.setPaintTicks(true);
		continueChatSlider.setPaintLabels(true);
		continueChatSlider.setMinorTickSpacing(250);
		continueChatSlider.setMinimum(1000);
		continueChatSlider.setMaximum(3000);
		continueChatSlider.setMajorTickSpacing(500);
		continueChatSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		continueChatSlider.setBackground(Color.WHITE);
		continueChatSlider.setBounds(134, 115, 173, 39);
		Configuration.add(continueChatSlider);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(0, 165, 318, 2);
		Configuration.add(separator_1);

		JLabel lblHoverNpcobjectSleep = new JLabel("Hover NPC/Object Sleep:");
		lblHoverNpcobjectSleep.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblHoverNpcobjectSleep.setBounds(10, 191, 141, 14);
		Configuration.add(lblHoverNpcobjectSleep);

		hoverSlider = new JSlider();
		hoverSlider.setValue(1500);
		hoverSlider.setPaintTicks(true);
		hoverSlider.setPaintLabels(true);
		hoverSlider.setMinorTickSpacing(100);
		hoverSlider.setMinimum(200);
		hoverSlider.setMaximum(1000);
		hoverSlider.setMajorTickSpacing(200);
		hoverSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		hoverSlider.setBackground(Color.WHITE);
		hoverSlider.setBounds(153, 181, 155, 39);
		Configuration.add(hoverSlider);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(0, 231, 318, 2);
		Configuration.add(separator_2);

		JLabel lblDesignArrows = new JLabel("Amount of design arrows to ignore:");
		lblDesignArrows.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDesignArrows.setBounds(10, 238, 206, 20);
		Configuration.add(lblDesignArrows);

		designArrowSpinner = new JSpinner();
		designArrowSpinner.setModel(new SpinnerNumberModel(10, 0, 24, 2));
		designArrowSpinner.setBounds(212, 238, 40, 20);
		Configuration.add(designArrowSpinner);

		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(0, 264, 318, 2);
		Configuration.add(separator_3);

		JLabel lblMinChanges = new JLabel("Minimum amount of arrow clicks:");
		lblMinChanges.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblMinChanges.setBounds(10, 271, 185, 20);
		Configuration.add(lblMinChanges);

		minArrowSpinner = new JSpinner();
		minArrowSpinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		minArrowSpinner.setBounds(212, 271, 40, 20);
		Configuration.add(minArrowSpinner);

		JSeparator separator_4 = new JSeparator();
		separator_4.setBounds(0, 298, 318, 2);
		Configuration.add(separator_4);

		JLabel lblMaxChanges = new JLabel("Maximum amount of arrow clicks:");
		lblMaxChanges.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblMaxChanges.setBounds(10, 304, 185, 20);
		Configuration.add(lblMaxChanges);

		maxArrowSpinner = new JSpinner();
		maxArrowSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		maxArrowSpinner.setBounds(212, 304, 40, 20);
		Configuration.add(maxArrowSpinner);

		JSeparator separator_5 = new JSeparator();
		separator_5.setBounds(0, 329, 318, 2);
		Configuration.add(separator_5);

		JLabel lblCharacterGender = new JLabel("Character Gender:");
		lblCharacterGender.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCharacterGender.setBounds(10, 337, 105, 20);
		Configuration.add(lblCharacterGender);

		genderBox = new JComboBox();
		genderBox.setModel(new DefaultComboBoxModel<Gender>(Gender.values()));
		genderBox.setBounds(117, 337, 135, 20);
		Configuration.add(genderBox);

		JSeparator separator_6 = new JSeparator();
		separator_6.setBounds(0, 364, 318, 2);
		Configuration.add(separator_6);

		JLabel lblSetRunFrom = new JLabel("Set run from:");
		lblSetRunFrom.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSetRunFrom.setBounds(10, 374, 80, 20);
		Configuration.add(lblSetRunFrom);

		runBox = new JComboBox();
		runBox.setModel(new DefaultComboBoxModel(RunOptions.values()));
		runBox.setBounds(91, 374, 161, 20);
		Configuration.add(runBox);

		JSeparator separator_7 = new JSeparator();
		separator_7.setBounds(0, 402, 318, 2);
		Configuration.add(separator_7);

		questGuidePathBox = new JComboBox();
		questGuidePathBox.setModel(new DefaultComboBoxModel(new String[] { "PATH ONE", "PATH TWO", "PATH THREE" }));
		questGuidePathBox.setBounds(140, 413, 112, 20);
		Configuration.add(questGuidePathBox);

		JLabel lblQuestGuide = new JLabel("Quest Guide Path:");
		lblQuestGuide.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblQuestGuide.setBounds(10, 413, 130, 20);
		Configuration.add(lblQuestGuide);

		brotherBracePathBox = new JComboBox();
		brotherBracePathBox.setModel(new DefaultComboBoxModel(new String[] { "PATH ONE", "PATH TWO" }));
		brotherBracePathBox.setBounds(140, 439, 112, 20);
		Configuration.add(brotherBracePathBox);

		JLabel lblBrotherBrace = new JLabel("Brother Brace Path:");
		lblBrotherBrace.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBrotherBrace.setBounds(10, 439, 130, 20);
		Configuration.add(lblBrotherBrace);

		magicInstructorPathBox = new JComboBox();
		magicInstructorPathBox
				.setModel(new DefaultComboBoxModel(new String[] { "PATH ONE", "PATH TWO", "PATH THREE" }));
		magicInstructorPathBox.setBounds(140, 465, 112, 20);
		Configuration.add(magicInstructorPathBox);

		JLabel lblMagicInstructor = new JLabel("Magic Instructor Path:");
		lblMagicInstructor.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblMagicInstructor.setBounds(10, 465, 130, 20);
		Configuration.add(lblMagicInstructor);

		JSeparator separator_8 = new JSeparator();
		separator_8.setBounds(0, 496, 318, 2);
		Configuration.add(separator_8);

		wieldWeaponsCheckbox = new JCheckBox("Wield all Weapons from Inventory");
		wieldWeaponsCheckbox.setFont(new Font("Tahoma", Font.PLAIN, 12));
		wieldWeaponsCheckbox.setBackground(Color.WHITE);
		wieldWeaponsCheckbox.setBounds(10, 505, 298, 23);
		Configuration.add(wieldWeaponsCheckbox);

		catchTwoShrimpsCheckbox = new JCheckBox("Catch Two Shrimps before cooking");
		catchTwoShrimpsCheckbox.setFont(new Font("Tahoma", Font.PLAIN, 12));
		catchTwoShrimpsCheckbox.setBackground(Color.WHITE);
		catchTwoShrimpsCheckbox.setBounds(10, 533, 298, 23);
		Configuration.add(catchTwoShrimpsCheckbox);

		useKeyboardCheckbox = new JCheckBox("Use Keyboard spacebar to continue chat");
		useKeyboardCheckbox.setFont(new Font("Tahoma", Font.PLAIN, 12));
		useKeyboardCheckbox.setBackground(Color.WHITE);
		useKeyboardCheckbox.setBounds(10, 560, 298, 23);
		Configuration.add(useKeyboardCheckbox);

		JPanel Load = new JPanel();
		Load.setBorder(null);
		Load.setBackground(Color.WHITE);
		tabbedPane.addTab("Load Accounts", null, Load, null);
		Load.setLayout(null);

		useLoadOption = new JCheckBox("Load Accounts");
		useLoadOption.setBackground(Color.WHITE);
		useLoadOption.setBounds(10, 7, 114, 23);
		Load.add(useLoadOption);

		JSeparator separator_17 = new JSeparator();
		separator_17.setBounds(118, 18, 188, 2);
		Load.add(separator_17);

		JSeparator separator_18 = new JSeparator();
		separator_18.setOrientation(SwingConstants.VERTICAL);
		separator_18.setBounds(306, 18, 2, 428);
		Load.add(separator_18);

		JSeparator separator_19 = new JSeparator();
		separator_19.setOrientation(SwingConstants.VERTICAL);
		separator_19.setBounds(10, 18, 2, 428);
		Load.add(separator_19);

		JLabel lblUsername_1 = new JLabel("Login:");
		lblUsername_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblUsername_1.setBounds(34, 40, 66, 20);
		Load.add(lblUsername_1);

		loadUsernameText = new JTextField();
		loadUsernameText.setToolTipText("The domain of the email address");
		loadUsernameText.setHorizontalAlignment(SwingConstants.CENTER);
		loadUsernameText.setColumns(10);
		loadUsernameText.setBounds(107, 40, 177, 22);
		Load.add(loadUsernameText);

		JLabel label = new JLabel("Password:");
		label.setFont(new Font("Tahoma", Font.PLAIN, 12));
		label.setBounds(34, 71, 66, 20);
		Load.add(label);

		loadPasswordText = new JTextField();
		loadPasswordText.setHorizontalAlignment(SwingConstants.CENTER);
		loadPasswordText.setColumns(10);
		loadPasswordText.setBounds(107, 71, 177, 22);
		Load.add(loadPasswordText);

		accountModel = new DefaultListModel();

		createdAccountsList = new JList(accountModel);
		createdAccountsList.setBorder(new LineBorder(SystemColor.controlShadow));
		createdAccountsList.setBounds(45, 494, 239, 87);
		// Creation.add(createdAccountsList);

		accountScrollPane = new JScrollPane();
		accountScrollPane.setViewportView(createdAccountsList);
		Load.add(accountScrollPane);
		accountScrollPane.setBounds(new Rectangle(34, 133, 250, 208));

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = loadUsernameText.getText();
				String password = loadPasswordText.getText();
				if (username.length() > 0 && password.length() > 0) {
					useLoadOption.setSelected(true);
					accountModel.addElement((username + ":" + password));
					loadUsernameText.setText("");
				}
			}
		});
		btnAdd.setBounds(34, 102, 250, 23);
		Load.add(btnAdd);

		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object[] selected = createdAccountsList.getSelectedValues();
				for (Object o : selected) {
					if (accountModel.contains(o))
						accountModel.removeElement(o);
				}
			}
		});
		btnRemove.setBounds(34, 352, 250, 23);
		Load.add(btnRemove);

		JSeparator separator_11 = new JSeparator();
		separator_11.setBounds(10, 386, 296, 2);
		Load.add(separator_11);

		JSeparator separator_20 = new JSeparator();
		separator_20.setBounds(10, 445, 296, 2);
		Load.add(separator_20);

		JButton btnNewButton = new JButton("Load Accounts from File");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFileChooser fc = new JFileChooser(Util.getWorkingDirectory());
					int value = fc.showOpenDialog(null);
					if (value == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						if (file.exists()) {
							String line = null;
							try {
								FileReader fr = new FileReader(file);
								BufferedReader br = new BufferedReader(fr);
								while ((line = br.readLine()) != null) {
									String[] split = line.split(":");
									if (split.length == 2) {
										useLoadOption.setSelected(true);
										accountModel.addElement((split[0] + ":" + split[1]));
									} else {
										General.println(
												"Incorrect format! '" + line + "' should be 'username:password'");
									}
								}
								fr.close();
								br.close();
							} catch (FileNotFoundException ex) {
								System.out.println("Unable to open file: " + file.getAbsolutePath());
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnNewButton.setBounds(34, 399, 250, 23);
		Load.add(btnNewButton);

		JLabel lblFormat = new JLabel("usa@tribot.org:password");
		lblFormat.setVerticalAlignment(SwingConstants.TOP);
		lblFormat.setHorizontalAlignment(SwingConstants.CENTER);
		lblFormat.setFont(new Font("Tahoma", Font.ITALIC, 10));
		lblFormat.setBounds(34, 426, 250, 16);
		Load.add(lblFormat);

		JPanel Create = new JPanel();
		Create.setBackground(Color.WHITE);
		tabbedPane.addTab("Create Accounts", null, Create, null);
		Create.setLayout(null);

		useCreateOption = new JCheckBox("Create Accounts\r\n");
		useCreateOption.setBackground(Color.WHITE);
		useCreateOption.setBounds(10, 7, 118, 23);
		Create.add(useCreateOption);

		JSeparator separator_13 = new JSeparator();
		separator_13.setBounds(118, 18, 188, 2);
		Create.add(separator_13);

		JSeparator separator_14 = new JSeparator();
		separator_14.setOrientation(SwingConstants.VERTICAL);
		separator_14.setBounds(306, 18, 2, 564);
		Create.add(separator_14);

		JSeparator separator_15 = new JSeparator();
		separator_15.setOrientation(SwingConstants.VERTICAL);
		separator_15.setBounds(10, 18, 2, 564);
		Create.add(separator_15);

		JLabel lblcaptchaKey = new JLabel("2captcha:");
		lblcaptchaKey.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblcaptchaKey.setBounds(34, 40, 66, 20);
		Create.add(lblcaptchaKey);

		captchaText = new JTextField();
		captchaText.setFont(new Font("Tahoma", Font.PLAIN, 10));
		captchaText.setToolTipText("The domain of the email address");
		captchaText.setHorizontalAlignment(SwingConstants.CENTER);
		captchaText.setColumns(10);
		captchaText.setBounds(110, 40, 177, 22);
		Create.add(captchaText);

		JLabel lblAge = new JLabel("Age:");
		lblAge.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblAge.setBounds(34, 152, 40, 50);
		Create.add(lblAge);

		JSeparator separator_24 = new JSeparator();
		separator_24.setBounds(10, 513, 296, 2);
		Create.add(separator_24);

		createDisplayText = new JTextField();
		createDisplayText.setHorizontalAlignment(SwingConstants.CENTER);
		createDisplayText.setColumns(10);
		createDisplayText.setBounds(110, 213, 177, 22);
		Create.add(createDisplayText);

		JLabel lblDisplay = new JLabel("* Display:");
		lblDisplay.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDisplay.setBounds(34, 213, 66, 20);
		Create.add(lblDisplay);

		JLabel lblHttpscaptchacom = new JLabel("https://2captcha.com/setting \u2192 \"captcha KEY\"");
		lblHttpscaptchacom.setForeground(SystemColor.textHighlight);
		lblHttpscaptchacom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Web.open("https://2captcha.com/setting");
			}
		});
		lblHttpscaptchacom.setHorizontalAlignment(SwingConstants.CENTER);
		lblHttpscaptchacom.setBounds(34, 73, 250, 20);
		Create.add(lblHttpscaptchacom);

		createEmailText = new JTextField();
		createEmailText.setHorizontalAlignment(SwingConstants.CENTER);
		createEmailText.setColumns(10);
		createEmailText.setBounds(110, 244, 177, 22);
		Create.add(createEmailText);

		JLabel lblEmail = new JLabel("* Email:");
		lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEmail.setBounds(34, 244, 66, 20);
		Create.add(lblEmail);

		createPasswordText = new JTextField();
		createPasswordText.setHorizontalAlignment(SwingConstants.CENTER);
		createPasswordText.setColumns(10);
		createPasswordText.setBounds(110, 276, 177, 22);
		Create.add(createPasswordText);

		JLabel lblPassword = new JLabel("* Password:");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblPassword.setBounds(34, 276, 66, 20);
		Create.add(lblPassword);

		createAgeSlider = new JSlider();
		createAgeSlider.setMajorTickSpacing(10);
		createAgeSlider.setValue(21);
		createAgeSlider.setPaintTicks(true);
		createAgeSlider.setPaintLabels(true);
		createAgeSlider.setMinorTickSpacing(2);
		createAgeSlider.setMinimum(18);
		createAgeSlider.setMaximum(50);
		createAgeSlider.setBackground(Color.WHITE);
		createAgeSlider.setBounds(77, 152, 210, 50);
		Create.add(createAgeSlider);

		JSeparator separator_16 = new JSeparator();
		separator_16.setBounds(10, 99, 296, 2);
		Create.add(separator_16);

		JSeparator separator_25 = new JSeparator();
		separator_25.setBounds(10, 313, 296, 2);
		Create.add(separator_25);

		JSeparator separator_26 = new JSeparator();
		separator_26.setBounds(10, 143, 296, 2);
		Create.add(separator_26);

		JLabel lblAmountToCreate = new JLabel("Amount To Create:");
		lblAmountToCreate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblAmountToCreate.setBounds(34, 111, 118, 20);
		Create.add(lblAmountToCreate);

		createAccountsSpinner = new JSpinner();
		createAccountsSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		createAccountsSpinner.setBounds(150, 111, 40, 20);
		Create.add(createAccountsSpinner);

		JLabel lblRandomWord = new JLabel("Random Word \u2192 &");
		lblRandomWord.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRandomWord.setBounds(34, 350, 118, 20);
		Create.add(lblRandomWord);

		JLabel lblRandomLetter = new JLabel("Random Letter [a-Z] \u2192 $");
		lblRandomLetter.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRandomLetter.setBounds(34, 403, 155, 20);
		Create.add(lblRandomLetter);

		JLabel lblRandomLetter_1 = new JLabel("Random Number [0-9] \u2192 #");
		lblRandomLetter_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRandomLetter_1.setBounds(34, 429, 155, 20);
		Create.add(lblRandomLetter_1);

		JLabel lblExamples = new JLabel("Result \u2192 Soduku King1");
		lblExamples.setHorizontalAlignment(SwingConstants.CENTER);
		lblExamples.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblExamples.setBounds(33, 556, 119, 20);
		Create.add(lblExamples);

		generatorText = new JTextField();
		generatorText.setText("{6} King[1]");
		generatorText.setHorizontalAlignment(SwingConstants.CENTER);
		generatorText.setColumns(10);
		generatorText.setBounds(32, 531, 120, 22);
		Create.add(generatorText);

		JButton btnStringGenerator = new JButton("String Generator");
		btnStringGenerator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while ((System.currentTimeMillis() + 1000) > System.currentTimeMillis()) {
					String name = AccountCreator.generateString(true, 0, generatorText.getText().trim());
					if (name != null) {
						lblExamples.setText("Result \u2192 " + name);
						break;
					}
				}
			}
		});
		btnStringGenerator.setBounds(164, 531, 120, 23);
		Create.add(btnStringGenerator);

		JLabel lblRandomLetterOr = new JLabel("Random Letter, Number, or Space \u2192 ?");
		lblRandomLetterOr.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRandomLetterOr.setBounds(34, 456, 262, 20);
		Create.add(lblRandomLetterOr);

		JSeparator separator_27 = new JSeparator();
		separator_27.setBounds(10, 581, 296, 2);
		Create.add(separator_27);

		JLabel lblStringCreator = new JLabel("(*) String Creator");
		lblStringCreator.setHorizontalAlignment(SwingConstants.CENTER);
		lblStringCreator.setFont(new Font("Verdana", Font.ITALIC, 12));
		lblStringCreator.setBounds(10, 320, 296, 20);
		Create.add(lblStringCreator);

		JLabel lblRandomWorldBy = new JLabel("Random Word by Length \u2192 {Number}");
		lblRandomWorldBy.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRandomWorldBy.setBounds(34, 377, 253, 20);
		Create.add(lblRandomWorldBy);

		JLabel lblIndexnumber = new JLabel("Starting Index \u2192 [Number]");
		lblIndexnumber.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblIndexnumber.setBounds(34, 482, 262, 20);
		Create.add(lblIndexnumber);

		JButton btnSaveSettings = new JButton("Save Settings");
		btnSaveSettings.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnSaveSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveProfile("USA Tutorial Island");
			}
		});
		btnSaveSettings.setBounds(237, 11, 100, 23);
		contentPane.add(btnSaveSettings);

		JButton btnLoadSettings = new JButton("Load Settings");
		btnLoadSettings.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnLoadSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadProfile("USA Tutorial Island");
			}
		});
		btnLoadSettings.setBounds(15, 11, 100, 23);
		contentPane.add(btnLoadSettings);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UsaTutorial.loadGUI();
			}
		});
		btnStart.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnStart.setBounds(131, 11, 89, 23);
		contentPane.add(btnStart);
	}
}
