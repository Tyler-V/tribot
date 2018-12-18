package scripts.usa.api.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.tribot.api.General;
import org.tribot.api2007.Equipment.SLOTS;
import org.tribot.api2007.Player;
import org.tribot.util.Util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class FxProperties {

	private final static String EXTENSION = ".properties";
	private Properties properties;
	private String directory;
	private String fileName;

	public FxProperties(String directory) {
		this(directory, Player.getRSPlayer().getName());
	}

	public FxProperties(String directory, String fileName) {
		this.directory = directory;
		this.fileName = fileName;
		this.properties = loadProperties(this.fileName);
	}

	public Properties getProperties() {
		return this.properties;
	}

	public void put(List<Control> controls) {
		controls.forEach(control -> put(control));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void put(Control control) {
		try {
			if (control instanceof TextField) {
				put(control.getId(), ((TextField) control).getText());
			}
			else if (control instanceof Slider) {
				put(control.getId(), Double.toString(((Slider) control).getValue()));
			}
			else if (control instanceof ChoiceBox) {
				put(control.getId(), ((ChoiceBox) control).getValue());
			}
			else if (control instanceof Spinner) {
				IntegerSpinnerValueFactory valueFactory = (IntegerSpinnerValueFactory) ((Spinner) control).getValueFactory();
				String values = valueFactory.getMin() + ", " +
						valueFactory.getMax() +
						", " +
						valueFactory.getValue() +
						", " +
						valueFactory.getAmountToStepBy();
				put(control.getId(), values);
			}
			else if (control instanceof ListView) {
				Object[] values = ((ListView) control).getItems().stream().toArray(String[]::new);
				if (values.length > 0) {
					StringBuilder sb = new StringBuilder();
					for (Object name : values)
						sb.append(name.toString() + ", ");
					put(control.getId(), sb.toString().substring(0, sb.length() - 2));
				}
			}
			else if (control instanceof CheckBox) {
				put(control.getId(), Boolean.toString(((CheckBox) control).isSelected()));
			}
			else if (control instanceof ComboBox) {
				put(control.getId(), ((ComboBox) control).getValue());
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public void put(Object key, Object value) {
		if (value != null)
			this.properties.put(key, value.toString());
		else
			this.properties.remove(key);
	}

	public void put(SLOTS slot, Object value) {
		if (value != null)
			this.properties.put("SLOTS." + slot.toString(), value.toString());
		else
			this.properties.remove("SLOTS." + slot.toString());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void set(Control control) {
		try {
			String value = getProperty(control.getId());
			if (value == null)
				return;
			if (control instanceof TextField) {
				((TextField) control).setText(value);
			}
			else if (control instanceof Slider) {
				((Slider) control).setValue(Double.parseDouble(value));
			}
			else if (control instanceof ChoiceBox) {
				((ChoiceBox) control).setValue(value);
			}
			else if (control instanceof Spinner) {
				String[] split = value.split(", ");
				((Spinner) control).setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.parseInt(split[0]),
						Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
			}
			else if (control instanceof ListView) {
				ObservableList<String> items = FXCollections.observableArrayList(value.split(", "));
				((ListView) control).setItems(items);
			}
			else if (control instanceof CheckBox) {
				((CheckBox) control).setSelected(Boolean.parseBoolean(value));
			}
			else if (control instanceof ComboBox) {
				((ComboBox) control).setValue(value);
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	public String getProperty(String key) {
		return (String) this.properties.get(key);
	}

	public boolean hasProperty(String key) {
		return this.properties.containsKey(key);
	}

	public int getProperty(SLOTS slot) {
		return Integer.parseInt((String) this.properties.get("SLOTS." + slot.toString()));
	}

	public boolean hasProperty(SLOTS slot) {
		return this.properties.containsKey("SLOTS." + slot.toString());
	}

	public Properties loadProperties(String fileName) {
		try {
			File folder = new File(Util.getWorkingDirectory() + "/" + directory);
			File file = new File(folder.toString() + "/" + fileName + EXTENSION);
			if (!file.exists()) {
				return new Properties();
			}
			Properties prop = new Properties();
			prop.load(new FileInputStream(file));
			return prop;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return new Properties();
	}

	public void save(String fileName) {
		try {
			if (fileName == null)
				return;
			File folder = new File(Util.getWorkingDirectory() + "/" + this.directory);
			File file = new File(folder.toString() + "/" + fileName + EXTENSION);
			if (!folder.exists())
				folder.mkdir();
			if (file.createNewFile())
				System.out.println("Added " + fileName + " to " + file.toString());
			else
				System.out.println("Updated " + fileName + " at " + folder.toString());
			if (file.exists())
				this.properties.store(new FileOutputStream(file), null);
			System.out.println(this.properties);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		if (this.fileName == null) {
			this.fileName = this.promptTextInput();
			if (this.fileName == null)
				return;
		}
		save(this.fileName);
	}

	private String promptTextInput() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle(directory);
		dialog.setHeaderText("Please enter a name to save your profile.");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
			return result.get();
		else
			System.out.println("You must enter a name to save your profile.");
		return null;
	}

	public List<String> getProfiles() {
		List<String> names = new ArrayList<String>();
		try {
			File folder = new File(Util.getWorkingDirectory() + "/" + directory);
			for (File file : folder.listFiles()) {
				if (file.getName().contains(EXTENSION))
					names.add(file.getName().replaceAll(EXTENSION, ""));
			}
		}
		catch (Exception e) {
		}
		return names;
	}
}
