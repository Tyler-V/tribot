package scripts.thugs;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.tribot.api.General;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import scripts.usa.api.combat.CombatTrainer;
import scripts.usa.api.combat.CombatTrainer.TRAINING_MODE;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import scripts.usa.api.combat.CombatTrainer.TRAINING_MODE;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class GUI extends JFrame {

	private JPanel contentPane;

	static JTextField foodText;
	static JSpinner foodSpinner;
	static JSpinner potionSpinner;
	static JSpinner lootSpinner;
	static JCheckBox lootBox;
	static JCheckBox lootingBagBox;
	static JCheckBox autoResponderBox;
	static JCheckBox evadeBox;
	static JComboBox modeBox;
	static JSpinner attackSpinner;
	static JSpinner strengthSpinner;
	static JSpinner defenceSpinner;

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
		setBounds(100, 100, 322, 471);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblUsaThugs = new JLabel("USA Thugs");
		lblUsaThugs.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblUsaThugs.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsaThugs.setBounds(10, 11, 275, 28);
		contentPane.add(lblUsaThugs);

		JLabel lblVersion = new JLabel("v1.5");
		lblVersion.setText(Thugs.version);
		lblVersion.setBounds(245, 20, 46, 14);
		contentPane.add(lblVersion);

		JSeparator separator = new JSeparator();
		separator.setBounds(0, 50, 434, 2);
		contentPane.add(separator);

		JLabel lblFood = new JLabel("Food:");
		lblFood.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblFood.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFood.setBounds(10, 63, 129, 20);
		contentPane.add(lblFood);

		foodText = new JTextField();
		foodText.setHorizontalAlignment(SwingConstants.CENTER);
		foodText.setText("Tuna");
		foodText.setBounds(149, 63, 86, 20);
		contentPane.add(foodText);
		foodText.setColumns(10);

		foodSpinner = new JSpinner();
		foodSpinner.setModel(new SpinnerNumberModel(new Integer(16), new Integer(1), null, new Integer(1)));
		foodSpinner.setBounds(245, 63, 40, 20);
		contentPane.add(foodSpinner);

		JLabel lblCombatPotions = new JLabel("Combat Potions:");
		lblCombatPotions.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCombatPotions.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblCombatPotions.setBounds(10, 94, 129, 20);
		contentPane.add(lblCombatPotions);

		potionSpinner = new JSpinner();
		potionSpinner.setModel(new SpinnerNumberModel(new Integer(8), null, null, new Integer(1)));
		potionSpinner.setBounds(149, 94, 40, 20);
		contentPane.add(potionSpinner);

		lootBox = new JCheckBox("");
		lootBox.setSelected(true);
		lootBox.setBounds(21, 125, 20, 20);
		contentPane.add(lootBox);

		JLabel lblLootOver = new JLabel("Loot items over:");
		lblLootOver.setHorizontalAlignment(SwingConstants.RIGHT);
		lblLootOver.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblLootOver.setBounds(39, 125, 100, 20);
		contentPane.add(lblLootOver);

		lootSpinner = new JSpinner();
		lootSpinner.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(0), null, new Integer(100)));
		lootSpinner.setBounds(149, 125, 54, 20);
		contentPane.add(lootSpinner);

		JLabel lblGp = new JLabel("gp");
		lblGp.setHorizontalAlignment(SwingConstants.LEFT);
		lblGp.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblGp.setBounds(213, 125, 26, 20);
		contentPane.add(lblGp);

		JLabel lbllootingBag = new JLabel("Use Looting Bag:");
		lbllootingBag.setHorizontalAlignment(SwingConstants.RIGHT);
		lbllootingBag.setFont(new Font("Verdana", Font.PLAIN, 11));
		lbllootingBag.setBounds(10, 156, 129, 20);
		contentPane.add(lbllootingBag);

		lootingBagBox = new JCheckBox("");
		lootingBagBox.setBounds(145, 156, 20, 20);
		contentPane.add(lootingBagBox);

		JLabel lblUseAutoResponder = new JLabel("Use Auto Responder:");
		lblUseAutoResponder.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUseAutoResponder.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblUseAutoResponder.setBounds(10, 187, 129, 20);
		contentPane.add(lblUseAutoResponder);

		autoResponderBox = new JCheckBox("");
		autoResponderBox.setBounds(145, 187, 20, 20);
		contentPane.add(autoResponderBox);

		JLabel lblEvade = new JLabel("Evade Pkers:");
		lblEvade.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEvade.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblEvade.setBounds(10, 218, 129, 20);
		contentPane.add(lblEvade);

		evadeBox = new JCheckBox("");
		evadeBox.setBounds(145, 218, 20, 20);
		contentPane.add(evadeBox);
		lootingBagBox.setSelected(true);
		autoResponderBox.setSelected(true);
		evadeBox.setSelected(true);

		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(0, 244, 434, 2);
		contentPane.add(separator_2);

		JLabel lblTrainingMode = new JLabel("Training Mode:");
		lblTrainingMode.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTrainingMode.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblTrainingMode.setBounds(10, 257, 129, 20);
		contentPane.add(lblTrainingMode);

		modeBox = new JComboBox(TRAINING_MODE.values());
		modeBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {

				TRAINING_MODE selected = (TRAINING_MODE) modeBox.getSelectedItem();

				attackSpinner.setEnabled(selected != TRAINING_MODE.TRAIN_COMBAT);
				strengthSpinner.setEnabled(selected != TRAINING_MODE.TRAIN_COMBAT);
				defenceSpinner.setEnabled(selected != TRAINING_MODE.TRAIN_COMBAT);

			}
		});
		modeBox.setBounds(149, 258, 136, 20);
		contentPane.add(modeBox);

		JLabel lblDesiredAttack = new JLabel("Desired Attack:");
		lblDesiredAttack.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDesiredAttack.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblDesiredAttack.setBounds(10, 288, 129, 20);
		contentPane.add(lblDesiredAttack);

		attackSpinner = new JSpinner();
		attackSpinner.setBounds(149, 289, 40, 20);
		attackSpinner.setEnabled(false);
		attackSpinner.setValue(Skills.getActualLevel(SKILLS.ATTACK));
		contentPane.add(attackSpinner);

		JLabel lblDesiredStrength = new JLabel("Desired Strength:");
		lblDesiredStrength.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDesiredStrength.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblDesiredStrength.setBounds(10, 319, 129, 20);
		contentPane.add(lblDesiredStrength);

		strengthSpinner = new JSpinner();
		strengthSpinner.setBounds(149, 320, 40, 20);
		strengthSpinner.setEnabled(false);
		strengthSpinner.setValue(Skills.getActualLevel(SKILLS.STRENGTH));
		contentPane.add(strengthSpinner);

		JLabel lblDesiredDefence = new JLabel("Desired Defence:");
		lblDesiredDefence.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDesiredDefence.setFont(new Font("Verdana", Font.PLAIN, 11));
		lblDesiredDefence.setBounds(10, 350, 129, 20);
		contentPane.add(lblDesiredDefence);

		defenceSpinner = new JSpinner();
		defenceSpinner.setBounds(149, 351, 40, 20);
		defenceSpinner.setEnabled(false);
		defenceSpinner.setValue(Skills.getActualLevel(SKILLS.DEFENCE));
		contentPane.add(defenceSpinner);

		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(-16, 385, 434, 2);
		contentPane.add(separator_1);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thugs.loadGUI();
			}
		});
		btnStart.setBounds(97, 398, 103, 23);
		contentPane.add(btnStart);
	}
}
