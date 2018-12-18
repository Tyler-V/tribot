package scripts.bot_tools.gui;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.tribot.api.General;

import com.allatori.annotations.DoNotRename;
import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import scripts.bot_tools.data.BotToolsSettings;
import scripts.bot_tools.data.Vars;
import scripts.bot_tools.models.GEItem;
import scripts.usa.api.framework.task.ScriptVars;
import scripts.usa.api.gui.AbstractFxController;
import scripts.usa.api.gui.FxProfile;
import scripts.usa.api.gui.JFXDialogs;
import scripts.usa.api.gui.JFXDialogs.OptionDialog;
import scripts.usa.api.gui.JFXDialogs.TextPrompt;
import scripts.usa.api.web.items.osbuddy.OSBuddy;
import scripts.usa.api.web.items.osbuddy.OSBuddyItem;
import scripts.usa.api2007.grand_exchange.enums.OfferType;

public class FxController extends AbstractFxController {

	@FXML
	@DoNotRename
	private StackPane stackPane;

	@FXML
	@DoNotRename
	private Label versionLabel;

	@FXML
	@DoNotRename
	private JFXComboBox<OfferType> offerComboBox;

	@FXML
	@DoNotRename
	private JFXTextField nameTextField;

	@FXML
	@DoNotRename
	private Label quantityLabel;

	@FXML
	@DoNotRename
	private Spinner<Integer> quantitySpinner;

	@FXML
	@DoNotRename
	private Slider pricePercentageSlider;

	@FXML
	@DoNotRename
	private JFXButton addItemButton;

	@FXML
	@DoNotRename
	private JFXListView<GEItem> sellingListView;

	@FXML
	@DoNotRename
	private JFXButton sellingEditButton;

	@FXML
	@DoNotRename
	private JFXButton sellingRemoveButton;

	@FXML
	@DoNotRename
	private JFXListView<GEItem> buyingListView;

	@FXML
	@DoNotRename
	private JFXButton buyingEditButton;

	@FXML
	@DoNotRename
	private JFXButton buyingRemoveButton;

	@FXML
	@DoNotRename
	private JFXButton saveButton;

	@FXML
	@DoNotRename
	private JFXButton startButton;

	@FXML
	@DoNotRename
	private JFXButton loadButton;

	private FxProfile profile;
	private BotToolsSettings settings;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		profile = new FxProfile(ScriptVars.get()
				.getScriptManifest()
				.name());
		settings = new BotToolsSettings();
		bindDirectional();

		offerComboBox.setItems(FXCollections.observableArrayList(OfferType.values()));
		offerComboBox.getSelectionModel()
				.selectedItemProperty()
				.addListener((ObservableValue<? extends OfferType> observable, OfferType oldValue, OfferType newValue) -> {
					boolean isSelling = newValue == OfferType.SELL;
					quantityLabel.setDisable(isSelling);
					quantitySpinner.setDisable(isSelling);
				});
		offerComboBox.getSelectionModel()
				.select(OfferType.SELL);

		HashMap<Object, OSBuddyItem> items = OSBuddy.getItems();
		List<String> names = items.entrySet()
				.stream()
				.map(Map.Entry::getKey)
				.filter(item -> item instanceof String)
				.map(key -> (String) key)
				.collect(Collectors.toList());

		setAutoCompletePopup(nameTextField, names);

		quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1, 1));
		quantitySpinner.getEditor()
				.textProperty()
				.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
					int value = newValue.isEmpty() ? Integer.parseInt(oldValue) : Integer.parseInt(newValue);
					quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, value, 1));
				});

		setAddItemButton(addItemButton);

		setSellingListView(sellingListView);
		setEditItemButton(sellingEditButton, sellingListView);
		setRemoveItemButton(sellingRemoveButton, sellingListView);

		setBuyingListView(buyingListView);
		setEditItemButton(buyingEditButton, buyingListView);
		setRemoveItemButton(buyingRemoveButton, buyingListView);

		saveButton.setOnAction(e1 -> {
			TextPrompt textPrompt = JFXDialogs.createTextPrompt(stackPane, "Save your profile", "Enter a name:", profile.getFileName(), "Save");
			textPrompt.getButton()
					.setOnAction(e2 -> {
						profile.save(textPrompt.getText(), settings);
						textPrompt.close();
					});
		});

		loadButton.setOnAction(e1 -> {
			OptionDialog optionDialog = JFXDialogs.createOptionDialog(stackPane, "Select a profile", "Load", "Profiles", profile.getProfiles());
			optionDialog.getButton()
					.setOnAction(e2 -> {
						optionDialog.close();
						settings = (BotToolsSettings) profile.load(optionDialog.getSelected(), BotToolsSettings.class);
						bindDirectional();
					});
		});

		startButton.setOnAction(e -> {
			Vars.get().buyingItems = new ArrayList<GEItem>(settings.getBuyingItems());
			Vars.get().sellingItems = new ArrayList<GEItem>(settings.getSellingItems());
			getFxApplication().close();
		});
	}

	private void bindDirectional() {
		buyingListView.itemsProperty()
				.bindBidirectional(settings.getBuyingItemsProperty());
		sellingListView.itemsProperty()
				.bindBidirectional(settings.getSellingItemsProperty());
	}

	private void setAddItemButton(JFXButton button) {
		button.setOnAction(e -> {
			String name = nameTextField.getText();
			if (name == null || name.isEmpty() || OSBuddy.get(name) == null)
				return;
			OfferType offerType = offerComboBox.getSelectionModel()
					.getSelectedItem();
			JFXListView<GEItem> listView = offerType == OfferType.SELL ? sellingListView : buyingListView;
			GEItem item = new GEItem(offerType, name, offerType == OfferType.BUY ? quantitySpinner.getValue() : 0, pricePercentageSlider.getValue());
			try {
				for (int i = 0; i < listView.getItems()
						.size(); i++) {
					GEItem listItem = listView.getItems()
							.get(i);
					if (listItem.getName()
							.equals(item.getName())) {
						listView.getItems()
								.set(i, item);
						return;
					}
				}
				listView.getItems()
						.add(item);
			}
			finally {
				nameTextField.clear();
			}
		});
	}

	private void setEditItemButton(JFXButton button, JFXListView<GEItem> listView) {
		button.setOnAction(e -> {
			GEItem item = listView.getSelectionModel()
					.getSelectedItem();
			offerComboBox.getSelectionModel()
					.select(item.getOfferType());
			nameTextField.setText(item.getName());
			pricePercentageSlider.setValue(item.getPricePercentage());
		});
	}

	private void setRemoveItemButton(JFXButton button, JFXListView<GEItem> listView) {
		button.setOnAction(e -> {
			listView.getItems()
					.remove(listView.getSelectionModel()
							.getSelectedItem());
		});
	}

	private void setSellingListView(JFXListView<GEItem> listView) {
		listView.setCellFactory(param -> new ListCell<GEItem>() {
			@Override
			protected void updateItem(GEItem item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				}
				else {
					setText("Selling all " + item.getName() + (item.getPricePercentage() != 0 ? " (" + (int) item.getPricePercentage() + "%)" : ""));
				}
			}
		});
	}

	private void setBuyingListView(JFXListView<GEItem> listView) {
		listView.setCellFactory(param -> new ListCell<GEItem>() {
			@Override
			protected void updateItem(GEItem item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				}
				else {
					setText("Buying " + item.getQuantity() + " " + item.getName() + (item.getPricePercentage() != 0 ? " (" + (int) item.getPricePercentage() + "%)" : ""));
				}
			}
		});
	}

	private void setAutoCompletePopup(TextField textField, List<String> names) {
		JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<String>();

		autoCompletePopup.getSuggestions()
				.addAll(names);
		autoCompletePopup.setSelectionHandler(event -> {
			textField.setText(event.getObject());
		});
		textField.textProperty()
				.addListener(observable -> {
					autoCompletePopup.filter(string -> string.toLowerCase()
							.contains(textField.getText()
									.toLowerCase()));
					if (autoCompletePopup.getFilteredSuggestions()
							.isEmpty() ||
							textField.getText()
									.isEmpty())
						autoCompletePopup.hide();
					else {
						if (textField.isFocused())
							autoCompletePopup.show(textField);
					}
				});
	}
}
