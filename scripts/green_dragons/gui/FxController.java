package scripts.green_dragons.gui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.tribot.api.General;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;

import com.allatori.annotations.DoNotRename;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import scripts.green_dragons.data.EvadeOption;
import scripts.green_dragons.data.Locations;
import scripts.green_dragons.data.Vars;
import scripts.green_dragons.models.PlayerEquipment;
import scripts.usa.api.gui.AbstractFxController;
import scripts.usa.api.gui.FxProperties;
import scripts.usa.api.util.Strings;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.enums.Jewelry;
import scripts.usa.api2007.enums.Potions;
import scripts.usa.api2007.looting.Looter;

public class FxController extends AbstractFxController {

	private List<Control> getControls() {
		return Arrays.asList(locationComboBox,
				maxPlayersSpinner,
				dwarfCannonCheckBox,
				foodTextField,
				foodSpinner,
				eatSlider,
				potion1ComboBox,
				potion2ComboBox,
				potion3ComboBox,
				specialAttackCheckBox,
				specialAttackSlider,
				ammunitionTextField,
				ammunitionPerTripSpinner,
				lootAmmunitionCheckBox,
				minLootValueSpinner,
				maxLootValueSpinner,
				maxLootDistanceSlider,
				lootAmmunitionCheckBox,
				lootAmmunitionStackSpinner,
				quickPrayerCheckBox,
				evadeComboBox,
				antibanCheckbox,
				reactionTimingCheckbox);
	}

	@FXML
	@DoNotRename
	private Label versionLabel;

	@FXML
	@DoNotRename
	private ComboBox<Locations> locationComboBox;

	@FXML
	@DoNotRename
	private Spinner<Integer> maxPlayersSpinner;

	@FXML
	@DoNotRename
	private CheckBox dwarfCannonCheckBox;

	@FXML
	@DoNotRename
	private TextField foodTextField;

	@FXML
	@DoNotRename
	private Spinner<Integer> foodSpinner;

	@FXML
	@DoNotRename
	private Slider eatSlider;

	@FXML
	@DoNotRename
	private ComboBox<Potions> potion1ComboBox;

	@FXML
	@DoNotRename
	private ComboBox<Potions> potion2ComboBox;

	@FXML
	@DoNotRename
	private ComboBox<Potions> potion3ComboBox;

	@FXML
	@DoNotRename
	private Button potionsResetButton;

	@FXML
	@DoNotRename
	private ImageView equipmentSlotsImageView;

	@FXML
	@DoNotRename
	private ImageView equipmentEmptyImageView;

	@FXML
	@DoNotRename
	private ImageView helmetImageView;

	@FXML
	@DoNotRename
	private ImageView amuletImageView;

	@FXML
	@DoNotRename
	private ImageView bodyImageView;

	@FXML
	@DoNotRename
	private ImageView legsImageView;

	@FXML
	@DoNotRename
	private ImageView bootsImageView;

	@FXML
	@DoNotRename
	private ImageView weaponImageView;

	@FXML
	@DoNotRename
	private ImageView capeImageView;

	@FXML
	@DoNotRename
	private ImageView shieldImageView;

	@FXML
	@DoNotRename
	private ImageView ringImageView;

	@FXML
	@DoNotRename
	private ImageView glovesImageView;

	@FXML
	@DoNotRename
	private ImageView arrowImageView;

	@FXML
	@DoNotRename
	private Button refreshEquipmentButton;

	@FXML
	@DoNotRename
	private CheckBox specialAttackCheckBox;

	@FXML
	@DoNotRename
	private Slider specialAttackSlider;

	@FXML
	@DoNotRename
	private TextField ammunitionTextField;

	@FXML
	@DoNotRename
	private Spinner<Integer> ammunitionPerTripSpinner;

	@FXML
	@DoNotRename
	private Spinner<Integer> minLootValueSpinner;

	@FXML
	@DoNotRename
	private Spinner<Integer> maxLootValueSpinner;

	@FXML
	@DoNotRename
	private Slider maxLootDistanceSlider;

	@FXML
	@DoNotRename
	private CheckBox lootAmmunitionCheckBox;

