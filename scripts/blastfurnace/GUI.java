package scripts.blastfurnace;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.util.Util;
import javax.swing.JSlider;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class GUI extends JFrame {

	private JPanel contentPane;
	private JTextField usernameText;
	private JLabel lblBar;

	static JComboBox<TASK> taskBox;
	static JComboBox<BAR> barBox;
	static JCheckBox progressiveCheckbox;
	static JCheckBox reactionCheckbox;
	static JCheckBox antibanCheckbox;

	static JTextField muleUsernameText;
	static JList<String> accountsList;
	static DefaultListModel<String> accountModel;
	static JScrollPane accountScrollPane;

	static JSlider timeSlider;

	private String directory = "USA Blast Furnace";

	private ArrayList<String> getSavedProfiles(String name) {

		File folder = new File(Util.getWorkingDirectory() + "/" + name);

		if (!folder.exists())
			folder.mkdir();

		ArrayList<String> files = new ArrayList<>();

		for (File file : folder.listFiles()) {

			if (file.getName().contains(".txt"))

				files.add(file.getName().replaceAll(".txt", ""));

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

			} else {

				System.out.println("Updated " + name + " at " + folder.toString());

			}

			if (file.exists()) {

				Properties prop = new Properties();

				prop.put("task", taskBox.getSelectedItem().toString());
				prop.put("bar", barBox.getSelectedItem().toString());
				prop.put("progressive", String.valueOf(Boolean.valueOf(progressiveCheckbox.isSelected())));
				prop.put("reaction", String.valueOf(Boolean.valueOf(reactionCheckbox.isSelected())));
				prop.put("antiban", String.valueOf(Boolean.valueOf(antibanCheckbox.isSelected())));

				prop.put("mule", muleUsernameText.getText());

				prop.put("delay", String.valueOf(timeSlider.getValue()));

				if (!accountModel.isEmpty()) {

					String s = "";

					for (Object o : accountModel.toArray())
						s = s + o.toString() + " ";

					prop.put("accounts", s);

				}

				prop.store(new FileOutputStream(file), null);

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
				prop.load(new FileInputStream(newFile));

				taskBox.setSelectedItem(TASK.valueOf(prop.getProperty("task")));
				barBox.setSelectedItem(BAR.valueOf(prop.getProperty("bar")));
				progressiveCheckbox.setSelected(Boolean.parseBoolean(prop.getProperty("progressive")));
				reactionCheckbox.setSelected(Boolean.parseBoolean(prop.getProperty("reaction")));
				antibanCheckbox.setSelected(Boolean.parseBoolean(prop.getProperty("antiban")));

				muleUsernameText.setText(prop.getProperty("mule"));

				String value = prop.getProperty("accounts").trim();
				String[] split = value.split(" ");

				for (String s : split)
					accountModel.addElement(s);

				barBox.setVisible(TASK.valueOf(prop.getProperty("task")) == TASK.SMITHER);
				lblBar.setVisible(TASK.valueOf(prop.getProperty("task")) == TASK.SMITHER);
				progressiveCheckbox.setVisible(TASK.valueOf(prop.getProperty("task")) == TASK.SMITHER);

				timeSlider.setValue((int) Integer.parseInt(prop.getProperty("delay")));

			}

		} catch (IOException e) {

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
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 264, 504);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(204, 153, 51));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel title = new JLabel("USA Blast Furnace");
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setFont(new Font("Verdana", Font.BOLD, 16));
		title.setBounds(0, 10, 228, 44);
		contentPane.add(title);

		JLabel version = new JLabel(BlastFurnace.version);
		version.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		version.setForeground(Color.WHITE);
		version.setBounds(208, 27, 40, 14);
		contentPane.add(version);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(21, 57, 207, 328);
		contentPane.add(tabbedPane);

		JPanel settings = new JPanel();
		settings.setBackground(Color.WHITE);
		tabbedPane.addTab("Settings", null, settings, null);
		settings.setLayout(null);

		JLabel lblTask = new JLabel("Task:");
		lblTask.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblTask.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTask.setBounds(5, 14, 35, 20);
		settings.add(lblTask);

		taskBox = new JComboBox(TASK.values());
		taskBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				barBox.setVisible((TASK) taskBox.getSelectedItem() == TASK.SMITHER);
				lblBar.setVisible((TASK) taskBox.getSelectedItem() == TASK.SMITHER);
				progressiveCheckbox.setVisible((TASK) taskBox.getSelectedItem() == TASK.SMITHER);
			}
		});
		taskBox.setBounds(50, 14, 138, 20);
		settings.add(taskBox);

		lblBar = new JLabel("Bar:");
		lblBar.setVisible(false);
		lblBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblBar.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBar.setBounds(5, 44, 35, 20);
		settings.add(lblBar);

		barBox = new JComboBox(BAR.values());
		barBox.setVisible(false);
		barBox.setBounds(50, 44, 138, 20);
		settings.add(barBox);

		progressiveCheckbox = new JCheckBox("Use Progressive Leveling");
		progressiveCheckbox.setVisible(false);
		progressiveCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				lblBar.setEnabled(!progressiveCheckbox.isSelected());
				barBox.setEnabled(!progressiveCheckbox.isSelected());
			}
		});
		progressiveCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
		progressiveCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		progressiveCheckbox.setBackground(Color.WHITE);
		progressiveCheckbox.setBounds(0, 71, 204, 23);
		settings.add(progressiveCheckbox);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 101, 202, 2);
		settings.add(separator);

		reactionCheckbox = new JCheckBox("Enable ABC2 Reaction");
		reactionCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		reactionCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
		reactionCheckbox.setBackground(Color.WHITE);
		reactionCheckbox.setBounds(0, 109, 202, 23);
		settings.add(reactionCheckbox);

		antibanCheckbox = new JCheckBox("Enable ABC2 Antiban");
		antibanCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		antibanCheckbox.setHorizontalAlignment(SwingConstants.CENTER);
		antibanCheckbox.setBackground(Color.WHITE);
		antibanCheckbox.setBounds(0, 137, 202, 23);
		settings.add(antibanCheckbox);

		JSeparator separator_4 = new JSeparator();
		separator_4.setBounds(0, 167, 202, 2);
		settings.add(separator_4);

		JPanel mule = new JPanel();
		mule.setBackground(Color.WHITE);
		tabbedPane.addTab("Mule", null, mule, null);
		mule.setLayout(null);

		JLabel lblUsername = new JLabel("Mule Username:");
		lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblUsername.setHorizontalAlignment(SwingConstants.LEFT);
		lblUsername.setBounds(10, 11, 90, 20);
		mule.add(lblUsername);

		muleUsernameText = new JTextField();
		muleUsernameText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		muleUsernameText.setBounds(100, 11, 90, 20);
		mule.add(muleUsernameText);
		muleUsernameText.setColumns(10);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(0, 40, 202, 2);
		mule.add(separator_1);

		JLabel lblAccounts = new JLabel("Smither Username(s)");
		lblAccounts.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblAccounts.setBounds(0, 44, 202, 20);
		lblAccounts.setHorizontalAlignment(SwingConstants.CENTER);
		mule.add(lblAccounts);

		accountModel = new DefaultListModel<String>();

		accountsList = new JList<String>(accountModel);
		accountsList.setBounds(87, 53, 153, 156);
		accountsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		accountsList.setFont(new Font("Segoe UI", Font.PLAIN, 11));

		accountScrollPane = new JScrollPane();
		accountScrollPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		accountScrollPane.setBounds(new Rectangle(24, 65, 153, 156));
		accountScrollPane.setViewportView(accountsList);
		mule.add(accountScrollPane);

		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				String text = usernameText.getText().trim();

				if (text != null) {

					accountModel.addElement(text);
					usernameText.setText("");

				}

			}
		});
		add.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		add.setBounds(24, 232, 56, 23);
		mule.add(add);

		usernameText = new JTextField();
		usernameText.setHorizontalAlignment(SwingConstants.CENTER);
		usernameText.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		usernameText.setColumns(10);
		usernameText.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		usernameText.setBounds(87, 233, 90, 23);
		mule.add(usernameText);

		JButton remove = new JButton("Remove");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Object[] selected = accountsList.getSelectedValues();

				for (Object o : selected) {

					if (accountModel.contains(o))

						accountModel.removeElement(o);

				}

			}
		});
		remove.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		remove.setBounds(24, 266, 153, 23);
		mule.add(remove);

		JPanel world = new JPanel();
		world.setBackground(Color.WHITE);
		tabbedPane.addTab("World Hopper", null, world, null);
		world.setLayout(null);

		JLabel lblInClanChat = new JLabel("In Clan Chat, use Command \"W###\"");
		lblInClanChat.setHorizontalAlignment(SwingConstants.CENTER);
		lblInClanChat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblInClanChat.setBounds(0, 10, 202, 20);
		world.add(lblInClanChat);

		JLabel lblExw = new JLabel("Ex: \"W302\"");
		lblExw.setHorizontalAlignment(SwingConstants.CENTER);
		lblExw.setFont(new Font("Segoe UI", Font.ITALIC, 11));
		lblExw.setBounds(0, 34, 202, 20);
		world.add(lblExw);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(0, 60, 202, 2);
		world.add(separator_2);

		JLabel lblTimeToRemain = new JLabel("Delay before changing worlds (min)");
		lblTimeToRemain.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimeToRemain.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		lblTimeToRemain.setBounds(0, 70, 202, 20);
		world.add(lblTimeToRemain);

		timeSlider = new JSlider();
		timeSlider.setPaintTicks(true);
		timeSlider.setPaintLabels(true);
		timeSlider.setMajorTickSpacing(2);
		timeSlider.setMinorTickSpacing(1);
		timeSlider.setValue(3);
		timeSlider.setMaximum(6);
		timeSlider.setSnapToTicks(true);
		timeSlider.setBackground(Color.WHITE);
		timeSlider.setBounds(10, 95, 182, 45);
		world.add(timeSlider);

		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(0, 152, 202, 2);
		world.add(separator_3);

		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BlastFurnace.load();
			}
		});
		start.setBounds(21, 396, 207, 23);
		contentPane.add(start);

		JButton load = new JButton("Load");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				ArrayList<?> files = getSavedProfiles(directory);

				String[] profiles = (String[]) files.toArray(new String[files.size()]);

				if (profiles.length == 0) {

					JOptionPane.showMessageDialog(null, "No saved profiles found.");

				} else {

					String playerName = null;

					RSPlayer player = Player.getRSPlayer();

					if (player != null)
						playerName = player.getName();

					boolean foundProfile = false;

					if (playerName != null) {

						for (String profile : profiles) {

							if (profile.contains(playerName)) {

								loadSettings(directory, profile);
								foundProfile = true;
								break;

							}

						}

					}

					if (!foundProfile) {

						JComboBox<String> options = new JComboBox<String>(profiles);
						JOptionPane.showMessageDialog(null, options, "Select a saved profile",
								JOptionPane.QUESTION_MESSAGE);
						loadSettings(directory, options.getSelectedItem().toString());

					}

				}

			}
		});
		load.setBounds(21, 427, 100, 23);
		contentPane.add(load);

		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String playerName = null;

				RSPlayer player = Player.getRSPlayer();

				if (player != null)
					playerName = player.getName();

				if (playerName != null) {

					saveSettings(directory, playerName);

				} else {

					String profileName = JOptionPane.showInputDialog("Enter a name to save the profile");
					saveSettings(directory, profileName);

				}

			}
		});
		save.setBounds(128, 427, 100, 23);
		contentPane.add(save);

	}
}
