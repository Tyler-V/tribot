package scripts.crafter.gui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.tribot.api2007.Player;

import com.allatori.annotations.DoNotRename;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.text.Text;
import scripts.crafter.data.Crafting;
import scripts.crafter.data.Locations;
import scripts.crafter.data.Locations.Type;
import scripts.crafter.data.Vars;
import scripts.usa.api.gui.AbstractFxController;
import scripts.usa.api.gui.FxProperties;

public class FxController extends AbstractFxController {

	private List<Control> getControls() {
		return Arrays.asList(materialOption, productOption, locationOption, autoProgressionCheckbox);
	}

	private FxProperties properties;

	@FXML
	@DoNotRename
	private Text versionText;

	@FXML
	@DoNotRename
	public ComboBox<String> materialOption;

	@FXML
	@DoNotRename
	public ComboBox<String> productOption;

	@FXML
	@DoNotRename
	public CheckBox autoProgressionCheckbox;

	@FXML
	@DoNotRename
	public ComboBox<String> locationOption;

	@FXML
	@DoNotRename
	private Button startButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("JavaFX Initialized");
		properties = new FxProperties("Usa Crafter");
		versionText.setText("v" + Vars.get().getScriptManifest().version());

		materialOption.setItems(FXCollections.observableArrayList(Crafting.getMaterials()));
		materialOption.getSelectionModel().selectFirst();
		materialOption.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				productOption
						.setItems(FXCollections.observableArrayList(Crafting.getProductsFor(materialOption.getSelectionModel().getSelectedItem())));
				if (autoProgressionCheckbox.isSelected()) {
					productOption.getSelectionModel()
							.select(Crafting.getHighestProductFor(materialOption.getSelectionModel().getSelectedItem()).getName());
				}
				else {
					productOption.getSelectionModel().selectFirst();
				}
			}
		});

		productOption.setItems(FXCollections.observableArrayList(Crafting.getProductsFor(materialOption.getSelectionModel().getSelectedItem())));
		productOption.getSelectionModel().selectFirst();
		productOption.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Type type = Crafting.getProduct(productOption.getSelectionModel().getSelectedItem()).getType();
				if (type != Type.BANK) {
					locationOption.setItems(FXCollections.observableArrayList(Locations.getLocations(type)));
					locationOption.getSelectionModel().selectFirst();
					locationOption.setDisable(false);
				}
				else {
					locationOption.getItems().clear();
					locationOption.setDisable(true);
				}
			}
		});

		autoProgressionCheckbox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				productOption.setDisable(newValue);
				if (newValue) {
					productOption.getSelectionModel()
							.select(Crafting.getHighestProductFor(materialOption.getSelectionModel().getSelectedItem()).getName());
				}
			}
		});

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Vars.get().material = materialOption.getSelectionModel().getSelectedItem();
				Vars.get().product = Crafting.getProduct(productOption.getSelectionModel().getSelectedItem());
				Vars.get().location = Locations.getLocation(locationOption.getSelectionModel().getSelectedItem());
				Vars.get().autoProgression = autoProgressionCheckbox.isSelected();
				System.out.println("Product: " + Vars.get().product);
				System.out.println("Auto Progression: " + Vars.get().autoProgression);
				System.out.println("Location: " + Vars.get().location);

				getControls().forEach(control -> properties.put(control));
				properties.save(Player.getRSPlayer().getName());
				getFxApplication().close();
			}
		});

		properties.loadProperties(Player.getRSPlayer().getName());
		for (Control control : getControls()) {
			properties.set(control);
		}
	}
}