	@FXML
	@DoNotRename
	private Spinner<Integer> lootAmmunitionStackSpinner;

	@FXML
	@DoNotRename
	private ListView<String> equipmentListView;

	@FXML
	@DoNotRename
	private ComboBox<EvadeOption> evadeComboBox;

	@FXML
	@DoNotRename
	private CheckBox quickPrayerCheckBox;

	@FXML
	@DoNotRename
	private CheckBox antibanCheckbox;

	@FXML
	@DoNotRename
	private CheckBox reactionTimingCheckbox;

	@FXML
	@DoNotRename
	private Button startButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			FxProperties properties = new FxProperties(Vars.get()
					.getScriptManifest()
					.name());
			versionLabel.setText("v" + Vars.get()
					.getScriptManifest()
					.version());

			// General
			locationComboBox.setItems(FXCollections.observableArrayList(Locations.values()));
			locationComboBox.getSelectionModel()
					.selectFirst();
			maxPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 3, 1));
			maxPlayersSpinner.getEditor()
					.textProperty()
					.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
						int value = newValue.isEmpty() ? Integer.parseInt(oldValue) : Integer.parseInt(newValue);
						maxPlayersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, value, 1));
					});

			// Consumables
			foodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 28, 20, 1));
			foodSpinner.getEditor()
					.textProperty()
					.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
						int value = newValue.isEmpty() ? Integer.parseInt(oldValue) : Integer.parseInt(newValue);
						foodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 28, value, 1));
					});
			potion1ComboBox.setItems(FXCollections.observableArrayList(Potions.values()));
			potion2ComboBox.setItems(FXCollections.observableArrayList(Potions.values()));
			potion3ComboBox.setItems(FXCollections.observableArrayList(Potions.values()));
			potionsResetButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					potion1ComboBox.getSelectionModel()
							.clearSelection();
					potion2ComboBox.getSelectionModel()
							.clearSelection();
					potion3ComboBox.getSelectionModel()
							.clearSelection();
				}
			});

			// Equipment
			loadEquipment(properties);
			refreshEquipmentButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					refreshEquipment();
				}
			});
			equipmentEmptyImageView.setImage(new Image("https://i.imgur.com/Xe3DztM.png"));
			equipmentSlotsImageView.setImage(new Image("https://i.imgur.com/fxRSYtd.png"));
			ammunitionPerTripSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, Integer.MAX_VALUE, 200, 50));
			ammunitionPerTripSpinner.getEditor()
					.textProperty()
					.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
						int value = newValue.isEmpty() ? Integer.parseInt(oldValue) : Integer.parseInt(newValue);
						ammunitionPerTripSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, Integer.MAX_VALUE, value, 50));
					});

			// Looting
			minLootValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 750, 250));
			minLootValueSpinner.getEditor()
					.textProperty()
					.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
						int value = newValue.isEmpty() ? Integer.parseInt(oldValue) : Integer.parseInt(newValue);
						maxLootValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100000, Integer.MAX_VALUE, value, 250));
					});
			maxLootValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100000, Integer.MAX_VALUE, 500000, 100000));
			maxLootValueSpinner.getEditor()
					.textProperty()
					.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
						int value = newValue.isEmpty() ? Integer.parseInt(oldValue) : Integer.parseInt(newValue);
						maxLootValueSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100000, Integer.MAX_VALUE, value, 100000));
					});
			lootAmmunitionStackSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 5, 1));
			lootAmmunitionStackSpinner.getEditor()
					.textProperty()
					.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
						int value = newValue.isEmpty() ? Integer.parseInt(oldValue) : Integer.parseInt(newValue);
						lootAmmunitionStackSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, value, 1));
					});

			// Evade
			equipmentListView.getItems()
					.addAll("Staff", "Mystic", "Zamorak", "Guthix", "Saradomin", "Enchanted", "Infinity", "Ahrim", "Ghostly", "");
			equipmentListView.setEditable(true);
			equipmentListView.setCellFactory(TextFieldListCell.forListView());
			equipmentListView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
				@Override
				public void handle(ListView.EditEvent<String> editEvent) {
					String value = editEvent.getNewValue();
					if (!value.isEmpty()) {
						if (!equipmentListView.getItems()
								.contains(value) || value.equalsIgnoreCase(
										equipmentListView.getItems()
												.get(editEvent.getIndex()))) {
							equipmentListView.getItems()
									.set(editEvent.getIndex(), value);
						}
					}
					else {
						equipmentListView.getItems()
								.remove(editEvent.getIndex());
					}
					if (!equipmentListView.getItems()
							.contains(""))
						equipmentListView.getItems()
								.add("");
				}

			});
			evadeComboBox.setItems(FXCollections.observableArrayList(EvadeOption.values()));
			evadeComboBox.getSelectionModel()
					.selectFirst();

			startButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Vars.get().location = locationComboBox.getValue();
					Vars.get().maxPlayers = maxPlayersSpinner.getValue();
					Vars.get().dwarfCannon = dwarfCannonCheckBox.isSelected();
					General.println(Vars.get().location);
					General.println("Hopping worlds if more than " + Vars.get().maxPlayers + " found");
					if (Vars.get().dwarfCannon)
						General.println("Hopping worlds if dwarf cannon is active");

					Vars.get().foodName = foodTextField.getText()
							.trim();
					Vars.get().eatHealthPercent = eatSlider.getValue() / 100;
					Vars.get().foodQuantity = foodSpinner.getValue();
					Vars.get().potion1 = potion1ComboBox.getValue();
					Vars.get().potion2 = potion2ComboBox.getValue();
					Vars.get().potion3 = potion3ComboBox.getValue();
					General.println("Taking " + Vars.get().foodQuantity + " " + Vars.get().foodName + " per trip");
					General.println("Eating when health is below " + (int) (Vars.get().eatHealthPercent * Skills.getActualLevel(SKILLS.HITPOINTS)));
					if (Vars.get().potion1 != null)
						General.println("Using " + Vars.get().potion1.getName());
					if (Vars.get().potion2 != null)
						General.println("Using " + Vars.get().potion2.getName());
					if (Vars.get().potion3 != null)
						General.println("Using " + Vars.get().potion3.getName());

					Vars.get().looter = new Looter((int) minLootValueSpinner.getValue(), (int) maxLootValueSpinner.getValue(), Vars.get().location.getArea(), (int) maxLootDistanceSlider.getValue());
					General.println("Looting all items above " + Vars.get().looter.getMinimumValue() + " and below " + Vars.get().looter.getMaximumValue());

					Vars.get().useSpecialAttack = specialAttackCheckBox.isSelected();
					Vars.get().specialEnergy = (int) specialAttackSlider.getValue();
					if (Vars.get().useSpecialAttack)
						General.println("Using special attack when energy is greater or equal to " + Vars.get().specialEnergy);
					if (Vars.get().arrow != null) {
						Vars.get().ammunitionPerTrip = ammunitionPerTripSpinner.getValue();
						General.println("Taking " + Vars.get().ammunitionPerTrip + " " + Vars.get().arrow.getName() + " per trip");
						if (lootAmmunitionCheckBox.isSelected()) {
							Vars.get().ammunitionLooter = new Looter(Integer.MAX_VALUE, Integer.MAX_VALUE, Vars.get().location.getArea(), (int) maxLootDistanceSlider.getValue());
							Vars.get().ammunitionLooter.add(ammunitionTextField.getText(),
									lootAmmunitionStackSpinner.getValue(),
									() -> !Player.getRSPlayer()
											.isInCombat() && Player.getAnimation() == -1);
							General.println("We will be looting " + ammunitionTextField.getText() + " with a minimum stack size of " + lootAmmunitionStackSpinner.getValue() + " when out of combat");
						}
					}

					Vars.get().evadeOption = evadeComboBox.getValue();
					General.println("We will evade other players when " + Strings.toLowerCase(Vars.get().evadeOption.toString()));
					Vars.get().activateQuickPrayers = quickPrayerCheckBox.isSelected();
					if (Vars.get().activateQuickPrayers)
						General.println("Activating quick prayers when evading");

					Vars.get().useAntiban = antibanCheckbox.isSelected();
					Vars.get().useReactionTiming = reactionTimingCheckbox.isSelected();
					if (Vars.get().useAntiban)
						General.println("Using ABCL Antiban");
					if (Vars.get().useReactionTiming)
						General.println("Using ABCL Reaction Timing");

					properties.put(getControls());
					saveEquipment(properties);
					properties.save();
					getFxApplication().close();
				}
			});

			for (Control control : getControls()) {
				String value = properties.getProperty(control.getId());
				if (value == null)
					continue;
				if (control == locationComboBox) {
					locationComboBox.setValue(Locations.valueOf(value));
				}
				else if (control == potion1ComboBox) {
					potion1ComboBox.setValue(Potions.valueOf(value));
				}
				else if (control == potion2ComboBox) {
					potion2ComboBox.setValue(Potions.valueOf(value));
				}
				else if (control == potion3ComboBox) {
					potion3ComboBox.setValue(Potions.valueOf(value));
				}
				else if (control == evadeComboBox) {
					evadeComboBox.setValue(EvadeOption.valueOf(value));
				}
				else {
					properties.set(control);
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	private void loadEquipment(FxProperties properties) {
		if (properties.hasProperty(SLOTS.HELMET))
			Vars.get().helmet = new PlayerEquipment(SLOTS.HELMET, properties.getProperty(SLOTS.HELMET));

		if (properties.hasProperty(SLOTS.BODY))
			Vars.get().body = new PlayerEquipment(SLOTS.BODY, properties.getProperty(SLOTS.BODY));

		if (properties.hasProperty(SLOTS.LEGS))
			Vars.get().legs = new PlayerEquipment(SLOTS.LEGS, properties.getProperty(SLOTS.LEGS));

		if (properties.hasProperty(SLOTS.WEAPON))
			Vars.get().weapon = new PlayerEquipment(SLOTS.WEAPON, properties.getProperty(SLOTS.WEAPON));

		if (properties.hasProperty(SLOTS.CAPE))
			Vars.get().cape = new PlayerEquipment(SLOTS.CAPE, properties.getProperty(SLOTS.CAPE));

		if (properties.hasProperty(SLOTS.BOOTS))
			Vars.get().boots = new PlayerEquipment(SLOTS.BOOTS, properties.getProperty(SLOTS.BOOTS));

		if (properties.hasProperty(SLOTS.GLOVES))
			Vars.get().gloves = new PlayerEquipment(SLOTS.GLOVES, properties.getProperty(SLOTS.GLOVES));

		if (properties.hasProperty(SLOTS.ARROW))
			Vars.get().arrow = new PlayerEquipment(SLOTS.ARROW, properties.getProperty(SLOTS.ARROW));

		Vars.get().shield = new PlayerEquipment(SLOTS.SHIELD, "Anti-dragon shield");
		Vars.get().amulet = new PlayerEquipment(SLOTS.AMULET, "Amulet of glory(6)", Jewelry.AMULET_OF_GLORY.getPredicate());
		Vars.get().ring = new PlayerEquipment(SLOTS.RING, "Ring of dueling(8)", Jewelry.RING_OF_DUELING.getPredicate());

		setEquipmentImages();
	}

	private void saveEquipment(FxProperties properties) {
		properties.put(SLOTS.HELMET, Vars.get().helmet != null ? Vars.get().helmet.getId() : null);
		properties.put(SLOTS.BODY, Vars.get().body != null ? Vars.get().body.getId() : null);
		properties.put(SLOTS.LEGS, Vars.get().legs != null ? Vars.get().legs.getId() : null);
		properties.put(SLOTS.WEAPON, Vars.get().weapon != null ? Vars.get().weapon.getId() : null);
		properties.put(SLOTS.CAPE, Vars.get().cape != null ? Vars.get().cape.getId() : null);
		properties.put(SLOTS.BOOTS, Vars.get().boots != null ? Vars.get().boots.getId() : null);
		properties.put(SLOTS.GLOVES, Vars.get().gloves != null ? Vars.get().gloves.getId() : null);
		properties.put(SLOTS.ARROW, Vars.get().arrow != null ? Vars.get().arrow.getId() : null);

		if (Vars.get().helmet != null)
			Vars.get().playerEquipment.add(Vars.get().helmet);
		if (Vars.get().amulet != null)
			Vars.get().playerEquipment.add(Vars.get().amulet);
		if (Vars.get().body != null)
			Vars.get().playerEquipment.add(Vars.get().body);
		if (Vars.get().legs != null)
			Vars.get().playerEquipment.add(Vars.get().legs);
		if (Vars.get().weapon != null)
			Vars.get().playerEquipment.add(Vars.get().weapon);
		if (Vars.get().cape != null)
			Vars.get().playerEquipment.add(Vars.get().cape);
		if (Vars.get().boots != null)
			Vars.get().playerEquipment.add(Vars.get().boots);
		if (Vars.get().gloves != null)
			Vars.get().playerEquipment.add(Vars.get().gloves);
		if (Vars.get().arrow != null)
			Vars.get().playerEquipment.add(Vars.get().arrow);
		if (Vars.get().shield != null)
			Vars.get().playerEquipment.add(Vars.get().shield);
		if (Vars.get().ring != null)
			Vars.get().playerEquipment.add(Vars.get().ring);
	}

	private void refreshEquipment() {
		Vars.get().helmet = Equipment.getItem(SLOTS.HELMET) != null ? new PlayerEquipment(SLOTS.HELMET) : null;
		Vars.get().body = Equipment.getItem(SLOTS.BODY) != null ? new PlayerEquipment(SLOTS.BODY) : null;
		Vars.get().legs = Equipment.getItem(SLOTS.LEGS) != null ? new PlayerEquipment(SLOTS.LEGS) : null;
		Vars.get().weapon = Equipment.getItem(SLOTS.WEAPON) != null ? new PlayerEquipment(SLOTS.WEAPON) : null;
		Vars.get().cape = Equipment.getItem(SLOTS.CAPE) != null ? new PlayerEquipment(SLOTS.CAPE) : null;
		Vars.get().boots = Equipment.getItem(SLOTS.BOOTS) != null ? new PlayerEquipment(SLOTS.BOOTS) : null;
		Vars.get().gloves = Equipment.getItem(SLOTS.GLOVES) != null ? new PlayerEquipment(SLOTS.GLOVES) : null;
		Vars.get().arrow = Equipment.getItem(SLOTS.ARROW) != null ? new PlayerEquipment(SLOTS.ARROW) : null;
		Vars.get().shield = new PlayerEquipment(SLOTS.SHIELD, "Anti-dragon shield");
		Vars.get().amulet = new PlayerEquipment(SLOTS.AMULET, "Amulet of glory(6)", Jewelry.AMULET_OF_GLORY.getPredicate());
		Vars.get().ring = new PlayerEquipment(SLOTS.RING, "Ring of dueling(8)", Jewelry.RING_OF_DUELING.getPredicate());

		ammunitionTextField.setText(Vars.get().arrow != null ? Vars.get().arrow.getName() : "");

		setEquipmentImages();
	}

	private void setEquipmentImages() {
		equipmentSlotsImageView.setVisible(false);
		equipmentEmptyImageView.setVisible(true);

		helmetImageView.setImage(Vars.get().helmet != null ? Vars.get().helmet.getWritableImage() : null);
		bodyImageView.setImage(Vars.get().body != null ? Vars.get().body.getWritableImage() : null);
		legsImageView.setImage(Vars.get().legs != null ? Vars.get().legs.getWritableImage() : null);
		weaponImageView.setImage(Vars.get().weapon != null ? Vars.get().weapon.getWritableImage() : null);
		capeImageView.setImage(Vars.get().cape != null ? Vars.get().cape.getWritableImage() : null);
		bootsImageView.setImage(Vars.get().boots != null ? Vars.get().boots.getWritableImage() : null);
		glovesImageView.setImage(Vars.get().gloves != null ? Vars.get().gloves.getWritableImage() : null);
		arrowImageView.setImage(Vars.get().arrow != null ? Vars.get().arrow.getWritableImage() : null);
		amuletImageView.setImage(Vars.get().amulet.getWritableImage());
		shieldImageView.setImage(Vars.get().shield.getWritableImage());
		ringImageView.setImage(Vars.get().ring.getWritableImage());
	}
}
