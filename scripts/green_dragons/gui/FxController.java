package scripts.green_dragons.gui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.tribot.api.General;
import org.tribot.api2007.Equipment.SLOTS;

import com.allatori.annotations.DoNotRename;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import scripts.green_dragons.data.Locations;
import scripts.green_dragons.data.Vars;
import scripts.usa.api.gui.AbstractFxController;
import scripts.usa.api.gui.FxProperties;
import scripts.usa.api.web.items.osbuddy.OSBuddyUtils;
import scripts.usa.api2007.Equipment;
import scripts.usa.api2007.enums.Potions;

public class FxController extends AbstractFxController {

	private List<Control> getControls() {
		return Arrays.asList(locationComboBox, foodTextField, foodSpinner, potion1ComboBox, potion2ComboBox, potion3ComboBox);
	}

	@FXML
	@DoNotRename
	private Label versionLabel;

	@FXML
	@DoNotRename
	private ComboBox<Locations> locationComboBox;

	@FXML
	@DoNotRename
	private TextField foodTextField;

	@FXML
	@DoNotRename
	private Spinner<Integer> foodSpinner;

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
	private ImageView ammunitionImageView;

	@FXML
	@DoNotRename
	private Button refreshEquipmentButton;

	@FXML
	@DoNotRename
	private TextField ammunitionTextField;

	@FXML
	@DoNotRename
	private Spinner<Integer> ammunitionSpinner;

	@FXML
	@DoNotRename
	private Slider minItemValueSlider;

	@FXML
	@DoNotRename
	private Spinner<Integer> maxItemValueSpinner;

	@FXML
	@DoNotRename
	private ListView<String> equipmentListView;

	@FXML
	@DoNotRename
	private Button startButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			FxProperties properties = new FxProperties(Vars.get().getScriptManifest().name());
			versionLabel.setText("v" + Vars.get().getScriptManifest().version());

			// General
			locationComboBox.setItems(FXCollections.observableArrayList(Locations.values()));

			// Consumables
			foodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 28, 0, 1));
			potion1ComboBox.setItems(FXCollections.observableArrayList(Potions.values()));
			potion2ComboBox.setItems(FXCollections.observableArrayList(Potions.values()));
			potion3ComboBox.setItems(FXCollections.observableArrayList(Potions.values()));

			// Equipment
			refreshEquipmentButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					refreshEquipment();
				}
			});

			equipmentListView.getItems().addAll("Staff", "Mystic", "Zamorak", "Guthix", "Saradomin", "Enchanted", "Infinity", "Ahrim", "Ghostly", "");
			equipmentListView.setEditable(true);
			equipmentListView.setCellFactory(TextFieldListCell.forListView());
			equipmentListView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
				@Override
				public void handle(ListView.EditEvent<String> editEvent) {
					String value = editEvent.getNewValue();
					if (!value.isEmpty()) {
						if (!equipmentListView.getItems().contains(value) ||
								value.equalsIgnoreCase(equipmentListView.getItems().get(editEvent.getIndex()))) {
							equipmentListView.getItems().set(editEvent.getIndex(), value);
						}
					}
					else {
						equipmentListView.getItems().remove(editEvent.getIndex());
					}
					if (!equipmentListView.getItems().contains(""))
						equipmentListView.getItems().add("");
				}

			});

			helmetImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
				System.out.println("Tile pressed");
				event.consume();
			});

			startButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					getControls().forEach(control -> properties.put(control));
					properties.save();
					Vars.get().location = locationComboBox.getValue();
					Vars.get().foodName = foodTextField.getText().trim();
					Vars.get().foodQuantity = foodSpinner.getValue();
					Vars.get().potion1 = potion1ComboBox.getValue();
					Vars.get().potion2 = potion2ComboBox.getValue();
					Vars.get().potion3 = potion3ComboBox.getValue();
					getFxApplication().close();

					General.println(Vars.get().location);
					General.println(Vars.get().foodName + " " + Vars.get().foodQuantity);
					General.println(Vars.get().potion1);
					General.println(Vars.get().potion2);
					General.println(Vars.get().potion3);
				}
			});

			for (Control control : getControls()) {
				String value = properties.getProperty(control.getId());
				if (value == null)
					return;
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
				else {
					properties.set(control);
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	private void refreshEquipment() {
		equipmentSlotsImageView.setVisible(false);
		equipmentEmptyImageView.setVisible(true);
		helmetImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.HELMET)));
		amuletImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.AMULET)));
		bodyImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.BODY)));
		legsImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.LEGS)));
		bootsImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.BOOTS)));
		capeImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.CAPE)));
		weaponImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.WEAPON)));
		glovesImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.GLOVES)));
		ammunitionImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.ARROW)));
		shieldImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.SHIELD)));
		ringImageView.setImage(OSBuddyUtils.getWritableImage(Equipment.getItem(SLOTS.RING)));
	}
}
