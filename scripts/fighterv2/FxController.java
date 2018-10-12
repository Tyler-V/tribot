package scripts.fighterv2;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSNPC;

import com.allatori.annotations.DoNotRename;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import scripts.usa.api.util.fx.FxSettings;

public class FxController implements Initializable {

	private FxSettings settings;
	private double xOffset;
	private double yOffset;

	@FXML
	@DoNotRename
	private Pane pane;

	@FXML
	@DoNotRename
	private Label title;

	@FXML
	@DoNotRename
	public TextField startTileText;

	@FXML
	@DoNotRename
	private Button refreshTileButton;

	@FXML
	@DoNotRename
	private ListView<String> availableNpcsList;

	@FXML
	@DoNotRename
	public ListView<String> selectedNpcsList;

	@FXML
	@DoNotRename
	public Slider npcDistanceSlider;

	@FXML
	@DoNotRename
	private Button refreshNpcsButton;

	@FXML
	@DoNotRename
	public ChoiceBox<String> trainingOption;

	@FXML
	@DoNotRename
	public TableView<Skill> trainingTable;

	@FXML
	@DoNotRename
	private TableColumn<Skill, String> skillColumn;

	@FXML
	@DoNotRename
	private TableColumn<Skill, String> levelColumn;

	@FXML
	@DoNotRename
	private Button refreshSkillsButton;

	@FXML
	@DoNotRename
	public ListView<String> lootList;

	@FXML
	@DoNotRename
	public TextField lootText;

	@FXML
	@DoNotRename
	private Button addLootButton;

	@FXML
	@DoNotRename
	public Spinner<Integer> minLootSpinner;

	@FXML
	@DoNotRename
	public Spinner<Integer> maxLootSpinner;

	@FXML
	@DoNotRename
	public ChoiceBox<String> lootingStyleOption;

	@FXML
	@DoNotRename
	public Slider lootDistanceSlider;

	@FXML
	@DoNotRename
	public TextField foodText;

	@FXML
	@DoNotRename
	public Spinner<Integer> foodSpinner;

	@FXML
	@DoNotRename
	public Slider eatSlider;

	@FXML
	@DoNotRename
	public CheckBox ABCL;

	@FXML
	@DoNotRename
	public CheckBox autoResponder;

	@FXML
	@DoNotRename
	private Button closeButton;

	@FXML
	@DoNotRename
	private Button saveButton;

	@FXML
	@DoNotRename
	private SplitMenuButton loadButton;

	@SuppressWarnings("rawtypes")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("JavaFX Initialized");
		settings = new FxSettings(title.getText());

		trainingOption.setItems(FXCollections.observableArrayList("Combat Level", "Skill Level"));
		trainingOption.getSelectionModel().selectFirst();

