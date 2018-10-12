package scripts.tablets.gui;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.allatori.annotations.DoNotRename;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import scripts.tablets.data.Vars;
import scripts.usa.api.gui.AbstractFxController;
import scripts.usa.api.gui.FxProperties;
import scripts.usa.api2007.House.HouseMode;
import scripts.usa.api2007.enums.Lecterns;
import scripts.usa.api2007.enums.Tablets;

public class TabletsFxController extends AbstractFxController {

	private List<Control> getControls() {
		return Arrays
				.asList(lecternComboBox, tabletComboBox, houseComboBox, friendTextField, servantCheckBox, hostingCheckBox, antibanCheckBox, reactionCheckBox);
	}

	@FXML
	@DoNotRename
	private Text versionText;

	@FXML
	@DoNotRename
	private Button startButton;

	@FXML
	@DoNotRename
	public ComboBox<Lecterns> lecternComboBox;

	@FXML
	@DoNotRename
	public ComboBox<Tablets> tabletComboBox;

	@FXML
	@DoNotRename
	public ComboBox<HouseMode> houseComboBox;

	@FXML
	@DoNotRename
	public TextField friendTextField;

	@FXML
	@DoNotRename
	public CheckBox servantCheckBox;

	@FXML
	@DoNotRename
	public CheckBox hostingCheckBox;

	@FXML
	@DoNotRename
	public CheckBox antibanCheckBox;

	@FXML
	@DoNotRename
	public CheckBox reactionCheckBox;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		FxProperties prop = new FxProperties(Vars.get().getScriptManifest().name());
		versionText.setText("v" + Vars.get().getScriptManifest().version());

		lecternComboBox.setItems(FXCollections.observableArrayList(Lecterns.values()));
		tabletComboBox.setItems(FXCollections.observableArrayList(Tablets.values()));
		houseComboBox.setItems(FXCollections.observableArrayList(HouseMode.HOME, HouseMode.FRIENDS));

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Vars.get().lectern = lecternComboBox.getValue();
				Vars.get().tablet = tabletComboBox.getValue();
				Vars.get().houseMode = houseComboBox.getValue();
				Vars.get().friendName = friendTextField.getText().trim();
				Vars.get().servant = servantCheckBox.isSelected();
				Vars.get().hosting = hostingCheckBox.isSelected();
				Vars.get().antiban = antibanCheckBox.isSelected();
				Vars.get().reactionSleep = reactionCheckBox.isSelected();
				getControls().forEach(control -> prop.put(control));
				prop.save();
				getFxApplication().close();
			}
		});

		for (Control control : getControls()) {
			String value = prop.getProperty(control.getId());
			if (value == null)
				continue;
			if (control == lecternComboBox) {
				lecternComboBox.setValue(Lecterns.valueOf(value));
			}
			else if (control == tabletComboBox) {
				tabletComboBox.setValue(Tablets.valueOf(value));
			}
			else if (control == houseComboBox) {
				houseComboBox.setValue(HouseMode.valueOf(value));
			}
			else {
				prop.set(control);
			}
		}
	}
}