		trainingOption.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number indexFrom, Number indexTo) {
				toggleTrainingTable((Integer) indexTo == 1 ? false : true);
			}
		});

		skillColumn.setCellValueFactory(new PropertyValueFactory<>("skill"));
		levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
		trainingTable.setItems(FXCollections
				.observableArrayList(new Skill(SKILLS.ATTACK, "1"), new Skill(SKILLS.STRENGTH, "1"), new Skill(SKILLS.DEFENCE, "1")));

		levelColumn.setCellFactory(TextFieldTableCell.<Skill> forTableColumn());
		levelColumn.setOnEditCommit((CellEditEvent<Skill, String> t) -> {
			((Skill) t.getTableView().getItems().get(t.getTablePosition().getRow())).setLevel(t.getNewValue());
		});

		trainingTable.setFixedCellSize(25);
		trainingTable.prefHeightProperty().bind(trainingTable.fixedCellSizeProperty().multiply(Bindings.size(trainingTable.getItems()).add(1.01)));
		trainingTable.minHeightProperty().bind(trainingTable.prefHeightProperty());
		trainingTable.maxHeightProperty().bind(trainingTable.prefHeightProperty());

		trainingTable.setDisable(true);
		refreshSkillsButton.setDisable(true);

		lootText.setPromptText("Item Name");

		minLootSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 1));
		minLootSpinner.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String before, String after) {
				try {
					IntegerSpinnerValueFactory vf = (IntegerSpinnerValueFactory) ((javafx.scene.control.Spinner) minLootSpinner).getValueFactory();
					int value = Integer.parseInt(after);
					if (value >= vf.getMin() && value <= vf.getMax()) {
						vf.setValue(value);
					}
					else {
						vf.setValue(Math.max(Math.min(vf.getMax(), value), vf.getMin()));
					}
				}
				catch (Exception e) {
				}
			}
		});

		maxLootSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0, 1));
		maxLootSpinner.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String before, String after) {
				try {
					IntegerSpinnerValueFactory vf = (IntegerSpinnerValueFactory) ((javafx.scene.control.Spinner) maxLootSpinner).getValueFactory();
					int value = Integer.parseInt(after);
					if (value >= vf.getMin() && value <= vf.getMax()) {
						vf.setValue(value);
					}
					else {
						vf.setValue(Math.max(Math.min(vf.getMax(), value), vf.getMin()));
					}
				}
				catch (Exception e) {
				}
			}
		});

		lootingStyleOption.setItems(FXCollections.observableArrayList("Always Loot", "Loot After Kill"));
		lootingStyleOption.getSelectionModel().selectFirst();

		foodSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 28, 0, 1));
		foodSpinner.getEditor().textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observableValue, String before, String after) {
				try {
					IntegerSpinnerValueFactory vf = (IntegerSpinnerValueFactory) ((javafx.scene.control.Spinner) foodSpinner).getValueFactory();
					int value = Integer.parseInt(after);
					if (value >= vf.getMin() && value <= vf.getMax()) {
						vf.setValue(value);
					}
					else {
						vf.setValue(Math.max(Math.min(vf.getMax(), value), vf.getMin()));
					}
				}
				catch (Exception e) {
				}
			}
		});

		ABCL.setSelected(true);

		loadButton.showingProperty().addListener((obs, wasShowing, isNowShowing) -> {
			if (isNowShowing) {
				loadButton.getItems().clear();
				List<MenuItem> menuItems = new ArrayList<MenuItem>();
				for (String profile : settings.getProfiles()) {
					MenuItem menu = new MenuItem(profile);
					menu.setOnAction(event -> loadSettings(profile));
					menuItems.add(menu);
				}
				loadButton.getItems().addAll(menuItems);
			}
		});
	}

	public class Skill {
		private final SKILLS skill;
		private final SimpleStringProperty level;

		public Skill(SKILLS skill, String level) {
			this.skill = skill;
			this.level = new SimpleStringProperty(level);
		}

		public SKILLS getSkill() {
			return this.skill;
		}

		public String getLevel() {
			return this.level.get();
		}

		public void setLevel(String level) {
			this.level.set(level);
		}
	}

	@FXML
	@DoNotRename
	private void onStart() {
		closeWindow();
	}

	@FXML
	@DoNotRename
	private void refreshTile(MouseEvent event) {
		startTileText.setText(Player.getPosition().toString());
	}

	@FXML
	@DoNotRename
	private void refreshNpcs(ActionEvent event) {
		RSNPC[] npcs = NPCs.getAll(new Filter<RSNPC>() {
			public boolean accept(RSNPC npc) {
				String[] actions = npc.getActions();
				if (actions.length == 0 || !Arrays.asList(actions).contains("Attack"))
					return false;
				return true;
			}
		});
		List<String> list = new ArrayList<String>();
		for (RSNPC npc : npcs) {
			if (npc == null)
				continue;
			String name = npc.getName();
			if (name == null || list.contains(name) || selectedNpcsList.getItems().contains(name))
				continue;
			list.add(npc.getName());
		}
		ObservableList<String> items = FXCollections.observableArrayList(list);
		availableNpcsList.setItems(items);
	}

	@FXML
	@DoNotRename
	private void addNpc(MouseEvent event) {
		String selected = availableNpcsList.getSelectionModel().getSelectedItem();
		if (selected == null || selectedNpcsList.getItems().contains(selected))
			return;
		availableNpcsList.getItems().remove(selected);
		availableNpcsList.getSelectionModel().clearSelection();
		selectedNpcsList.getItems().add(selected);
	}

	@FXML
	@DoNotRename
	private void removeNpc(MouseEvent event) {
		String selected = selectedNpcsList.getSelectionModel().getSelectedItem();
		if (selected == null)
			return;
		selectedNpcsList.getItems().remove(selected);
		selectedNpcsList.getSelectionModel().clearSelection();
		availableNpcsList.getItems().add(selected);
	}

	@FXML
	@DoNotRename
	private void refreshSkills(ActionEvent event) {
		ObservableList<Skill> items = FXCollections.observableArrayList();
		trainingTable.getItems().forEach(s -> {
			items.add(new Skill(s.getSkill(), Integer.toString(Skills.getActualLevel(s.getSkill()))));
		});
		trainingTable.setItems(items);
	}

	private void toggleTrainingTable(boolean visible) {
		trainingTable.setDisable(visible);
		refreshSkillsButton.setDisable(visible);
	}

	@FXML
	@DoNotRename
	private void addItem() {
		if (lootList != null && !lootList.getItems().contains(lootText.getText()))
			lootList.getItems().add(lootText.getText());
		lootText.clear();
	}

	@FXML
	@DoNotRename
	private void removeItem(MouseEvent event) {
		String selected = lootList.getSelectionModel().getSelectedItem();
		lootList.getItems().remove(selected);
		lootList.getSelectionModel().clearSelection();
	}

	@FXML
	@DoNotRename
	private void minValueChanged(InputMethodEvent event) {
		System.out.println(event);
	}

	private List<Object> getControls() {
		return Arrays
				.asList(startTileText, npcDistanceSlider, selectedNpcsList, trainingOption, lootList, minLootSpinner, maxLootSpinner, lootDistanceSlider, lootingStyleOption, foodText, foodSpinner, eatSlider, autoResponder, ABCL);
	}

	@FXML
	@DoNotRename
	private void saveSettings() {
		settings.newProperties();
		getControls().forEach(control -> settings.put(control));
		settings.save();
	}

	public void loadSettings(String fileName) {
		settings.load(fileName);
		getControls().forEach(control -> settings.set(control));
	}

	@FXML
	@DoNotRename
	public void closeWindow() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	@DoNotRename
	private void minimizeWindow() {
		Stage stage = (Stage) closeButton.getScene().getWindow();
		stage.setIconified(true);
	}

	@FXML
	@DoNotRename
	private void onMousePressed(MouseEvent event) {
		xOffset = event.getSceneX();
		yOffset = event.getSceneY();
	}

	@FXML
	@DoNotRename
	private void onMouseDragged(MouseEvent event) {
		pane.getScene().getWindow().setX(event.getScreenX() - xOffset);
		pane.getScene().getWindow().setY(event.getScreenY() - yOffset);
	}
}
